package dev.oblac.sisyphus.game

import dev.oblac.sisyphus.MemoryStore
import dev.oblac.sisyphus.Store
import dev.oblac.sisyphus.gfx.hash01
import dev.oblac.sisyphus.wallSeconds
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

/** How far Sisyphus walks, in pixels, for one full turn of the knob. */
const val PX_PER_TURN = 12.0
const val DAY_LENGTH = PX_PER_TURN * Config.TURNS_PER_DAY

/** The first day starts just after sunrise. */
const val START_TIME = 0.27

/** The final climb, in pixels walked. Everything is bought by then, so he walks at the fastest. */
const val CLIMB = Config.CLIMB_TURNS * Config.SUN_CAP * PX_PER_TURN

/** How far he walks while one text of a part shows in the popup over its stone. */
private const val READ = Config.READ_TURNS * PX_PER_TURN

private const val TICKS_PER_TURN = 24
private const val TICK_ANGLE = 2 * PI / TICKS_PER_TURN
private const val KEY_EFFORT = "effort"
private const val KEY_POSITION = "position"
private const val KEY_SOUND = "sound"
private const val KEY_KNOB = "knob"
private const val KEY_QUOTE_SEED = "quoteSeed"
private const val KEY_QUOTES_DRAWN = "quotesDrawn"
private const val KEY_OBOLS = "obols"
private const val KEY_NEXT_OBOL = "nextObol"
private const val KEY_SAVED_AT = "savedAt"
private const val KEY_SUMMIT = "summit"
private const val KEY_CLIMBS = "climbs"
private const val KEY_PLACE = "place"
private const val KEY_NEXT_VISION = "nextVision"
private const val KEY_VISION_SEED = "visionSeed"
private const val KEY_VISIONS_DRAWN = "visionsDrawn"
private const val KEY_RIGHT_ANSWERS = "rightAnswers"
private const val KEY_TURNS_PUSHED = "turnsPushed"
private const val KEY_DAYS_BEFORE = "daysBefore"
private const val KEY_LABORS_SEEN = "laborsSeen"
private const val KEY_STELES_TOLD = "stelesTold"
private const val KEY_NEXT_STELE = "nextStele"
private const val KEY_TALE = "tale"
private const val KEY_TALES_TOLD = "talesTold"
private const val KEY_NEXT_JOKER = "nextJoker"
private const val KEY_JOKER = "joker"
private const val KEY_JOKERS_SEEN = "jokersSeen"
private const val SAVE_EVERY = 1.0

/** How many clicks of the knob the dev screen leaves before the next obol. */
private const val CLICKS_TO_OBOL = 3

private fun shopKey(line: Line) = "shop." + line.id
private fun metKey(joker: Joker) = "met." + joker.id

fun wrapAngle(a: Double): Double {
    var x = a
    while (x > PI) x -= 2 * PI
    while (x < -PI) x += 2 * PI
    return x
}

/** A heavy knob: it follows the finger, and coasts for a short while after a flick. */
class Knob(angle: Double = 0.0) {
    var angle = angle
        private set
    var velocity = 0.0
        private set
    var held = false
        private set
    /** How many times a finger touched the knob. */
    var grabs = 0
        private set
    private var touch: Double? = null
    private var pending = 0.0

    fun grab(touchAngle: Double?) {
        held = true
        grabs++
        touch = touchAngle
        velocity = 0.0
        pending = 0.0
    }

    /** The knob stops at once: it does not coast. */
    fun stop() {
        velocity = 0.0
        pending = 0.0
    }

    /** A null angle means the finger is too close to the center to read a direction. */
    fun drag(touchAngle: Double?) {
        if (!held) return
        val last = touch
        if (last != null && touchAngle != null) pending += wrapAngle(touchAngle - last)
        touch = touchAngle
    }

    fun release() {
        held = false
        touch = null
        velocity = velocity.coerceIn(-MAX_SPIN, MAX_SPIN)
    }

