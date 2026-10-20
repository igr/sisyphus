package dev.oblac.sisyphus.ui

import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.QuoteBox
import dev.oblac.sisyphus.gfx.PixelFont
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.bayer
import dev.oblac.sisyphus.gfx.hash01
import dev.oblac.sisyphus.gfx.rgb
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

val BACKGROUND = rgb(0x17110D)
private val BACKGROUND_GRAIN = rgb(0x1D1611)
private val OUTLINE = rgb(0x0A0705)
private val TERRA = rgb(0xC8643C)
private val TERRA_DARK = rgb(0x7A3A22)
private val TERRA_LIGHT = rgb(0xF29A62)
private val CREAM = rgb(0xF1E4C8)
private val SHADOW = rgb(0x0C0907)
private val BRONZE = intArrayOf(
    rgb(0x3A2412), rgb(0x5E3C1E), rgb(0x85592E), rgb(0xAE7C42), rgb(0xD2A462), rgb(0xF0CE8C),
)
private val GROOVE = rgb(0x2A1A0C)

private val MEANDER = listOf(
    "#######",
    "......#",
    ".####.#",
    ".#..#.#",
    ".#.##.#",
    ".#....#",
    ".######",
)

/** Draws everything around the display: the frame, the ornament, the wise words and the knob. */
class UiRenderer {
    private var grain: Pixels? = null

    fun background(px: Pixels) {
        val g = grain?.takeIf { it.width == px.width && it.height == px.height } ?: Pixels(px.width, px.height).also {
            for (y in 0 until it.height) for (x in 0 until it.width) {
                it[x, y] = if (hash01(x, y, 3) < 0.07f) BACKGROUND_GRAIN else BACKGROUND
            }
            grain = it
        }
        g.data.copyInto(px.data)
    }

    fun bezel(px: Pixels, d: Rect) {
        frame(px, d, OUTLINE)
        px.hline(d.x + 1, d.right - 2, d.y + 1, BRONZE[4])
        px.vline(d.x + 1, d.y + 1, d.bottom - 2, BRONZE[4])
        px.hline(d.x + 1, d.right - 2, d.bottom - 2, BRONZE[1])
        px.vline(d.right - 2, d.y + 1, d.bottom - 2, BRONZE[1])
        px[d.x + 1, d.bottom - 2] = BRONZE[3]
        px[d.right - 2, d.y + 1] = BRONZE[3]
        frame(px, d.inset(2), OUTLINE)
    }

    fun band(px: Pixels, r: Rect) {
        val tile = MEANDER[0].length
        val count = r.w / tile
        val x0 = r.x + (r.w - count * tile) / 2
        for (k in 0 until count) for ((y, row) in MEANDER.withIndex()) for ((x, ch) in row.withIndex()) {
            if (ch == '#') px[x0 + k * tile + x, r.y + y] = TERRA
        }
        px.hline(x0, x0 + count * tile - 1, r.y + MEANDER.size + 1, TERRA_DARK)
    }

    fun quote(px: Pixels, r: Rect, box: QuoteBox, clock: Double) {
        val q = box.quote
        val lines = PixelFont.wrap(q.text, r.w - 4)
        val blockH = (lines.size + 1) * PixelFont.LINE_HEIGHT - 2
        var y = r.y + (r.h - blockH) / 2
        val typed = box.typed.toInt()
        var left = typed
        var cursorX = -1
        var cursorY = 0
        for (line in lines) {
            val x = r.x + (r.w - PixelFont.width(line)) / 2
            val n = left.coerceIn(0, line.length)
            if (n > 0) PixelFont.draw(px, line.take(n), x, y, CREAM, box.fade)
            if (cursorX < 0 && n < line.length) {
                cursorX = x + PixelFont.width(line.take(n)) + if (n > 0) 2 else 0
                cursorY = y
            }
            left -= line.length
            y += PixelFont.LINE_HEIGHT
        }
        val shown = (typed - q.text.length).coerceIn(0, q.author.length)
        if (shown > 0) {
            val author = "— ${q.author}"
            val x = r.x + (r.w - PixelFont.width(author)) / 2
            PixelFont.draw(px, author.take(2 + shown), x, y + 2, TERRA, box.fade)
        }
        val typing = box.phase == QuoteBox.Phase.TYPING && cursorX >= 0
        if (typing && floor(clock * 4).toInt() % 2 == 0) px.fillRect(cursorX, cursorY, 3, PixelFont.CAP_HEIGHT, TERRA)
    }

