package dev.oblac.sisyphus

import dev.oblac.sisyphus.audio.CLICKS
import dev.oblac.sisyphus.audio.SAMPLE_RATE
import dev.oblac.sisyphus.audio.click
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.set
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioFormat
import platform.AVFAudio.AVAudioPCMBuffer
import platform.AVFAudio.AVAudioPlayerNode
import platform.AVFAudio.AVAudioPlayerNodeBufferInterrupts
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryAmbient
import platform.AVFAudio.setPreferredIOBufferDuration

/**
 * Plays the knob clicks. The ambient session mixes with other apps' music
 * and stays quiet when the phone is on silent.
 */
@OptIn(ExperimentalForeignApi::class)
internal class EngineSound : Sound {
    private val engine = AVAudioEngine()
    private val format = AVAudioFormat(standardFormatWithSampleRate = SAMPLE_RATE.toDouble(), channels = 1u)
    private val players = List(3) { AVAudioPlayerNode() }
    private val buffers = List(CLICKS) { buffer(click(it)) }
    private var next = 0

    init {
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryAmbient, error = null)
        session.setPreferredIOBufferDuration(0.008, error = null)
        for (player in players) {
            engine.attachNode(player)
            engine.connect(player, to = engine.mainMixerNode, format = format)
        }
    }

    override fun prepare() {
        start()
    }

    override fun click() {
        if (!start()) return
        val player = players[next % players.size]
        player.scheduleBuffer(buffers[next % buffers.size], atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
        next++
    }

    override fun pause() {
        if (engine.running) engine.pause()
    }

    private fun start(): Boolean {
        if (!engine.running && !engine.startAndReturnError(null)) return false
        for (player in players) if (!player.playing) player.play()
        return true
    }

    private fun buffer(samples: FloatArray): AVAudioPCMBuffer {
        val buffer = AVAudioPCMBuffer(pCMFormat = format, frameCapacity = samples.size.toUInt())
        buffer.frameLength = samples.size.toUInt()
        val channel = buffer.floatChannelData!![0]!!
        samples.forEachIndexed { i, s -> channel[i] = s }
        return buffer
    }
}