    /** Advances the knob and returns how far it turned, in radians. */
    fun update(dt: Double): Double {
        val turn: Double
        if (held) {
            turn = pending
            pending = 0.0
            if (dt > 0) velocity += (turn / dt - velocity) * (1 - exp(-dt * 18))
        } else {
            turn = velocity * dt
            velocity *= exp(-FRICTION * dt)
            if (abs(velocity) < 0.2) velocity = 0.0
        }
        angle += turn
        return turn
    }

    private companion object {
        const val MAX_SPIN = 6 * PI
        const val FRICTION = 2.6
    }
}

enum class Weather(val clouds: Int, val wind: Double, val overcast: Float, val rain: Boolean, val birds: Double) {
    CLEAR(1, 1.2, 0f, false, 0.035),
    FAIR(4, 1.6, 0f, false, 0.05),
    CLOUDY(7, 2.2, 0.45f, false, 0.02),
    WINDY(5, 6.5, 0.1f, false, 0.12),
    RAIN(9, 3.2, 0.75f, true, 0.0);

    companion object {
        fun of(day: Int): Weather {
            if (day <= 1) return FAIR
            val r = hash01(day, seed = 7)
            return when {
                r < 0.25f -> CLEAR
                r < 0.55f -> FAIR
                r < 0.70f -> CLOUDY
                r < 0.85f -> WINDY
                else -> RAIN
            }
        }
    }
}

/**
 * The worlds Sisyphus can push his rock in, each with its own look and items.
 * Saved games know a world by its [id], which never changes. Corinth is free; every other world is bought once as its [product],
 * the name of its entitlement and its package in RevenueCat, which never changes either. The stores' products carry the same name,
 * except on Google Play, where The Underworld is world_underworld2.
 */
enum class Place(val id: Int, val title: String, val note: String, val product: String? = null) {
    CORINTH(0, "Corinth", "the hill by the sea"),
    ETNA(1, "Mount Etna", "the hill on the volcano", "world_etna"),
    UNDERWORLD(2, "The Underworld", "the hill in Tartarus", "world_underworld"),
    CAIRO(3, "Cairo", "the pyramid by the Nile", "world_cairo"),
}

/**
 * The state of the push. The knob moves Sisyphus up the hill, whichever way it turns.
 * Effort is what moves the sun: days pass only when he works. A strong rock and strong helpers
 * earn obols faster, but move the sun at most [Config.SUN_CAP] times faster. Helpers on their own push
 * all the time: while the game runs, and for the time it was away, measured by the [wall] clock.
 * Everything the player can see comes back from [store] when the game starts again.
 */
class Game(private val store: Store = MemoryStore(), seed: Int = Random.nextInt(), private val wall: () -> Double = ::wallSeconds) {
    val knob = Knob(store.load(KEY_KNOB) ?: 0.0)

    var position = store.load(KEY_POSITION) ?: 0.0
        private set
    var effort = store.load(KEY_EFFORT) ?: 0.0
        private set

    /** Pixels walked up during the last update. */
    var moved = 0.0
        private set
    /** Smoothed walking speed, in pixels per second. */
    var speed = 0.0
        private set
    var stillFor = 10.0
        private set
    var clock = 0.0
        private set

    var soundOn = store.load(KEY_SOUND) != 0.0
        private set

    /** The world he pushes in. */
    var place = store.load(KEY_PLACE)?.toInt().let { id -> Place.entries.firstOrNull { it.id == id } } ?: Place.CORINTH
        private set
    private val random = Random(seed)
    var shop = Shop(
        Line.entries.associateWith { store.load(shopKey(it))?.toInt() ?: 0 }, store.load(KEY_OBOLS) ?: 0.0, place,
        store.load(KEY_NEXT_OBOL)?.takeIf { it > 0 }, random,
    )
        private set

