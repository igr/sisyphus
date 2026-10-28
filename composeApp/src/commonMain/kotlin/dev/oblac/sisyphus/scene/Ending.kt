package dev.oblac.sisyphus.scene

/**
 * The top. He holds the rock a moment, then it rolls back past him and down the hill until it is gone:
 * he stands up, and turns around to watch it go.
 * The screen goes black for Camus' words. After a while a tap opens the Worlds screen,
 * and when it closes, the first morning comes back, in the world the player chose.
 */
class Ending {
    var time = 0.0
        private set
    private var wokeAt: Double? = null
    /** The Worlds screen is open over the black: the first morning waits for the player's choice. */
    var choosing = false
        private set

    fun update(dt: Double) {
        time += dt
    }

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
        const val ROLL_AT = 1.5
        const val GONE_AT = 5.0
        const val FADE = 1.5
        const val BLACK_AT = GONE_AT + FADE
        const val READ = 3.0
        const val ROLL_SPEEDUP = 30.0
    }
}
