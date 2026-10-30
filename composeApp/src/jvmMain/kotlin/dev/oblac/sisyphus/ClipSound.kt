package dev.oblac.sisyphus

import dev.oblac.sisyphus.audio.SAMPLE_RATE
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl
import kotlin.math.log10

/**
 * Plays the knob click, the click of every other tap, the sound of buying, the fanfare, the coin, the spell, the victory and the pop of a bubble on desktop.
 * The roll of the rock loops, and stops while the rock stands. Without an audio device it stays silent.
 */
internal class ClipSound : Sound {
    private var clips: List<Clip> = emptyList()
    private var uiClip: Clip? = null
    private var winClip: Clip? = null
    private var fanfareClip: Clip? = null
    private var coinClip: Clip? = null
    private var spellClip: Clip? = null
    private var victoryClip: Clip? = null
    private var popupClip: Clip? = null
    private var rollClip: Clip? = null
    private var next = 0

    override fun load(click: FloatArray, ui: FloatArray, win: FloatArray, fanfare: FloatArray, coin: FloatArray, spell: FloatArray, victory: FloatArray, popup: FloatArray, roll: FloatArray) {
        try {
            clips = List(6) { clip(click) }
            uiClip = clip(ui)
            winClip = clip(win)
            fanfareClip = clip(fanfare)
            coinClip = clip(coin)
            spellClip = clip(spell)
            victoryClip = clip(victory)
            popupClip = clip(popup)
            rollClip = clip(roll)
        } catch (e: Exception) {
            clips = emptyList()
            uiClip = null
            winClip = null
            fanfareClip = null
            coinClip = null
            spellClip = null
            victoryClip = null
            popupClip = null
            rollClip = null
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

    override fun fanfare() {
        fanfareClip?.restart()
    }

    override fun coin() {
        coinClip?.restart()
    }

    override fun spell() {
        spellClip?.restart()
    }

    override fun victory() {
        victoryClip?.restart()
    }

    override fun popup() {
        popupClip?.restart()
    }

    override fun roll(level: Float) {
        val clip = rollClip ?: return
        if (level == 0f) {
            if (clip.isRunning) clip.stop()
            return
        }
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            val gain = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
            gain.value = (20 * log10(level)).coerceIn(gain.minimum, 0f)
        }
        if (!clip.isRunning) clip.loop(Clip.LOOP_CONTINUOUSLY)
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