    /** Where the hill ends, in pixels walked; null until all 15 items are bought and the final climb begins. */
    var summit = store.load(KEY_SUMMIT)?.takeIf { it >= 0 }?.let { min(it, position + CLIMB) }
        private set
    /** How many times Sisyphus reached the top. Starting over keeps it. */
    var climbs = store.load(KEY_CLIMBS)?.toInt() ?: 0
        private set
    /** The player's own turns that pushed, in all climbs and worlds. */
    var turnsPushed = store.load(KEY_TURNS_PUSHED) ?: 0.0
        private set
    /** The days pushed through in all the climbs before this one. */
    var daysBefore = store.load(KEY_DAYS_BEFORE)?.toInt() ?: 0
        private set
    /** How many labors were done when the Labors tab was last open. */
    var laborsSeen = store.load(KEY_LABORS_SEEN)?.toInt() ?: 0
        private set
    /** How far the final climb has come: from 0 when it begins to 1 at the top; null before it begins. */
    val climbed: Double? get() = summit?.let { (1 - (it - position) / CLIMB).coerceIn(0.0, 1.0) }

    /** Where the herms of the final climb stand, in pixels walked: one every [Config.HERM_TURNS], up to the top. None before the climb. */
    val herms: List<Double>
        get() = summit?.let { top -> (1 until Config.CLIMB_TURNS / Config.HERM_TURNS).map { top - CLIMB + it * Config.HERM_TURNS * Config.SUN_CAP * PX_PER_TURN } }
            ?: emptyList()

    /** The tale that the stones of this climb tell, one part for each item bought. An old save tells the tale of Sisyphus. */
    var tale = store.load(KEY_TALE)?.let { TALES.getOrNull(it.toInt()) } ?: SISYPHUS
        private set
    /** The tales told so far, one bit for each id. A tale is told when its last part is told. */
    var talesTold = store.load(KEY_TALES_TOLD)?.toInt() ?: 0
        private set
    /** The tales take their own random numbers, so that the visions and the sights stay the same. */
    private val taleRandom = Random(seed + 1)
    /** The parts of the tale told in this climb. An old save counts its items as told. */
    var stelesTold = store.load(KEY_STELES_TOLD)?.toInt() ?: min(shop.bought, tale.parts.size)
        private set
    /** Where the next stone stands, in pixels walked, until its part is told; null when no stone stands ahead. */
    var nextStele = store.load(KEY_NEXT_STELE)?.takeIf { it >= 0 }
        private set
    /** The stones whose parts were told in this run of the app. */
    private val passed = mutableListOf<Stele>()
    /** The stones by the path: those whose parts were told, then the next one. */
    val steles: List<Stele> get() = passed + listOfNotNull(nextStele?.let { Stele(it, stelesTold) })

    /** The jokers take their own random numbers, so that the visions, the sights and the tales stay the same. */
    private val jokerRandom = Random(seed + 2)
    /** The next joker by the path. An id that the game does not have is Diogenes. */
    var joker = store.load(KEY_JOKER)?.let { JOKERS.getOrNull(it.toInt()) } ?: JOKERS.first()
        private set
    /** Where the next joker stands, in pixels walked, until he has talked; null when none stands ahead yet. */
    var nextJoker = store.load(KEY_NEXT_JOKER)?.takeIf { it >= 0 }
        private set
    /** How many times the player met each joker, by id, in all climbs and worlds. */
    private val met = IntArray(JOKERS.size) { id -> store.load(metKey(JOKERS[id]))?.toInt() ?: 0 }
    /** The cards seen face up on the Jokers tab, one bit for each id. */
    var jokersSeen = store.load(KEY_JOKERS_SEEN)?.toInt() ?: 0
        private set
    /** The joker met last in this climb and this run of the app, and where he stands: he stays by the path behind him. */
    var metJoker: Pair<Double, Joker>? = null
        private set
    /** The jokers that the dev screen brought, in the order of the cards. */
    private var devJokers = 0

    /** He stands on the top: the knob moves him no more. */
    val atTop get() = summit?.let { position >= it } == true

    private val quoteSeed = store.load(KEY_QUOTE_SEED)?.toInt() ?: seed
    var quotes = QuoteBox(quoteSeed, drawn = store.load(KEY_QUOTES_DRAWN)?.toInt() ?: 0, firstTime = effort == 0.0, news = ::nextNews)
        private set
    var onTick: () -> Unit = {}
    /** New obols came, while the game runs or for the time away: it tells how many came together. */
    var onObol: (Int) -> Unit = {}

