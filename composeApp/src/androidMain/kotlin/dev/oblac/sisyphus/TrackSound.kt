package dev.oblac.sisyphus

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import dev.oblac.sisyphus.audio.SAMPLE_RATE

/**
 * Plays the knob click: three short tracks take turns, each restarted at a detent.
 * The click of every other tap, the sound of buying, the fanfare, the coin, the spell and the victory have a track each. Without audio it stays silent.
 */
internal class TrackSound : Sound {
    private var tracks: List<AudioTrack> = emptyList()
    private var uiTrack: AudioTrack? = null
    private var winTrack: AudioTrack? = null
    private var fanfareTrack: AudioTrack? = null
    private var coinTrack: AudioTrack? = null
    private var spellTrack: AudioTrack? = null
    private var victoryTrack: AudioTrack? = null
    private var next = 0

    override fun load(click: FloatArray, ui: FloatArray, win: FloatArray, fanfare: FloatArray, coin: FloatArray, spell: FloatArray, victory: FloatArray) {
        try {
            tracks = List(3) { track(click) }.filter { it.state == AudioTrack.STATE_INITIALIZED }
            uiTrack = track(ui).takeIf { it.state == AudioTrack.STATE_INITIALIZED }
            winTrack = track(win).takeIf { it.state == AudioTrack.STATE_INITIALIZED }
            fanfareTrack = track(fanfare).takeIf { it.state == AudioTrack.STATE_INITIALIZED }
            coinTrack = track(coin).takeIf { it.state == AudioTrack.STATE_INITIALIZED }
            spellTrack = track(spell).takeIf { it.state == AudioTrack.STATE_INITIALIZED }
            victoryTrack = track(victory).takeIf { it.state == AudioTrack.STATE_INITIALIZED }
        } catch (e: Exception) {
            tracks = emptyList()
            uiTrack = null
            winTrack = null
            fanfareTrack = null
            coinTrack = null
            spellTrack = null
            victoryTrack = null
        }
    }

    override fun click() {
        if (tracks.isEmpty()) return
        tracks[next++ % tracks.size].restart()
    }

    override fun ui() {
        uiTrack?.restart()
    }

    override fun win() {
        winTrack?.restart()
    }

    override fun fanfare() {
        fanfareTrack?.restart()
    }

    override fun coin() {
        coinTrack?.restart()
    }

    override fun spell() {
        spellTrack?.restart()
    }

    override fun victory() {
        victoryTrack?.restart()
    }

    fun release() {
        tracks.forEach { it.release() }
        uiTrack?.release()
        winTrack?.release()
        fanfareTrack?.release()
        coinTrack?.release()
        spellTrack?.release()
        victoryTrack?.release()
    }

    private fun AudioTrack.restart() {
        stop()
        reloadStaticData()
        play()
    }

    private fun track(samples: FloatArray): AudioTrack {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val format = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
            .setSampleRate(SAMPLE_RATE)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        return AudioTrack.Builder()
            .setAudioAttributes(attributes)
            .setAudioFormat(format)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .setBufferSizeInBytes(samples.size * 4)
            .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
            .build()
            .apply { write(samples, 0, samples.size, AudioTrack.WRITE_BLOCKING) }
    }
}
