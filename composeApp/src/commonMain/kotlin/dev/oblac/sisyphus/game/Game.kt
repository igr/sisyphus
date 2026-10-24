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

private const val TICKS_PER_TURN = 24
private const val TICK_ANGLE = 2 * PI / TICKS_PER_TURN
private const val KEY_EFFORT = "effort"
private const val KEY_POSITION = "position"
private const val KEY_SOUND = "sound"
private const val KEY_KNOB = "knob"
private const val KEY_QUOTE_SEED = "quoteSeed"
private const val KEY_QUOTES_DRAWN = "quotesDrawn"
private const val KEY_OBOLS = "obols"
private const val KEY_SAVED_AT = "savedAt"
private const val SAVE_EVERY = 1.0

private fun shopKey(line: Line) = "shop." + line.name.lowercase()

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

    val shop = Shop(Line.entries.associateWith { store.load(shopKey(it))?.toInt() ?: 0 }, store.load(KEY_OBOLS) ?: 0.0)

    private val quoteSeed = store.load(KEY_QUOTE_SEED)?.toInt() ?: seed
    val quotes = QuoteBox(quoteSeed, drawn = store.load(KEY_QUOTES_DRAWN)?.toInt() ?: 0, firstTime = effort == 0.0)
    var onTick: () -> Unit = {}

    private var lastTick = floor(knob.angle / TICK_ANGLE).toInt()
    private var tickCooldown = 0.0
    private var sinceSave = 0.0
    private var dirty = false
    private var pausedAt: Double? = null

    init {
        store.load(KEY_SAVED_AT)?.let { away(wall() - it) }
    }

    val dayValue get() = START_TIME + effort / DAY_LENGTH
    val day get() = floor(dayValue).toInt() + 1
    val timeOfDay get() = dayValue - floor(dayValue)
    val weather get() = Weather.of(day)

    fun update(dt: Double) {
        clock += dt
        val turn = knob.update(dt)
        val turns = abs(turn) / (2 * PI)
        moved = turns * min(shop.bonus, SUN_CAP) * PX_PER_TURN
        position += moved
        effort += moved
        shop.addWork(turns * shop.bonus)
        val helped = if (pausedAt == null) shop.helpersPerHour * dt / 3600 else 0.0
        effort += helped * PX_PER_TURN
        shop.addWork(helped)
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
        shop.addWork(turns)
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
        save()
        return true
    }

    /** For dev builds: adds [step] obols, or takes them away, but never below zero. */
    fun addObols(step: Int) {
        shop.add(step.toDouble())
        save()
    }

    /** For dev builds: the shop starts over. */
    fun resetShop() {
        shop.reset()
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
        store.save(KEY_SAVED_AT, pausedAt ?: wall())
        for (line in Line.entries) store.save(shopKey(line), shop.tier(line).toDouble())
        dirty = false
        sinceSave = 0.0
    }
}