    private val visionSeed = store.load(KEY_VISION_SEED)?.toInt() ?: seed
    /** The gods who come as visions, and ask. Only the player's own turns bring them. */
    val voices = Voices(
        visionSeed, store.load(KEY_VISIONS_DRAWN)?.toInt() ?: 0, store.load(KEY_NEXT_VISION),
        store.load(KEY_RIGHT_ANSWERS)?.toInt() ?: 0, random,
    )
    val vision: Vision? get() = voices.vision

    /** A screen covers the panel of the wise words: the news of a labor waits. */
    var covered = false
    /** The labors done so far: those done when the game started bring no news. */
    private val known = LABORS.filter { it.done(counts) }.toMutableSet()
    /** The news of labors done while playing, waiting for the panel. */
    private val news = ArrayDeque<Quote>()
    /** A vision came. */
    var onVision: () -> Unit = {}
    /** The touches of the knob when the answers showed, and of the touch whose turn picks an answer. */
    private var askedGrabs = 0
    private var answerGrabs = 0
    /** How far the newest touch turned the knob since the answers showed, in turns: plus is clockwise. */
    private var answerTurn = 0.0

    private var lastTick = floor(knob.angle / TICK_ANGLE).toInt()
    private var tickCooldown = 0.0
    private var sinceSave = 0.0
    private var dirty = false
    private var pausedAt: Double? = null

    init {
        store.load(KEY_SAVED_AT)?.let { away(wall() - it) }
        beginClimb()
        placeJoker()
    }

    val dayValue get() = START_TIME + effort / DAY_LENGTH
    val day get() = floor(dayValue).toInt() + 1

    /** The days pushed through in all climbs: the first day of a climb has none. */
    val days get() = daysBefore + day - 1
    val counts get() = Counts(climbs, turnsPushed, days, voices.rightAnswers)
    val laborsDone get() = LABORS.count { it.done(counts) }
    /** A labor is done that the player has not seen on the Labors tab. */
    val newLabors get() = laborsDone > laborsSeen

    fun seeLabors() {
        laborsSeen = laborsDone
        save()
    }

    /** How many times the player met [joker]. Once met, his card is face up. */
    fun met(joker: Joker) = met[joker.id]

    /** A joker is met whose card the player has not seen on the Jokers tab. */
    val newJokers get() = JOKERS.any { met[it.id] > 0 && jokersSeen and (1 shl it.id) == 0 }

    fun seeJokers() {
        jokersSeen = JOKERS.filter { met[it.id] > 0 }.fold(0) { bits, joker -> bits or (1 shl joker.id) }
        save()
    }
    val timeOfDay get() = dayValue - floor(dayValue)
    val weather get() = Weather.of(day)

    fun update(dt: Double) {
        clock += dt
        val turn = knob.update(dt)
        val held = vision?.let { it.phase != Vision.Phase.LEAVING } == true
        val turns = if (atTop || held) 0.0 else abs(turn) / (2 * PI)
        turnsPushed += turns
        moved = turns * min(shop.bonus, Config.SUN_CAP) * PX_PER_TURN
        summit?.let { if (position + moved >= it - 1e-6) moved = it - position }
        val from = position
        position += moved
        passHerm(from)
        passStele()
        placeStele()
        passJoker()
        placeJoker()
        effort += moved
        val helped = if (pausedAt == null) shop.helpersPerHour * dt / 3600 else 0.0
        effort += helped * PX_PER_TURN
        earn(turns * shop.bonus + helped)
        if (moved != 0.0) dirty = true
        if (dt > 0) speed += (moved / dt - speed) * (1 - exp(-dt * 8))
        stillFor = if (moved > 1e-4) 0.0 else stillFor + dt
        tick(dt)
        val drawn = quotes.drawn
        quotes.update(dt, moved, stillFor)
        if (quotes.drawn != drawn) dirty = true
        listen(turn, dt)
        checkLabors()
        sinceSave += dt
        if (dirty && sinceSave > SAVE_EVERY) save()
    }

