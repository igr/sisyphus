package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.CORINTH_TALE
import dev.oblac.sisyphus.game.Config
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.PX_PER_TURN
import dev.oblac.sisyphus.game.Quote
import dev.oblac.sisyphus.game.QuoteBox
import dev.oblac.sisyphus.game.Vision
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StationsTest {
    private fun store(vararg keys: Pair<String, Double>) = MemoryStore(mapOf("obols" to 10.0, "nextVision" to 600.0, *keys))

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

    private fun Game.showsTale() = CORINTH_TALE.any { quotes.quote in it.quotes }

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
    fun aStoneStandsAheadWhenAnItemIsBought() {
        val g = game()
        assertTrue(g.buy(Line.GODS))
        assertEquals(g.position + 360.0, g.nextStele!!, 1e-9)
        assertEquals(0, g.steles.single().part)
    }

    @Test
    fun walkingPastTheStoneTellsThePart() {
        val g = game()
        g.buy(Line.GODS)
        assertTrue(g.turnUntil { g.nextStele == null })
        repeat(60) { g.update(DT) }
        assertTrue(g.quotes.quote === CORINTH_TALE[0].quotes[0])
        assertEquals(0, g.stelesTold)
        assertTrue(g.turnUntil { g.stelesTold == 1 })
    }

    @Test
    fun thePartsComeInOrderOnceEach() {
        val g = game()
        for (line in listOf(Line.GODS, Line.BEAUTY, Line.ROCK)) assertTrue(g.buy(line))
        val shown = mutableListOf<Quote>()
        val stones = mutableListOf<Double>()
        assertTrue(g.turnUntil(400.0) {
            assertTrue(g.steles.count { it.at > g.position } <= 1)
            g.nextStele?.let { if (stones.lastOrNull() != it) stones += it }
            if (g.showsTale() && shown.lastOrNull() !== g.quotes.quote) shown += g.quotes.quote
            g.stelesTold == 3
        })
        assertEquals((0..2).map { CORINTH_TALE[it].quotes[0] }, shown)
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
    fun aPartNotToldBeforeARestartComesWithANewStone() {
        val store = store()
        val g = game(store)
        g.buy(Line.GODS)
        assertTrue(g.turnUntil { g.nextStele == null })
        g.save()
        val again = game(store)
        again.update(DT)
        assertEquals(0, again.stelesTold)
        assertEquals(again.position + 360.0, again.nextStele!!, 1e-9)
        assertTrue(again.turnUntil { again.quotes.quote === CORINTH_TALE[0].quotes[0] })
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
    fun aMoveDropsTheWaitingPart() {
        val g = game()
        g.buy(Line.GODS)
        assertTrue(g.turnUntil { g.nextStele == null })
        g.moveTo(Place.ETNA)
        assertTrue(!g.turnUntil(60.0) { g.showsTale() })
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

    @Test
    fun aPartWaitsForAVision() {
        val g = game(store("shop.alone" to 1.0, "stelesTold" to 0.0, "nextStele" to 10.0, "nextVision" to 0.5))
        g.turn(2 * PI)
        g.untilAsked()
        val god = g.vision!!.god.title
        g.answer(right = true)
        var replyFaded = false
        assertTrue(g.turnUntil {
            if (g.quotes.quote.author == god && g.quotes.phase == QuoteBox.Phase.FADING) replyFaded = true
            g.showsTale()
        })
        assertTrue(replyFaded)
        assertTrue(g.turnUntil { g.stelesTold == 1 })
        assertTrue(!g.turnUntil(20.0) { g.stelesTold > 1 })
    }

    @Test
    fun aPartCutByAnotherTextComesAgain() {
        val g = game(store("shop.alone" to 1.0, "stelesTold" to 0.0, "nextStele" to 6.0))
        val first = CORINTH_TALE[0].quotes[0]
        assertTrue(g.turnUntil { g.quotes.quote === first && g.quotes.phase == QuoteBox.Phase.SHOWING })
        g.summonVision()
        g.untilAsked()
        g.answer(right = false)
        assertTrue(g.turnUntil { g.quotes.quote === first })
        assertEquals(1, g.steles.size, "it came again without a new stone")
        assertTrue(g.turnUntil { g.stelesTold == 1 })

        val top = game(store(*fourteen, "stelesTold" to 13.0, "nextStele" to 6.0))
        val gate = CORINTH_TALE[13].quotes
        assertTrue(top.turnUntil { top.quotes.quote === gate[0] && top.quotes.phase == QuoteBox.Phase.SHOWING })
        assertTrue(top.buy(Line.BEAUTY))
        assertEquals("The top is in sight. Push on.", top.quotes.quote.text)
        val shown = mutableListOf<Quote>()
        assertTrue(top.turnUntil {
            if (top.showsTale() && shown.lastOrNull() !== top.quotes.quote) shown += top.quotes.quote
            top.stelesTold == 14
        })
        assertEquals(gate, shown)
        assertTrue(top.turnUntil { top.nextStele != null })
        assertEquals(14, top.steles.last().part)
    }

    @Test
    fun thePartComesBeforeTheNewsOfALabor() {
        val g = game(store("shop.alone" to 1.0, "stelesTold" to 0.0, "nextStele" to 8.0, "turnsPushed" to 99.5))
        val order = mutableListOf<String>()
        assertTrue(g.turnUntil {
            val text = g.quotes.quote.text
            if ((g.showsTale() || text.startsWith("A labor is done")) && order.lastOrNull() != text) order += text
            order.size >= 2
        })
        assertEquals(listOf(CORINTH_TALE[0].quotes[0].text, "A labor is done: Turn On."), order)
    }

    @Test
    fun aPartWithTwoTextsShowsBoth() {
        val g = game(store(*fourteen, "stelesTold" to 13.0, "nextStele" to 6.0))
        val gate = CORINTH_TALE[13].quotes
        assertTrue(g.turnUntil { g.quotes.quote === gate[0] })
        assertTrue(g.turnUntil { g.quotes.quote === gate[1] })
        assertEquals(13, g.stelesTold)
        assertTrue(g.turnUntil { g.stelesTold == 14 })
        assertTrue(g.quotes.quote === gate[1])
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

    /** A text cut on the very frame it starts to fade has been read: its part is counted, and no second stone tells it again. */
    @Test
    fun aPartThatStartsToFadeIsCountedAtOnce() {
        val g = game(store("shop.alone" to 1.0, "stelesTold" to 0.0, "nextStele" to 6.0))
        val first = CORINTH_TALE[0].quotes[0]
        assertTrue(g.turnUntil { g.quotes.quote === first && g.quotes.phase == QuoteBox.Phase.FADING })
        g.summonVision()
        assertEquals(1, g.stelesTold)
    }

    /** The dev screen's jumps carry him past a stone: its part is still told. */
    @Test
    fun aJumpPastTheStoneStillTellsItsPart() {
        val g = game()
        g.buy(Line.GODS)
        g.nearTheNextObol()
        g.update(DT)
        assertNull(g.nextStele)
        assertTrue(g.turnUntil { g.quotes.quote === CORINTH_TALE[0].quotes[0] })
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
