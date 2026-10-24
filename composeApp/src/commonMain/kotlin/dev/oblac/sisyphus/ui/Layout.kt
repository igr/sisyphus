package dev.oblac.sisyphus.ui

import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.math.min
import kotlin.math.roundToInt

class Rect(val x: Int, val y: Int, val w: Int, val h: Int) {
    val right get() = x + w
    val bottom get() = y + h
    fun inset(d: Int) = Rect(x + d, y + d, w - 2 * d, h - 2 * d)
}

/**
 * Splits the screen, in virtual pixels: the display takes the top half,
 * the wise words come below it, and the knob takes the rest.
 * The Agora, the shop, covers all of it when open.
 */
class Layout(val width: Int, val height: Int, safeTop: Int, safeBottom: Int, safeLeft: Int, safeRight: Int) {
    private val left = safeLeft + 4
    private val right = width - safeRight - 4
    private val top = safeTop + 3
    private val bottom = height - safeBottom - 3

    val display = Rect(left, top, right - left, ((bottom - top) * 0.5).roundToInt())
    val scene = display.inset(BEZEL)
    val band = Rect(left, display.bottom + 4, right - left, BAND_HEIGHT)
    val quote = Rect(left + 2, band.bottom + 4, right - left - 4, PixelFont.LINE_HEIGHT * 4)

    /**
     * The obols, in the top-right corner of the display across from the day. They open and close the Agora,
     * and stay in the same place there.
     */
    val coinIcon = (COIN_SIZE + 2 + PixelFont.width("9999")).let { w -> Rect(scene.right - 4 - w, scene.y + 4, w, COIN_SIZE) }

    val agoraTitle = Rect(left + 2, coinIcon.y, coinIcon.right - left - 2, PixelFont.LINE_HEIGHT)

    /** In the Agora, the speaker in the bottom-right corner turns the sound on and off. */
    val agoraSound = Rect(coinIcon.right - SPEAKER_WIDTH, bottom - SPEAKER_HEIGHT - 1, SPEAKER_WIDTH, SPEAKER_HEIGHT)

    /** One of the five lines of the Agora, from the top. */
    fun agoraRow(index: Int): Rect {
        val y0 = agoraTitle.bottom + 6
        val h = (agoraSound.y - 6 - y0) / 5
        return Rect(agoraTitle.x, y0 + index * h, agoraTitle.w, h)
    }

    /** In the dev screen, the button for one of [DEV_STEPS], below the "Obols" title. */
    fun devStep(index: Int): Rect {
        val r = agoraRow(0)
        val w = (r.w - 2 * (DEV_STEPS.size - 1)) / DEV_STEPS.size
        return Rect(r.x + index * (w + 2), r.y + PixelFont.LINE_HEIGHT, w, BUTTON_HEIGHT)
    }

    /** In the dev screen, the button that empties the shop, below the "Shop" title. */
    val devReset = agoraRow(1).let { Rect(it.x, it.y + PixelFont.LINE_HEIGHT, PixelFont.width("Reset") + 8, BUTTON_HEIGHT) }

    val knobX: Int
    val knobY: Int
    val knobR: Int

    init {
        val knobTop = quote.bottom + 3
        val room = bottom - knobTop
        knobR = min(room / 2 - 6, (right - left) / 2 - 14).coerceIn(14, 44)
        knobX = left + (right - left) / 2
        knobY = knobTop + room / 2
    }

    companion object {
        const val BEZEL = 3
        const val BAND_HEIGHT = 9
        const val SPEAKER_WIDTH = 11
        const val SPEAKER_HEIGHT = 7
        const val COIN_SIZE = 7
        const val BUTTON_HEIGHT = 13
    }
}

/** The obols each button of the dev screen adds or takes away, from the left. */
val DEV_STEPS = listOf(-10, -1, 1, 10, 100)
