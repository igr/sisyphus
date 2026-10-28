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
 * Plays the knob click, the click of every other tap, the sound of buying, the fanfare, the coin and the spell. The ambient session mixes with other apps' music
 * and stays quiet when the phone is on silent.
 */
@OptIn(ExperimentalForeignApi::class)
internal class EngineSound : Sound {
    private val engine = AVAudioEngine()
    private val format = AVAudioFormat(standardFormatWithSampleRate = SAMPLE_RATE.toDouble(), channels = 1u)
    private val players = List(3) { AVAudioPlayerNode() }
    private val uiPlayer = AVAudioPlayerNode()
    private val winPlayer = AVAudioPlayerNode()
    private val fanfarePlayer = AVAudioPlayerNode()
    private val coinPlayer = AVAudioPlayerNode()
    private val spellPlayer = AVAudioPlayerNode()
    private val nodes = players + uiPlayer + winPlayer + fanfarePlayer + coinPlayer + spellPlayer
    private var clickBuffer: AVAudioPCMBuffer? = null
    private var uiBuffer: AVAudioPCMBuffer? = null
    private var winBuffer: AVAudioPCMBuffer? = null
    private var fanfareBuffer: AVAudioPCMBuffer? = null
    private var coinBuffer: AVAudioPCMBuffer? = null
    private var spellBuffer: AVAudioPCMBuffer? = null
    private var next = 0

    init {
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryAmbient, error = null)
        session.setPreferredIOBufferDuration(0.008, error = null)
        for (player in nodes) {
            engine.attachNode(player)
            engine.connect(player, to = engine.mainMixerNode, format = format)
        }
    }

    override fun load(click: FloatArray, ui: FloatArray, win: FloatArray, fanfare: FloatArray, coin: FloatArray, spell: FloatArray) {
        clickBuffer = buffer(click)
        uiBuffer = buffer(ui)
        winBuffer = buffer(win)
        fanfareBuffer = buffer(fanfare)
        coinBuffer = buffer(coin)
        spellBuffer = buffer(spell)
    }

    override fun prepare() {
        start()
    }

    override fun click() {
        val buffer = clickBuffer ?: return
        if (!start()) return
        val player = players[next++ % players.size]
        player.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    override fun ui() {
        val buffer = uiBuffer ?: return
        if (!start()) return
        uiPlayer.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    override fun win() {
        val buffer = winBuffer ?: return
        if (!start()) return
        winPlayer.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    override fun fanfare() {
        val buffer = fanfareBuffer ?: return
        if (!start()) return
        fanfarePlayer.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    override fun coin() {
        val buffer = coinBuffer ?: return
        if (!start()) return
        coinPlayer.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    override fun spell() {
        val buffer = spellBuffer ?: return
        if (!start()) return
        spellPlayer.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    override fun pause() {
        if (engine.running) engine.pause()
    }

    private fun start(): Boolean {
        if (!engine.running && !engine.startAndReturnError(null)) return false
        for (player in nodes) if (!player.playing) player.play()
        return true
    }

    private fun buffer(samples: FloatArray) = AVAudioPCMBuffer(pCMFormat = format, frameCapacity = samples.size.toUInt()).apply {
        frameLength = samples.size.toUInt()
        val channel = floatChannelData!![0]!!
        samples.forEachIndexed { i, s -> channel[i] = s }
    }
}
