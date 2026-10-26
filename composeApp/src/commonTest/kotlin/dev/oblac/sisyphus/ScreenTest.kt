package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.PX_PER_TURN
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.SUN_CAP
import dev.oblac.sisyphus.game.items
import dev.oblac.sisyphus.gfx.PixelFont
import dev.oblac.sisyphus.gfx.TRANSPARENT
import dev.oblac.sisyphus.scene.Ending
import dev.oblac.sisyphus.ui.Layout
import dev.oblac.sisyphus.ui.OBOL_FILL
import dev.oblac.sisyphus.ui.Rect
import dev.oblac.sisyphus.ui.itemIcon
import dev.oblac.sisyphus.ui.worldPreview
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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
    fun onlyThePriceAndTheSquaresShowWhatYouCannotAfford() {
        /** The pixels of the rock row: its price, its three squares, and the rest. */
        fun rockRow(obols: Double): Map<String, List<Int>> {
            val s = screen(MemoryStore(mapOf("obols" to obols)))
            val l = s.layout!!
            s.tap(l.coinIcon)
            val px = s.render()
            val r = l.agoraRow(Line.ROCK.ordinal)
            val nameY = r.y + PixelFont.LINE_HEIGHT
            val noteY = nameY + PixelFont.LINE_HEIGHT
            return (r.y until r.bottom).flatMap { y -> (r.x until r.right).map { x -> x to y } }.groupBy({ (x, y) ->
                when {
                    x >= r.right - 20 && y in nameY until noteY -> "price"
                    x >= r.right - 11 && y >= noteY -> "squares"
                    else -> "rest"
                }
            }, { (x, y) -> px[x, y] })
        }
        val poor = rockRow(0.0)
        val rich = rockRow(100.0)
        assertEquals(rich["rest"], poor["rest"])
        assertNotEquals(rich["price"], poor["price"])
        assertNotEquals(rich["squares"], poor["squares"])
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
        assertEquals(Touch.DEV, s.tap(s.layout!!.devReset))
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
        assertTrue(l.devReset.bottom <= l.agoraRow(1).bottom)
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

    @Test
    fun theBarShowsHowFarTheNextObolHasCome() {
        fun filled(obols: Double): Int {
            val s = screen(MemoryStore(mapOf("obols" to obols)))
            val px = s.render()
            val bar = s.layout!!.obolBar
            return (bar.x + 1 until bar.right - 1).count { px[it, bar.y + bar.h / 2] == OBOL_FILL }
        }
        val inner = screen().layout!!.obolBar.w - 2
        assertEquals(0, filled(3.0))
        assertEquals(inner / 4, filled(0.25))
        assertEquals(inner / 2, filled(2.5))
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

    @Test
    fun atTheTopTheRockRollsAwayTheScreenGoesBlackAndATapStartsOver() {
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
        repeat((2.0 / dt).toInt()) { s.update(dt) }
        assertNull(s.ending)
        assertEquals(Touch.KNOB, s.tap(l.knobX + l.knobR * 0.6f, l.knobY + 0.5f))
    }

    @Test
    fun theClimbsShowUnderTheDay() {
        fun underDay(climbs: Int): List<Int> {
            val s = screen(MemoryStore(mapOf("climbs" to climbs.toDouble())))
            val px = s.render()
            val d = s.layout!!.dayLabel(s.game.day)
            return (d.bottom until d.bottom + 10).flatMap { y -> (d.x until d.right).map { x -> px[x, y] } }
        }
        assertNotEquals(underDay(0), underDay(2))
    }

    @Test
    fun theDevScreenTakesHimNearTheTop() {
        val s = devScreen()
        s.openDev()
        assertEquals(Touch.DEV, s.tap(s.layout!!.devTop))
        assertTrue(Line.entries.all { s.game.shop.tier(it) == 3 })
        val left = s.game.summit!! - s.game.position
        assertTrue(left > 0 && left <= 2 * SUN_CAP * PX_PER_TURN, "left: $left")
    }

    @Test
    fun tappingTheDayOpensTheWorlds() {
        val s = screen()
        val l = s.layout!!
        assertEquals(Touch.WORLDS, s.tap(l.dayLabel(s.game.day)))
        assertTrue(s.worldsOpen)
        assertFalse(s.agoraOpen)
        assertEquals(Touch.WORLDS, s.tap(l.agoraTitle))
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
    fun theWorldYouAreInIsNotOfferedAgain() {
        val s = screen()
        val l = s.layout!!
        s.tap(l.dayLabel(s.game.day))
        assertEquals(Touch.NONE, s.choose(l.worldRow(Place.CORINTH.ordinal)))
        assertNull(s.moving)
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
        assertEquals(Place.UNDERWORLD, s.moving)
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
        val l = smallest().layout!!
        assertTrue(l.worldRow(0).y > l.agoraTitle.bottom)
        assertTrue(l.worldRow(Place.entries.size - 1).bottom <= l.agoraSound.y)
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
    fun everyWorldIsDrawnNow() {
        assertEquals("the hill on the volcano", Place.ETNA.note)
        assertEquals("the hill in Tartarus", Place.UNDERWORLD.note)
    }

    private val allItems = Place.entries.flatMap { items(it) }
}