    fun knob(px: Pixels, layout: Layout, game: Game) {
        val cx = layout.knobX
        val cy = layout.knobY
        val r = layout.knobR
        val angle = game.knob.angle
        val kx = cx + 0.5
        val ky = cy + 0.5

        for (k in 0 until TICKS) {
            val a = k * 2 * PI / TICKS - PI / 2
            val major = k % (TICKS / 4) == 0
            dot(px, kx + cos(a) * (r + 3.5), ky + sin(a) * (r + 3.5), if (major) TERRA else TERRA_DARK)
            if (major) dot(px, kx + cos(a) * (r + 4.5), ky + sin(a) * (r + 4.5), TERRA)
        }
        px.disc(kx + 1.5, ky + 2.5, r + 0.5, SHADOW)

        val faceR = r - RIM - 1.0
        for (j in cy - r - 1..cy + r + 1) for (i in cx - r - 1..cx + r + 1) {
            val dx = (i - cx).toDouble()
            val dy = (j - cy).toDouble()
            val d = sqrt(dx * dx + dy * dy)
            if (d > r + 0.5) continue
            px[i, j] = when {
                d > r - 0.5 -> OUTLINE
                d > r - RIM -> ridge(dx, dy, d, angle, bayer(i, j))
                d > faceR -> BRONZE[0]
                else -> face(dx, dy, faceR, bayer(i, j))
            }
        }

        val a = angle - PI / 2
        val c = cos(a)
        val s = sin(a)
        px.segment(kx + c * faceR * 0.22, ky + s * faceR * 0.22, kx + c * faceR * 0.72, ky + s * faceR * 0.72, 1.8, GROOVE)
        val dotR = faceR * 0.72 + 2.6
        px.disc(kx + c * dotR, ky + s * dotR, 1.8, if (game.knob.held) TERRA_LIGHT else TERRA)

        if (game.effort == 0.0 && !game.knob.held && floor(game.clock * 2).toInt() % 2 == 0) {
            arrow(px, kx, ky, r + 8.0, -0.45, 0.45)
            arrow(px, kx, ky, r + 8.0, PI - 0.45, PI + 0.45)
        }
    }

    /** The grip on the knob edge: grooves turn with the knob, the light stays put. */
    private fun ridge(dx: Double, dy: Double, d: Double, angle: Double, dither: Float): Int {
        val phi = atan2(dy, dx) - angle
        val seg = phi / (2 * PI / GRIP)
        val groove = seg - floor(seg) < 0.32
        val l = -(dx * 0.6 + dy * 0.8) / d
        val tone = ((l + 1) * 2.2 + 0.6 + dither - 0.5).toInt() - if (groove) 2 else 0
        return BRONZE[tone.coerceIn(0, BRONZE.size - 1)]
    }

    /** A slightly domed face: flat bands of bronze, a soft shadow and one crisp highlight. */
    private fun face(dx: Double, dy: Double, r: Double, dither: Float): Int {
        val nx = dx / r
        val ny = dy / r
        if ((nx + 0.5) * (nx + 0.5) + (ny + 0.52) * (ny + 0.52) < 0.008) return BRONZE[5]
        val nz = sqrt(max(0.0, 1 - 0.55 * (nx * nx + ny * ny)))
        val l = -0.45 * nx - 0.6 * ny + 0.65 * nz
        val tone = (2.2 + l * 1.9 + (dither - 0.5) * 0.7).toInt()
        return BRONZE[tone.coerceIn(1, 4)]
    }

    private fun arrow(px: Pixels, kx: Double, ky: Double, r: Double, from: Double, to: Double) {
        val steps = 18
        for (k in 0..steps) {
            val a = from + (to - from) * k / steps
            dot(px, kx + cos(a) * r, ky + sin(a) * r, CREAM)
        }
        val ex = kx + cos(to) * r
        val ey = ky + sin(to) * r
        val tx = -sin(to)
        val ty = cos(to)
        val nx = cos(to)
        val ny = sin(to)
        for (k in 1..2) {
            dot(px, ex - tx * k + nx * k, ey - ty * k + ny * k, CREAM)
            dot(px, ex - tx * k - nx * k, ey - ty * k - ny * k, CREAM)
        }
    }

    private fun frame(px: Pixels, r: Rect, color: Int) {
        px.hline(r.x, r.right - 1, r.y, color)
        px.hline(r.x, r.right - 1, r.bottom - 1, color)
        px.vline(r.x, r.y, r.bottom - 1, color)
        px.vline(r.right - 1, r.y, r.bottom - 1, color)
    }

    private fun dot(px: Pixels, x: Double, y: Double, c: Int) {
        px[floor(x).toInt(), floor(y).toInt()] = c
    }

    private companion object {
        const val TICKS = 24
        const val GRIP = 20
        const val RIM = 5
    }
}
