package dev.oblac.sisyphus

import androidx.compose.ui.graphics.ImageBitmap
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.gfx.PixelFont
import dev.oblac.sisyphus.gfx.Pixels
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.time.Clock

interface Store {
    fun load(key: String): Double?
    fun save(key: String, value: Double)
}

class MemoryStore(values: Map<String, Double> = emptyMap()) : Store {
    private val values = values.toMutableMap()
    override fun load(key: String) = values[key]
    override fun save(key: String, value: Double) {
        values[key] = value
    }
}

/** The worlds bought with money. Each is bought once and owned forever; the store answers later, through `done`. */
interface Billing {
    fun owns(place: Place): Boolean
    /** What [place] costs, written by [shownPrice]; null until the store answers. */
    fun price(place: Place): String?
    /** Opens the store's payment sheet. [done] tells whether the player owns [place] now. */
    fun buy(place: Place, done: (Boolean) -> Unit)
    /** Asks the store for the worlds bought before: on another phone, or before the game was installed again. */
    fun restore(done: () -> Unit)
    /** The player's id at the store's service, which support can look up; null where there is no store. */
    val userId: String? get() = null
}

/** On the desktop there is no store: every world is open. */
object NoBilling : Billing {
    override fun owns(place: Place) = true
    override fun price(place: Place): String? = null
    override fun buy(place: Place, done: (Boolean) -> Unit) = done(true)
    override fun restore(done: () -> Unit) = done()
}

/**
 * A price as the store writes it, such as "$0.99", when the pixel font has all of its letters.
 * Otherwise it is the amount and the currency, such as "89 INR".
 */
fun shownPrice(formatted: String, amountMicros: Long, currency: String): String {
    val text = formatted.replace('\u00A0', ' ').replace('\u202F', ' ')
    if (text.all(PixelFont::hasGlyph)) return text
    val cents = (amountMicros + 5_000) / 10_000
    val amount = if (cents % 100 == 0L) "${cents / 100}" else "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
    return "$amount $currency"
}

interface Haptics {
    fun prepare() {}
    fun tick()
}

object NoHaptics : Haptics {
    override fun tick() {}
}

interface Sound {
    /**
     * Takes the knob click, the click of every other tap, the sound of buying, the fanfare, the coin, the spell of a vision,
     * the victory at the top, the pop of a bubble and the loop of the rolling rock, as mono samples at 44.1 kHz.
     */
    fun load(click: FloatArray, ui: FloatArray, win: FloatArray, fanfare: FloatArray, coin: FloatArray, spell: FloatArray, victory: FloatArray, popup: FloatArray, roll: FloatArray) {}
    /** Called when a finger touches the knob, before the first click. */
    fun prepare() {}
    /** One detent of the knob. */
    fun click()
    /** A tap anywhere but the knob: menus, buttons, the help. */
    fun ui() {}
    /** Plays when something is bought in the Agora. */
    fun win() {}
    /** Plays when the player arrives in another world. */
    fun fanfare() {}
    /** Plays when a new obol comes. */
    fun coin() {}
    /** Plays when a god comes as a vision. */
    fun spell() {}
    /** Plays when he reaches the top. */
    fun victory() {}
    /** Plays when a bubble opens, over a stone or a joker. */
    fun popup() {}
    /** The rock rolls in a loop as loud as [level]: 0 while it stands still, 1 at a brisk push. */
    fun roll(level: Float) {}
    /** Releases the audio hardware until the next click. */
    fun pause() {}
}

/** Each detent of the knob clicks, unless the sound is off. */
fun Sound.tick(on: Boolean) {
    if (on) click()
}

private const val BRISK_TURNS = 1.5

/** The rock rolls as fast as he pushes it, [turns] a second, and loudest at [BRISK_TURNS] a second. It is silent while the sound is off. */
fun Sound.rock(turns: Double, on: Boolean) {
    roll(if (on) sqrt(min(1.0, turns / BRISK_TURNS)).toFloat() else 0f)
}

/** A new obol plays the coin, unless the sound is off. */
fun Sound.obol(on: Boolean) {
    if (on) coin()
}

/** A vision plays the spell, unless the sound is off. */
fun Sound.vision(on: Boolean) {
    if (on) spell()
}

/** A bubble that opens plays the pop, unless the sound is off. */
fun Sound.bubble(on: Boolean) {
    if (on) popup()
}

/** The top plays the victory, unless the sound is off. */
fun Sound.top(on: Boolean) {
    if (on) victory()
}

/**
 * The sound of a [touch]: the knob gets ready to click, buying wins, a new world has its fanfare, and every other tap clicks.
 * Nothing plays while the sound is off; turning it off releases the audio hardware.
 */
fun Sound.play(touch: Touch, on: Boolean) {
    if (!on) {
        if (touch == Touch.SOUND) pause()
        return
    }
    when (touch) {
        Touch.NONE, Touch.LIST -> {}
        Touch.KNOB -> prepare()
        Touch.BUY -> win()
        Touch.MOVE -> fanfare()
        Touch.SOUND, Touch.SHOP, Touch.DEV, Touch.HELP, Touch.AGAIN, Touch.WORLDS, Touch.COPY, Touch.SETTINGS -> ui()
    }
}

object Silence : Sound {
    override fun click() {}
}

/** Turns the framebuffer into an image that Compose can draw. */
fun interface FrameUploader {
    fun upload(pixels: Pixels): ImageBitmap
}

expect fun frameUploader(): FrameUploader

/** Seconds since 1970, from the device clock. */
fun wallSeconds(): Double = Clock.System.now().toEpochMilliseconds() / 1000.0
