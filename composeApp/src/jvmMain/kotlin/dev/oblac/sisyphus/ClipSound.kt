package dev.oblac.sisyphus

import dev.oblac.sisyphus.audio.SAMPLE_RATE
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

/** Plays the knob click on desktop. Without an audio device it stays silent. */
internal class ClipSound : Sound {
    private var clips: List<Clip> = emptyList()
    private var next = 0

    override fun load(click: FloatArray) {
        clips = try {
            val format = AudioFormat(SAMPLE_RATE.toFloat(), 16, 1, true, false)
            val pcm = pcm16(click)
            List(6) { AudioSystem.getClip().apply { open(format, pcm, 0, pcm.size) } }
        } catch (e: Exception) {
            emptyList()
        }
    }

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
