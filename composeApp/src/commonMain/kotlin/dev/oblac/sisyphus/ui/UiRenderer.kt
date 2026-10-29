package dev.oblac.sisyphus.ui

import dev.oblac.sisyphus.Billing
import dev.oblac.sisyphus.game.Category
import dev.oblac.sisyphus.game.Counts
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.Item
import dev.oblac.sisyphus.game.LABORS
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.Shop
import dev.oblac.sisyphus.game.QuoteBox
import dev.oblac.sisyphus.game.Vision
import dev.oblac.sisyphus.game.done
import dev.oblac.sisyphus.game.items
import dev.oblac.sisyphus.game.progress
import dev.oblac.sisyphus.game.shortCount
import dev.oblac.sisyphus.gfx.PixelFont
import dev.oblac.sisyphus.gfx.TRANSPARENT
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.bayer
import dev.oblac.sisyphus.gfx.hash01
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.gfx.sprite
import dev.oblac.sisyphus.scene.Ending
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

val BACKGROUND = rgb(0x17110D)

/** Text and coins in the Agora that the obols cannot buy yet. */
private val DISABLED = rgb(0x5A5048)
/** A labor not done yet: gray, but still easy to read. */
private val NOT_DONE = rgb(0x8A7E72)

/** The dot of a tier of the labors, done and not done. */
private val DOT_FULL = listOf(".###.", "#####", "#####", "#####", ".###.")
private val DOT_RING = listOf(".###.", "#...#", "#...#", "#...#", ".###.")

/** The card of a labor not done yet, and its edge: brighter for the next labor of its category. */
private val CARD = rgb(0x211913)
private val CARD_EDGE = rgb(0x3A2E24)
private val NEXT_EDGE = rgb(0x5A5048)
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

/** On the Worlds screen, next to its price, a world that is not bought yet. */
private val LOCK = listOf(
    "..###..",
    ".#...#.",
    ".#...#.",
    "#######",
    "###.###",
    "###.###",
    "#######",
)

internal val COIN_COLORS = mapOf('#' to BRONZE[1], 'h' to BRONZE[5], 'f' to BRONZE[4], 's' to BRONZE[3])
private val COIN_GRAY = mapOf('#' to rgb(0x2E2924), 'h' to rgb(0x7A7068), 'f' to DISABLED, 's' to rgb(0x4A423A))

/** The bronze of the Greek key of the obol bar, as far as the next obol has come. */
internal val OBOL_FILL = BRONZE[4]

/** The dark bronze of the key, where the next obol has not come yet. */
internal val OBOL_EMPTY = BRONZE[1]

/** In the Agora, the dots after the name of a line, one for each of its items: bought, on offer, and for later. */
internal val DOT_BOUGHT = BRONZE[4]
internal val DOT_OFFERED = CREAM
internal val DOT_LATER = TERRA_DARK

/** The size of a dot, after the name of a line in the Agora and for a tier of the labors, in pixels. */
private const val DOT = 5

/** The light of the key during the final climb, as far as he has climbed. */
internal val CLIMB_FILL = CREAM

/** The thin line around the panel while a god asks in it. */
internal val VOICE_FRAME = TERRA_DARK

/** What the help says of the obols. During the final climb, obols stop coming, and the coin only opens the market. */
internal fun obolsNote(climbing: Boolean) = if (climbing) "Tap the coin for the market." else "Obols you earn by pushing. Tap them for the market."

/** What the help says of the bar: it fills up to the next obol, and during the final climb, to the top. */
internal fun barNote(climbing: Boolean) = if (climbing) "The bar fills up to the top." else "The bar fills up to the next obol."

/** The light of the glint on the coin of the count when new obols come. */
internal val GLINT = CREAM

/** The number of new obols, such as "+1", that rises under the count. */
internal val GAIN = BRONZE[5]

/** Seconds that the number of new obols rises under the count, and fades in the second half. */
internal const val GAIN_TIME = 1.0

/** Seconds that the coin of the count glints when new obols come. */
private const val COIN_GLINT = 0.4

/** How far the number of new obols rises, in virtual pixels. */
private const val GAIN_RISE = 4

/** A color at a third of its light, for what the help does not point at. */
private fun dim(c: Int) = rgb((c shr 16 and 0xFF) / 3, (c shr 8 and 0xFF) / 3, (c and 0xFF) / 3)