    /**
     * The voices. With no vision, the player's own turns bring the next one, but not in the final climb.
     * A vision asks once it is fully in and its question is typed out. Then a new touch that turns the knob
     * [Voices.ANSWER_TURN] to one side picks the answer on that side.
     */
    private fun listen(turn: Double, dt: Double) {
        val v = vision
        if (v == null) {
            if (summit == null && !atTop) voices.pushed(abs(turn) / (2 * PI), place)?.let { come(it) }
            return
        }
        v.update(dt)
        when (v.phase) {
            Vision.Phase.COMING -> when {
                quotes.quote.text != v.question.text -> quotes.announce(Quote(v.question.text, v.god.title))
                v.light >= 1.0 && quotes.phase == QuoteBox.Phase.SHOWING -> {
                    v.ask()
                    askedGrabs = knob.grabs
                    answerGrabs = knob.grabs
                    answerTurn = 0.0
                }
            }
            Vision.Phase.ASKING -> {
                if (knob.grabs != answerGrabs) {
                    answerGrabs = knob.grabs
                    answerTurn = 0.0
                }
                if (knob.grabs > askedGrabs) answerTurn += turn / (2 * PI)
                when {
                    answerTurn >= Voices.ANSWER_TURN -> pick(v, left = false)
                    answerTurn <= -Voices.ANSWER_TURN -> pick(v, left = true)
                    v.asked >= Voices.ASK_TIME -> {
                        quotes.announce(Quote(v.god.farewell, v.god.title))
                        v.leave()
                    }
                }
            }
            Vision.Phase.LEAVING -> if (v.over) voices.end()
        }
    }

    /** Labors that became done bring their news, one after the other. */
    private fun checkLabors() {
        val now = counts
        for (labor in LABORS) if (labor !in known && labor.done(now)) {
            known += labor
            news.addLast(laborNews(labor))
        }
    }

    /** The next news, in place of a wise word, unless a screen covers the panel or a vision shows. */
    private fun nextNews(): Quote? = if (covered || vision != null) null else news.removeFirstOrNull()

    /** A vision comes: the knob stops, and the god asks in the panel of the wise words. */
    private fun come(v: Vision) {
        knob.stop()
        quotes.announce(Quote(v.question.text, v.god.title))
        onVision()
        save()
    }

    /** The player picked the answer on one side. A right answer brings [Config.VISION_OBOLS] obols. */
    private fun pick(v: Vision, left: Boolean) {
        val right = left == v.rightOnLeft
        if (right) {
            shop.add(Config.VISION_OBOLS.toDouble())
            onObol(Config.VISION_OBOLS)
        }
        voices.answer(right)
        quotes.announce(Quote(v.question.reply(right), v.god.title))
        save()
    }

    /** A vision that comes or asks ends without an answer, and its god says farewell. */
    fun endVision() {
        val v = vision ?: return
        if (v.phase != Vision.Phase.LEAVING) quotes.announce(Quote(v.god.farewell, v.god.title))
        voices.end()
    }

    /** For dev builds: a vision comes at once. */
    fun summonVision() {
        voices.end()
        come(voices.summon(place))
    }

    /** For dev builds: the next labor of the knob turns is done at the next click of the knob. */
    fun nearTheNextLabor() {
        val next = LABORS.firstOrNull { it.category == Category.TURNS && !it.done(counts) } ?: return
        turnsPushed = next.goal - 1.0 / TICKS_PER_TURN
        save()
    }

    /** For dev builds: a vision comes at the next click of the knob. */
    fun nearTheNextVision() {
        voices.soon(1.0 / TICKS_PER_TURN)
        save()
    }

