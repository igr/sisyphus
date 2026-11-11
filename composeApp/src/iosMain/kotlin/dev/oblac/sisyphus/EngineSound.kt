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
import platform.AVFAudio.AVAudioPlayerNodeBufferLoops
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryAmbient
import platform.AVFAudio.setPreferredIOBufferDuration
import platform.darwin.dispatch_async
import platform.darwin.dispatch_queue_create

/**
 * Plays the knob click, the click of every other tap, the sound of buying, the fanfare, the coin, the spell, the victory and the pop of a bubble. The ambient session mixes with other apps' music
 * and stays quiet when the phone is on silent. The roll of the rock loops from its first push, and its volume follows the push.
 * All work with the audio is on a queue of its own, never on the main thread: the session, the engine and its players, the buffers, and each sound.
 * Over Bluetooth, such as AirPods, a start of the engine can wait for the audio hardware, and the knob and the frames must go on.
 */
internal class EngineSound : Sound {
    private val queue = dispatch_queue_create("dev.oblac.sisyphus.sound", null)
    private lateinit var engine: Engine

    init {
        dispatch_async(queue) { engine = Engine() }
    }

    override fun load(click: FloatArray, ui: FloatArray, win: FloatArray, fanfare: FloatArray, coin: FloatArray, spell: FloatArray, victory: FloatArray, popup: FloatArray, roll: FloatArray) =
        on { it.load(click, ui, win, fanfare, coin, spell, victory, popup, roll) }

    override fun prepare() = on { it.start() }

    override fun click() = on { it.click() }

    override fun ui() = on { it.ui() }

    override fun win() = on { it.win() }

    override fun fanfare() = on { it.fanfare() }

    override fun coin() = on { it.coin() }

    override fun spell() = on { it.spell() }

    override fun victory() = on { it.victory() }

    override fun popup() = on { it.popup() }

    override fun roll(level: Float) = on { it.roll(level) }

    override fun pause() = on { it.pause() }

    /** Does the [work] on the [queue]: so always after the engine is ready. */
    private fun on(work: (Engine) -> Unit) = dispatch_async(queue) { work(engine) }
}

/** The engine with its players and buffers. Only the queue of [EngineSound] uses it. The category of the session comes first, before the engine takes the audio hardware. */
@OptIn(ExperimentalForeignApi::class)
private class Engine {
    init {
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryAmbient, error = null)
        session.setPreferredIOBufferDuration(0.008, error = null)
    }

    private val engine = AVAudioEngine()
    private val format = AVAudioFormat(standardFormatWithSampleRate = SAMPLE_RATE.toDouble(), channels = 1u)
    private val players = List(3) { AVAudioPlayerNode() }
    private val uiPlayer = AVAudioPlayerNode()
    private val winPlayer = AVAudioPlayerNode()
    private val fanfarePlayer = AVAudioPlayerNode()
    private val coinPlayer = AVAudioPlayerNode()
    private val spellPlayer = AVAudioPlayerNode()
    private val victoryPlayer = AVAudioPlayerNode()
    private val popupPlayer = AVAudioPlayerNode()
    private val rollPlayer = AVAudioPlayerNode()
    private val nodes = players + uiPlayer + winPlayer + fanfarePlayer + coinPlayer + spellPlayer + victoryPlayer + popupPlayer + rollPlayer
    private var clickBuffer: AVAudioPCMBuffer? = null
    private var uiBuffer: AVAudioPCMBuffer? = null
    private var winBuffer: AVAudioPCMBuffer? = null
    private var fanfareBuffer: AVAudioPCMBuffer? = null
    private var coinBuffer: AVAudioPCMBuffer? = null
    private var spellBuffer: AVAudioPCMBuffer? = null
    private var victoryBuffer: AVAudioPCMBuffer? = null
    private var popupBuffer: AVAudioPCMBuffer? = null
    private var rollBuffer: AVAudioPCMBuffer? = null
    private var rolling = false
    private var next = 0

    init {
        for (player in nodes) {
            engine.attachNode(player)
            engine.connect(player, to = engine.mainMixerNode, format = format)
        }
    }

    fun load(click: FloatArray, ui: FloatArray, win: FloatArray, fanfare: FloatArray, coin: FloatArray, spell: FloatArray, victory: FloatArray, popup: FloatArray, roll: FloatArray) {
        clickBuffer = buffer(click)
        uiBuffer = buffer(ui)
        winBuffer = buffer(win)
        fanfareBuffer = buffer(fanfare)
        coinBuffer = buffer(coin)
        spellBuffer = buffer(spell)
        victoryBuffer = buffer(victory)
        popupBuffer = buffer(popup)
        rollBuffer = buffer(roll)
    }

    fun click() {
        val buffer = clickBuffer ?: return
        if (!start()) return
        val player = players[next++ % players.size]
        player.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    fun ui() {
        val buffer = uiBuffer ?: return
        if (!start()) return
        uiPlayer.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    fun win() {
        val buffer = winBuffer ?: return
        if (!start()) return
        winPlayer.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    fun fanfare() {
        val buffer = fanfareBuffer ?: return
        if (!start()) return
        fanfarePlayer.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    fun coin() {
        val buffer = coinBuffer ?: return
        if (!start()) return
        coinPlayer.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    fun spell() {
        val buffer = spellBuffer ?: return
        if (!start()) return
        spellPlayer.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    fun victory() {
        val buffer = victoryBuffer ?: return
        if (!start()) return
        victoryPlayer.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    fun popup() {
        val buffer = popupBuffer ?: return
        if (!start()) return
        popupPlayer.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
    }

    fun roll(level: Float) {
        rollPlayer.volume = level
        val buffer = rollBuffer ?: return
        if (level == 0f || rolling && engine.running) return
        if (!start()) return
        rollPlayer.scheduleBuffer(buffer, atTime = null, options = AVAudioPlayerNodeBufferLoops or AVAudioPlayerNodeBufferInterrupts, completionHandler = null)
        rolling = true
    }

    /** Every sound stops: a paused engine would play the rest of a long sound, such as the victory, at its next start. */
    fun pause() {
        for (player in nodes) player.stop()
        rolling = false
        if (engine.running) engine.pause()
    }

    /**
     * An engine that stopped, in a pause or by itself when the output changes, such as AirPods or a car, has lost the loop of the
     * roll: the next push schedules it again.
     */
    fun start(): Boolean {
        if (!engine.running) {
            rolling = false
            if (!engine.startAndReturnError(null)) return false
        }
        for (player in nodes) if (!player.playing) player.play()
        return true
    }

    private fun buffer(samples: FloatArray) = AVAudioPCMBuffer(pCMFormat = format, frameCapacity = samples.size.toUInt()).apply {
        frameLength = samples.size.toUInt()
        val channel = floatChannelData!![0]!!
        samples.forEachIndexed { i, s -> channel[i] = s }
    }
}
