package dev.oblac.sisyphus

import dev.oblac.sisyphus.audio.SAMPLE_RATE
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

/** Plays the knob click, the click of every other tap and the sound of buying on desktop. Without an audio device it stays silent. */
internal class ClipSound : Sound {
    private var clips: List<Clip> = emptyList()
    private var uiClip: Clip? = null
    private var winClip: Clip? = null
    private var next = 0

    override fun load(click: FloatArray, ui: FloatArray, win: FloatArray) {
        try {
            clips = List(6) { clip(click) }
            uiClip = clip(ui)
            winClip = clip(win)
        } catch (e: Exception) {
            clips = emptyList()
            uiClip = null
            winClip = null
        }
    }

    override fun click() {
        if (clips.isEmpty()) return
        clips[next++ % clips.size].restart()
    }

    override fun ui() {
        uiClip?.restart()
    }

    override fun win() {
        winClip?.restart()
    }

    private fun clip(samples: FloatArray): Clip {
        val pcm = pcm16(samples)
        return AudioSystem.getClip().apply { open(AudioFormat(SAMPLE_RATE.toFloat(), 16, 1, true, false), pcm, 0, pcm.size) }
    }

    private fun Clip.restart() {
        stop()
        framePosition = 0
        start()
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
