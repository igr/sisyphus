package dev.oblac.sisyphus

import dev.oblac.sisyphus.audio.SAMPLE_RATE
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
 * Plays the knob click. The ambient session mixes with other apps' music
 * and stays quiet when the phone is on silent.
 */
@OptIn(ExperimentalForeignApi::class)
internal class EngineSound : Sound {
    private val engine = AVAudioEngine()
    private val format = AVAudioFormat(standardFormatWithSampleRate = SAMPLE_RATE.toDouble(), channels = 1u)
    private val players = List(3) { AVAudioPlayerNode() }
    private var buffer: AVAudioPCMBuffer? = null
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

    override fun load(click: FloatArray) {
        buffer = AVAudioPCMBuffer(pCMFormat = format, frameCapacity = click.size.toUInt()).apply {
            frameLength = click.size.toUInt()
            val channel = floatChannelData!![0]!!
            click.forEachIndexed { i, s -> channel[i] = s }
        }
    }

    override fun prepare() {
        start()
    }

    override fun click() {
        val buffer = buffer ?: return
        if (!start()) return
        val player = players[next++ % players.size]
        player.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    override fun pause() {
        if (engine.running) engine.pause()
    }

    private fun start(): Boolean {
        if (!engine.running && !engine.startAndReturnError(null)) return false
        for (player in players) if (!player.playing) player.play()
        return true
    }
}