    /**
     * Work fills the obols, but not during the final climb: there is nothing more to buy then.
     * New obols are saved soon, with the work of the obol after them.
     */
    private fun earn(work: Double) {
        if (summit != null) return
        val before = floor(shop.obols)
        shop.addWork(work)
        val gained = (floor(shop.obols) - before).toInt()
        if (gained == 0) return
        dirty = true
        onObol(gained)
    }

    /** As he passes a herm, Hermes says how far the top is. */
    private fun passHerm(from: Double) {
        val top = summit ?: return
        val herm = herms.firstOrNull { it > from && it <= position } ?: return
        val left = ((top - herm) / (Config.SUN_CAP * PX_PER_TURN)).roundToInt()
        quotes.announce(Quote("$left turns to the top.", "Hermes"))
    }

    private val part get() = tale.parts.getOrNull(stelesTold)

    /** How far he walked into the popup of the next stone, in pixels: it opens [Config.NEAR_TURNS] before the rock reaches the stone. Null before it opens, and at the top. */
    private val near: Double?
        get() = nextStele?.let { position - (it - Config.NEAR_TURNS * PX_PER_TURN) }?.takeIf { it >= 0 && !atTop }

    /** The text that the next stone tells in a popup over it while he is near it: the texts of its part one after the other, each for [Config.READ_TURNS]. */
    val telling: String? get() = near?.let { part?.texts?.getOrNull((it / READ).toInt()) }

    /**
     * The tale of a new climb: the tale of Sisyphus while it is not told, then a tale not told yet, at random.
     * When all are told, any tale but the one of the climb before.
     */
    private fun nextTale(): Tale {
        val untold = TALES.filter { talesTold and (1 shl it.id) == 0 }
        if (SISYPHUS in untold) return SISYPHUS
        val choices = untold.ifEmpty { TALES - tale }
        return choices[taleRandom.nextInt(choices.size)]
    }

    /** The tale starts again from its first part: no part told, and no stone ahead. */
    private fun restartTale() {
        stelesTold = 0
        nextStele = null
        passed.clear()
    }

    /** He walked through the popup of the next stone, also in a jump of the dev screen: its part is told, and the stone stays behind him. */
    private fun passStele() {
        val at = nextStele ?: return
        val texts = part?.texts ?: return
        if ((near ?: return) < texts.size * READ) return
        nextStele = null
        passed += Stele(at, stelesTold)
        stelesTold++
        if (stelesTold == tale.parts.size) talesTold = talesTold or (1 shl tale.id)
        dirty = true
    }

    /**
     * The parts of the tale due in this climb: none before [Config.TALE_DAY]. Then the first part, and one more for each item
     * bought, up to the last but one. The last part, the rock, comes with the final climb.
     */
    private val partsDue: Int
        get() {
            return when {
                summit != null -> tale.parts.size
                day < Config.TALE_DAY -> 0
                else -> min(1 + shop.bought, tale.parts.size - 1)
            }
        }

    /** The next stone stands ahead for a part that is due and not yet told, once the part before it is told. */
    private fun placeStele() {
        if (part == null || nextStele != null || atTop || stelesTold >= partsDue) return
        nextStele = position + Config.STELE_TURNS * PX_PER_TURN
        keepRoom()
        dirty = true
    }

    private val joke get() = joker.jokes[met[joker.id] % joker.jokes.size]

    /** How far he walked into the bubble of the next joker, in pixels: it opens as the popup of a stone does. Null before it opens, and at the top. */
    private val nearJoker: Double?
        get() = nextJoker?.let { position - (it - Config.NEAR_TURNS * PX_PER_TURN) }?.takeIf { it >= 0 && !atTop }

    /** The text that the next joker tells in a bubble over him while he is near: the texts of his joke one after the other, each for [Config.READ_TURNS]. */
    val jesting: String? get() = nearJoker?.let { joke.texts.getOrNull((it / READ).toInt()) }

    /** He walked through the bubble of the next joker, also in a jump of the dev screen: the joker is met once more, and stays behind him. */
    private fun passJoker() {
        val at = nextJoker ?: return
        if ((nearJoker ?: return) < joke.texts.size * READ) return
        met[joker.id]++
        metJoker = at to joker
        nextJoker = null
        dirty = true
    }

