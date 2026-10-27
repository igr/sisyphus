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
import kotlin.random.Random

/** How far Sisyphus walks, in pixels, for one full turn of the knob. */
const val PX_PER_TURN = 12.0
const val TURNS_PER_DAY = 72.0
const val DAY_LENGTH = PX_PER_TURN * TURNS_PER_DAY

/** The first day starts just after sunrise. */
const val START_TIME = 0.27

/**
 * The final climb, in pixels walked: as much pushing as 7 days of 5 minutes, at one turn a second,
 * with everything bought, so at the fastest walk.
 */
const val CLIMB = 7 * 5 * 60 * SUN_CAP * PX_PER_TURN

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
private const val SAVE_EVERY = 1.0

/** How many clicks of the knob the dev screen leaves before the next obol. */
private const val CLICKS_TO_OBOL = 3

private fun shopKey(line: Line) = "shop." + line.id

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
    private var touch: Double? = null
    private var pending = 0.0

    fun grab(touchAngle: Double?) {
        held = true
        touch = touchAngle
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
}

/**
 * The state of the push. The knob moves Sisyphus up the hill, whichever way it turns.
 * Effort is what moves the sun: days pass only when he works. A strong rock and strong helpers
 * earn obols faster, but move the sun at most [SUN_CAP] times faster. Helpers on their own push
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
    var summit = store.load(KEY_SUMMIT)?.takeIf { it >= 0 }
        private set
    /** How many times Sisyphus reached the top. Starting over keeps it. */
    var climbs = store.load(KEY_CLIMBS)?.toInt() ?: 0
        private set
    /** How far the final climb has come: from 0 when it begins to 1 at the top; null before it begins. */
    val climbed: Double? get() = summit?.let { (1 - (it - position) / CLIMB).coerceIn(0.0, 1.0) }

    /** He stands on the top: the knob moves him no more. */
    val atTop get() = summit?.let { position >= it } == true

    private val quoteSeed = store.load(KEY_QUOTE_SEED)?.toInt() ?: seed
    var quotes = QuoteBox(quoteSeed, drawn = store.load(KEY_QUOTES_DRAWN)?.toInt() ?: 0, firstTime = effort == 0.0)
        private set
    var onTick: () -> Unit = {}
    /** New obols came, while the game runs or for the time away: it tells how many came together. */
    var onObol: (Int) -> Unit = {}

    private var lastTick = floor(knob.angle / TICK_ANGLE).toInt()
    private var tickCooldown = 0.0
    private var sinceSave = 0.0
    private var dirty = false
    private var pausedAt: Double? = null

    init {
        store.load(KEY_SAVED_AT)?.let { away(wall() - it) }
        beginClimb()
    }

    val dayValue get() = START_TIME + effort / DAY_LENGTH
    val day get() = floor(dayValue).toInt() + 1
    val timeOfDay get() = dayValue - floor(dayValue)
    val weather get() = Weather.of(day)

    fun update(dt: Double) {
        clock += dt
        val turn = knob.update(dt)
        val turns = if (atTop) 0.0 else abs(turn) / (2 * PI)
        moved = turns * min(shop.bonus, SUN_CAP) * PX_PER_TURN
        summit?.let { if (position + moved >= it - 1e-6) moved = it - position }
        position += moved
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
        sinceSave += dt
        if (dirty && sinceSave > SAVE_EVERY) save()
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

    /** Pausing again keeps the first time: iOS also pauses on its way back to the foreground. */
    fun pause() {
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
        save()
        return true
    }

    /** Once everything is bought, the top comes within reach: [CLIMB] more pixels of pushing. */
    private fun beginClimb() {
        if (summit != null || !shop.allBought) return
        summit = position + CLIMB
        quotes.announce(Quote("The top is in sight: seven more days of pushing.", "Hermes"))
        dirty = true
    }

    /** At the top the rock rolls away, and everything starts from the beginning. Only the climbs are counted on. */
    fun startOver() {
        climbs++
        begin()
    }

    /** Another world means another climb: everything starts from the beginning there. */
    fun moveTo(place: Place) {
        this.place = place
        begin()
    }

    private fun begin() {
        position = 0.0
        effort = 0.0
        summit = null
        shop = Shop(place = place, random = random)
        quotes = QuoteBox(quoteSeed, drawn = 0, firstTime = true)
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
        save()
    }

    /** For dev builds: the climb starts again from day 1, with no items and no obols. */
    fun resetClimb() = begin()

    /** For dev builds: he pushes on until [CLICKS_TO_OBOL] clicks of the knob are left before the next obol. The sun moves with him. */
    fun nearTheNextObol() {
        val turns = (floor(shop.obols) + 1 - shop.obols) * shop.nextObol / shop.bonus - CLICKS_TO_OBOL.toDouble() / TICKS_PER_TURN
        if (turns <= 0) return
        var moved = turns * min(shop.bonus, SUN_CAP) * PX_PER_TURN
        summit?.let { moved = min(moved, it - position) }
        position += moved
        effort += moved
        shop.addWork(turns * shop.bonus)
        save()
    }

    /** For dev builds: everything bought, and the top two turns away. */
    fun nearTheTop() {
        shop.complete()
        beginClimb()
        summit?.let { position = it - 2 * SUN_CAP * PX_PER_TURN }
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
        for (line in Line.entries) store.save(shopKey(line), shop.tier(line).toDouble())
        dirty = false
        sinceSave = 0.0
    }
}
