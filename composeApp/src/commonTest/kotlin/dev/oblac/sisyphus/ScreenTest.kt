package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.CLIMB
import dev.oblac.sisyphus.game.Config
import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.FIRST_OBOL
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.JOKERS
import dev.oblac.sisyphus.game.rare
import dev.oblac.sisyphus.game.God
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.PX_PER_TURN
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.SISYPHUS
import dev.oblac.sisyphus.game.Question
import dev.oblac.sisyphus.game.Shop
import dev.oblac.sisyphus.game.START_TIME
import dev.oblac.sisyphus.game.Vision
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.game.items
import dev.oblac.sisyphus.game.LABORS
import dev.oblac.sisyphus.game.shortCount
import dev.oblac.sisyphus.game.Category
import dev.oblac.sisyphus.game.progress
import dev.oblac.sisyphus.game.SAYINGS
import dev.oblac.sisyphus.game.whoSaid
import dev.oblac.sisyphus.gfx.PixelFont
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.ui.AGORA_NOTE
import dev.oblac.sisyphus.ui.BACKGROUND
import dev.oblac.sisyphus.ui.COIN_COLORS
import dev.oblac.sisyphus.ui.JOKERS_TITLE
import dev.oblac.sisyphus.ui.NOTE_HERO
import dev.oblac.sisyphus.ui.DAY_NOTE
import dev.oblac.sisyphus.gfx.TRANSPARENT
import dev.oblac.sisyphus.gfx.luminance
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.game.godsOf
import dev.oblac.sisyphus.scene.ANSWER
import dev.oblac.sisyphus.scene.Ending
import dev.oblac.sisyphus.scene.GOD_GLOW
import dev.oblac.sisyphus.scene.SPARKLE
import dev.oblac.sisyphus.scene.godMiddle
import dev.oblac.sisyphus.scene.HERM
import dev.oblac.sisyphus.scene.JOKER_SPRITES
import dev.oblac.sisyphus.scene.SceneGeometry
import dev.oblac.sisyphus.scene.SceneRenderer
import dev.oblac.sisyphus.scene.World
import dev.oblac.sisyphus.scene.drawVision
import dev.oblac.sisyphus.scene.lookOf
import dev.oblac.sisyphus.ui.CLIMB_FILL
import dev.oblac.sisyphus.ui.GAIN
import dev.oblac.sisyphus.ui.GAIN_TIME
import dev.oblac.sisyphus.ui.GLINT
import dev.oblac.sisyphus.ui.Layout
import dev.oblac.sisyphus.ui.OBOL_EMPTY
import dev.oblac.sisyphus.ui.OBOL_FILL
import dev.oblac.sisyphus.ui.Rect
import dev.oblac.sisyphus.ui.DOT_BOUGHT
import dev.oblac.sisyphus.ui.DOT_LATER
import dev.oblac.sisyphus.ui.DOT_OFFERED
import dev.oblac.sisyphus.ui.VOICE_FRAME
import dev.oblac.sisyphus.ui.WORLDS_TITLE
import dev.oblac.sisyphus.ui.barNote
import dev.oblac.sisyphus.ui.buyText
import dev.oblac.sisyphus.ui.buyTitle
import dev.oblac.sisyphus.ui.itemIcon
import dev.oblac.sisyphus.ui.itemsBought
import dev.oblac.sisyphus.ui.obolsNote
import dev.oblac.sisyphus.ui.supportTag
import dev.oblac.sisyphus.ui.worldPreview
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ScreenTest {
    private fun screen(store: Store = MemoryStore()) =
        Screen(Game(store, seed = 1), seed = 2).apply { resize(1179, 2556, 177, 102) }

    /** The narrowest phone: 135 virtual pixels wide. */
    private fun smallest() = Screen(Game(MemoryStore(), seed = 1), seed = 2).apply { resize(1080, 2340, 144, 102) }

    private fun Screen.tap(x: Float, y: Float) = press(x * scale + offsetX, y * scale + offsetY)

    private fun Screen.tap(r: Rect) = tap(r.x + r.w / 2f, r.y + r.h / 2f)

    private fun Screen.drag(x: Float, y: Float) = move(x * scale + offsetX, y * scale + offsetY)

    /** A tap on the list of worlds: the finger lands and lifts at once. */
    private fun Screen.choose(r: Rect): Touch {
        assertEquals(Touch.LIST, tap(r))
        return release()
    }

    @Test
    fun theKnobIsGrabbedAroundItsCenter() {
        val s = screen()
        val l = s.layout!!
        assertEquals(Touch.KNOB, s.tap(l.knobX + l.knobR * 0.6f, l.knobY + 0.5f))
        assertTrue(s.game.knob.held)
    }

    @Test
    fun aFingerCirclingEitherWayOnlyClimbs() {
        val s = screen()
        val l = s.layout!!
        val r = l.knobR * 0.7f
        var a = 0.0
        s.tap(l.knobX + 0.5f + r, l.knobY + 0.5f)
        var last = s.game.position
        repeat(240) { frame ->
            a += if (frame < 120) -0.1 else 0.1
            val x = l.knobX + 0.5f + r * cos(a).toFloat()
            val y = l.knobY + 0.5f + r * sin(a).toFloat()
            s.move(x * s.scale + s.offsetX, y * s.scale + s.offsetY)
            s.update(1 / 60.0)
            assertTrue(s.game.position >= last, "walked down at frame $frame")
            last = s.game.position
        }
        s.release()
        assertTrue(s.game.position > 3 * PX_PER_TURN)
    }

    @Test
    fun theDisplayIgnoresTouches() {
        val s = screen()
        assertEquals(Touch.NONE, s.tap(s.layout!!.scene))
        assertFalse(s.game.knob.held)
    }

    @Test
    fun theCoinOpensAndClosesTheAgora() {
        val s = screen()
        val l = s.layout!!
        assertEquals(Touch.SHOP, s.tap(l.coinIcon))
        assertTrue(s.agoraOpen)
        assertEquals(Touch.SHOP, s.tap(l.coinIcon))
        assertFalse(s.agoraOpen)
    }

    @Test
    fun theObolsSitAcrossFromTheDay() {
        val l = screen().layout!!
        val c = l.coinIcon
        assertTrue(c.x > l.scene.x + l.scene.w / 2 && c.right <= l.scene.right, "in the right half of the display")
        assertTrue(c.y >= l.scene.y && c.y <= l.scene.y + 6, "at the top, level with the day")
    }

    @Test
    fun aRowYouCannotAffordDoesNothing() {
        val s = screen(MemoryStore(mapOf("obols" to 1.0)))
        val l = s.layout!!
        s.tap(l.coinIcon)
        assertEquals(Touch.NONE, s.tap(l.agoraRow(Line.ROCK.ordinal)))
        assertEquals(0, s.game.shop.tier(Line.ROCK))
        assertEquals(1.0, s.game.shop.obols, 1e-9)
    }

    @Test
    fun onlyThePriceShowsWhatYouCannotAfford() {
        /** The pixels of the rock row: its price, and the rest. */
        fun rockRow(obols: Double): Map<String, List<Int>> {
            val s = screen(MemoryStore(mapOf("obols" to obols, "shop.rock" to 1.0)))
            val l = s.layout!!
            s.tap(l.coinIcon)
            val px = s.render()
            val r = l.agoraRow(Line.ROCK.ordinal)
            val nameY = r.y + PixelFont.LINE_HEIGHT
            val noteY = nameY + PixelFont.LINE_HEIGHT
            return (r.y until r.bottom).flatMap { y -> (r.x until r.right).map { x -> x to y } }.groupBy({ (x, y) ->
                if (x >= r.right - 20 && y in nameY until noteY) "price" else "rest"
            }, { (x, y) -> px[x, y] })
        }
        val poor = rockRow(0.0)
        val rich = rockRow(100.0)
        assertEquals(rich["rest"], poor["rest"])
        assertNotEquals(rich["price"], poor["price"])
    }

    /** After the name of a line, a dot for each of its items, from the left: the ones bought, the one on offer, and the ones after it. */
    @Test
    fun dotsAfterTheLineNameShowWhatIsBoughtOnOfferAndLater() {
        fun squares(tier: Int): List<String> {
            val s = screen(MemoryStore(mapOf("shop.rock" to tier.toDouble())))
            val l = s.layout!!
            s.tap(l.coinIcon)
            val px = s.render()
            val r = l.agoraRow(Line.ROCK.ordinal)
            val looks = mapOf(DOT_BOUGHT to "bought", DOT_OFFERED to "offered", DOT_LATER to "later")
            val columns = (r.x + PixelFont.width("Rock") until r.right).map { x ->
                (r.y until r.y + PixelFont.CAP_HEIGHT).firstNotNullOfOrNull { looks[px[x, it]] }
            }
            return columns.filterIndexed { i, look -> look != null && look != columns.getOrNull(i - 1) }.filterNotNull()
        }
        assertEquals(listOf("offered", "later", "later"), squares(0))
        assertEquals(listOf("bought", "offered", "later"), squares(1))
        assertEquals(listOf("bought", "bought", "bought"), squares(3))
    }

    /** The marks after the name of a line are dots, as the tiers of the labors are: round, with empty corners. */
    @Test
    fun theAgoraMarksItsItemsWithDots() {
        val s = screen(MemoryStore(mapOf("shop.rock" to 1.0)))
        val l = s.layout!!
        s.tap(l.coinIcon)
        val px = s.render()
        val r = l.agoraRow(Line.ROCK.ordinal)
        val x = r.x + PixelFont.width("Rock") + 4
        val y = r.y + PixelFont.CAP_HEIGHT - 5
        assertEquals(DOT_BOUGHT, px[x + 2, y])
        assertEquals(DOT_BOUGHT, px[x + 2, y + 2])
        assertNotEquals(DOT_BOUGHT, px[x, y])
    }

    /** The title of the Agora counts the items bought, so that the 15 read as one goal. The dev screen has no count. */
    @Test
    fun theAgoraTitleCountsTheItemsBought() {
        assertEquals("3/15", itemsBought(Shop(mapOf(Line.ROCK to 2, Line.GODS to 1))))
        fun count(store: Map<String, Double>, dev: Boolean = false): List<Int> {
            val s = Screen(Game(MemoryStore(store), seed = 1), seed = 2, dev = dev).apply { resize(1179, 2556, 177, 102) }
            val l = s.layout!!
            s.tap(l.coinIcon)
            if (dev) s.tap(l.agoraTitle)
            val px = s.render()
            val r = l.agoraCount
            return (r.y until r.bottom).flatMap { y -> (r.x until r.right).map { px[it, y] } }
        }
        val bought = mapOf("shop.rock" to 2.0, "shop.alone" to 1.0)
        assertNotEquals(count(emptyMap()), count(bought))
        assertEquals(count(emptyMap(), dev = true), count(bought, dev = true))
    }

    @Test
    fun theCountOfTheAgoraFitsBeforeTheObolsOnTheSmallestPhone() {
        val l = smallest().layout!!
        assertTrue(PixelFont.width("15/15") <= l.agoraCount.w)
        assertTrue(l.agoraCount.right + 2 <= l.coinIcon.x, "the count ends at ${l.agoraCount.right}, the obols start at ${l.coinIcon.x}")
    }

    @Test
    fun aFinishedLineShowsACheckWhereTheCoinWas() {
        val s = screen(MemoryStore(mapOf("shop.rock" to 3.0)))
        val l = s.layout!!
        s.tap(l.coinIcon)
        val px = s.render()
        val r = l.agoraRow(Line.ROCK.ordinal)
        val nameY = r.y + PixelFont.LINE_HEIGHT
        /** Bright pixels on the name line, from [from] to [to] pixels left of the row's right edge. */
        fun drawn(from: Int, to: Int) = (nameY until nameY + Layout.COIN_SIZE).sumOf { y ->
            (r.right - from until r.right - to).count { x -> (px[x, y] shr 8 and 0xFF) > 0x60 }
        }
        assertTrue(drawn(Layout.COIN_SIZE, 0) > 5)
        assertEquals(0, drawn(20, Layout.COIN_SIZE))
    }

    private fun devScreen(store: Store = MemoryStore()) =
        Screen(Game(store, seed = 1), seed = 2, dev = true).apply { resize(1179, 2556, 177, 102) }

    /** Opens the Agora, then the dev screen in it. */
    private fun Screen.openDev() {
        tap(layout!!.coinIcon)
        tap(layout!!.agoraTitle)
    }

    @Test
    fun onlyADevBuildOpensTheDevScreen() {
        val plain = screen()
        plain.tap(plain.layout!!.coinIcon)
        assertEquals(Touch.NONE, plain.tap(plain.layout!!.agoraTitle))
        assertFalse(plain.devOpen)

        val dev = devScreen()
        dev.tap(dev.layout!!.coinIcon)
        assertEquals(Touch.DEV, dev.tap(dev.layout!!.agoraTitle))
        assertTrue(dev.devOpen)
    }

    @Test
    fun theDevScreenSetsTheObols() {
        val store = MemoryStore()
        val s = devScreen(store)
        val l = s.layout!!
        s.openDev()
        for (button in listOf(4, 3, 3, 1)) assertEquals(Touch.DEV, s.tap(l.devStep(button)))
        assertEquals(119.0, s.game.shop.obols, 1e-9)
        assertEquals(119.0, store.load("obols"))
        repeat(13) { s.tap(l.devStep(0)) }
        assertEquals(0.0, s.game.shop.obols)
    }

    @Test
    fun theDevScreenResetsTheShop() {
        val store = MemoryStore(mapOf("shop.rock" to 3.0, "shop.alone" to 2.0))
        val s = devScreen(store)
        s.openDev()
        assertEquals(Touch.DEV, s.tap(s.layout!!.devResetShop))
        assertTrue(Line.entries.all { s.game.shop.tier(it) == 0 })
        assertEquals(0.0, store.load("shop.rock"))
    }

    @Test
    fun closingTheAgoraLeavesTheDevScreen() {
        val s = devScreen()
        s.openDev()
        s.tap(s.layout!!.coinIcon)
        s.tap(s.layout!!.coinIcon)
        assertTrue(s.agoraOpen)
        assertFalse(s.devOpen)
        s.tap(s.layout!!.agoraTitle)
        assertTrue(s.back())
        assertFalse(s.devOpen)
    }

    @Test
    fun theDevButtonsFitTheSmallestPhone() {
        val l = smallest().layout!!
        assertTrue(PixelFont.width("+100") + 4 <= l.devStep(0).w)
        assertTrue(l.devStep(4).right <= l.agoraRow(0).right)
        assertTrue(l.devStep(0).bottom <= l.agoraRow(0).bottom)
        assertTrue(l.devResetShop.bottom <= l.agoraRow(1).bottom)
        assertTrue(l.devResetClimb.right < l.devNextObol.x && l.devNextObol.right < l.devTop.x && l.devTop.right < l.devStone.x)
        assertTrue(l.devStone.right <= l.agoraRow(2).right, "the buttons end at ${l.devStone.right}, the row at ${l.agoraRow(2).right}")
        assertTrue(l.devStone.bottom <= l.agoraRow(2).bottom)
        assertTrue(l.devWorlds.bottom <= l.agoraRow(3).bottom)
    }

    /** Testers reach every world from the dev screen, without the store. A second tap gives the store's answer back. */
    @Test
    fun theDevScreenOpensEveryWorld() {
        val s = Screen(Game(MemoryStore(), seed = 1), seed = 2, dev = true, billing = FakeBilling()).apply { resize(1179, 2556, 177, 102) }
        val l = s.layout!!
        fun chooseEtna() {
            s.tap(l.coinIcon)
            s.tap(l.dayLabel(s.game.day))
            s.choose(l.worldRow(Place.ETNA.ordinal))
        }
        s.openDev()
        assertEquals(Touch.DEV, s.tap(l.devWorlds))
        assertTrue(s.openWorlds)
        chooseEtna()
        assertEquals(Place.ETNA, s.moving)
        assertNull(s.buying)
        s.back()
        s.back()
        s.openDev()
        assertEquals(Touch.DEV, s.tap(l.devWorlds))
        assertFalse(s.openWorlds)
        chooseEtna()
        assertEquals(Place.ETNA, s.buying)
    }

    @Test
    fun theHelpOpensFromTheCornerAndAnyTapClosesIt() {
        val s = screen()
        val l = s.layout!!
        assertEquals(Touch.HELP, s.tap(l.helpIcon))
        assertTrue(s.helpOpen)
        assertEquals(Touch.HELP, s.tap(l.knobX + l.knobR * 0.6f, l.knobY + 0.5f))
        assertFalse(s.helpOpen)
        assertFalse(s.game.knob.held)
    }

    @Test
    fun theHelpIsNotInTheAgora() {
        val s = screen()
        val l = s.layout!!
        s.tap(l.coinIcon)
        assertEquals(Touch.NONE, s.tap(l.helpIcon))
        assertFalse(s.helpOpen)
    }

    @Test
    fun backClosesTheHelp() {
        val s = screen()
        s.tap(s.layout!!.helpIcon)
        assertTrue(s.back())
        assertFalse(s.helpOpen)
        assertFalse(s.back())
    }

    @Test
    fun theHelpDimsAllButTheDayTheBarTheObolsAndTheKnob() {
        val s = screen(MemoryStore(mapOf("obols" to 12.5)))
        val l = s.layout!!
        fun shot() = s.render().let { px -> List(px.height) { y -> List(px.width) { x -> px[x, y] } } }
        val plain = shot()
        s.tap(l.helpIcon)
        val help = shot()
        fun pixels(shot: List<List<Int>>, r: Rect) = (r.y until r.bottom).flatMap { y -> (r.x until r.right).map { x -> shot[y][x] } }
        fun light(c: Int) = (c shr 16 and 0xFF) + (c shr 8 and 0xFF) + (c and 0xFF)
        for (text in listOf(l.dayLabel(s.game.day), l.obolBar, l.coinIcon)) {
            assertEquals(pixels(plain, text).maxOf(::light), pixels(help, text).maxOf(::light))
        }
        val knob = Rect(l.knobX - 8, l.knobY - 8, 16, 16)
        assertEquals(pixels(plain, knob), pixels(help, knob))
        assertTrue(pixels(help, l.band).sumOf(::light) < pixels(plain, l.band).sumOf(::light) / 2)
    }

    /** Turns the knob a little: more than the three clicks that the dev screen leaves before the next obol. */
    private fun Screen.turnALittle() {
        game.knob.grab(0.0)
        game.knob.drag(1.0)
        update(1 / 60.0)
    }

    /** Earns the next obol with a turn of the knob, from three clicks before it. */
    private fun Screen.earnAnObol() {
        game.nearTheNextObol()
        turnALittle()
    }

    @Test
    fun aNewObolGoesIntoTheCountAtOnceAndRings() {
        val s = screen(MemoryStore(mapOf("obols" to 2.5, "nextObol" to 300.0)))
        var rings = 0
        s.onCoin = { rings++ }
        s.earnAnObol()
        assertEquals(3, s.game.shop.obols.toInt())
        assertEquals(1, rings)
        assertEquals(1, s.gain?.obols)
        s.update(GAIN_TIME)
        assertNull(s.gain)
    }

    @Test
    fun obolsSpentOrAddedInTheDevScreenShowNoGain() {
        val s = screen(MemoryStore(mapOf("obols" to 5.0)))
        assertTrue(s.game.buy(Line.GODS))
        s.game.addObols(10)
        s.update(1 / 60.0)
        assertNull(s.gain)
    }

    /** The coin of the count glints, and "+1" rises under the count and fades. Nothing else moves. */
    @Test
    fun theCoinGlintsAndPlusOneRisesUnderTheCountAndFades() {
        val s = screen(MemoryStore(mapOf("obols" to 2.5, "nextObol" to 300.0)))
        val icon = s.layout!!.coinIcon
        val glint = icon.right - PixelFont.width("3") - 2 - Layout.COIN_SIZE + 5 to icon.y + 1
        fun plus(px: Pixels) = (icon.bottom until icon.bottom + 14).flatMap { y -> (icon.x until icon.right).filter { x -> px[x, y] == GAIN }.map { y } }
        s.game.nearTheNextObol()
        assertEquals(emptyList(), plus(s.render()))
        s.turnALittle()
        val first = s.render()
        assertEquals(GLINT, first[glint.first, glint.second])
        val start = plus(first)
        s.update(GAIN_TIME * 0.4)
        assertTrue(plus(s.render()).min() < start.min())
        s.update(GAIN_TIME * 0.45)
        val late = s.render()
        assertTrue(plus(late).size < start.size / 2, "${plus(late).size} of ${start.size}")
        assertNotEquals(GLINT, late[glint.first, glint.second])
        s.update(GAIN_TIME * 0.2)
        assertEquals(emptyList(), plus(s.render()))
    }

    /** The Greek key of the bar is bronze as far as the next obol has come, and dark after that: 1 is a bronze column, 0 a dark one. */
    @Test
    fun theKeyTurnsBronzeAsFarAsTheNextObolHasCome() {
        fun columns(obols: Double): List<Int> {
            val s = screen(MemoryStore(mapOf("obols" to obols)))
            val px = s.render()
            val bar = s.layout!!.obolBar
            return (bar.x until bar.right).map { x ->
                val colors = (bar.y until bar.bottom).map { px[x, it] }
                when {
                    OBOL_FILL in colors && OBOL_EMPTY !in colors -> 1
                    OBOL_EMPTY in colors && OBOL_FILL !in colors -> 0
                    else -> -1
                }
            }
        }
        val w = screen().layout!!.obolBar.w
        assertEquals(List(w) { 0 }, columns(3.0))
        assertEquals(List(w) { if (it < w / 4) 1 else 0 }, columns(0.25))
        assertEquals(List(w) { if (it < w / 2) 1 else 0 }, columns(2.5))
    }

    /** At their highest, at noon and at midnight, the sun and the moon of every world stay a little under the obol bar. */
    @Test
    fun theSunAndTheMoonPassUnderTheObolBar() {
        val l = smallest().layout!!
        val geo = SceneGeometry(l.scene.w, l.scene.h)
        val under = l.obolBar.bottom - l.scene.y + 2
        val moon = listOf(rgb(0xF2F0DE), rgb(0xC8C6B2))
        for (place in Place.entries) {
            val sun = lookOf(place).sun.let { listOf(it[1], it[3]) }
            for ((time, colors) in listOf(0.5 to sun, 1.0 to moon)) {
                val game = Game(MemoryStore(mapOf("effort" to (time - START_TIME) * DAY_LENGTH, "place" to place.id.toDouble())), seed = 1)
                val px = Pixels(geo.width, geo.height)
                SceneRenderer(geo).render(px, game, World(2).also { it.reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
                assertTrue((0 until geo.height).any { y -> (0 until geo.width).any { x -> px[x, y] in colors } }, "nothing shows in ${place.title} at $time")
                for (y in 0 until under) for (x in 0 until geo.width) assertFalse(px[x, y] in colors, "in ${place.title} at $time, ($x, $y)")
            }
        }
    }

    @Test
    fun theBarSitsBetweenTheDayAndTheObolsOnTheSmallestPhone() {
        val l = smallest().layout!!
        val bar = l.obolBar
        assertTrue(l.dayLabel(9999).right + 2 <= bar.x, "the bar starts at ${bar.x}")
        assertTrue(bar.right + 2 <= l.coinIcon.x, "the bar ends at ${bar.right}")
        assertTrue(bar.y >= l.coinIcon.y && bar.bottom <= l.coinIcon.bottom)
    }

    @Test
    fun theBarIsOnlyOnTheMainScreen() {
        val s = screen(MemoryStore(mapOf("obols" to 2.5)))
        val l = s.layout!!
        fun start() = s.render()[l.obolBar.x + 1, l.obolBar.y + l.obolBar.h / 2]
        assertEquals(OBOL_FILL, start())
        s.tap(l.coinIcon)
        assertNotEquals(OBOL_FILL, start())
    }

    private val allBought = Line.entries.associate { "shop." + it.id to 3.0 }

    /** On the Worlds screen, across from Restore, the player's id for support: its last characters. A tap copies the whole id. */
    @Test
    fun theWorldsScreenShowsTheSupportIdAndATapCopiesIt() {
        val billing = FakeBilling()
        val s = Screen(Game(MemoryStore(), seed = 1), seed = 2, billing = billing).apply { resize(1179, 2556, 177, 102) }
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        fun corner() = s.render().let { px -> (l.supportId.y until l.supportId.bottom).flatMap { y -> (l.supportId.x until l.supportId.right).map { px[it, y] } } }
        val tag = corner()
        assertEquals("ID cdef", supportTag(billing.userId, build = null))
        assertEquals(Touch.COPY, s.tap(l.supportId))
        assertEquals(billing.userId, s.supportId)
        assertNotEquals(tag, corner())
        s.update(3.0)
        assertEquals(tag, corner())
    }

    /** Across from Restore, the build of the game stands after the player's id, such as "ID cdef [41]". */
    @Test
    fun theBuildStandsAfterTheSupportId() {
        assertEquals("ID cdef [41]", supportTag("abcdef", "41"))
        assertTrue(supportTag("abcdef", "41")!!.all(PixelFont::hasGlyph))
        assertEquals("[41]", supportTag(null, "41"))
        assertNull(supportTag(null, null))
        fun corner(build: String?) = worlds(build).let { (s, px) ->
            val r = s.layout!!.supportId
            (r.y until r.bottom).flatMap { y -> (r.x until r.right).map { px[it, y] } }
        }
        assertNotEquals(corner(null), corner("41"))
        assertNotEquals(corner("41"), corner("42"))
    }

    /** The title row of the Worlds screen holds only its title, so that the tabs have room. */
    @Test
    fun nothingStandsAfterTheWorldsTitle() {
        fun row(build: String?) = worlds(build).let { (s, px) ->
            val l = s.layout!!
            (l.agoraTitle.y until l.agoraTitle.bottom).flatMap { y -> (l.agoraTitle.x + PixelFont.width(WORLDS_TITLE) + 1 until l.coinIcon.x).map { px[it, y] } }
        }
        assertEquals(row(null), row("41"))
    }

    /** The Worlds screen of a phone with a store and [build], and its pixels. */
    private fun worlds(build: String?): Pair<Screen, Pixels> {
        val s = Screen(Game(MemoryStore(), seed = 1), seed = 2, billing = FakeBilling(), build = build).apply { resize(1179, 2556, 177, 102) }
        s.tap(s.layout!!.dayLabel(s.game.day))
        return s to s.render()
    }

    @Test
    fun withoutAStoreThereIsNoSupportId() {
        val s = screen()
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        assertNull(s.supportId)
        assertEquals(Touch.NONE, s.tap(l.supportId))
    }

    @Test
    fun theSupportIdFitsBesideRestoreOnTheSmallestPhone() {
        val l = smallest().layout!!
        assertTrue(l.restore.right + 6 <= l.supportId.x, "Restore ends at ${l.restore.right}, the id starts at ${l.supportId.x}")
        assertTrue(PixelFont.width("Copied") <= l.supportId.w)
        assertTrue(PixelFont.width("ID cdef [999]") <= l.supportId.w)
        assertEquals(l.coinIcon.right, l.supportId.right)
    }

    /** In the final climb, a herm stands on the slope ahead of him: the scene changes only where it stands. */
    @Test
    fun aHermStandsOnTheSlopeAhead() {
        val position = 5000.0
        val step = Config.HERM_TURNS * Config.SUN_CAP * PX_PER_TURN
        fun scene(ahead: Double): Pixels {
            val s = screen(MemoryStore(allBought + mapOf("position" to position, "summit" to position + ahead - step + CLIMB)))
            val l = s.layout!!
            return Pixels(l.scene.w, l.scene.h).also { SceneRenderer(SceneGeometry(l.scene.w, l.scene.h)).render(it, s.game, s.world, banner = 0.0, hud = false) }
        }
        val near = scene(50.0)
        val far = scene(900.0)
        val geo = SceneGeometry(near.width, near.height)
        val x = geo.heroX + 50
        val changed = (0 until near.height).flatMap { y -> (0 until near.width).filter { near[it, y] != far[it, y] }.map { it to y } }
        assertTrue(changed.size > 20, "${changed.size} pixels")
        assertTrue(changed.all { (cx, _) -> cx in x - 5..x + 5 }, "${changed.map { it.first }.distinct()}")
        assertEquals(geo.surfaceY(x - HERM.width / 2, position.toInt()), changed.maxOf { it.second })
    }

    /** During the final climb the corner shows only the coin, as the way into the Agora: no count, because obols stop coming. */
    @Test
    fun duringTheFinalClimbTheCornerShowsOnlyTheCoin() {
        fun corner(obols: Double, climbing: Boolean): List<Int> {
            val climb = if (climbing) allBought + mapOf("position" to 1000.0, "summit" to 1000.0 + CLIMB) else emptyMap()
            val s = screen(MemoryStore(climb + mapOf("obols" to obols)))
            val px = s.render()
            val r = s.layout!!.coinIcon
            return (r.y until r.bottom).flatMap { y -> (r.x until r.right).map { px[it, y] } }
        }
        assertEquals(corner(12.0, climbing = true), corner(345.0, climbing = true))
        assertNotEquals(corner(12.0, climbing = false), corner(345.0, climbing = false))
        val s = screen(MemoryStore(allBought + mapOf("position" to 1000.0, "summit" to 1000.0 + CLIMB)))
        val l = s.layout!!
        val px = s.render()
        assertTrue((l.coinIcon.right - Layout.COIN_SIZE until l.coinIcon.right).any { x -> (l.coinIcon.y until l.coinIcon.bottom).any { px[x, it] == OBOL_FILL } })
        assertEquals(Touch.SHOP, s.tap(l.coinIcon))
        assertTrue(s.agoraOpen)
        assertEquals("Tap the coin for the market.", obolsNote(climbing = true))
    }

    /** The help during the final climb shows the coin as the main screen does: without a count. */
    @Test
    fun theHelpDuringTheFinalClimbShowsOnlyTheCoin() {
        val s = screen(MemoryStore(allBought + mapOf("obols" to 12.0, "position" to 1000.0, "summit" to 1000.0 + CLIMB)))
        val l = s.layout!!
        fun light(c: Int) = (c shr 16 and 0xFF) + (c shr 8 and 0xFF) + (c and 0xFF)
        fun brightest() = s.render().let { px -> (l.coinIcon.y until l.coinIcon.bottom).maxOf { y -> (l.coinIcon.x until l.coinIcon.right).maxOf { light(px[it, y]) } } }
        val plain = brightest()
        s.tap(l.helpIcon)
        assertEquals(plain, brightest())
    }

    /** During the final climb the key shows the way to the top: cream as far as he has climbed, and no bronze for the obols. */
    @Test
    fun duringTheFinalClimbTheKeyShowsTheWayToTheTop() {
        val s = screen(MemoryStore(allBought + mapOf("obols" to 2.5, "position" to 1000.0 + CLIMB / 2, "summit" to 1000.0 + CLIMB)))
        val px = s.render()
        val bar = s.layout!!.obolBar
        val columns = (bar.x until bar.right).map { x -> (bar.y until bar.bottom).map { px[x, it] } }
        assertEquals(bar.w / 2, columns.count { CLIMB_FILL in it })
        assertTrue(columns.none { OBOL_FILL in it })
        assertEquals("The bar fills up to the top.", barNote(climbing = true))
    }

    @Test
    fun atTheTopTheRockRollsAwayTheScreenGoesBlackAndATapOffersTheWorlds() {
        val s = screen(MemoryStore(allBought + mapOf("position" to 100.0, "summit" to 100.0, "effort" to 50_000.0)))
        val l = s.layout!!
        val dt = 1 / 60.0
        s.update(dt)
        assertNotNull(s.ending)
        assertEquals(Touch.NONE, s.tap(l.knobX + l.knobR * 0.6f, l.knobY + 0.5f))
        assertFalse(s.game.knob.held)
        repeat((Ending.BLACK_AT / dt).toInt() + 2) { s.update(dt) }
        assertEquals(1, s.game.climbs)
        assertEquals(1, s.game.day)
        repeat((3.0 / dt).toInt()) { s.update(dt) }
        assertEquals(Touch.AGAIN, s.tap(l.knobX.toFloat(), l.knobY.toFloat()))
        assertTrue(s.worldsOpen)
        assertFalse(s.laborsShown)
        repeat((2.0 / dt).toInt()) { s.update(dt) }
        assertNotNull(s.ending, "the first morning waits for the choice")
        assertTrue(s.back())
        repeat((2.0 / dt).toInt()) { s.update(dt) }
        assertNull(s.ending)
        assertEquals(Place.CORINTH, s.game.place)
        assertEquals(Touch.KNOB, s.tap(l.knobX + l.knobR * 0.6f, l.knobY + 0.5f))
    }

    @Test
    fun reachingTheTopPlaysTheVictoryOnce() {
        val s = screen(MemoryStore(allBought + mapOf("position" to 100.0, "summit" to 100.0, "effort" to 50_000.0)))
        var tops = 0
        s.onTop = { tops++ }
        val dt = 1 / 60.0
        repeat(((Ending.BLACK_AT + Ending.READ) / dt).toInt()) { s.update(dt) }
        assertEquals(1, tops)
    }

    /** A screen that has just reached the top, after [climbs] climbs. */
    private fun atTheTop(climbs: Int = 0): Screen {
        val store = MemoryStore(allBought + mapOf("position" to 100.0, "summit" to 100.0, "effort" to 50_000.0, "climbs" to climbs.toDouble()))
        return screen(store).apply { update(1 / 60.0) }
    }

    private fun Screen.idle(seconds: Double) = repeat((seconds * 60).toInt()) { update(1 / 60.0) }

    /** At the top, two of the three gods of the world glow over him and cheer him in the panel. The rock rolls when they are gone. */
    @Test
    fun atTheTopTwoGodsOfTheWorldCheerHimBeforeTheRockRolls() {
        val s = atTheTop()
        val l = s.layout!!
        fun edge() = s.render()[l.quote.x - 2, l.quote.y + 5]
        val end = s.ending!!
        assertEquals(2, end.gods.toSet().size)
        assertTrue(end.gods.all { it in godsOf(s.game.place) })
        assertEquals("You made it to the top.", s.game.quotes.quote.text)
        assertEquals("the Gods", s.game.quotes.quote.author)
        s.idle(2.0)
        assertEquals(1f, end.godLight)
        assertEquals(0.0, end.rolled)
        assertEquals(VOICE_FRAME, edge())
        s.idle(Ending.ROLL_AT - 2.0 + 0.5)
        assertEquals(0f, end.godLight)
        assertTrue(end.rolled!! > 0.0)
        assertNotEquals(VOICE_FRAME, edge())
    }

    @Test
    fun eachClimbHasItsOwnPairOfGods() {
        val pairs = (0..5).map { atTheTop(climbs = it).ending!!.gods }
        assertTrue(pairs.toSet().size > 1)
    }

    @Test
    fun goldSparklesTwinkleAroundHimWhileTheGodsAreThere() {
        val s = atTheTop()
        val d = s.layout!!.display
        fun sparkles() = s.render().let { px -> (d.y until d.bottom).sumOf { y -> (d.x until d.right).count { x -> px[x, y] == SPARKLE } } }
        assertEquals(0, sparkles())
        s.idle(2.0)
        assertTrue(sparkles() > 4)
        s.idle(Ending.ROLL_AT - 2.0)
        assertEquals(0, sparkles())
    }

    @Test
    fun theGodsAtTheTopFitTheSmallestPhone() {
        val w = smallest().layout!!.scene.w
        val (left, right) = (0..1).map { godMiddle(it, w) - GOD_GLOW / 2 until godMiddle(it, w) + GOD_GLOW / 2 }
        assertTrue(left.first >= 0 && right.last < w, "$left $right in $w")
        assertTrue(left.last < right.first)
    }

    /** A screen at the top, after Camus' words have had time to be read, with [billing] as the store. */
    private fun afterTheWords(billing: Billing = NoBilling): Screen {
        val store = MemoryStore(allBought + mapOf("position" to 100.0, "summit" to 100.0, "effort" to 50_000.0))
        val s = Screen(Game(store, seed = 1), seed = 2, billing = billing).apply { resize(1179, 2556, 177, 102) }
        val dt = 1 / 60.0
        repeat(((Ending.BLACK_AT + Ending.READ) / dt).toInt() + 2) { s.update(dt) }
        assertTrue(s.ending!!.waiting)
        return s
    }

    /** Everything starts from the beginning after the top anyway, so a tap on a world goes there without a question. */
    @Test
    fun afterTheTopTheNextClimbCanBeginInANewWorld() {
        val s = afterTheWords()
        val l = s.layout!!
        val dt = 1 / 60.0
        s.tap(l.knobX.toFloat(), l.knobY.toFloat())
        assertEquals(Touch.MOVE, s.choose(l.worldRow(Place.ETNA.ordinal)))
        assertNull(s.moving)
        assertFalse(s.worldsOpen)
        assertEquals(Place.ETNA, s.game.place)
        assertEquals(1, s.game.climbs)
        repeat((2.0 / dt).toInt()) { s.update(dt) }
        assertNull(s.ending)
    }

    @Test
    fun afterTheTopATapOnTheSameWorldBeginsTheClimbThere() {
        val s = afterTheWords()
        val l = s.layout!!
        val dt = 1 / 60.0
        s.tap(l.knobX.toFloat(), l.knobY.toFloat())
        assertEquals(Touch.WORLDS, s.choose(l.worldRow(Place.CORINTH.ordinal)))
        assertFalse(s.worldsOpen)
        assertEquals(Place.CORINTH, s.game.place)
        repeat((2.0 / dt).toInt()) { s.update(dt) }
        assertNull(s.ending)
    }

    @Test
    fun afterTheTopAWorldJustBoughtBeginsWithItsFanfare() {
        val billing = FakeBilling()
        val s = afterTheWords(billing)
        val l = s.layout!!
        var fanfares = 0
        s.onMove = { fanfares++ }
        s.tap(l.knobX.toFloat(), l.knobY.toFloat())
        s.choose(l.worldRow(Place.ETNA.ordinal))
        assertEquals(Place.ETNA, s.buying)
        s.tap(l.confirmYes)
        billing.pending!!(true)
        assertNull(s.moving)
        assertFalse(s.worldsOpen)
        assertEquals(Place.ETNA, s.game.place)
        assertEquals(1, fanfares)
    }

    @Test
    fun theBlackOfTheTopDoesNotHideTheWorlds() {
        val s = afterTheWords()
        val l = s.layout!!
        val black = s.render()[0, 0]
        s.tap(l.knobX.toFloat(), l.knobY.toFloat())
        val px = s.render()
        val tab = l.worldsTab
        assertTrue((tab.x until tab.right).any { x -> (tab.y until tab.bottom).any { y -> px[x, y] != black } })
    }

    @Test
    fun theDevScreenStartsTheClimbAgain() {
        val store = MemoryStore(mapOf("effort" to 5000.0, "position" to 900.0, "obols" to 7.5, "shop.rock" to 2.0, "climbs" to 2.0))
        val s = devScreen(store)
        s.openDev()
        assertEquals(Touch.DEV, s.tap(s.layout!!.devResetClimb))
        assertEquals(1, s.game.day)
        assertEquals(0.0, s.game.position)
        assertEquals(0.0, s.game.shop.obols)
        assertTrue(Line.entries.all { s.game.shop.tier(it) == 0 })
        assertEquals(FIRST_OBOL, s.game.shop.nextObol)
        assertEquals(2, s.game.climbs)
        assertEquals(Place.CORINTH, s.game.place)
        assertEquals(0.0, store.load("position"))
    }

    /** The next obol is three clicks of the knob away, and the sun has moved as if he pushed all the way there. */
    @Test
    fun theDevScreenTakesHimNearTheNextObol() {
        val store = MemoryStore(mapOf("obols" to 2.25, "nextObol" to 150.0, "shop.rock" to 1.0))
        val s = devScreen(store)
        val effort = s.game.effort
        s.openDev()
        assertEquals(Touch.DEV, s.tap(s.layout!!.devNextObol))
        assertEquals(2, s.game.shop.obols.toInt())
        assertEquals(3 * 1.5 / 24, (3 - s.game.shop.obols) * 150, 1e-6)
        val turns = 0.75 * 150 / 1.5 - 3.0 / 24
        assertEquals(turns * Config.SUN_CAP * PX_PER_TURN, s.game.effort - effort, 1e-6)
        assertEquals(s.game.shop.obols, store.load("obols"))
    }

    @Test
    fun theDevScreenTakesHimNearTheTop() {
        val s = devScreen()
        s.openDev()
        assertEquals(Touch.DEV, s.tap(s.layout!!.devTop))
        assertTrue(Line.entries.all { s.game.shop.tier(it) == 3 })
        val left = s.game.summit!! - s.game.position
        assertTrue(left > 0 && left <= 2 * Config.SUN_CAP * PX_PER_TURN, "left: $left")
    }

    @Test
    fun tappingTheDayOpensTheWorlds() {
        val s = screen()
        val l = s.layout!!
        assertEquals(Touch.WORLDS, s.tap(l.dayLabel(s.game.day)))
        assertTrue(s.worldsOpen)
        assertFalse(s.agoraOpen)
        assertEquals(Touch.WORLDS, s.tap(l.worldsTab))
        assertFalse(s.worldsOpen)
    }

    @Test
    fun goingToAnotherWorldAsksFirstBecauseTheClimbStartsOver() {
        val s = screen(MemoryStore(mapOf("effort" to 5000.0, "obols" to 7.0)))
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        assertEquals(Touch.WORLDS, s.choose(l.worldRow(Place.ETNA.ordinal)))
        assertEquals(Place.ETNA, s.moving)
        assertEquals(Touch.WORLDS, s.tap(l.confirmNo))
        assertNull(s.moving)
        assertEquals(Place.CORINTH, s.game.place)
        assertEquals(7.0, s.game.shop.obols)
        s.choose(l.worldRow(Place.ETNA.ordinal))
        assertEquals(Touch.MOVE, s.tap(l.confirmYes))
        assertEquals(Place.ETNA, s.game.place)
        assertEquals(1, s.game.day)
        assertEquals(0.0, s.game.shop.obols)
        assertFalse(s.worldsOpen)
    }

    @Test
    fun aTapOnTheWorldYouAreInGoesBackToTheClimb() {
        val s = screen(MemoryStore(mapOf("effort" to 5000.0, "obols" to 7.0)))
        val l = s.layout!!
        val day = s.game.day
        s.tap(l.dayLabel(day))
        assertEquals(Touch.WORLDS, s.choose(l.worldRow(Place.CORINTH.ordinal)))
        assertNull(s.moving)
        assertFalse(s.worldsOpen)
        assertEquals(Place.CORINTH, s.game.place)
        assertEquals(day, s.game.day)
        assertEquals(7.0, s.game.shop.obols)
    }

    @Test
    fun theWorldsScrollWhenTheyDoNotFit() {
        val s = Screen(Game(MemoryStore(), seed = 1), seed = 2).apply { resize(1179, 1800) }
        val l = s.layout!!
        val most = l.worldScroll(Place.entries.size)
        assertTrue(most > 0, "most: $most")
        s.tap(l.dayLabel(s.game.day))
        val first = l.worldRow(0)
        assertEquals(Touch.LIST, s.tap(first))
        s.drag(first.x + 10f, first.y + 10f - 1000f)
        assertEquals(Touch.NONE, s.release())
        assertEquals(most, s.worldsScroll)
        assertNull(s.moving)
        val last = l.worldRow(Place.entries.size - 1)
        assertEquals(Touch.WORLDS, s.choose(Rect(last.x, last.y - most, last.w, last.h)))
        assertEquals(Place.entries.last(), s.moving)
    }

    /** On a short screen, opens the worlds and swipes the list up quickly; the finger lifts at the end. */
    private fun flingWorlds(): Screen {
        val s = Screen(Game(MemoryStore(), seed = 1), seed = 2).apply { resize(1179, 1800) }
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        val x = l.worldRow(0).x + 10f
        var y = l.worldRow(0).y + 40f
        s.tap(x, y)
        repeat(4) {
            y -= 3f
            s.drag(x, y)
            s.update(1 / 60.0)
        }
        assertEquals(Touch.NONE, s.release())
        return s
    }

    @Test
    fun aFlungListCoastsThenStops() {
        val s = flingWorlds()
        val lifted = s.worldsScroll
        repeat(6) { s.update(1 / 60.0) }
        assertTrue(s.worldsScroll > lifted, "coasts: ${s.worldsScroll} after $lifted")
        repeat(300) { s.update(1 / 60.0) }
        val rest = s.worldsScroll
        assertTrue(rest <= s.layout!!.worldScroll(Place.entries.size))
        s.update(1 / 60.0)
        assertEquals(rest, s.worldsScroll)
    }

    @Test
    fun aTapThatStopsAFlingChoosesNothing() {
        val s = flingWorlds()
        s.update(1 / 60.0)
        val row = s.layout!!.worldRow(Place.ETNA.ordinal)
        assertEquals(Touch.LIST, s.tap(row.x + 10f, row.y + 10f - s.worldsScroll))
        assertEquals(Touch.NONE, s.release())
        assertNull(s.moving)
        val stopped = s.worldsScroll
        repeat(30) { s.update(1 / 60.0) }
        assertEquals(stopped, s.worldsScroll)
    }

    @Test
    fun theWorldsFitTheSmallestPhone() {
        val s = smallest()
        val l = s.layout!!
        assertTrue(l.worldRow(0).y > l.agoraTitle.bottom && l.worldList.bottom <= l.agoraSound.y)
        s.tap(l.dayLabel(s.game.day))
        val first = l.worldRow(0)
        s.tap(first)
        s.drag(first.x + 10f, first.y + 10f - 1000f)
        s.release()
        assertTrue(l.worldRow(Place.entries.size - 1).bottom - s.worldsScroll <= l.worldList.bottom)
    }

    @Test
    fun everyWorldHasItsOwnPicture() {
        val pictures = Place.entries.map { place ->
            val px = worldPreview(place, 116, 42)
            assertEquals(116 to 42, px.width to px.height)
            assertTrue((0 until 42).all { y -> (0 until 116).all { x -> px[x, y] != TRANSPARENT } }, place.title)
            (0 until 42).flatMap { y -> (0 until 116).map { x -> px[x, y] } }
        }
        assertEquals(Place.entries.size, pictures.toSet().size)
    }

    @Test
    fun backClosesTheQuestionThenTheWorlds() {
        val s = screen()
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        s.choose(l.worldRow(Place.UNDERWORLD.ordinal))
        assertTrue(s.back())
        assertNull(s.moving)
        assertTrue(s.worldsOpen)
        assertTrue(s.back())
        assertFalse(s.worldsOpen)
    }

    /** A store that sells every world but [owned] for the price of [Config.WORLD_PRICE]. A purchase waits in [pending] until the test answers for the player. */
    private class FakeBilling(vararg owned: Place) : Billing {
        private val owned = owned.toMutableSet()
        override val userId = "\$RCAnonymousID:0123456789abcdef"
        var pending: ((Boolean) -> Unit)? = null
        var restores = 0
        override fun owns(place: Place) = place.product == null || place in owned
        override fun price(place: Place) = "\$${Config.WORLD_PRICE}"
        override fun buy(place: Place, done: (Boolean) -> Unit) {
            pending = { bought ->
                pending = null
                if (bought) owned += place
                done(bought)
            }
        }
        override fun restore(done: () -> Unit) {
            restores++
            done()
        }
    }

    /** The Worlds screen, open in [place], with [billing] as the store. */
    private fun worlds(billing: Billing, place: Place = Place.CORINTH) =
        Screen(Game(MemoryStore(mapOf("place" to place.id.toDouble())), seed = 1), seed = 2, billing = billing).apply {
            resize(1179, 2556, 177, 102)
            tap(layout!!.dayLabel(game.day))
        }

    @Test
    fun aWorldNotBoughtAsksToBuyItFirst() {
        val billing = FakeBilling()
        val s = worlds(billing)
        val l = s.layout!!
        assertEquals(Touch.WORLDS, s.choose(l.worldRow(Place.ETNA.ordinal)))
        assertEquals(Place.ETNA, s.buying)
        assertNull(s.moving)
        assertEquals(Touch.WORLDS, s.tap(l.confirmNo))
        assertNull(s.buying)
        assertNull(billing.pending)
    }

    @Test
    fun onceBoughtTheWorldAsksToGoThere() {
        val billing = FakeBilling()
        val s = worlds(billing)
        val l = s.layout!!
        s.choose(l.worldRow(Place.ETNA.ordinal))
        assertEquals(Touch.WORLDS, s.tap(l.confirmYes))
        assertTrue(s.paying)
        assertEquals(Touch.NONE, s.tap(l.confirmNo), "the store asks now")
        assertEquals(Touch.NONE, s.tap(l.coinIcon), "the store asks now")
        assertTrue(s.back(), "the store asks now")
        billing.pending!!(true)
        assertFalse(s.paying)
        assertNull(s.buying)
        assertEquals(Place.ETNA, s.moving)
        assertEquals(Touch.MOVE, s.tap(l.confirmYes))
        assertEquals(Place.ETNA, s.game.place)
    }

    @Test
    fun whenThePlayerDoesNotPayNothingChanges() {
        val billing = FakeBilling()
        val s = worlds(billing)
        val l = s.layout!!
        s.choose(l.worldRow(Place.UNDERWORLD.ordinal))
        s.tap(l.confirmYes)
        billing.pending!!(false)
        assertFalse(s.paying)
        assertNull(s.buying)
        assertNull(s.moving)
        assertTrue(s.worldsOpen)
        assertEquals(Place.CORINTH, s.game.place)
    }

    @Test
    fun aWorldBoughtBeforeIsNotSoldAgain() {
        val s = worlds(FakeBilling(Place.ETNA))
        s.choose(s.layout!!.worldRow(Place.ETNA.ordinal))
        assertNull(s.buying)
        assertEquals(Place.ETNA, s.moving)
    }

    /** Beta testers went to other worlds before worlds cost money: they stay, and pay only to come back after they leave. */
    @Test
    fun aPlayerStaysInAWorldNotBought() {
        val s = worlds(FakeBilling(), Place.ETNA)
        assertEquals(Place.ETNA, s.game.place)
        assertEquals(Touch.WORLDS, s.choose(s.layout!!.worldRow(Place.ETNA.ordinal)))
        assertNull(s.buying)
        assertFalse(s.worldsOpen)
        assertEquals(Place.ETNA, s.game.place)
    }

    @Test
    fun backClosesTheQuestionToBuy() {
        val s = worlds(FakeBilling())
        s.choose(s.layout!!.worldRow(Place.ETNA.ordinal))
        assertEquals(Place.ETNA, s.buying)
        assertTrue(s.back())
        assertNull(s.buying)
        assertTrue(s.worldsOpen)
    }

    @Test
    fun restoreAsksTheStoreForTheWorldsBoughtBefore() {
        val billing = FakeBilling()
        val s = worlds(billing)
        assertEquals(Touch.WORLDS, s.tap(s.layout!!.restore))
        assertEquals(1, billing.restores)
        assertFalse(s.restoring)
    }

    @Test
    fun withEveryWorldBoughtThereIsNothingToRestore() {
        val billing = FakeBilling(*Place.entries.toTypedArray())
        val s = worlds(billing)
        assertEquals(Touch.NONE, s.tap(s.layout!!.restore))
        assertEquals(0, billing.restores)
    }

    @Test
    fun pricesTheFontCannotWriteShowAmountAndCurrency() {
        assertEquals("$0.99", shownPrice("$0.99", 990_000, "USD"))
        assertEquals("0,99 €", shownPrice("0,99\u00A0€", 990_000, "EUR"))
        assertEquals("£0.99", shownPrice("£0.99", 990_000, "GBP"))
        assertEquals("CHF 1.00", shownPrice("CHF 1.00", 1_000_000, "CHF"))
        assertEquals("89 INR", shownPrice("₹89.00", 89_000_000, "INR"))
        assertEquals("89.50 INR", shownPrice("₹89.50", 89_500_000, "INR"))
        assertEquals("160 JPY", shownPrice("¥160", 160_000_000, "JPY"))
    }

    @Test
    fun theOfferOfAWorldFitsTheSmallestPhone() {
        val l = smallest().layout!!
        val price = "10.99 CHF"
        for (place in Place.entries.filter { it.product != null }) {
            assertTrue(PixelFont.width(buyTitle(place)) <= l.confirmText.w, place.title)
            assertTrue(PixelFont.width(place.title) + 4 + PixelFont.width(price) + 2 + Layout.COIN_SIZE <= l.worldText(0).w, place.title)
        }
        assertTrue(PixelFont.wrap(buyText(price), l.confirmText.w).size <= Layout.CONFIRM_LINES)
        assertTrue(l.restore.right <= l.worldList.right)
        assertTrue(l.restore.y >= l.worldList.bottom)
    }

    @Test
    fun backClosesTheAgora() {
        val s = screen()
        assertFalse(s.back())
        s.tap(s.layout!!.coinIcon)
        assertTrue(s.back())
        assertFalse(s.agoraOpen)
    }

    @Test
    fun theSpeakerLivesInTheAgora() {
        val store = MemoryStore()
        val s = screen(store)
        val l = s.layout!!
        s.tap(l.coinIcon)
        assertEquals(Touch.SOUND, s.tap(l.agoraSound))
        assertFalse(s.game.soundOn)
        assertFalse(Game(store, seed = 1).soundOn)
        assertEquals(Touch.SOUND, s.tap(l.agoraSound))
        assertTrue(s.game.soundOn)
    }

    @Test
    fun tappingAnItemAsksFirst() {
        val s = screen(MemoryStore(mapOf("obols" to 10.0)))
        val l = s.layout!!
        s.tap(l.coinIcon)
        assertEquals(Touch.SHOP, s.tap(l.agoraRow(Line.ROCK.ordinal)))
        assertEquals(Line.ROCK, s.offer)
        assertEquals(0, s.game.shop.tier(Line.ROCK))
        assertEquals(10.0, s.game.shop.obols)
    }

    @Test
    fun yesBuysTheNextItem() {
        val s = screen(MemoryStore(mapOf("obols" to 2.0)))
        val l = s.layout!!
        s.tap(l.coinIcon)
        s.tap(l.agoraRow(Line.GODS.ordinal))
        assertEquals(Touch.BUY, s.tap(l.offerYes))
        assertEquals(1, s.game.shop.tier(Line.GODS))
        assertEquals(1.0, s.game.shop.obols, 1e-9)
        assertNull(s.offer)
    }

    @Test
    fun oneYesBuysOneItem() {
        val s = screen(MemoryStore(mapOf("obols" to 100.0)))
        val l = s.layout!!
        s.tap(l.coinIcon)
        s.tap(l.agoraRow(Line.ROCK.ordinal))
        s.tap(l.offerYes)
        assertEquals(1, s.game.shop.tier(Line.ROCK))
    }

    @Test
    fun noOrATapOutsideKeepsTheObols() {
        val s = screen(MemoryStore(mapOf("obols" to 10.0)))
        val l = s.layout!!
        s.tap(l.coinIcon)
        s.tap(l.agoraRow(Line.ROCK.ordinal))
        assertEquals(Touch.SHOP, s.tap(l.offerNo))
        assertNull(s.offer)
        s.tap(l.agoraRow(Line.ROCK.ordinal))
        assertEquals(Touch.SHOP, s.tap(l.agoraTitle))
        assertNull(s.offer)
        assertEquals(0, s.game.shop.tier(Line.ROCK))
        assertEquals(10.0, s.game.shop.obols)
        assertTrue(s.agoraOpen)
    }

    @Test
    fun backClosesTheOfferFirst() {
        val s = screen(MemoryStore(mapOf("obols" to 10.0)))
        val l = s.layout!!
        s.tap(l.coinIcon)
        s.tap(l.agoraRow(Line.ROCK.ordinal))
        assertTrue(s.back())
        assertNull(s.offer)
        assertTrue(s.agoraOpen)
    }

    @Test
    fun everyOfferFitsTheSmallestPhone() {
        val l = smallest().layout!!
        for (item in allItems) {
            for (ch in item.details) assertTrue(PixelFont.hasGlyph(ch), "missing '$ch' in the details of ${item.name}")
            assertTrue(PixelFont.width(item.name) <= l.offerText.w, item.name)
            assertTrue(PixelFont.wrap(item.details, l.offerText.w).size * PixelFont.LINE_HEIGHT <= l.offerText.h, item.name)
        }
        assertTrue(l.offer.y >= l.agoraTitle.bottom && l.offer.bottom <= l.agoraSound.y)
        assertTrue(l.offerYes.bottom <= l.offer.bottom && l.offerNo.right <= l.offer.right)
    }

    @Test
    fun theSidekickGodAndBlessingStandSideBySideInTheDisplaysBottomRightCorner() {
        val corner = items(Place.CORINTH).filter { it.line == Line.SIDEKICKS || it.line == Line.GODS || it.line == Line.BLESSING }
        /** The names of the items shown in the corner, in the place next to it, and in the place after that. */
        fun shown(sidekick: Int, god: Int, blessing: Int = 0): List<List<String>> {
            val store = MemoryStore(mapOf("shop.beside" to sidekick.toDouble(), "shop.alone" to god.toDouble(), "shop.blessing" to blessing.toDouble()))
            val s = screen(store)
            val px = s.render()
            return (0..2).map { slot ->
                val r = s.layout!!.helper(slot)
                corner.filter { item ->
                    val icon = itemIcon(item)
                    (0 until 16).all { y -> (0 until 16).all { x -> icon[x, y] == TRANSPARENT || px[r.x + 1 + x, r.y + 1 + y] == icon[x, y] } }
                }.map { it.name }
            }
        }
        assertEquals(listOf(listOf(), listOf(), listOf()), shown(0, 0))
        assertEquals(listOf(listOf("Bellerophon"), listOf(), listOf()), shown(2, 0))
        assertEquals(listOf(listOf("Amalthea"), listOf(), listOf()), shown(0, 1))
        assertEquals(listOf(listOf("Thanatos"), listOf("Heracles"), listOf()), shown(3, 3))
        assertEquals(listOf(listOf("Purse of Hermes"), listOf(), listOf()), shown(0, 0, 1))
        assertEquals(listOf(listOf("Gift of Tyche"), listOf("Merope"), listOf()), shown(1, 0, 2))
        assertEquals(listOf(listOf("Wealth of Plutus"), listOf("Thanatos"), listOf("Heracles")), shown(3, 3, 3))
    }

    @Test
    fun everyItemHasAnIcon() {
        for (item in allItems) {
            val icon = itemIcon(item)
            assertEquals(16, icon.width, item.name)
            assertEquals(16, icon.height, item.name)
            assertTrue((0 until 16).sumOf { y -> (0 until 16).count { x -> icon[x, y] != TRANSPARENT } } > 20, item.name)
        }
    }

    @Test
    fun theKnobRestsWhileTheAgoraIsOpen() {
        val s = screen()
        val l = s.layout!!
        s.tap(l.coinIcon)
        assertNotEquals(Touch.KNOB, s.tap(l.knobX + l.knobR * 0.6f, l.knobY + 0.5f))
        assertFalse(s.game.knob.held)
    }

    @Test
    fun theCoinWinsOverTheKnobOnTheSmallestPhone() {
        val s = smallest()
        assertEquals(Touch.SHOP, s.tap(s.layout!!.coinIcon))
        assertFalse(s.game.knob.held)
    }

    @Test
    fun everyItemFitsItsRowOnTheSmallestPhone() {
        val l = smallest().layout!!
        val row = l.agoraRow(0)
        for (item in allItems) {
            for (ch in item.name + item.note) assertTrue(PixelFont.hasGlyph(ch), "missing '$ch' in ${item.name}")
            assertTrue(PixelFont.width(item.name) + 24 <= row.w, item.name)
            assertTrue(PixelFont.width(item.note) + 16 <= row.w, item.note)
        }
        assertTrue(row.h >= 3 * PixelFont.LINE_HEIGHT)
        assertTrue(l.agoraRow(4).bottom <= l.agoraSound.y)
    }

    @Test
    fun everyWorldNameFitsItsRowOnTheSmallestPhone() {
        val l = smallest().layout!!
        val text = l.worldText(0)
        for (place in Place.entries) {
            for (ch in place.title + place.note) assertTrue(PixelFont.hasGlyph(ch), "missing '$ch' in ${place.title}")
            assertTrue(PixelFont.width(place.title) + Layout.COIN_SIZE + 2 <= text.w, place.title)
            assertTrue(PixelFont.width(place.note) <= text.w, place.note)
        }
    }

    @Test
    fun etnaHasIconsOfItsOwn() {
        for (item in items(Place.ETNA)) {
            val corinth = items(Place.CORINTH).first { it.line == item.line && it.tier == item.tier }
            assertFalse(itemIcon(item).data.contentEquals(itemIcon(corinth).data), item.name)
        }
    }

    @Test
    fun theUnderworldHasIconsOfItsOwn() {
        for (item in items(Place.UNDERWORLD)) {
            for (other in listOf(Place.CORINTH, Place.ETNA)) {
                val twin = items(other).first { it.line == item.line && it.tier == item.tier }
                assertFalse(itemIcon(item).data.contentEquals(itemIcon(twin).data), item.name)
            }
        }
    }

    @Test
    fun cairoHasIconsOfItsOwn() {
        for (item in items(Place.CAIRO)) {
            for (other in listOf(Place.CORINTH, Place.ETNA, Place.UNDERWORLD)) {
                val twin = items(other).first { it.line == item.line && it.tier == item.tier }
                assertFalse(itemIcon(item).data.contentEquals(itemIcon(twin).data), item.name)
            }
        }
    }

    @Test
    fun mountFujiHasIconsOfItsOwn() {
        for (item in items(Place.FUJI)) {
            for (other in listOf(Place.CORINTH, Place.ETNA, Place.UNDERWORLD, Place.CAIRO)) {
                val twin = items(other).first { it.line == item.line && it.tier == item.tier }
                assertFalse(itemIcon(item).data.contentEquals(itemIcon(twin).data), item.name)
            }
        }
    }

    @Test
    fun everyWorldIsDrawnNow() {
        assertEquals("the hill on the volcano", Place.ETNA.note)
        assertEquals("the hill in Tartarus", Place.UNDERWORLD.note)
        assertEquals("the pyramid by the Nile", Place.CAIRO.note)
        assertEquals("the hill by the white peak", Place.FUJI.note)
    }

    private val allItems = Place.entries.flatMap { items(it) }

    /** A screen whose vision asks: it came, its question is typed out, and the answers show. */
    private fun asking(s: Screen = screen()): Screen {
        s.game.summonVision()
        repeat(600) { if (s.game.vision?.phase == Vision.Phase.COMING) s.update(1 / 60.0) }
        assertEquals(Vision.Phase.ASKING, s.game.vision?.phase)
        return s
    }

    @Test
    fun aVoiceFramesThePanel() {
        val s = screen()
        val l = s.layout!!
        fun edge() = s.render()[l.quote.x - 2, l.quote.y + 5]
        assertNotEquals(VOICE_FRAME, edge())
        s.game.summonVision()
        repeat(180) { s.update(1 / 60.0) }
        assertEquals(VOICE_FRAME, edge())
    }

    @Test
    fun openingTheAgoraEndsTheVision() {
        val s = asking()
        s.tap(s.layout!!.coinIcon)
        assertNull(s.game.vision)
    }

    @Test
    fun openingTheHelpEndsTheVision() {
        val s = asking()
        s.tap(s.layout!!.helpIcon)
        assertNull(s.game.vision)
    }

    @Test
    fun openingTheWorldsEndsTheVision() {
        val s = asking()
        s.tap(s.layout!!.dayLabel(s.game.day))
        assertTrue(s.worldsOpen)
        assertNull(s.game.vision)
    }

    @Test
    fun theDevScreenSummonsAVision() {
        val s = devScreen()
        s.openDev()
        s.tap(s.layout!!.devVision)
        assertNotNull(s.game.vision)
        assertFalse(s.agoraOpen)
        assertFalse(s.devOpen)
    }

    /** Stone sets the next stone of the tale just ahead and closes the Agora: its popup shows at once. */
    @Test
    fun theDevScreenSetsAStoneJustAhead() {
        val s = devScreen()
        s.openDev()
        s.tap(s.layout!!.devStone)
        assertFalse(s.agoraOpen)
        assertFalse(s.devOpen)
        assertEquals(SISYPHUS.parts[0].texts[0], s.game.telling)
    }

    @Test
    fun theDevScreenBringsAVisionOnTheNextTurn() {
        val s = devScreen()
        s.openDev()
        s.tap(s.layout!!.devNextVision)
        assertFalse(s.agoraOpen)
        repeat(60) { s.update(1 / 60.0) }
        assertNull(s.game.vision)
        s.turnALittle()
        assertNotNull(s.game.vision)
    }

    @Test
    fun theVisionButtonsFitTheSmallestPhone() {
        val l = smallest().layout!!
        val row = l.agoraRow(4)
        assertTrue(l.devVision.y > row.y && l.devNextVision.y == l.devVision.y)
        assertTrue(l.devNextVision.x > l.devVision.right)
        assertTrue(l.devNextVision.right <= row.right, "the buttons end at ${l.devNextVision.right}, the row at ${row.right}")
    }

    @Test
    fun theVisionDarkensTheScene() {
        val s = screen()
        val l = s.layout!!
        /** The light of the bottom third of the scene, away from the god. */
        fun light(): Long {
            val px = s.render()
            return (l.scene.bottom - l.scene.h / 3 until l.scene.bottom).sumOf { y -> (l.scene.x until l.scene.right).sumOf { x -> luminance(px[x, y]).toLong() } }
        }
        val before = light()
        s.game.summonVision()
        repeat(90) { s.update(1 / 60.0) }
        val after = light()
        assertTrue(after < before * 0.6, "before $before, after $after")
    }

    @Test
    fun theLongestAnswersFitTheSmallestPhone() {
        val l = smallest().layout!!
        /** The columns of the answer pixels when only [left], or only [right], stands in its corner. */
        fun columns(left: String, right: String): List<Int> {
            val px = Pixels(l.scene.w, l.scene.h).apply { clear(rgb(0x404040)) }
            val v = Vision(Question("Who?", left, right, "Yes.", "No."), God.HERMES, rightOnLeft = true)
            v.update(1.0)
            v.ask()
            drawVision(px, v)
            return (0 until px.height).flatMap { y -> (0 until px.width).filter { x -> px[x, y] == ANSWER } }
        }
        val left = columns("Marcus Aurelius", "")
        val right = columns("", "Publilius Syrus")
        assertTrue(left.isNotEmpty() && right.isNotEmpty())
        assertTrue(left.min() > 0 && left.max() < l.scene.w / 2, "left answer from ${left.min()} to ${left.max()}")
        assertTrue(right.min() >= l.scene.w / 2 && right.max() < l.scene.w - 1, "right answer from ${right.min()} to ${right.max()}")
    }

    @Test
    fun theHelpersMakeRoomForTheAnswers() {
        /** Where the answers of a vision that asks are drawn, with or without the three helpers in the corner of the scene. */
        fun answers(store: Map<String, Double>): Set<Pair<Int, Int>> {
            val s = asking(screen(MemoryStore(store)))
            val l = s.layout!!
            val px = s.render()
            return (l.scene.y until l.scene.bottom).flatMap { y -> (l.scene.x until l.scene.right).filter { px[it, y] == ANSWER }.map { it to y } }.toSet()
        }
        assertEquals(answers(emptyMap()), answers(mapOf("shop.beside" to 3.0, "shop.alone" to 3.0, "shop.blessing" to 3.0)))
    }

    @Test
    fun aCoastingKnobBringsNoVisionUnderTheAgora() {
        val s = screen(MemoryStore(mapOf("nextVision" to 0.6)))
        val game = s.game
        var touch = 0.0
        game.knob.grab(touch)
        repeat(6) {
            touch += 0.5
            game.knob.drag(touch)
            s.update(1 / 60.0)
        }
        game.knob.release()
        s.tap(s.layout!!.coinIcon)
        assertTrue(s.agoraOpen)
        repeat(180) { s.update(1 / 60.0) }
        assertNull(game.vision)
    }

    /** The Agora and the other screens cover the panel of the wise words, so that the news of a labor waits. */
    @Test
    fun aScreenCoversThePanel() {
        val s = screen()
        val l = s.layout!!
        s.update(1 / 60.0)
        assertFalse(s.game.covered)
        s.tap(l.coinIcon)
        s.update(1 / 60.0)
        assertTrue(s.game.covered)
        s.tap(l.coinIcon)
        s.update(1 / 60.0)
        assertFalse(s.game.covered)
    }

    @Test
    fun noClimbsUnderTheDay() {
        fun under(climbs: Double): List<Int> {
            val s = Screen(Game(MemoryStore(mapOf("climbs" to climbs, "laborsSeen" to 20.0)), seed = 1), seed = 2).apply { resize(1179, 2556, 177, 102) }
            val d = s.layout!!.dayLabel(s.game.day)
            val px = s.render()
            return (d.bottom until d.bottom + 12).flatMap { y -> (d.x until d.x + 24).map { px[it, y] } }
        }
        assertEquals(under(0.0), under(7.0))
    }

    @Test
    fun aDotStandsAfterTheDayWhileALaborIsNew() {
        val s = Screen(Game(MemoryStore(mapOf("climbs" to 1.0)), seed = 1), seed = 2).apply { resize(1179, 2556, 177, 102) }
        val d = s.layout!!.dayLabel(s.game.day)
        fun after() = s.render().let { px -> (d.y until d.bottom).flatMap { y -> (d.right until d.right + 6).map { px[it, y] } } }
        val dot = after()
        s.game.seeLabors()
        assertNotEquals(dot, after())
    }

    @Test
    fun theHelpFitsTheSmallestPhone() {
        val s = Screen(Game(MemoryStore(mapOf("obols" to 9999.0)), seed = 1), seed = 2).apply { resize(1080, 2340, 144, 102) }
        val l = s.layout!!
        s.tap(l.helpIcon)
        val px = s.render()
        val cream = rgb(0xF1E4C8)
        val below = (l.scene.bottom until l.quote.y).filter { y -> (0 until px.width).any { px[it, y] == cream } }
        assertTrue(below.isEmpty(), "the notes of the help reach row ${below.firstOrNull()}, the scene ends at ${l.scene.bottom}")
        assertTrue("labors" in DAY_NOTE)
    }

    /** A tap on a tab that is not shown shows it; a tap on the shown tab closes the screen, which opens again on it. */
    @Test
    fun theWorldsScreenHasThreeTabs() {
        val s = screen()
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        assertTrue(s.worldsOpen)
        assertFalse(s.laborsShown)
        assertFalse(s.jokersShown)
        assertEquals(Touch.WORLDS, s.tap(l.laborsTab))
        assertTrue(s.laborsShown)
        assertTrue(s.worldsOpen)
        assertEquals(Touch.WORLDS, s.tap(l.jokersTab))
        assertTrue(s.jokersShown)
        assertFalse(s.laborsShown)
        assertEquals(Touch.WORLDS, s.tap(l.jokersTab))
        assertFalse(s.worldsOpen)
        s.tap(l.dayLabel(s.game.day))
        assertTrue(s.jokersShown)
        s.tap(l.laborsTab)
        assertEquals(Touch.WORLDS, s.tap(l.laborsTab))
        assertFalse(s.worldsOpen)
        s.tap(l.dayLabel(s.game.day))
        assertTrue(s.laborsShown)
        s.tap(l.worldsTab)
        assertFalse(s.laborsShown)
        assertFalse(s.jokersShown)
        assertTrue(s.worldsOpen)
    }

    /** Each column of "Jokers" shows the Jokers tab from the Worlds tab, and the room of the dot of Labors stays with Labors. */
    @Test
    fun theWholeJokersTabTakesItsTaps() {
        for (dx in 0 until PixelFont.width(JOKERS_TITLE)) {
            val s = smallest()
            val l = s.layout!!
            s.tap(l.dayLabel(s.game.day))
            s.tap(l.jokersTab.x + dx + 0.5f, l.jokersTab.y + l.jokersTab.h / 2f)
            assertTrue(s.jokersShown, "column $dx of Jokers")
        }
        val s = smallest()
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        s.tap(l.laborsTab)
        s.tap(l.laborsTab.right - 2f, l.laborsTab.y + l.laborsTab.h / 2f)
        assertFalse(s.jokersShown)
    }

    /**
     * The Worlds screen shows no obols. A tap where they stand on the other screens is a tap on its title row: it closes an open card,
     * then the screen, and opens no Agora.
     */
    @Test
    fun theWorldsScreenShowsNoObols() {
        fun corner(obols: Double): List<Int> {
            val s = screen(MemoryStore(mapOf("obols" to obols)))
            s.tap(s.layout!!.dayLabel(s.game.day))
            val px = s.render()
            val r = s.layout!!.coinIcon
            return (r.y until r.bottom).flatMap { y -> (r.x until r.right).map { px[it, y] } }
        }
        assertEquals(corner(0.0), corner(345.0))
        assertTrue(corner(345.0).none { it in COIN_COLORS.values })
        val s = screen(MemoryStore(mapOf("met.0" to 1.0, "jokersSeen" to 1.0)))
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        s.tap(l.jokersTab)
        s.tap(l.jokerCards[0])
        assertNotNull(s.openCard)
        s.tap(l.coinIcon)
        assertNull(s.openCard)
        assertTrue(s.worldsOpen)
        s.tap(l.coinIcon)
        assertFalse(s.worldsOpen)
        assertFalse(s.agoraOpen)
    }

    /** The cards of the jokers met are face up, and a tap opens one; the others are face down, and a tap does nothing. */
    @Test
    fun aCardOpensOnceItsJokerIsMet() {
        val s = screen(MemoryStore(mapOf("met.2" to 1.0, "jokersSeen" to 4.0)))
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        s.tap(l.jokersTab)
        assertEquals(Touch.NONE, s.tap(l.jokerCards[0]))
        assertNull(s.openCard)
        assertEquals(Touch.WORLDS, s.tap(l.jokerCards[2]))
        assertSame(JOKERS[2], s.openCard)
        assertEquals(Touch.WORLDS, s.tap(l.jokerCards[0]))
        assertNull(s.openCard)
        assertTrue(s.worldsOpen)
        s.tap(l.jokerCards[2])
        assertTrue(s.back())
        assertNull(s.openCard)
        assertTrue(s.worldsOpen)
        s.tap(l.jokerCards[2])
        s.tap(l.laborsTab)
        assertNull(s.openCard)
    }

    /** A card face shows only its joker, on paper: no rank in a corner, and none on the open card. */
    @Test
    fun theCardsShowNoRank() {
        val s = screen(MemoryStore(JOKERS.associate { "met.${it.id}" to 1.0 } + ("jokersSeen" to ((1 shl JOKERS.size) - 1).toDouble())))
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        s.tap(l.jokersTab)
        val rank = rgb(0xC8643C)
        fun inks(px: Pixels, r: Rect) = (r.y until r.bottom).flatMap { y -> (r.x until r.right).map { px[it, y] } }
        val tab = s.render()
        for ((i, card) in l.jokerCards.withIndex()) assertTrue(inks(tab, card).none { it == rank }, "card $i")
        s.tap(l.jokerCards[9])
        val open = s.render()
        assertTrue(inks(open, Rect(l.openCard.x + 1, l.openCard.y + 1, 20, 20)).none { it == rank })
    }

    /** The cards show which jokers the player met, and the open card shows how many times. */
    @Test
    fun theCardsShowTheJokersMet() {
        fun tab(vararg keys: Pair<String, Double>): IntArray {
            val s = screen(MemoryStore(mapOf("jokersSeen" to ((1 shl JOKERS.size) - 1).toDouble(), *keys)))
            s.tap(s.layout!!.dayLabel(s.game.day))
            s.tap(s.layout!!.jokersTab)
            return s.render().data.copyOf()
        }
        fun open(met: Double): IntArray {
            val s = screen(MemoryStore(mapOf("jokersSeen" to ((1 shl JOKERS.size) - 1).toDouble(), "met.9" to met)))
            s.tap(s.layout!!.dayLabel(s.game.day))
            s.tap(s.layout!!.jokersTab)
            s.tap(s.layout!!.jokerCards[9])
            return s.render().data.copyOf()
        }
        assertFalse(tab().contentEquals(tab("met.9" to 1.0)))
        assertTrue(tab("met.9" to 1.0).contentEquals(tab("met.9" to 2.0)))
        assertFalse(open(1.0).contentEquals(open(2.0)))
    }

    /** A joker met whose card was not seen yet: a dot by the day, and the day opens the Jokers tab, which takes the dot away. */
    @Test
    fun aNewCardShowsTheDotUntilTheJokersTabShows() {
        fun scene(seen: Double) = screen(MemoryStore(mapOf("met.0" to 1.0, "jokersSeen" to seen))).render().data.copyOf()
        assertFalse(scene(0.0).contentEquals(scene(1.0)))
        val s = screen(MemoryStore(mapOf("met.0" to 1.0)))
        assertTrue(s.game.newJokers)
        s.tap(s.layout!!.dayLabel(s.game.day))
        assertTrue(s.jokersShown)
        assertFalse(s.game.newJokers)
    }

    /** A new labor comes first: the day opens the Labors tab, and the next time the Jokers tab. */
    @Test
    fun aNewLaborComesBeforeANewCard() {
        val s = screen(MemoryStore(mapOf("climbs" to 1.0, "met.0" to 1.0)))
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        assertTrue(s.laborsShown)
        s.back()
        s.tap(l.dayLabel(s.game.day))
        assertTrue(s.jokersShown)
    }

    /**
     * The card of a rare joker has a gold banner across its top-right corner, face up and face down, and so has his open card.
     * The other cards have none.
     */
    @Test
    fun rareCardsHaveACornerBanner() {
        val gold = rgb(0xD2A462)
        fun corner(px: Pixels, r: Rect, size: Int) = (r.y until r.y + size).flatMap { y -> (r.right - size until r.right).map { px[it, y] } }
        for (met in listOf(true, false)) {
            val keys = if (met) JOKERS.associate { "met.${it.id}" to 1.0 } + ("jokersSeen" to ((1 shl JOKERS.size) - 1).toDouble()) else emptyMap()
            val s = screen(MemoryStore(keys))
            val l = s.layout!!
            s.tap(l.dayLabel(s.game.day))
            s.tap(l.jokersTab)
            val px = s.render()
            for (joker in JOKERS) assertEquals(joker.rare, gold in corner(px, l.jokerCards[joker.id], 12), "${joker.name}, met: $met")
            if (!met) continue
            for (joker in listOf(JOKERS[0], JOKERS[9])) {
                s.tap(l.jokerCards[joker.id])
                assertEquals(joker.rare, gold in corner(s.render(), l.openCard, 24), joker.name)
                s.tap(l.jokerCards[joker.id])
            }
        }
    }

    /** The cards lie in rows of four, as a hand of three rows. */
    @Test
    fun theCardsLieInRowsOfFour() {
        val cards = screen().layout!!.jokerCards
        for ((i, card) in cards.withIndex()) {
            assertEquals(cards[i % 4].x, card.x, "card $i")
            assertEquals(cards[i / 4 * 4].y, card.y, "card $i")
        }
        assertTrue(cards[4].y > cards[0].bottom)
    }

    @Test
    fun theCardsFitTheSmallestPhone() {
        val l = smallest().layout!!
        assertEquals(JOKERS.size, l.jokerCards.size)
        val cards = l.jokerCards
        for ((i, card) in cards.withIndex()) {
            assertTrue(card.x >= l.worldList.x && card.right <= l.worldList.right && card.y >= l.jokersMet.bottom && card.bottom <= l.worldList.bottom, "card $i")
            for (other in cards.drop(i + 1)) assertTrue(card.right <= other.x || other.right <= card.x || card.bottom <= other.y || other.bottom <= card.y)
        }
        for ((joker, sprite) in JOKERS.zip(JOKER_SPRITES.map { it.outlined(rgb(0x2A1A0C)) })) {
            assertTrue(sprite.width + 4 <= cards[0].w, joker.name)
            assertTrue(sprite.height + 4 <= cards[0].h, joker.name)
            assertTrue(PixelFont.width(joker.name) <= l.openCardText.w, joker.name)
            assertTrue(PixelFont.wrap(joker.about, l.openCardText.w).size <= Layout.ABOUT_LINES, joker.name)
            assertTrue(2 * sprite.height <= Layout.OPEN_SPRITE, joker.name)
        }
        assertTrue(l.openCard.y >= l.worldList.y && l.openCard.bottom <= l.worldList.bottom)
    }

    @Test
    fun theDevScreenBringsAJokerAtOnce() {
        val s = devScreen()
        s.openDev()
        s.tap(s.layout!!.devJoker)
        assertFalse(s.agoraOpen)
        assertSame(JOKERS[0], s.game.joker)
        assertNotNull(s.game.jesting)
    }

    @Test
    fun theJokerButtonFitsTheSmallestPhone() {
        val l = smallest().layout!!
        assertEquals(l.devWorlds.y, l.devJoker.y)
        assertTrue(l.devJoker.x > l.devWorlds.right)
        assertTrue(l.devJoker.right <= l.agoraRow(3).right, "the buttons end at ${l.devJoker.right}, the row at ${l.agoraRow(3).right}")
    }

    @Test
    fun theDayOpensTheLaborsWhenTheDotShows() {
        val s = screen(MemoryStore(mapOf("climbs" to 1.0)))
        assertTrue(s.game.newLabors)
        s.tap(s.layout!!.dayLabel(s.game.day))
        assertTrue(s.laborsShown)
        assertFalse(s.game.newLabors)
    }

    @Test
    fun theTabsFitTheSmallestPhone() {
        val l = smallest().layout!!
        assertTrue(l.worldsTab.right + 4 <= l.laborsTab.x)
        assertTrue(l.laborsTab.right + 4 <= l.jokersTab.x)
        assertTrue(l.jokersTab.right <= l.agoraTitle.right, "the tabs end at ${l.jokersTab.right}, the row at ${l.agoraTitle.right}")
    }

    @Test
    fun everyLaborFitsTheSmallestPhone() {
        val l = smallest().layout!!
        assertEquals(Category.entries.size, l.laborCards.size)
        for (labor in LABORS) {
            val name = PixelFont.width(labor.name) + 4 + PixelFont.width(progress(labor.goal - 1.0, labor.goal))
            assertTrue(name <= l.laborText, "${labor.name}: $name > ${l.laborText}")
            assertTrue(9 + PixelFont.width(labor.name) <= l.laborText, "done: ${labor.name}")
            assertTrue(PixelFont.width(shortCount(labor.goal)) + 2 <= l.laborText / 5, "tier ${labor.goal}")
        }
    }

    @Test
    fun theLaborListScrollsToTheLastCategory() {
        val s = smallest()
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        s.tap(l.laborsTab)
        assertTrue(l.laborScroll > 0)
        val first = l.laborCards[0]
        assertEquals(Touch.LIST, s.tap(first))
        s.drag(first.x + 10f, first.y + 10f - 2000f)
        assertEquals(Touch.NONE, s.release())
        assertEquals(l.laborScroll, s.worldsScroll)
        assertTrue(l.laborCards.last().bottom - l.laborScroll <= l.worldList.bottom)
    }

    @Test
    fun restoreAndTheIdStayOnBothTabs() {
        val s = Screen(Game(MemoryStore(), seed = 1), seed = 2, billing = FakeBilling(), build = "41").apply { resize(1179, 2556, 177, 102) }
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        fun bottom() = s.render().let { px -> (l.restore.y until l.restore.bottom).flatMap { y -> (0 until px.width).map { px[it, y] } } }
        val worlds = bottom()
        s.tap(l.laborsTab)
        assertEquals(worlds, bottom())
    }

    @Test
    fun theDevScreenBringsALaborOnTheNextClick() {
        val s = devScreen()
        s.openDev()
        s.tap(s.layout!!.devNextLabor)
        assertFalse(s.agoraOpen)
        val done = s.game.laborsDone
        s.turnALittle()
        assertEquals(done + 1, s.game.laborsDone)
    }

    @Test
    fun theLaborButtonFitsTheSmallestPhone() {
        val l = smallest().layout!!
        assertEquals(l.devVision.y, l.devNextLabor.y)
        assertTrue(l.devNextLabor.x > l.devNextVision.right)
        assertTrue(l.devNextLabor.right <= l.agoraRow(4).right, "the buttons end at ${l.devNextLabor.right}, the row at ${l.agoraRow(4).right}")
    }

    /** A tap a little under the tab that is not shown still shows it, as a tap near the day or the coin does. */
    @Test
    fun aTapJustUnderTheOtherTabStillSwitches() {
        val s = smallest()
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        assertEquals(Touch.WORLDS, s.tap(l.laborsTab.x + l.laborsTab.w / 2f, l.laborsTab.bottom + 2f))
        assertTrue(s.worldsOpen)
        assertTrue(s.laborsShown)
        s.tap(l.worldsTab.x + l.worldsTab.w / 2f, l.worldsTab.bottom + 2f)
        assertTrue(s.worldsOpen)
        assertFalse(s.laborsShown)
    }

    private fun relativeLuminance(c: Int): Double {
        fun lin(v: Int) = (v / 255.0).let { if (it <= 0.03928) it / 12.92 else ((it + 0.055) / 1.055).pow(2.4) }
        return 0.2126 * lin(c shr 16 and 0xFF) + 0.7152 * lin(c shr 8 and 0xFF) + 0.0722 * lin(c and 0xFF)
    }

    /** Most tiers stay not done for months, so their gray numbers must still read well: 4.5 to 1 against the background. */
    @Test
    fun aLaborNotDoneIsEasyToRead() {
        val s = smallest()
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        s.tap(l.laborsTab)
        val card = l.laborCards[0]
        val px = s.render()
        val numbers = card.y + Layout.LABOR_TIERS
        val text = (numbers until numbers + PixelFont.CAP_HEIGHT).flatMap { y -> (card.x + Layout.LABOR_PAD until card.right - 1).map { px[it, y] } }.maxBy(::relativeLuminance)
        val contrast = (relativeLuminance(text) + 0.05) / (relativeLuminance(BACKGROUND) + 0.05)
        assertTrue(contrast >= 4.5, "contrast $contrast")
    }

    /** The list of the Worlds screen ends with a gap above Restore and the id, so that a row cut by the scroll never touches them. */
    @Test
    fun theListStopsAboveTheBottomRow() {
        for (s in listOf(smallest(), screen())) {
            val l = s.layout!!
            assertTrue(l.worldList.bottom + 4 <= l.restore.y, "the list ends at ${l.worldList.bottom}, Restore starts at ${l.restore.y}")
            assertTrue(l.worldList.bottom + 4 <= l.supportId.y)
        }
    }

    /** At the bottom of the Agora, a note says that all 15 items lead to the top. It goes when everything is bought, and the dev screen has none. */
    @Test
    fun theAgoraSaysToBuyAllForTheTop() {
        fun note(s: Screen): List<Int> {
            val r = s.layout!!.agoraNote
            val px = s.render()
            return (r.y until r.bottom).flatMap { y -> (r.x until r.right).map { px[it, y] } }
        }
        fun agora(store: MemoryStore) = screen(store).also { it.tap(it.layout!!.coinIcon) }
        val none = note(agora(MemoryStore(allBought)))
        assertNotEquals(none, note(agora(MemoryStore())))
        assertEquals(none, note(devScreen().also { it.openDev() }))
    }

    @Test
    fun theAgoraNoteFitsTheSmallestPhone() {
        val l = smallest().layout!!
        assertTrue(NOTE_HERO.width + 3 + PixelFont.width(AGORA_NOTE) <= l.agoraNote.w)
        assertTrue(NOTE_HERO.height <= l.agoraNote.h)
        assertTrue(l.agoraNote.right + 2 <= l.agoraSound.x)
        assertTrue(AGORA_NOTE.all(PixelFont::hasGlyph))
    }

    /** Each category has one card: its next labor, a bar of how far it has come, and a dot for each tier, filled when done. */
    @Test
    fun eachCardShowsItsNextLaborAndItsProgress() {
        fun card(climbs: Double, from: Int, to: Int): List<Int> {
            val s = screen(MemoryStore(mapOf("climbs" to climbs, "laborsSeen" to 20.0)))
            val l = s.layout!!
            s.tap(l.dayLabel(s.game.day))
            s.tap(l.laborsTab)
            val r = l.laborCards[0]
            val px = s.render()
            return (r.y + from until r.y + to).flatMap { y -> (r.x + 1 until r.right - 1).map { px[it, y] } }
        }
        val bar = Layout.LABOR_BAR to Layout.LABOR_BAR + 3
        val dots = Layout.LABOR_DOTS to Layout.LABOR_DOTS + 5
        val name = 3 to 3 + PixelFont.CAP_HEIGHT
        assertNotEquals(card(5.0, bar.first, bar.second), card(6.0, bar.first, bar.second))
        assertEquals(card(5.0, dots.first, dots.second), card(6.0, dots.first, dots.second))
        assertNotEquals(card(9.0, dots.first, dots.second), card(10.0, dots.first, dots.second))
        assertNotEquals(card(9.0, name.first, name.second), card(10.0, name.first, name.second))
    }

    @Test
    fun eachGodHasItsOwnPicture() {
        val pictures = God.entries.map { god ->
            val v = Vision(whoSaid(SAYINGS[0], "Seneca"), god, rightOnLeft = true).apply { update(1.0) }
            Pixels(120, 120).apply { clear(rgb(0x404040)); drawVision(this, v) }.data.toList()
        }
        assertEquals(God.entries.size, pictures.toSet().size)
    }
}