    /** With no joker ahead, the next one stands one to two days of walking ahead, at random, clear of the next stone. */
    private fun placeJoker() {
        if (nextJoker != null) return
        val turns = Config.JOKER_TURNS.let { it.start + jokerRandom.nextDouble() * (it.endInclusive - it.start) }
        joker = pickJoker(jokerRandom)
        nextJoker = position + turns * PX_PER_TURN
        keepRoom()
        dirty = true
    }

    /** A joker stands at least [Config.JOKER_ROOM] turns from the next stone: when he would stand nearer, he stands that far past it. */
    private fun keepRoom() {
        val stone = nextStele ?: return
        val at = nextJoker ?: return
        val room = Config.JOKER_ROOM * PX_PER_TURN
        if (abs(at - stone) < room) nextJoker = stone + room
    }

    private fun tick(dt: Double) {
        tickCooldown -= dt
        val tick = floor(knob.angle / TICK_ANGLE).toInt()
        if (tick == lastTick) return
        lastTick = tick
        if (tickCooldown <= 0) {
            onTick()
            tickCooldown = 0.028
        }
    }

    /** Pausing again keeps the first time: iOS also pauses on its way back to the foreground. A vision ends. */
    fun pause() {
        endVision()
        if (pausedAt == null) pausedAt = wall()
        save()
    }

    fun resume() {
        val at = pausedAt ?: return
        pausedAt = null
        away(wall() - at)
    }

    /** The helpers on their own kept pushing while nobody watched. Hermes brings the news. */
    private fun away(seconds: Double) {
        val helper = shop.helper ?: return
        if (seconds <= 0) return
        val turns = shop.helpersPerHour * seconds / 3600
        val before = floor(shop.obols)
        effort += turns * PX_PER_TURN
        earn(turns)
        dirty = true
        if (turns < 1) return
        val gained = (floor(shop.obols) - before).toInt()
        val author = when (gained) {
            0 -> "Hermes"
            1 -> "Hermes · 1 obol"
            else -> "Hermes · $gained obols"
        }
        val times = turns.toInt().let { if (it == 1) "once" else "$it times" }
        quotes.announce(Quote("${helper.name} pushed $times while you were away.", author))
    }

    fun buy(line: Line): Boolean {
        if (!shop.buy(line)) return false
        if (shop.bought == 1) quotes.announce(GOAL)
        beginClimb()
        placeStele()
        save()
        return true
    }

    /** Once everything is bought, the top comes within reach: [CLIMB] more pixels of pushing. */
    private fun beginClimb() {
        if (summit != null || !shop.allBought) return
        endVision()
        summit = position + CLIMB
        quotes.announce(Quote("The top is in sight. Push on.", "Hermes"))
        dirty = true
    }

    /** At the top the rock rolls away, and everything starts from the beginning. Only the climbs are counted on. */
    fun startOver() {
        climbs++
        begin()
    }

    /**
     * Another world means another climb: everything starts from the beginning there. A climb that has not begun, as right
     * after the top, takes its tale with it, so that the tale of the climb before does not come again.
     */
    fun moveTo(place: Place) {
        this.place = place
        begin(newTale = position > 0)
    }

    private fun begin(newTale: Boolean = true) {
        if (newTale) tale = nextTale()
        daysBefore += day - 1
        endVision()
        position = 0.0
        effort = 0.0
        summit = null
        shop = Shop(place = place, random = random)
        voices.newClimb()
        restartTale()
        nextJoker = null
        metJoker = null
        placeJoker()
        quotes = QuoteBox(quoteSeed, drawn = 0, firstTime = true, news = ::nextNews)
        save()
    }

    /** For dev builds: adds [step] obols, or takes them away, but never below zero. */
    fun addObols(step: Int) {
        shop.add(step.toDouble())
        save()
    }

    /** For dev builds: the shop starts over, and with it the final climb. */
    fun resetShop() {
        shop.reset()
        summit = null
        restartTale()
        save()
    }

