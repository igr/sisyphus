package dev.oblac.sisyphus

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import dev.oblac.sisyphus.audio.CLICKS
import dev.oblac.sisyphus.audio.SAMPLE_RATE
import dev.oblac.sisyphus.audio.click

/** Plays the knob clicks: one short track per click variant, restarted at each detent. Without audio it stays silent. */
internal class TrackSound : Sound {
    private val tracks: List<AudioTrack> = try {
        List(CLICKS) { track(click(it)) }.filter { it.state == AudioTrack.STATE_INITIALIZED }
    } catch (e: Exception) {
        emptyList()
    }
    private var next = 0

    override fun click() {
        if (tracks.isEmpty()) return
        val track = tracks[next++ % tracks.size]
        track.stop()
        track.reloadStaticData()
        track.play()
    }

    fun release() = tracks.forEach { it.release() }

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
