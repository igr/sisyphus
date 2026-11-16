package dev.oblac.sisyphus

import androidx.compose.ui.graphics.ImageBitmap
import dev.oblac.sisyphus.gfx.Pixels
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.time.Clock

interface Haptics {
    fun prepare() {}
    fun tick()
}

object NoHaptics : Haptics {
    override fun tick() {}
}

interface Sound {
    /**
     * Takes the knob click, the click of every other tap, the sound of buying, the fanfare, the coin, the spell of a vision,
     * the victory at the top, the pop of a bubble and the loop of the rolling rock, as mono samples at 44.1 kHz.
     */
    fun load(click: FloatArray, ui: FloatArray, win: FloatArray, fanfare: FloatArray, coin: FloatArray, spell: FloatArray, victory: FloatArray, popup: FloatArray, roll: FloatArray) {}
    /** Called when a finger touches the knob, before the first click. */
    fun prepare() {}
    /** One detent of the knob. */
    fun click()
    /** A tap anywhere but the knob: menus, buttons, the help. */
    fun ui() {}
    /** Plays when something is bought in the Agora. */
    fun win() {}
    /** Plays when the player arrives in another world. */
    fun fanfare() {}
    /** Plays when a new obol comes. */
    fun coin() {}
    /** Plays when a god comes as a vision. */
    fun spell() {}
    /** Plays when he reaches the top. */
    fun victory() {}
    /** Plays when a bubble opens, over a stone or a joker. */
    fun popup() {}
    /** The rock rolls in a loop as loud as [level]: 0 while it stands still, 1 at a brisk push. */
    fun roll(level: Float) {}
    /** Stops every sound, a long one too, and releases the audio hardware until the next click. */
    fun pause() {}
}

/** Each detent of the knob clicks, unless the sound is off. */
fun Sound.tick(on: Boolean) {
    if (on) click()
}

private const val BRISK_TURNS = 1.5

/** The rock rolls as fast as he pushes it, [turns] a second, and loudest at [BRISK_TURNS] a second. It is silent while the sound is off. */
fun Sound.rock(turns: Double, on: Boolean) {
    roll(if (on) sqrt(min(1.0, turns / BRISK_TURNS)).toFloat() else 0f)
}

/** A new obol plays the coin, unless the sound is off. */
fun Sound.obol(on: Boolean) {
    if (on) coin()
}

/** A vision plays the spell, unless the sound is off. */
fun Sound.vision(on: Boolean) {
    if (on) spell()
}

/** A bubble that opens plays the pop, unless the sound is off. */
fun Sound.bubble(on: Boolean) {
    if (on) popup()
}

/** The top plays the victory, unless the sound is off. */
fun Sound.top(on: Boolean) {
    if (on) victory()
}

/**
 * The sound of a [touch]: the knob gets ready to click, buying wins, a new world has its fanfare, and every other tap clicks.
 * Nothing plays while the sound is off; turning it off releases the audio hardware.
 */
fun Sound.play(touch: Touch, on: Boolean) {
    if (!on) {
        if (touch == Touch.SOUND) pause()
        return
    }
    when (touch) {
        Touch.NONE, Touch.LIST -> {}
        Touch.KNOB -> prepare()
        Touch.BUY -> win()
        Touch.MOVE -> fanfare()
        Touch.SOUND, Touch.SHOP, Touch.DEV, Touch.HELP, Touch.AGAIN, Touch.WORLDS, Touch.SETTINGS, Touch.PRIVACY -> ui()
    }
}

object Silence : Sound {
    override fun click() {}
}

/** Turns the framebuffer into an image that Compose can draw. */
fun interface FrameUploader {
    fun upload(pixels: Pixels): ImageBitmap
}

expect fun frameUploader(): FrameUploader

/** Seconds since 1970, from the device clock. */
fun wallSeconds(): Double = Clock.System.now().toEpochMilliseconds() / 1000.0
