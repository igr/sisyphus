package dev.oblac.sisyphus.ui

import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.ITEMS
import dev.oblac.sisyphus.game.Item
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.Shop
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
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

val BACKGROUND = rgb(0x17110D)

/** Text and coins in the Agora that the obols cannot buy yet. */
private val DISABLED = rgb(0x5A5048)
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

private val SPEAKER_ON = listOf(
    "...#....#..",
    "..##..#..#.",
    "####...#..#",
    "####...#..#",
    "####...#..#",
    "..##..#..#.",
    "...#....#..",
)

private val SPEAKER_OFF = listOf(
    "...#.......",
    "..##..#...#",
    "####...#.#.",
    "####....#..",
    "####...#.#.",
    "..##..#...#",
    "...#.......",
)

internal val COIN = listOf(
    "..###..",
    ".#hhh#.",
    "#hhfff#",
    "#hffff#",
    "#hfffs#",
    ".#fss#.",
    "..###..",
)

/** Takes the place of the coin when a line is all bought. */
private val CHECK = listOf(
    "......#",
    ".....##",
    "#...##.",
    "##.##..",
    ".###...",
    "..#....",
)

internal val COIN_COLORS = mapOf('#' to BRONZE[1], 'h' to BRONZE[5], 'f' to BRONZE[4], 's' to BRONZE[3])
private val COIN_GRAY = mapOf('#' to rgb(0x2E2924), 'h' to rgb(0x7A7068), 'f' to DISABLED, 's' to rgb(0x4A423A))

/** A color at a third of its light, for what the help does not point at. */
private fun dim(c: Int) = rgb((c shr 16 and 0xFF) / 3, (c shr 8 and 0xFF) / 3, (c and 0xFF) / 3)

private val LINE_TITLES = listOf("Rock", "Sidekicks", "Gods", "Blessings", "Beauty")