private val BLACK = rgb(0x000000)

/** The question before buying a world, and what the world costs: once, and the store's price when it has answered. */
/**
 * The bottom-right corner of the Worlds screen: "ID" and the last characters of the player's id for support, then the build
 * of the game, such as "ID cdef [41]". A tap copies the whole id.
 */
internal fun supportTag(id: String?, build: String?): String? =
    listOfNotNull(id?.let { "ID " + it.takeLast(4) }, build?.let { "[$it]" }).joinToString(" ").ifEmpty { null }

/** The title of the Worlds screen, and of its tab for the labors. */
internal const val WORLDS_TITLE = "Worlds"
internal const val LABORS_TITLE = "Labors"

/** The colors of each category of labors: its title and lines, and the band behind a labor that is done. */
private val CATEGORY_COLORS = mapOf(
    Category.HILLS to (rgb(0xF29A62) to rgb(0x4A2418)),
    Category.TURNS to (rgb(0xD2A462) to rgb(0x3E2C14)),
    Category.DAYS to (rgb(0xF2C14E) to rgb(0x3A3214)),
    Category.ANSWERS to (rgb(0x8FA8E0) to rgb(0x1A2240)),
)

/** At the bottom of the Agora, until everything is bought. */
internal const val AGORA_NOTE = "Buy all 15 for the top."

/** Before the note of the Agora, Sisyphus pushes his rock, as small as the letters. */
internal val NOTE_HERO = sprite(
    mapOf('k' to rgb(0xE0A070), 't' to rgb(0xF4ECDA), 'o' to rgb(0x9A948C), 's' to rgb(0x6E6A64)),
    """
    .....ooo.
    .kk.ooooo
    .tkkoooos
    .tt.oooos
    .kk..sss.
    k..k.....
    k...k....
    """.trimIndent(),
)

/** The note of the help that points at the day. */
internal const val DAY_NOTE = "The sun moves only while you push. Tap the day for worlds and labors."

/** The title of the Agora. */
internal const val AGORA_TITLE = "Agora Market"

/** After the title of the Agora, how many of its items are bought, such as "3/15". */
internal fun itemsBought(shop: Shop) = "${shop.bought}/${items(shop.place).size}"

internal fun buyTitle(place: Place) = "Buy ${place.title}?"

internal fun buyText(price: String?) = "You pay ${if (price == null) "" else "$price "}once, and the world is yours forever."

/** The button on the Worlds screen that asks the store for the worlds bought before. */
internal const val RESTORE = "Restore"

/** What going to another world costs, in its question. */
internal const val MOVE_WARNING = "The climb starts over there: day 1, no items and no obols. Your climbs stay."

/** The last words of The Myth of Sisyphus, on the black screen after the top. */
private const val SUMMIT_WORDS = "The struggle itself toward the heights is enough to fill a man's heart. One must imagine Sisyphus happy."

private val LINE_TITLES = listOf("Rock", "Sidekicks", "Gods", "Blessings", "Beauty")

/** Draws everything around the display: the frame, the ornament, the wise words, the knob and the Agora. */
class UiRenderer {
    private var grain: Pixels? = null
    private val outlined = mutableMapOf<Item, Pixels>()
    private val previews = mutableMapOf<Pair<Place, Int>, Pixels>()
    private var worldSheet: Pixels? = null
    private var laborSheet: Pixels? = null

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

