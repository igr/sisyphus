package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.game.God
import kotlin.math.min

/**
 * The top. Two of the three gods of the world, [gods], come and glow over him while he holds the rock, and gold sparkles
 * twinkle around him. When the gods are gone, the rock rolls back past him and down the hill until it is gone:
 * he stands up, and turns around to watch it go.
 * The screen goes black for Camus' words. After a while a tap opens the Worlds screen,
 * and when it closes, the first morning comes back, in the world the player chose.
 */
class Ending(val gods: List<God>) {
    var time = 0.0
        private set
    private var wokeAt: Double? = null
    /** The Worlds screen is open over the black: the first morning waits for the player's choice. */
    var choosing = false
        private set

    fun update(dt: Double) {
        time += dt
    }

    /** How bright the gods glow over the top, from 0 to 1: they come as he gets there, and are gone just before the rock rolls. */
    val godLight: Float
        get() = min(time / GODS_IN, (ROLL_AT - time) / GODS_OUT).coerceIn(0.0, 1.0).toFloat()

    /** Seconds since the gold sparkles began around him, while they twinkle under the gods; null before and after. */
    val sparkles: Double?
        get() = (time - GODS_IN).takeIf { it >= 0 && time < ROLL_AT - GODS_OUT }

    /** How far the rock has rolled back, in pixels along the ground, faster and faster; null once the screen is black. */
    val rolled: Double?
        get() = if (time >= BLACK_AT) null else (time - ROLL_AT).coerceAtLeast(0.0).let { ROLL_SPEEDUP * it * it / 2 }

    /** How dark the screen is: 0 shows the scene, 1 is black. */
    val darkness: Float
        get() = (wokeAt?.let { 1 - (time - it) / FADE } ?: ((time - GONE_AT) / FADE)).coerceIn(0.0, 1.0).toFloat()

    /** How much of Camus' words shows, from 0 to 1, while the screen is black. */
    val words: Float
        get() = if (wokeAt != null) 0f else (time - BLACK_AT).coerceIn(0.0, 1.0).toFloat()

    /** The words have had time to be read: a tap opens the Worlds screen. */
    val waiting get() = !choosing && time >= BLACK_AT + READ

    fun choose() {
        if (waiting) choosing = true
    }

    /** The Worlds screen closed: the first morning comes back. */
    fun wake() {
        if (choosing && wokeAt == null) wokeAt = time
    }

    /** The first morning is back in full. */
    val over get() = wokeAt?.let { time >= it + FADE } == true

    companion object {
        /** The gods come in this many seconds, and go in [GODS_OUT] seconds, just before the rock rolls. */
        const val GODS_IN = 0.8
        const val GODS_OUT = 0.7
        const val ROLL_AT = 4.5
        const val GONE_AT = ROLL_AT + 3.5
        const val FADE = 1.5
        const val BLACK_AT = GONE_AT + FADE
        const val READ = 3.0
        const val ROLL_SPEEDUP = 30.0
    }
}