/** Draws everything around the display: the frame, the ornament, the wise words, the knob and the Agora. */
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

    fun speaker(px: Pixels, r: Rect, on: Boolean) {
        val color = if (on) TERRA else TERRA_DARK
        for ((y, row) in (if (on) SPEAKER_ON else SPEAKER_OFF).withIndex()) {
            for ((x, ch) in row.withIndex()) if (ch == '#') px[r.x + x, r.y + y] = color
        }
    }

    /** The obols: a coin and how many there are, outlined like the day across from them. */
    fun coins(px: Pixels, r: Rect, obols: Int) {
        val count = min(obols, 9999).toString()
        val x = r.right - PixelFont.width(count)
        PixelFont.drawOutlined(px, count, x, r.y, CREAM, SHADOW)
        coin(px, x - 2 - Layout.COIN_SIZE, r.y, COIN_COLORS, outline = true)
    }

    fun helpButton(px: Pixels, r: Rect) = PixelFont.draw(px, "(?)", r.x, r.y, TERRA)

    /** The help, over the main screen: all dims but the day, the obols and the knob, and a note points at each. */
    fun help(px: Pixels, layout: Layout, day: Int, obols: Int) {
        val kx = layout.knobX + 0.5
        val ky = layout.knobY + 0.5
        val kr = layout.knobR + 6.0
        for (y in 0 until px.height) for (x in 0 until px.width) {
            if ((x - kx) * (x - kx) + (y - ky) * (y - ky) > kr * kr) px[x, y] = dim(px[x, y])
        }
        val dayLabel = layout.dayLabel(day)
        val coins = layout.coinIcon
        PixelFont.drawOutlined(px, "DAY $day", dayLabel.x + 1, dayLabel.y + 1, CREAM, SHADOW)
        coins(px, coins, obols)
        helpButton(px, layout.helpIcon)

        val dayNote = PixelFont.wrap("The sun moves only while you push.", layout.scene.w / 2)
        val dayY = dayLabel.bottom + 12
        note(px, dayNote, dayLabel.x + 1, dayY, right = false)
        arrowUp(px, dayLabel.x + 6, dayLabel.bottom + 1, dayY - 3)

        val obolsNote = PixelFont.wrap("Obols you earn by pushing. Tap them to open the market.", layout.scene.w * 2 / 3)
        val obolsY = dayY + dayNote.size * PixelFont.LINE_HEIGHT + 8
        note(px, obolsNote, coins.right, obolsY, right = true)
        arrowUp(px, coins.right - PixelFont.width(min(obols, 9999).toString()) - 6, coins.bottom + 2, obolsY - 3)

        val knobNote = PixelFont.wrap("Turn the knob, either way, to push the rock.", layout.quote.w)
        val tip = layout.knobY - layout.knobR - 7
        val knobY = tip - 7 - knobNote.size * PixelFont.LINE_HEIGHT
        for ((i, line) in knobNote.withIndex()) {
            PixelFont.drawOutlined(px, line, layout.knobX - PixelFont.width(line) / 2, knobY + i * PixelFont.LINE_HEIGHT, CREAM, OUTLINE)
        }
        arrowDown(px, layout.knobX, tip - 8, tip)
    }

    private fun note(px: Pixels, lines: List<String>, x: Int, y: Int, right: Boolean) {
        for ((i, line) in lines.withIndex()) {
            PixelFont.drawOutlined(px, line, if (right) x - PixelFont.width(line) else x, y + i * PixelFont.LINE_HEIGHT, CREAM, OUTLINE)
        }
    }

    private fun arrowUp(px: Pixels, x: Int, tip: Int, tail: Int) {
        px.vline(x, tip, tail, TERRA_LIGHT)
        px.hline(x - 1, x + 1, tip + 1, TERRA_LIGHT)
        px.hline(x - 2, x + 2, tip + 2, TERRA_LIGHT)
    }

    private fun arrowDown(px: Pixels, x: Int, tail: Int, tip: Int) {
        px.vline(x, tail, tip, TERRA_LIGHT)
        px.hline(x - 1, x + 1, tip - 1, TERRA_LIGHT)
        px.hline(x - 2, x + 2, tip - 2, TERRA_LIGHT)
    }

    /** The Agora: what the obols can buy next, line by line, and the sound setting. */
    fun agora(px: Pixels, layout: Layout, shop: Shop, soundOn: Boolean, dev: Boolean = false) {
        val t = layout.agoraTitle
        PixelFont.draw(px, if (dev) "Dev" else "Agora Market", t.x, t.y, CREAM)
        px.hline(t.x, t.right - 1, t.bottom + 2, TERRA_DARK)
        if (dev) devScreen(px, layout) else for (line in Line.entries) row(px, layout.agoraRow(line.ordinal), line, shop)
        speaker(px, layout.agoraSound, soundOn)
    }

    /** The offer over the Agora: the item's picture, name and details, its price, and Yes and No. */
    fun offer(px: Pixels, layout: Layout, item: Item) {
        for (y in 0 until px.height) for (x in 0 until px.width) px[x, y] = dim(px[x, y])
        val r = layout.offer
        px.fillRect(r.x, r.y, r.w, r.h, BACKGROUND)
        frame(px, r, TERRA)
        val icon = layout.offerIcon
        px.drawScaled(itemIcon(item), icon.x, icon.y, 2)
        PixelFont.draw(px, item.name, r.x + (r.w - PixelFont.width(item.name)) / 2, icon.bottom + 5, CREAM)
        val text = layout.offerText
        for ((i, line) in PixelFont.wrap(item.details, text.w).withIndex()) {
            PixelFont.draw(px, line, text.x, text.y + i * PixelFont.LINE_HEIGHT, TERRA_LIGHT)
        }
        val ask = "Buy for ${item.price}"
        val x = r.x + (r.w - PixelFont.width(ask) - 3 - Layout.COIN_SIZE - PixelFont.width("?")) / 2
        val y = text.bottom + 4
        PixelFont.draw(px, ask, x, y, CREAM)
        coin(px, x + PixelFont.width(ask) + 2, y, COIN_COLORS)
        PixelFont.draw(px, "?", x + PixelFont.width(ask) + 3 + Layout.COIN_SIZE, y, CREAM)
        button(px, layout.offerYes, "Yes")
        button(px, layout.offerNo, "No")
    }

    /** In dev builds only: buttons that add or take away obols, and one that resets the shop. */
    private fun devScreen(px: Pixels, layout: Layout) {
        val obols = layout.agoraRow(0)
        PixelFont.draw(px, "Obols", obols.x, obols.y, TERRA)
        DEV_STEPS.forEachIndexed { i, step -> button(px, layout.devStep(i), if (step > 0) "+$step" else "$step") }
        val shop = layout.agoraRow(1)
        PixelFont.draw(px, "Shop", shop.x, shop.y, TERRA)
        button(px, layout.devReset, "Reset")
    }

    private fun button(px: Pixels, r: Rect, label: String) {
        frame(px, r, TERRA)
        PixelFont.draw(px, label, r.x + (r.w - PixelFont.width(label) + 1) / 2, r.y + 3, CREAM)
    }

    private fun row(px: Pixels, r: Rect, line: Line, shop: Shop) {
        val next = shop.next(line)
        val item = next ?: ITEMS.last { it.line == line }
        val bright = next == null || shop.obols >= next.price
        PixelFont.draw(px, LINE_TITLES[line.ordinal], r.x, r.y, TERRA)
        val y = r.y + PixelFont.LINE_HEIGHT
        PixelFont.draw(px, item.name, r.x, y, CREAM)
        if (next != null) {
            val price = next.price.toString()
            coin(px, r.right - Layout.COIN_SIZE, y, if (bright) COIN_COLORS else COIN_GRAY)
            PixelFont.draw(px, price, r.right - Layout.COIN_SIZE - 2 - PixelFont.width(price), y, if (bright) TERRA_LIGHT else DISABLED)
        } else {
            for ((dy, row) in CHECK.withIndex()) for ((dx, ch) in row.withIndex()) {
                if (ch == '#') px[r.right - Layout.COIN_SIZE + dx, y + 1 + dy] = TERRA_LIGHT
            }
        }
        val noteY = y + PixelFont.LINE_HEIGHT
        PixelFont.draw(px, item.note, r.x, noteY, TERRA_LIGHT)
        for (k in 0 until 3) {
            val pip = Rect(r.right - 11 + k * 4, noteY + 2, 3, 3)
            if (k < shop.tier(line)) px.fillRect(pip.x, pip.y, pip.w, pip.h, if (bright) TERRA_LIGHT else DISABLED)
            else frame(px, pip, if (bright) TERRA else DISABLED)
        }
    }

    private fun coin(px: Pixels, x: Int, y: Int, colors: Map<Char, Int>, outline: Boolean = false) {
        if (outline) for ((dy, row) in COIN.withIndex()) for ((dx, ch) in row.withIndex()) {
            if (ch == '.') continue
            for ((ox, oy) in listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)) px[x + dx + ox, y + dy + oy] = SHADOW
        }
        for ((dy, row) in COIN.withIndex()) for ((dx, ch) in row.withIndex()) {
            px[x + dx, y + dy] = colors[ch] ?: continue
        }
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