    /** The wise words. While a god asks in the panel, it is the [voice]: a thin line frames the panel, and the god's name is lighter. */
    fun quote(px: Pixels, r: Rect, box: QuoteBox, clock: Double, voice: Boolean = false) {
        if (voice) frame(px, Rect(r.x - 2, r.y - 1, r.w + 4, r.h + 2), VOICE_FRAME)
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
            PixelFont.draw(px, author.take(2 + shown), x, y + 2, if (voice) TERRA_LIGHT else TERRA, box.fade)
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

    /** The obols: a coin and how many there are, outlined like the day across from them. Without [obols], only the coin. */
    fun coins(px: Pixels, r: Rect, obols: Int?) {
        val x = coinOf(r, obols)
        if (obols != null) PixelFont.drawOutlined(px, min(obols, 9999).toString(), x + Layout.COIN_SIZE + 2, r.y, CREAM, SHADOW)
        coin(px, x, r.y, COIN_COLORS, outline = true)
    }

    /** Where [coins] draws the coin: left of the count, which ends at the right of [r], or at the right itself without a count. */
    private fun coinOf(r: Rect, obols: Int?) =
        r.right - Layout.COIN_SIZE - (obols?.let { PixelFont.width(min(it, 9999).toString()) + 2 } ?: 0)

    /**
     * New obols, [age] seconds after they came: the coin of the count glints, and their number, such as "+1", rises under the count
     * and fades. [count] is the count that the coin is next to.
     */
    fun gain(px: Pixels, r: Rect, obols: Int, age: Double, count: Int) {
        val coinX = coinOf(r, count)
        if (age < COIN_GLINT) {
            val arm = if (age < COIN_GLINT / 2) 2 else 1
            for (d in -arm..arm) {
                px[coinX + 5 + d, r.y + 1] = GLINT
                px[coinX + 5, r.y + 1 + d] = GLINT
            }
        }
        val text = "+$obols"
        val y = r.bottom + 2 + GAIN_RISE - (age / GAIN_TIME * GAIN_RISE).roundToInt()
        val fade = ((age / GAIN_TIME - 0.5) * 2).coerceIn(0.0, 1.0).toFloat()
        PixelFont.drawOutlined(px, text, r.right - PixelFont.width(text), y, GAIN, SHADOW, fade)
    }

    /**
     * The bar between the day and the obols: tiles of the Greek key from the band, outlined in dark. The key turns bronze
     * as the next obol comes, and goes dark again when the obol is there. During the final climb, it shows the way to the top
     * instead: it turns cream as far as he has [climbed].
     */
    fun obolBar(px: Pixels, r: Rect, obols: Double, climbed: Double? = null) {
        val filled = ((climbed ?: (obols % 1.0)) * r.w).toInt()
        val fill = if (climbed != null) CLIMB_FILL else OBOL_FILL
        fun stroke(x: Int, y: Int) = x in 0 until r.w && y in MEANDER.indices && MEANDER[y][x % MEANDER[0].length] == '#'
        for (y in MEANDER.indices) for (x in 0 until r.w) {
            if (!stroke(x, y)) continue
            for ((ox, oy) in listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)) if (!stroke(x + ox, y + oy)) px[r.x + x + ox, r.y + y + oy] = SHADOW
        }
        for (y in MEANDER.indices) for (x in 0 until r.w) {
            if (!stroke(x, y)) continue
            px[r.x + x, r.y + y] = if (x < filled) fill else OBOL_EMPTY
        }
    }

    /** A helper bought in the Agora, with a dark outline, so it shows on any ground. */
    fun helper(px: Pixels, r: Rect, item: Item) = px.draw(outlined.getOrPut(item) { itemIcon(item).outlined(SHADOW) }, r.x, r.y)

    /** The [tag] of the player's id and the build, at the right of [r], or "Copied" just after a tap copied the id. */
    fun supportId(px: Pixels, r: Rect, tag: String, copied: Boolean) {
        val text = if (copied) "Copied" else tag
        PixelFont.draw(px, text, r.right - PixelFont.width(text), r.y, if (copied) TERRA_LIGHT else DISABLED)
    }

    fun helpButton(px: Pixels, r: Rect) = PixelFont.draw(px, "(?)", r.x, r.y, TERRA)

