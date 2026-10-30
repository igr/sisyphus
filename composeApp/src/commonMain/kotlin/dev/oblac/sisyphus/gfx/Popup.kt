package dev.oblac.sisyphus.gfx

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private val PAPER = rgb(0xF1E4C8)
private val INK = rgb(0x2A1A0C)
private val NOTE = rgb(0xC8643C)

/** The bubble keeps this far from the edges of the picture. */
private const val MARGIN = 2

/** Room between the outline and the text, on the sides and above and below. */
private const val PAD_X = 3
private const val PAD_Y = 2

/** Rows of the tail under the bubble, down to its tip. */
private const val TAIL = 5

/** Room between the last line and the note. */
private const val NOTE_GAP = 5

/** The rows of a popup in a picture [width] pixels wide: [text] wrapped, and an empty row when the [note] does not fit after the last line. */
internal fun popupRows(width: Int, text: String, note: String?): List<String> {
    val room = width - 2 * MARGIN - 2 - 2 * PAD_X
    val lines = PixelFont.wrap(text, room).ifEmpty { listOf("") }
    return if (note != null && PixelFont.width(lines.last()) + NOTE_GAP + PixelFont.width(note) > room) lines + "" else lines
}

/**
 * A comic popup: [text] in a bubble of paper with a dark outline, and a tail that points down at ([x], [tipY]).
 * The bubble stands above that point, as wide as its lines, and moves in from the edges so that all of it shows.
 * A [note], such as "1/15", stands at the right of the last line, or under it when the line is full.
 * The bubble stays at or under the row [ceiling]: when it would rise above it, the bubble and the tip of its tail move down.
 */
fun drawPopup(px: Pixels, text: String, x: Int, tipY: Int, note: String? = null, ceiling: Int = Int.MIN_VALUE) {
    val lines = popupRows(px.width, text, note)
    val noteWidth = note?.let { PixelFont.width(it) } ?: 0
    val inner = maxOf(4, lines.maxOf { PixelFont.width(it) }, if (note == null) 0 else PixelFont.width(lines.last()) + NOTE_GAP + noteWidth)
    val w = inner + 2 * PAD_X + 2
    val h = (lines.size - 1) * PixelFont.LINE_HEIGHT + PixelFont.CAP_HEIGHT + 2 + 2 * PAD_Y + 2
    val left = max(MARGIN, min(x - w / 2, px.width - MARGIN - w))
    val right = left + w - 1
    val y = max(tipY, ceiling + h - 1 + TAIL)
    val bottom = y - TAIL
    val top = bottom - h + 1

    px.fillRect(left + 2, top + 1, w - 4, h - 2, PAPER)
    px.fillRect(left + 1, top + 2, w - 2, h - 4, PAPER)
    px.hline(left + 2, right - 2, top, INK)
    px.hline(left + 2, right - 2, bottom, INK)
    px.vline(left, top + 2, bottom - 2, INK)
    px.vline(right, top + 2, bottom - 2, INK)
    px[left + 1, top + 1] = INK
    px[right - 1, top + 1] = INK
    px[left + 1, bottom - 1] = INK
    px[right - 1, bottom - 1] = INK

    val baseLeft = (x - 3).coerceIn(left + 3, right - 8)
    val baseRight = baseLeft + 5
    for (j in 0..TAIL) {
        val t = j / TAIL.toDouble()
        val l = (baseLeft + (x - baseLeft) * t).roundToInt()
        val r = (baseRight + (x - baseRight) * t).roundToInt()
        for (cx in l + 1 until r) px[cx, bottom + j] = PAPER
        px[l, bottom + j] = INK
        px[r, bottom + j] = INK
    }

    val textX = left + 1 + PAD_X
    lines.forEachIndexed { i, line -> PixelFont.draw(px, line, textX, top + 1 + PAD_Y + i * PixelFont.LINE_HEIGHT, INK) }
    if (note != null) PixelFont.draw(px, note, textX + inner - noteWidth, top + 1 + PAD_Y + (lines.size - 1) * PixelFont.LINE_HEIGHT, NOTE)
}