    /** For dev builds: the climb starts again from day 1, with no items and no obols. */
    fun resetClimb() = begin()

    /** For dev builds: he pushes on until [CLICKS_TO_OBOL] clicks of the knob are left before the next obol. The sun moves with him. */
    fun nearTheNextObol() {
        val turns = (floor(shop.obols) + 1 - shop.obols) * shop.nextObol / shop.bonus - CLICKS_TO_OBOL.toDouble() / TICKS_PER_TURN
        if (turns <= 0) return
        var moved = turns * min(shop.bonus, Config.SUN_CAP) * PX_PER_TURN
        summit?.let { moved = min(moved, it - position) }
        position += moved
        effort += moved
        shop.addWork(turns * shop.bonus)
        save()
    }

    /** For dev builds: the next stone of the tale stands a turn into its popup, also before it is due, so that its part shows at once. */
    fun nearTheNextStone() {
        if (part == null) return
        nextStele = position + (Config.NEAR_TURNS - 1) * PX_PER_TURN
        keepRoom()
        save()
    }

    /**
     * For dev builds: the next joker in the order of the cards stands a turn into his bubble, so that he talks at once.
     * A stone too near him stands [Config.JOKER_ROOM] turns past him.
     */
    fun nearTheNextJoker() {
        joker = JOKERS[devJokers++ % JOKERS.size]
        val at = position + (Config.NEAR_TURNS - 1) * PX_PER_TURN
        nextJoker = at
        val room = Config.JOKER_ROOM * PX_PER_TURN
        nextStele?.let { if (abs(it - at) < room) nextStele = at + room }
        save()
    }

    /** For dev builds: everything bought, and the top two turns away. */
    fun nearTheTop() {
        shop.complete()
        beginClimb()
        summit?.let { position = it - 2 * Config.SUN_CAP * PX_PER_TURN }
        save()
    }

    fun toggleSound() {
        soundOn = !soundOn
        store.save(KEY_SOUND, if (soundOn) 1.0 else 0.0)
    }

    fun save() {
        store.save(KEY_EFFORT, effort)
        store.save(KEY_POSITION, position)
        store.save(KEY_KNOB, knob.angle)
        store.save(KEY_QUOTE_SEED, quoteSeed.toDouble())
        store.save(KEY_QUOTES_DRAWN, quotes.drawn.toDouble())
        store.save(KEY_OBOLS, shop.obols)
        store.save(KEY_NEXT_OBOL, shop.nextObol)
        store.save(KEY_SAVED_AT, pausedAt ?: wall())
        store.save(KEY_SUMMIT, summit ?: -1.0)
        store.save(KEY_CLIMBS, climbs.toDouble())
        store.save(KEY_PLACE, place.id.toDouble())
        store.save(KEY_NEXT_VISION, voices.count)
        store.save(KEY_VISION_SEED, visionSeed.toDouble())
        store.save(KEY_VISIONS_DRAWN, voices.drawn.toDouble())
        store.save(KEY_RIGHT_ANSWERS, voices.rightAnswers.toDouble())
        store.save(KEY_TURNS_PUSHED, turnsPushed)
        store.save(KEY_DAYS_BEFORE, daysBefore.toDouble())
        store.save(KEY_LABORS_SEEN, laborsSeen.toDouble())
        store.save(KEY_STELES_TOLD, stelesTold.toDouble())
        store.save(KEY_NEXT_STELE, nextStele ?: -1.0)
        store.save(KEY_TALE, tale.id.toDouble())
        store.save(KEY_TALES_TOLD, talesTold.toDouble())
        store.save(KEY_NEXT_JOKER, nextJoker ?: -1.0)
        store.save(KEY_JOKER, joker.id.toDouble())
        store.save(KEY_JOKERS_SEEN, jokersSeen.toDouble())
        for (joker in JOKERS) store.save(metKey(joker), met[joker.id].toDouble())
        for (line in Line.entries) store.save(shopKey(line), shop.tier(line).toDouble())
        dirty = false
        sinceSave = 0.0
    }
}
