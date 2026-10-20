package dev.oblac.sisyphus.game

import dev.oblac.sisyphus.MemoryStore
import dev.oblac.sisyphus.Store
import dev.oblac.sisyphus.gfx.hash01
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.max
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

fun wrapAngle(a: Double): Double {
    var x = a
    while (x > PI) x -= 2 * PI
    while (x < -PI) x += 2 * PI
    return x
}

/** A heavy knob: it follows the finger, and coasts for a short while after a flick. */
class Knob {
    var angle = 0.0
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
 * The state of the push. The knob moves Sisyphus. Only forward pushing counts as effort,
 * and effort is what moves the sun: days pass only when he works.
 */
class Game(private val store: Store = MemoryStore(), seed: Int = Random.nextInt()) {
    val knob = Knob()

    var position = store.load(KEY_POSITION) ?: 0.0
        private set
    var effort = store.load(KEY_EFFORT) ?: 0.0
        private set

    /** Pixels walked during the last update. Negative when walking back down. */
    var moved = 0.0
        private set
    /** Smoothed walking speed, in pixels per second. */
    var speed = 0.0
        private set
    var stillFor = 10.0
        private set
    var clock = 0.0
        private set

    val quotes = QuoteBox(Random(seed), firstTime = effort == 0.0)
    var onTick: () -> Unit = {}

    private var lastTick = 0
    private var tickCooldown = 0.0
    private var sinceSave = 0.0
    private var dirty = false

    val dayValue get() = START_TIME + effort / DAY_LENGTH
    val day get() = floor(dayValue).toInt() + 1
    val timeOfDay get() = dayValue - floor(dayValue)
    val weather get() = Weather.of(day)

    fun update(dt: Double) {
        clock += dt
        val turn = knob.update(dt)
        moved = turn / (2 * PI) * PX_PER_TURN
        position += moved
        if (moved > 0) effort += moved
        if (moved != 0.0) dirty = true
        if (dt > 0) speed += (moved / dt - speed) * (1 - exp(-dt * 8))
        stillFor = if (abs(moved) > 1e-4) 0.0 else stillFor + dt
        tick(dt)
        quotes.update(dt, max(0.0, moved), stillFor)
        sinceSave += dt
        if (dirty && sinceSave > 3) save()
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

    fun save() {
        store.save(KEY_EFFORT, effort)
        store.save(KEY_POSITION, position)
        dirty = false
        sinceSave = 0.0
    }
}
