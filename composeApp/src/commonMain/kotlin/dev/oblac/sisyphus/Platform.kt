package dev.oblac.sisyphus

import androidx.compose.ui.graphics.ImageBitmap
import dev.oblac.sisyphus.gfx.Pixels
import kotlin.time.Clock

interface Store {
    fun load(key: String): Double?
    fun save(key: String, value: Double)
}

class MemoryStore(values: Map<String, Double> = emptyMap()) : Store {
    private val values = values.toMutableMap()
    override fun load(key: String) = values[key]
    override fun save(key: String, value: Double) {
        values[key] = value
    }
}

interface Haptics {
    fun prepare() {}
    fun tick()
}

object NoHaptics : Haptics {
    override fun tick() {}
}

interface Sound {
    /** Takes the knob click, the click of every other tap and the sound of buying, as mono samples at 44.1 kHz. */
    fun load(click: FloatArray, ui: FloatArray, win: FloatArray) {}
    /** Called when a finger touches the knob, before the first click. */
    fun prepare() {}
    /** One detent of the knob. */
    fun click()
    /** A tap anywhere but the knob: menus, buttons, the help. */
    fun ui() {}
    /** Plays when something is bought in the Agora. */
    fun win() {}
    /** Releases the audio hardware until the next click. */
    fun pause() {}
}

/** Each detent of the knob clicks, unless the sound is off. */
fun Sound.tick(on: Boolean) {
    if (on) click()
}

/**
 * The sound of a [touch]: the knob gets ready to click, buying wins, and every other tap clicks.
 * Nothing plays while the sound is off; turning it off releases the audio hardware.
 */
fun Sound.play(touch: Touch, on: Boolean) {
    if (!on) {
        if (touch == Touch.SOUND) pause()
        return
    }
    when (touch) {
        Touch.NONE -> {}
        Touch.KNOB -> prepare()
        Touch.BUY -> win()
        Touch.SOUND, Touch.SHOP, Touch.DEV, Touch.HELP -> ui()
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
