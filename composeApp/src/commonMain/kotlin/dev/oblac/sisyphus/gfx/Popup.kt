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

/**
 * A comic popup: [text] in a bubble of paper with a dark outline, and a tail that points down at ([x], [y]).
 * The bubble stands above that point, as wide as its lines, and moves in from the edges so that all of it shows.
 * A [note], such as "1/15", stands at the right of the last line, or under it when the line is full.
 */
fun drawPopup(px: Pixels, text: String, x: Int, y: Int, note: String? = null) {
    val room = px.width - 2 * MARGIN - 2 - 2 * PAD_X
    val lines = PixelFont.wrap(text, room).ifEmpty { listOf("") }.toMutableList()
    val noteWidth = note?.let { PixelFont.width(it) } ?: 0
    if (note != null && PixelFont.width(lines.last()) + NOTE_GAP + noteWidth > room) lines += ""
    val inner = maxOf(4, lines.maxOf { PixelFont.width(it) }, if (note == null) 0 else PixelFont.width(lines.last()) + NOTE_GAP + noteWidth)
    val w = inner + 2 * PAD_X + 2
    val h = (lines.size - 1) * PixelFont.LINE_HEIGHT + PixelFont.CAP_HEIGHT + 2 + 2 * PAD_Y + 2
    val left = max(MARGIN, min(x - w / 2, px.width - MARGIN - w))
    val right = left + w - 1
    val bottom = y - TAIL
    val top = bottom - h + 1

    px.fillRect(left + 2, top + 1, w - 4, h - 2, PAPER)
    px.fillRect(left + 1, top + 2, w - 2, h - 4, PAPER)
    px.hline(left + 2, right - 2, top, INK)
    px.hline(left + 2, right - 2, bottom, INK)
    px.vline(left, top + 2, bottom - 2, INK)
    px.vline(right, top + 2, bottom - 2, INK)
    for ((cx, cy) in listOf(left + 1 to top + 1, right - 1 to top + 1, left + 1 to bottom - 1, right - 1 to bottom - 1)) px[cx, cy] = INK

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
