package dev.oblac.sisyphus.gfx

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

const val TRANSPARENT = 0
private const val OPAQUE = -0x1000000

fun rgb(hex: Int): Int = hex or OPAQUE

fun rgb(r: Int, g: Int, b: Int): Int =
    OPAQUE or (r.coerceIn(0, 255) shl 16) or (g.coerceIn(0, 255) shl 8) or b.coerceIn(0, 255)

val WHITE = rgb(0xFFFFFF)

fun red(c: Int) = (c shr 16) and 0xFF
fun green(c: Int) = (c shr 8) and 0xFF
fun blue(c: Int) = c and 0xFF

fun lerpColor(a: Int, b: Int, t: Float): Int {
    val k = t.coerceIn(0f, 1f)
    return rgb(
        (red(a) + (red(b) - red(a)) * k).roundToInt(),
        (green(a) + (green(b) - green(a)) * k).roundToInt(),
        (blue(a) + (blue(b) - blue(a)) * k).roundToInt(),
    )
}

/** Multiplies [c] by [light], channel by channel. White light keeps the color. */
fun shade(c: Int, light: Int): Int =
    if (light == WHITE) c
    else rgb(red(c) * red(light) / 255, green(c) * green(light) / 255, blue(c) * blue(light) / 255)

fun luminance(c: Int): Int = (red(c) * 77 + green(c) * 150 + blue(c) * 29) shr 8

private val BAYER = intArrayOf(0, 8, 2, 10, 12, 4, 14, 6, 3, 11, 1, 9, 15, 7, 13, 5)

/** Ordered dither threshold in (0, 1) for a pixel. */
fun bayer(x: Int, y: Int): Float = (BAYER[(y and 3) * 4 + (x and 3)] + 0.5f) / 16f

fun hash(x: Int, y: Int = 0, seed: Int = 0): Int {
    var h = x * 374761393 + y * 668265263 + seed * 1274126177
    h = (h xor (h ushr 13)) * 1274126177
    return h xor (h ushr 16)
}

/** Stable pseudo random value in [0, 1) for integer coordinates. */
fun hash01(x: Int, y: Int = 0, seed: Int = 0): Float = (hash(x, y, seed) and 0xFFFFFF) / 16777216f

/** A plain ARGB framebuffer. Sprites are Pixels too: a zero pixel is transparent. */
class Pixels(val width: Int, val height: Int) {
    val data = IntArray(width * height)

    fun clear(color: Int) = data.fill(color)

    operator fun get(x: Int, y: Int): Int =
        if (x < 0 || y < 0 || x >= width || y >= height) TRANSPARENT else data[y * width + x]

    operator fun set(x: Int, y: Int, color: Int) {
        if (x < 0 || y < 0 || x >= width || y >= height) return
        data[y * width + x] = color
    }

    fun fillRect(x: Int, y: Int, w: Int, h: Int, color: Int) {
        val x0 = max(0, x)
        val y0 = max(0, y)
        val x1 = min(width, x + w)
        val y1 = min(height, y + h)
        for (yy in y0 until y1) {
            val row = yy * width
            for (xx in x0 until x1) data[row + xx] = color
        }
    }

    fun hline(x0: Int, x1: Int, y: Int, color: Int) = fillRect(x0, y, x1 - x0 + 1, 1, color)

    fun vline(x: Int, y0: Int, y1: Int, color: Int) = fillRect(x, y0, 1, y1 - y0 + 1, color)

    fun line(x0: Int, y0: Int, x1: Int, y1: Int, color: Int) {
        var x = x0
        var y = y0
        val dx = kotlin.math.abs(x1 - x0)
        val dy = -kotlin.math.abs(y1 - y0)
        val sx = if (x0 < x1) 1 else -1
        val sy = if (y0 < y1) 1 else -1
        var err = dx + dy
        while (true) {
            set(x, y, color)
            if (x == x1 && y == y1) return
            val e2 = 2 * err
            if (e2 >= dy) { err += dy; x += sx }
            if (e2 <= dx) { err += dx; y += sy }
        }
    }

    /** Fills pixels whose centers lie within [r] of the continuous point ([cx], [cy]). */
    fun disc(cx: Double, cy: Double, r: Double, color: Int) {
        val r2 = r * r
        for (j in floor(cy - r).toInt()..ceil(cy + r).toInt()) {
            val dy = j + 0.5 - cy
            for (i in floor(cx - r).toInt()..ceil(cx + r).toInt()) {
                val dx = i + 0.5 - cx
                if (dx * dx + dy * dy <= r2) set(i, j, color)
            }
        }
    }

    /** Fills pixels whose centers lie within [width]/2 of a segment. */
    fun segment(x0: Double, y0: Double, x1: Double, y1: Double, width: Double, color: Int) {
        val r = width / 2
        val vx = x1 - x0
        val vy = y1 - y0
        val len2 = vx * vx + vy * vy
        for (j in floor(min(y0, y1) - r).toInt()..ceil(max(y0, y1) + r).toInt()) {
            for (i in floor(min(x0, x1) - r).toInt()..ceil(max(x0, x1) + r).toInt()) {
                val px = i + 0.5 - x0
                val py = j + 0.5 - y0
                val t = if (len2 == 0.0) 0.0 else ((px * vx + py * vy) / len2).coerceIn(0.0, 1.0)
                val ex = px - vx * t
                val ey = py - vy * t
                if (ex * ex + ey * ey <= r * r) set(i, j, color)
            }
        }
    }

    /** Copies [src] at ([x], [y]), skipping transparent pixels and multiplying by [light]. */
    fun draw(src: Pixels, x: Int, y: Int, flipX: Boolean = false, light: Int = WHITE) {
        for (sy in 0 until src.height) {
            val ty = y + sy
            if (ty < 0 || ty >= height) continue
            val srcRow = sy * src.width
            for (sx in 0 until src.width) {
                val c = src.data[srcRow + if (flipX) src.width - 1 - sx else sx]
                if (c == TRANSPARENT) continue
                val tx = x + sx
                if (tx < 0 || tx >= width) continue
                data[ty * width + tx] = shade(c, light)
            }
        }
    }

    /** A copy grown by one pixel on each side, with a [color] outline around the opaque pixels. */
    fun outlined(color: Int): Pixels {
        val out = Pixels(width + 2, height + 2)
        out.draw(this, 1, 1)
        for (y in 0 until out.height) for (x in 0 until out.width) {
            if (out[x, y] != TRANSPARENT) continue
            if (this[x - 1, y - 2] != TRANSPARENT || this[x - 1, y] != TRANSPARENT ||
                this[x - 2, y - 1] != TRANSPARENT || this[x, y - 1] != TRANSPARENT
            ) out[x, y] = color
        }
        return out
    }
}

/** Builds a sprite from ASCII art. Characters missing from [palette] are transparent. */
fun sprite(palette: Map<Char, Int>, art: String): Pixels {
    val rows = art.trimIndent().lines()
    val px = Pixels(rows.maxOf { it.length }, rows.size)
    rows.forEachIndexed { y, row -> row.forEachIndexed { x, ch -> palette[ch]?.let { px[x, y] = it } } }
    return px
}
