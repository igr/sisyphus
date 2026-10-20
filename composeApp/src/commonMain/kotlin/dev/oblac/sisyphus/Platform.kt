package dev.oblac.sisyphus

import androidx.compose.ui.graphics.ImageBitmap
import dev.oblac.sisyphus.gfx.Pixels

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
    /** Called when a finger touches the knob, before the first click. */
    fun prepare() {}
    fun click()
    /** Releases the audio hardware until the next click. */
    fun pause() {}
}

object Silence : Sound {
    override fun click() {}
}

/** Turns the framebuffer into an image that Compose can draw. */
fun interface FrameUploader {
    fun upload(pixels: Pixels): ImageBitmap
}

expect fun frameUploader(): FrameUploader