    /** The help, over the main screen: all dims but the day, the obol bar, the obols and the knob, and a note points at each. */
    fun help(px: Pixels, layout: Layout, day: Int, obols: Double, climbed: Double? = null) {
        val kx = layout.knobX + 0.5
        val ky = layout.knobY + 0.5
        val kr = layout.knobR + 6.0
        for (y in 0 until px.height) for (x in 0 until px.width) {
            if ((x - kx) * (x - kx) + (y - ky) * (y - ky) > kr * kr) px[x, y] = dim(px[x, y])
        }
        val dayLabel = layout.dayLabel(day)
        val coins = layout.coinIcon
        val bar = layout.obolBar
        PixelFont.drawOutlined(px, "DAY $day", dayLabel.x + 1, dayLabel.y + 1, CREAM, SHADOW)
        val shown = if (climbed != null) null else obols.toInt()
        obolBar(px, bar, obols, climbed)
        coins(px, coins, shown)
        helpButton(px, layout.helpIcon)

        // Each arrow runs down to its note, past the notes above it: the bar's note goes first, then the day's, then the obols'.
        val obolsArrow = coinOf(coins, shown) + 3
        val barNote = PixelFont.wrap(barNote(climbed != null), layout.scene.w * 3 / 5)
        val barY = bar.bottom + 9
        for ((i, line) in barNote.withIndex()) {
            PixelFont.drawOutlined(px, line, bar.x + bar.w / 2 - PixelFont.width(line) / 2, barY + i * PixelFont.LINE_HEIGHT, CREAM, OUTLINE)
        }
        arrowUp(px, bar.x + bar.w / 2, bar.bottom + 1, barY - 3)

        val dayNote = PixelFont.wrap(DAY_NOTE, obolsArrow - 4 - (dayLabel.x + 1))
        val dayY = barY + barNote.size * PixelFont.LINE_HEIGHT + 8
        note(px, dayNote, dayLabel.x + 1, dayY, right = false)
        arrowUp(px, dayLabel.x + 6, dayLabel.bottom + 1, dayY - 3)

        val obolsNote = PixelFont.wrap(obolsNote(climbed != null), layout.scene.w - 8)
        val obolsY = dayY + dayNote.size * PixelFont.LINE_HEIGHT + 8
        note(px, obolsNote, coins.right, obolsY, right = true)
        arrowUp(px, obolsArrow, coins.bottom + 2, obolsY - 3)

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
    fun agora(px: Pixels, layout: Layout, shop: Shop, soundOn: Boolean, dev: Boolean = false, openWorlds: Boolean = false) {
        val t = layout.agoraTitle
        PixelFont.draw(px, if (dev) "Dev" else AGORA_TITLE, t.x, t.y, CREAM)
        if (!dev) PixelFont.draw(px, itemsBought(shop), layout.agoraCount.x, layout.agoraCount.y, TERRA)
        px.hline(t.x, t.right - 1, t.bottom + 2, TERRA_DARK)
        if (dev) devScreen(px, layout, openWorlds) else for (line in Line.entries) row(px, layout.agoraRow(line.ordinal), line, shop)
        if (!dev && !shop.allBought) {
            px.draw(NOTE_HERO, layout.agoraNote.x, layout.agoraNote.y)
            PixelFont.draw(px, AGORA_NOTE, layout.agoraNote.x + NOTE_HERO.width + 3, layout.agoraNote.y, NOT_DONE)
        }
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

    /** The end of a climb: the screen goes black, Camus' words and the climbs come, then the call to begin again. */
    fun ending(px: Pixels, layout: Layout, ending: Ending, climbs: Int) {
        val dark = ending.darkness
        if (dark > 0f) for (y in 0 until px.height) for (x in 0 until px.width) if (bayer(x, y) < dark) px[x, y] = BLACK
        if (ending.words <= 0f) return
        val width = layout.quote.w
        val lines = PixelFont.wrap(SUMMIT_WORDS, width) + "— Albert Camus" + "" + PixelFont.wrap("Sisyphus reached the top " + if (climbs == 1) "once." else "$climbs times.", width)
        val top = (px.height - lines.size * PixelFont.LINE_HEIGHT) / 2
        for ((i, line) in lines.withIndex()) {
            val color = if (line.startsWith("—")) TERRA else CREAM
            PixelFont.draw(px, line, (px.width - PixelFont.width(line)) / 2, top + i * PixelFont.LINE_HEIGHT, color, 1f - ending.words)
        }
        if (ending.waiting) {
            val again = "Tap to begin again"
            PixelFont.draw(px, again, (px.width - PixelFont.width(again)) / 2, layout.knobY, TERRA)
        }
    }

    /**
     * The Worlds screen: a picture of each world with its name, the world he is in framed bright and checked,
     * and the worlds not bought yet locked, with their price.
     * The list is drawn on a sheet of its own, so that it scrolls by [scroll] and its edges cut clean.
     */
    fun worlds(px: Pixels, layout: Layout, here: Place, scroll: Int, billing: Billing) {
        val list = layout.worldList
        val sheet = worldSheet?.takeIf { it.width == list.w && it.height == list.h } ?: Pixels(list.w, list.h).also { worldSheet = it }
        sheet.clear(TRANSPARENT)
        for (place in Place.entries) {
            val p = layout.worldPreview(place.ordinal).offset(-list.x, -list.y - scroll)
            sheet.draw(previews.getOrPut(place to p.w) { worldPreview(place, p.w - 2, p.h - 2) }, p.x + 1, p.y + 1)
            frame(sheet, p, if (place == here) TERRA_LIGHT else TERRA_DARK)
            val text = layout.worldText(place.ordinal).offset(-list.x, -list.y - scroll)
            PixelFont.draw(sheet, place.title, text.x, text.y, CREAM)
            PixelFont.draw(sheet, place.note, text.x, text.y + PixelFont.LINE_HEIGHT, TERRA_LIGHT)
            if (place == here) {
                check(sheet, text.right - Layout.COIN_SIZE, text.y + 1)
            } else if (!billing.owns(place)) {
                val lock = text.right - Layout.COIN_SIZE
                for ((dy, row) in LOCK.withIndex()) for ((dx, ch) in row.withIndex()) if (ch == '#') sheet[lock + dx, text.y + dy] = TERRA_LIGHT
                billing.price(place)?.let { PixelFont.draw(sheet, it, lock - 2 - PixelFont.width(it), text.y, TERRA_LIGHT) }
            }
        }
        px.draw(sheet, list.x, list.y)
        scrollbar(px, list, scroll, layout.worldScroll(Place.entries.size))
    }

    /** The title row of the Worlds screen: the tab shown in cream, the other in gray, and the dot after "Labors" while a labor is [new]. */
    fun tabs(px: Pixels, layout: Layout, labors: Boolean, new: Boolean) {
        PixelFont.draw(px, WORLDS_TITLE, layout.worldsTab.x, layout.worldsTab.y, if (labors) DISABLED else CREAM)
        PixelFont.draw(px, LABORS_TITLE, layout.laborsTab.x, layout.laborsTab.y, if (labors) CREAM else DISABLED)
        if (new) px.fillRect(layout.laborsTab.x + PixelFont.width(LABORS_TITLE) + 2, layout.laborsTab.y + PixelFont.CAP_HEIGHT / 2 - 1, 2, 2, BRONZE[4])
        val t = layout.agoraTitle
        px.hline(t.x, t.right - 1, t.bottom + 2, TERRA_DARK)
    }

    /**
     * The labors: under the header of each category, one card with its next labor and how far it has come, a bar,
     * a dot and the goal of each tier, bright when done, and the last labor done.
     * The list is drawn on a sheet of its own, so that it scrolls by [scroll].
     */
    fun labors(px: Pixels, layout: Layout, counts: Counts, scroll: Int) {
        val list = layout.worldList
        val sheet = laborSheet?.takeIf { it.width == list.w && it.height == list.h } ?: Pixels(list.w, list.h).also { laborSheet = it }
        sheet.clear(TRANSPARENT)
        for (category in Category.entries) {
            val (color, band) = CATEGORY_COLORS.getValue(category)
            val labors = LABORS.filter { it.category == category }
            val count = counts.of(category)
            val h = layout.laborHeaders[category.ordinal].offset(-list.x, -list.y - scroll)
            val done = "${labors.count { it.done(counts) }}/${labors.size}"
            PixelFont.draw(sheet, category.title, h.x, h.y, color)
            PixelFont.draw(sheet, done, h.right - PixelFont.width(done), h.y, color)
            val r = layout.laborCards[category.ordinal].offset(-list.x, -list.y - scroll)
            sheet.fillRect(r.x, r.y, r.w, r.h, CARD)
            frame(sheet, r, NEXT_EDGE)
            val x = r.x + Layout.LABOR_PAD
            val w = r.w - 2 * Layout.LABOR_PAD
            val next = labors.firstOrNull { !it.done(counts) }
            PixelFont.draw(sheet, (next ?: labors.last()).name, x, r.y + 3, CREAM)
            if (next != null) progress(count, next.goal).let { PixelFont.draw(sheet, it, r.right - Layout.LABOR_PAD - PixelFont.width(it), r.y + 3, color) }
            val fill = if (next == null) w else (w * (count / next.goal).coerceIn(0.0, 1.0)).toInt()
            sheet.fillRect(x, r.y + Layout.LABOR_BAR, w, 3, band)
            sheet.fillRect(x, r.y + Layout.LABOR_BAR, fill, 3, color)
            val column = w / labors.size
            for ((i, labor) in labors.withIndex()) {
                val reached = labor.done(counts)
                val cx = x + column * i + column / 2
                tierDot(sheet, cx - 2, r.y + Layout.LABOR_DOTS, reached, color)
                val goal = shortCount(labor.goal)
                PixelFont.draw(sheet, goal, cx - PixelFont.width(goal) / 2, r.y + Layout.LABOR_TIERS, if (reached) color else NOT_DONE)
            }
            labors.lastOrNull { it.done(counts) }?.let { last ->
                check(sheet, x, r.y + Layout.LABOR_DONE + 1, color)
                PixelFont.draw(sheet, last.name, x + CHECK[0].length + 2, r.y + Layout.LABOR_DONE, color)
            }
        }
        px.draw(sheet, list.x, list.y)
        scrollbar(px, list, scroll, layout.laborScroll)
    }

    /** The dot of a tier, five pixels wide: filled when the tier is [reached], else a ring. */
    private fun tierDot(px: Pixels, x: Int, y: Int, reached: Boolean, color: Int) =
        mark(px, x, y, if (reached) DOT_FULL else DOT_RING, if (reached) color else NOT_DONE)

    /** At the right of a [list] that can scroll by [most], the thumb shows where it is. */
    private fun scrollbar(px: Pixels, list: Rect, scroll: Int, most: Int) {
        if (most <= 0) return
        val x = list.right + 3
        val thumb = list.h * list.h / (list.h + most)
        val top = list.y + (list.h - thumb) * scroll / most
        px.vline(x, list.y, list.bottom - 1, TERRA_DARK)
        px.vline(x, top, top + thumb - 1, TERRA_LIGHT)
    }

    /** Asks before going to another world, since everything starts from the beginning there. */
    fun confirm(px: Pixels, layout: Layout, place: Place) = question(px, layout, "Go to ${place.title}?", MOVE_WARNING, "Yes")

    /** Asks before buying a world. While the store's payment sheet is open, Yes waits. */
    fun buy(px: Pixels, layout: Layout, place: Place, price: String?, paying: Boolean) =
        question(px, layout, buyTitle(place), buyText(price), if (paying) "..." else "Yes")

    /** Below the worlds, the button that asks the store for the worlds bought before, and the wait for its answer. */
    fun restore(px: Pixels, r: Rect, restoring: Boolean) = PixelFont.draw(px, if (restoring) "Restoring..." else RESTORE, r.x, r.y, TERRA)

    /** A question in a window over the Worlds screen: its [title], what it means, and [yes] and No. */
    private fun question(px: Pixels, layout: Layout, title: String, text: String, yes: String) {
        for (y in 0 until px.height) for (x in 0 until px.width) px[x, y] = dim(px[x, y])
        val r = layout.confirm
        px.fillRect(r.x, r.y, r.w, r.h, BACKGROUND)
        frame(px, r, TERRA)
        PixelFont.draw(px, title, r.x + (r.w - PixelFont.width(title)) / 2, r.y + 5, CREAM)
        val room = layout.confirmText
        for ((i, line) in PixelFont.wrap(text, room.w).withIndex()) {
            PixelFont.draw(px, line, room.x, room.y + i * PixelFont.LINE_HEIGHT, TERRA_LIGHT)
        }
        button(px, layout.confirmYes, yes)
        button(px, layout.confirmNo, "No")
    }

    /**
     * In dev builds only: buttons that add or take away obols, reset the shop, start the climb again,
     * take him near the next obol or near the top, set the next stone of the tale just ahead, and open every world.
     */
    private fun devScreen(px: Pixels, layout: Layout, openWorlds: Boolean) {
        val obols = layout.agoraRow(0)
        PixelFont.draw(px, "Obols", obols.x, obols.y, TERRA)
        DEV_STEPS.forEachIndexed { i, step -> button(px, layout.devStep(i), if (step > 0) "+$step" else "$step") }
        val shop = layout.agoraRow(1)
        PixelFont.draw(px, "Shop", shop.x, shop.y, TERRA)
        button(px, layout.devResetShop, "Reset")
        val climb = layout.agoraRow(2)
        PixelFont.draw(px, "Climb", climb.x, climb.y, TERRA)
        button(px, layout.devResetClimb, "Reset")
        button(px, layout.devNextObol, "Obol")
        button(px, layout.devTop, "Top")
        button(px, layout.devStone, "Stone")
        val worlds = layout.agoraRow(3)
        PixelFont.draw(px, "Worlds", worlds.x, worlds.y, TERRA)
        button(px, layout.devWorlds, if (openWorlds) "As bought" else "Open all")
        val voices = layout.agoraRow(4)
        PixelFont.draw(px, "Voices and labors", voices.x, voices.y, TERRA)
        button(px, layout.devVision, "Now")
        button(px, layout.devNextVision, "Next turn")
        button(px, layout.devNextLabor, "Labor")
    }

    private fun button(px: Pixels, r: Rect, label: String) {
        frame(px, r, TERRA)
        PixelFont.draw(px, label, r.x + (r.w - PixelFont.width(label) + 1) / 2, r.y + 3, CREAM)
    }

    private fun row(px: Pixels, r: Rect, line: Line, shop: Shop) {
        val next = shop.next(line)
        val item = next ?: shop.owned(line) ?: return
        val bright = next == null || shop.obols >= next.price
        val title = LINE_TITLES[line.ordinal]
        PixelFont.draw(px, title, r.x, r.y, TERRA)
        dots(px, r.x + PixelFont.width(title) + 4, r.y + PixelFont.CAP_HEIGHT - DOT, shop.tier(line))
        val y = r.y + PixelFont.LINE_HEIGHT
        PixelFont.draw(px, item.name, r.x, y, CREAM)
        if (next != null) {
            val price = next.price.toString()
            coin(px, r.right - Layout.COIN_SIZE, y, if (bright) COIN_COLORS else COIN_GRAY)
            PixelFont.draw(px, price, r.right - Layout.COIN_SIZE - 2 - PixelFont.width(price), y, if (bright) TERRA_LIGHT else DISABLED)
        } else {
            check(px, r.right - Layout.COIN_SIZE, y + 1)
        }
        PixelFont.draw(px, item.note, r.x, y + PixelFont.LINE_HEIGHT, TERRA_LIGHT)
    }

    /** A dot for each item of a line, from [x], as for the tiers of the labors: filled for the [bought] ones, a bright ring for the one on offer, dim rings for the ones after it. */
    private fun dots(px: Pixels, x: Int, y: Int, bought: Int) {
        for (k in 0 until 3) {
            val dx = x + k * (DOT + 2)
            when {
                k < bought -> mark(px, dx, y, DOT_FULL, DOT_BOUGHT)
                k == bought -> mark(px, dx, y, DOT_RING, DOT_OFFERED)
                else -> mark(px, dx, y, DOT_RING, DOT_LATER)
            }
        }
    }

    private fun mark(px: Pixels, x: Int, y: Int, art: List<String>, color: Int) {
        for ((dy, row) in art.withIndex()) for ((dx, ch) in row.withIndex()) if (ch == '#') px[x + dx, y + dy] = color
    }

    private fun check(px: Pixels, x: Int, y: Int, color: Int = TERRA_LIGHT) {
        for ((dy, row) in CHECK.withIndex()) for ((dx, ch) in row.withIndex()) if (ch == '#') px[x + dx, y + dy] = color
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
        if (game.vision?.phase == Vision.Phase.ASKING) {
            arrow(px, kx, ky, r + 8.0, -PI / 2 - 0.95, -PI / 2 - 1.55, TERRA_LIGHT)
            arrow(px, kx, ky, r + 8.0, -PI / 2 + 0.95, -PI / 2 + 1.55, TERRA_LIGHT)
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

    /** An arc around the knob from the angle [from] to [to], with its head at [to], pointing the way the angle goes. */
    private fun arrow(px: Pixels, kx: Double, ky: Double, r: Double, from: Double, to: Double, color: Int = CREAM) {
        val steps = 18
        for (k in 0..steps) {
            val a = from + (to - from) * k / steps
            dot(px, kx + cos(a) * r, ky + sin(a) * r, color)
        }
        val way = if (to >= from) 1.0 else -1.0
        val ex = kx + cos(to) * r
        val ey = ky + sin(to) * r
        val tx = -sin(to) * way
        val ty = cos(to) * way
        val nx = cos(to)
        val ny = sin(to)
        for (k in 1..2) {
            dot(px, ex - tx * k + nx * k, ey - ty * k + ny * k, color)
            dot(px, ex - tx * k - nx * k, ey - ty * k - ny * k, color)
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
