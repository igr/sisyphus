package dev.oblac.sisyphus

import dev.oblac.sisyphus.audio.CLICKS
import dev.oblac.sisyphus.audio.SAMPLE_RATE
import dev.oblac.sisyphus.audio.click
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

/** Plays the knob clicks on desktop. Without an audio device it stays silent. */
internal class ClipSound : Sound {
    private val clips: List<Clip> = try {
        val format = AudioFormat(SAMPLE_RATE.toFloat(), 16, 1, true, false)
        List(CLICKS * 2) {
            val pcm = pcm16(click(it % CLICKS))
            AudioSystem.getClip().apply { open(format, pcm, 0, pcm.size) }
        }
    } catch (e: Exception) {
        emptyList()
    }
    private var next = 0

    override fun click() {
        if (clips.isEmpty()) return
        val clip = clips[next++ % clips.size]
        clip.stop()
        clip.framePosition = 0
        clip.start()
    }
}

internal fun pcm16(samples: FloatArray): ByteArray {
    val bytes = ByteArray(samples.size * 2)
    samples.forEachIndexed { i, s ->
        val v = (s.coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt()
        bytes[2 * i] = v.toByte()
        bytes[2 * i + 1] = (v shr 8).toByte()
    }
    return bytes
}
