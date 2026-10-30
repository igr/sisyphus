package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Config
import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.ISIS_AND_OSIRIS
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.ORPHEUS
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.PX_PER_TURN
import dev.oblac.sisyphus.game.SISYPHUS
import dev.oblac.sisyphus.game.START_TIME
import dev.oblac.sisyphus.game.TALES
import dev.oblac.sisyphus.game.Vision
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StationsTest {
    /** The effort of [day] at [time] of it. */
    private fun effortAt(day: Int, time: Double = 0.5) = ((day - 1) + time - START_TIME) * DAY_LENGTH

    /** A Corinth game on day 3, when the tale has begun. */
    private fun store(vararg keys: Pair<String, Double>) =
        MemoryStore(mapOf("obols" to 10.0, "nextVision" to 600.0, "effort" to effortAt(3), *keys))

    private fun game(store: MemoryStore = store()) = Game(store, seed = 1, wall = { 0.0 })

    /** Turns the knob a frame at a time, about a turn a second, for up to [seconds], until [done]. Tells whether it came. */
    private fun Game.turnUntil(seconds: Double = 120.0, done: () -> Boolean): Boolean {
        var touch = 0.0
        knob.grab(touch)
        repeat((seconds / DT).toInt()) {
            if (done()) return true
            touch += 0.1
            knob.drag(touch)
            update(DT)
        }
        return done()
    }

    /** Where the popup of a stone at [at] opens: [Config.NEAR_TURNS] of walking before the rock reaches it. */
    private fun opens(at: Double) = at - Config.NEAR_TURNS * PX_PER_TURN

    /** How far he walks while one text of a part shows. */
    private val read = Config.READ_TURNS * PX_PER_TURN

    /** All the tales told: one bit for each tale. */
    private val allTold = (1 shl TALES.size) - 1

    /** 14 items bought: all but the last of Beauty. */
    private val fourteen = arrayOf("shop.rock" to 3.0, "shop.beside" to 3.0, "shop.alone" to 3.0, "shop.blessing" to 3.0, "shop.beauty" to 2.0)

    /** The vision that came asks, once it is fully in. */
    private fun Game.untilAsked() {
        repeat(600) { if (vision?.phase == Vision.Phase.COMING) update(DT) }
        assertEquals(Vision.Phase.ASKING, vision?.phase)
    }

    /** A new touch turns a quarter turn toward the right answer, or toward the wrong one. */
    private fun Game.answer(right: Boolean) {
        val toLeft = vision!!.rightOnLeft == right
        turn(if (toLeft) -PI / 2 else PI / 2)
    }

    @Test
    fun theTaleBeginsOnDayThreeWithoutAPurchase() {
        val g = game()
        g.update(DT)
        assertEquals(g.position + 360.0, g.nextStele!!, 1e-9)
        assertEquals(0, g.steles.single().part)
        assertTrue(g.turnUntil { g.telling == SISYPHUS.parts[0].texts[0] })
    }

    /** Before day 3, no stone stands. The parts of the items bought then come from day 3, one after the other. */
    @Test
    fun noStoneBeforeDayThree() {
        val g = game(store("effort" to effortAt(2, 0.9)))
        assertTrue(g.buy(Line.GODS))
        assertTrue(g.buy(Line.BEAUTY))
        g.update(DT)
        assertNull(g.nextStele)
        assertTrue(g.turnUntil { g.day == 3 && g.nextStele != null })
        assertEquals(0, g.steles.last().part)
        assertTrue(g.turnUntil(300.0) { g.stelesTold == 3 })
    }

    @Test
    fun aStoneStandsAheadWhenAnItemIsBought() {
        val g = game()
        assertTrue(g.buy(Line.GODS))
        assertEquals(g.position + 360.0, g.nextStele!!, 1e-9)
        assertEquals(0, g.steles.single().part)
    }

    /** A popup over the stone tells its part while he is near it: from 3 turns before the rock reaches it, for 6 turns. Then the part is told. */
    @Test
    fun theStoneTellsItsPartWhileHeIsNearIt() {
        val g = game()
        g.buy(Line.GODS)
        val at = g.nextStele!!
        assertTrue(g.turnUntil { g.telling != null })
        assertTrue(g.position - opens(at) in 0.0..1.0, "opened at ${g.position}")
        assertEquals(SISYPHUS.parts[0].texts[0], g.telling)
        assertEquals(0, g.stelesTold)
        assertTrue(g.turnUntil { g.stelesTold == 1 })
        assertTrue(g.position - (opens(at) + read) in 0.0..1.0, "told at ${g.position}")
        assertNull(g.telling)
        assertEquals(at, g.steles.first().at)
    }

    @Test
    fun thePanelTellsNoPartOfTheTale() {
        val g = game()
        g.buy(Line.GODS)
        assertTrue(g.turnUntil {
            assertTrue(TALES.none { tale -> tale.parts.any { g.quotes.quote.text in it.texts } }, g.quotes.quote.text)
            g.stelesTold == 1
        })
    }

    @Test
    fun thePartsComeInOrderOnceEach() {
        val g = game()
        for (line in listOf(Line.GODS, Line.BEAUTY, Line.ROCK)) assertTrue(g.buy(line))
        val shown = mutableListOf<String>()
        val stones = mutableListOf<Double>()
        assertTrue(g.turnUntil(400.0) {
            assertTrue(g.steles.count { it.at > g.position } <= 1)
            if (g.stelesTold < 3) g.nextStele?.let { if (stones.lastOrNull() != it) stones += it }
            g.telling?.let { if (shown.lastOrNull() != it) shown += it }
            g.stelesTold == 3
        })
        assertEquals((0..2).map { SISYPHUS.parts[it].texts[0] }, shown)
        assertEquals(3, stones.size)
        assertTrue(stones.zipWithNext().all { (a, b) -> b - a >= 360.0 }, "$stones")
    }

    @Test
    fun theGodsDoNotWalkHimToAStone() {
        var now = 0.0
        val g = Game(store(), seed = 1, wall = { now })
        g.buy(Line.GODS)
        val position = g.position
        val stele = g.nextStele
        g.pause()
        now += 10 * 3600.0
        g.resume()
        repeat(60) { g.update(DT) }
        assertEquals(position, g.position)
        assertEquals(stele, g.nextStele)
        assertEquals(0, g.stelesTold)
    }

    @Test
    fun stonesAndPartsSurviveARestart() {
        val store = store()
        val g = game(store)
        g.buy(Line.GODS)
        g.buy(Line.BEAUTY)
        assertTrue(g.turnUntil { g.stelesTold == 1 && g.nextStele != null })
        g.save()
        val again = game(store)
        assertEquals(1, again.stelesTold)
        assertEquals(g.nextStele, again.nextStele)
    }

    @Test
    fun aRestartKeepsTheStoneAndItsPopup() {
        val store = store()
        val g = game(store)
        g.buy(Line.GODS)
        assertTrue(g.turnUntil { g.telling != null })
        g.save()
        val again = game(store)
        assertEquals(g.nextStele, again.nextStele)
        assertEquals(SISYPHUS.parts[0].texts[0], again.telling)
        assertTrue(again.turnUntil { again.stelesTold == 1 })
    }

    @Test
    fun aNewClimbStartsTheTaleAgain() {
        val g = game()
        g.buy(Line.GODS)
        assertTrue(g.turnUntil { g.stelesTold == 1 })
        g.startOver()
        assertEquals(0, g.stelesTold)
        assertNull(g.nextStele)
        assertTrue(g.steles.isEmpty())
    }

    @Test
    fun aMoveDropsTheStoneAndItsPopup() {
        val g = game()
        g.buy(Line.GODS)
        assertTrue(g.turnUntil { g.telling != null })
        g.moveTo(Place.ETNA)
        assertNull(g.telling)
        assertNull(g.nextStele)
        g.addObols(10)
        assertTrue(g.buy(Line.GODS))
        g.update(DT)
        assertNull(g.nextStele)
    }

    @Test
    fun anOldSaveCountsItsItemsAsTold() {
        val g = Game(MemoryStore(mapOf("shop.rock" to 2.0, "shop.alone" to 1.0)), seed = 1, wall = { 0.0 })
        assertEquals(3, g.stelesTold)
        assertNull(g.nextStele)
        g.update(DT)
        assertNull(g.nextStele)
    }

    @Test
    fun nearTheTopPlacesOneStoneAtATime() {
        val g = game()
        g.nearTheTop()
        assertTrue(!g.turnUntil(10.0) { g.steles.size > 1 })
        assertTrue(g.turnUntil(30.0) { g.atTop })
    }

    /** A vision stops him, and the popup waits with him: the part is not told while the god is there. */
    @Test
    fun aPopupWaitsForAVision() {
        val g = game(store("shop.alone" to 1.0, "stelesTold" to 0.0, "nextStele" to 40.0))
        assertTrue(g.turnUntil { g.telling != null })
        g.summonVision()
        val position = g.position
        g.untilAsked()
        g.turn(-PI / 8)
        assertEquals(position, g.position)
        assertEquals(SISYPHUS.parts[0].texts[0], g.telling)
        g.answer(right = true)
        assertEquals(0, g.stelesTold)
        assertTrue(g.turnUntil { g.stelesTold == 1 })
    }

    /** The 15th item begins the final climb while the 14th part shows: Hermes speaks in the panel, and the popup goes on. */
    @Test
    fun theFinalClimbDoesNotCutAPart() {
        val g = game(store(*fourteen, "stelesTold" to 13.0, "nextStele" to 40.0))
        val gate = SISYPHUS.parts[13].texts
        assertTrue(g.turnUntil { g.telling == gate[0] })
        assertTrue(g.buy(Line.BEAUTY))
        assertEquals("The top is in sight. Push on.", g.quotes.quote.text)
        assertEquals(gate[0], g.telling)
        assertTrue(g.turnUntil { g.stelesTold == 14 })
        assertTrue(g.turnUntil { g.nextStele != null })
        assertEquals(14, g.steles.last().part)
    }

    /** A part with two texts shows them one after the other, each for 6 turns. */
    @Test
    fun aPartWithTwoTextsShowsThemOneAfterTheOther() {
        val g = game(store(*fourteen, "stelesTold" to 13.0, "nextStele" to 40.0))
        val gate = SISYPHUS.parts[13].texts
        assertTrue(g.turnUntil { g.telling == gate[0] })
        assertTrue(g.turnUntil { g.telling == gate[1] })
        assertTrue(g.position - (opens(40.0) + read) in 0.0..1.0, "the second at ${g.position}")
        assertEquals(13, g.stelesTold)
        assertTrue(g.turnUntil { g.stelesTold == 14 })
        assertTrue(g.position - (opens(40.0) + 2 * read) in 0.0..1.0, "told at ${g.position}")
    }

    /** A stone whose popup would still show at the top tells nothing there: the end of the climb has the scene to itself. */
    @Test
    fun noPopupAtTheTop() {
        val g = game(store(*fourteen, "shop.beauty" to 3.0, "summit" to 60.0, "stelesTold" to 14.0, "nextStele" to 70.0))
        assertTrue(g.turnUntil { g.telling != null })
        assertTrue(g.turnUntil { g.atTop })
        assertNull(g.telling)
    }

    @Test
    fun theLastPartComesBeforeTheFirstHerm() {
        val g = game(store(*fourteen, "stelesTold" to 14.0))
        assertTrue(g.buy(Line.BEAUTY))
        val start = g.position
        val herm = g.herms.first()
        assertTrue(g.nextStele!! < start + 100 * Config.SUN_CAP * PX_PER_TURN)
        assertTrue(g.turnUntil { g.stelesTold == 15 })
        assertTrue(g.position < herm, "told at ${g.position}, the herm at $herm")
    }

    /** The dev screen's jumps carry him past a stone and its popup: its part is counted, and the tale goes on. */
    @Test
    fun aJumpPastTheStoneCountsItsPart() {
        val g = game()
        g.buy(Line.GODS)
        val at = g.nextStele!!
        g.nearTheNextObol()
        assertTrue(g.position > opens(at) + read, "the jump ends at ${g.position}")
        g.update(DT)
        assertEquals(1, g.stelesTold)
        assertTrue(g.nextStele!! > g.position)
    }

    /** The dev screen's Stone sets the next stone a turn into its popup, also before it is due, so that its part shows at once. */
    @Test
    fun theDevStoneShowsTheNextPartAtOnce() {
        val g = game(store("effort" to effortAt(1), "stelesTold" to 4.0))
        g.nearTheNextStone()
        assertEquals(SISYPHUS.parts[4].texts[0], g.telling)
        assertTrue(g.nextStele!! > g.position)
        assertTrue(g.turnUntil { g.stelesTold == 5 })

        val told = game(store("stelesTold" to 15.0))
        told.nearTheNextStone()
        assertNull(told.nextStele)
    }

    @Test
    fun theFirstClimbTellsTheTaleOfSisyphus() {
        val g = Game(MemoryStore(), seed = 1, wall = { 0.0 })
        assertSame(SISYPHUS, g.tale)
        assertEquals(0, g.talesTold)
        assertSame(SISYPHUS, game(store("shop.rock" to 2.0)).tale)
    }

    @Test
    fun sisyphusComesAgainUntilItIsTold() {
        for (before in TALES) {
            val g = game(store("tale" to before.id.toDouble()))
            g.startOver()
            assertSame(SISYPHUS, g.tale)
        }
    }

    @Test
    fun aNewClimbTakesATaleNotToldYet() {
        for (told in (1 until allTold).filter { it and 1 == 1 }) for (seed in 1..5) {
            val g = Game(store("talesTold" to told.toDouble()), seed = seed, wall = { 0.0 })
            g.startOver()
            assertEquals(0, told and (1 shl g.tale.id), "told $told, then ${g.tale.id}")
        }
    }

    @Test
    fun whenAllAreToldTheTaleBeforeDoesNotComeAtOnce() {
        for (before in TALES) for (seed in 1..5) {
            val g = Game(store("talesTold" to allTold.toDouble(), "tale" to before.id.toDouble()), seed = seed, wall = { 0.0 })
            g.startOver()
            assertNotEquals(before.id, g.tale.id)
        }
    }

    /** At the top, the ending can take him to another world before the new climb begins: the move keeps the tale that the top drew. */
    @Test
    fun aMoveRightAfterTheTopKeepsItsNewTale() {
        for (before in TALES) for (seed in 1..10) {
            val g = Game(store("talesTold" to allTold.toDouble(), "tale" to before.id.toDouble()), seed = seed, wall = { 0.0 })
            g.startOver()
            val drawn = g.tale
            g.moveTo(Place.ETNA)
            assertSame(drawn, g.tale)
            assertNotEquals(before.id, g.tale.id)
        }
    }

    /** A move or the Reset of the climb in the middle of a climb takes a new tale: here the only one not told. */
    @Test
    fun aMoveInAClimbTakesANewTale() {
        val told = (allTold and (1 shl ISIS_AND_OSIRIS.id).inv()).toDouble()
        val moved = game(store("talesTold" to told, "tale" to 2.0, "position" to 500.0))
        moved.moveTo(Place.ETNA)
        assertSame(ISIS_AND_OSIRIS, moved.tale)
        val reset = game(store("talesTold" to told, "tale" to 2.0, "position" to 500.0))
        reset.resetClimb()
        assertSame(ISIS_AND_OSIRIS, reset.tale)
    }

    /** All 15 items, the final climb, and the 15th stone near: its part tells the whole tale. Also with a jump of the dev screen. */
    @Test
    fun aTaleIsToldWithItsLastPart() {
        val keys = arrayOf(*fourteen, "shop.beauty" to 3.0, "summit" to 5000.0, "stelesTold" to 14.0, "nextStele" to 40.0)
        val g = game(store(*keys))
        assertTrue(g.turnUntil { g.stelesTold == 15 })
        assertEquals(1, g.talesTold)

        val jump = game(store(*keys, "tale" to 2.0, "talesTold" to 1.0))
        jump.nearTheTop()
        jump.update(DT)
        assertEquals(1 + 4, jump.talesTold)
    }

    /** The choice of a tale takes no number from the random numbers of the game: the next vision comes at the same turn. */
    @Test
    fun theChoiceTakesNoRandomNumber() {
        fun nextVision(told: Int): Double? {
            val store = store("talesTold" to told.toDouble())
            Game(store, seed = 7, wall = { 0.0 }).startOver()
            return store.load("nextVision")
        }
        assertEquals(nextVision(0), nextVision(1))
    }

    @Test
    fun aClimbKeepsItsTaleAcrossARestart() {
        val store = store("tale" to 2.0)
        val g = game(store)
        assertSame(ORPHEUS, g.tale)
        g.save()
        assertEquals(2.0, store.load("tale"))
        assertSame(ORPHEUS, game(store).tale)
    }

    /** The player heard all of a tale told, the parts told so far of the tale of this climb, and nothing of the others. */
    @Test
    fun thePlayerHeardTheToldTalesAndThePartsOfThisClimb() {
        val g = game(store("talesTold" to 2.0, "tale" to 2.0, "stelesTold" to 9.0))
        assertEquals(0, g.partsHeard(SISYPHUS))
        assertEquals(15, g.partsHeard(TALES[1]))
        assertEquals(9, g.partsHeard(ORPHEUS))
        assertEquals(15, game(store("talesTold" to 4.0, "tale" to 2.0, "stelesTold" to 3.0)).partsHeard(ORPHEUS))
    }

    /** A tale told whose card the player has not seen is news, until the Cards tab shows; the cards seen stay seen after a restart. */
    @Test
    fun aToldTaleIsNewsUntilItsCardIsSeen() {
        val store = store("talesTold" to 3.0, "talesSeen" to 1.0)
        val g = game(store)
        assertTrue(g.newTales)
        g.seeTales()
        assertEquals(false, g.newTales)
        assertEquals(3, game(store).talesSeen)
        assertEquals(false, game(store("talesTold" to 0.0)).newTales)
    }

    /** A tale reads text by text: a part of two texts takes two pages, and each page knows its part. */
    @Test
    fun aTaleReadsTextByText() {
        assertEquals(16, SISYPHUS.pages.size)
        assertEquals(listOf(12, 13, 13, 14), SISYPHUS.pages.drop(12).map { it.first })
        assertEquals(15, ORPHEUS.pages.size)
        assertEquals(ORPHEUS.parts[8].texts.single(), ORPHEUS.pages[8].second)
    }

    @Test
    fun anUnknownTaleIdTellsTheTaleOfSisyphus() {
        assertSame(SISYPHUS, game(store("tale" to 99.0)).tale)
    }

    @Test
    fun theResetOfTheShopKeepsTheTale() {
        val g = game(store("tale" to 3.0, "shop.alone" to 3.0, "stelesTold" to 3.0))
        g.resetShop()
        assertSame(ISIS_AND_OSIRIS, g.tale)
        assertEquals(0, g.stelesTold)
    }

    @Test
    fun everyWorldTellsATale() {
        for (place in Place.entries) {
            val g = game(store("place" to place.id.toDouble()))
            g.update(DT)
            assertEquals(g.position + 360.0, g.nextStele!!, 1e-9, place.title)
        }
    }

    /** The dev screen's Reset of the shop starts the tale again with it. */
    @Test
    fun aShopResetStartsTheTaleAgain() {
        val g = game()
        g.buy(Line.GODS)
        assertTrue(g.turnUntil { g.stelesTold == 1 })
        g.resetShop()
        assertEquals(0, g.stelesTold)
        assertNull(g.nextStele)
        g.addObols(10)
        assertTrue(g.buy(Line.GODS))
        assertEquals(0, g.steles.single().part)
    }
}
