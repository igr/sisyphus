package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Config
import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.JOKERS
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.PX_PER_TURN
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.START_TIME
import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class JokersOnThePathTest {
    /** A Corinth game on day 1, with no vision soon. */
    private fun store(vararg keys: Pair<String, Double>) = MemoryStore(mapOf("nextVision" to 600.0, *keys))

    private fun game(store: MemoryStore = store(), seed: Int = 1) = Game(store, seed = seed, wall = { 0.0 })

    /** The effort of noon on day 3, when the tale has begun. */
    private val day3 = (2.5 - START_TIME) * DAY_LENGTH

    /** How far ahead a new joker stands: one to two days of walking. */
    private val ahead = Config.JOKER_TURNS.start * PX_PER_TURN..Config.JOKER_TURNS.endInclusive * PX_PER_TURN
    private val near = Config.NEAR_TURNS * PX_PER_TURN
    private val read = Config.READ_TURNS * PX_PER_TURN
    private val room = Config.JOKER_ROOM * PX_PER_TURN

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

    @Test
    fun aJokerStandsOneToTwoDaysAhead() {
        for (seed in 1..20) {
            val g = game(MemoryStore(), seed)
            assertTrue(g.nextJoker!! - g.position in ahead, "seed $seed: ${g.nextJoker}")
        }
    }

    /** The bubble opens 3 turns before the rock reaches him, and each text of his line shows for 6 turns. Then he is met. */
    @Test
    fun theBubbleTellsHisLineAndThenHeIsMet() {
        val at = 1000.0 + near + 24
        val s = store("position" to 1000.0, "nextJoker" to at, "joker" to 2.0)
        val g = game(s)
        val joke = JOKERS[2].jokes[0]
        assertNull(g.jesting)
        assertTrue(g.turnUntil { g.jesting != null })
        assertEquals(joke.texts[0], g.jesting)
        assertTrue(g.position in at - near..at - near + 1, "${g.position}")
        assertTrue(g.turnUntil { g.jesting != joke.texts[0] })
        assertEquals(joke.texts[1], g.jesting)
        assertTrue(g.position >= at - near + read)
        assertTrue(g.turnUntil { g.jesting == null })
        assertTrue(g.position >= at - near + 2 * read)
        assertEquals(1, g.met(JOKERS[2]))
        assertEquals(at to JOKERS[2], g.metJoker)
        assertTrue(g.nextJoker!! - g.position in ahead)
        g.save()
        assertEquals(1.0, s.load("met.2"))
    }

    @Test
    fun aJokerTellsHisLinesInTurn() {
        fun first(met: Int) = game(store("position" to 1000.0, "nextJoker" to 1000.0 + near - 1, "met.0" to met.toDouble())).jesting
        assertEquals(JOKERS[0].jokes[3].texts[0], first(3))
        assertEquals(JOKERS[0].jokes[0].texts[0], first(10))
        assertEquals(JOKERS[0].jokes[1].texts[0], first(11))
    }

    /** A stone that comes to stand near a joker sends him 15 turns past it. The stone stands 30 turns ahead, so he is not in the picture. */
    @Test
    fun aNewStoneNearAJokerSendsHimPastIt() {
        val g = game(store("effort" to day3, "obols" to 10.0, "stelesTold" to 1.0, "nextJoker" to 35 * PX_PER_TURN))
        assertNull(g.nextStele)
        assertTrue(g.buy(Line.ROCK))
        assertEquals(Config.STELE_TURNS * PX_PER_TURN, g.nextStele)
        assertEquals(g.nextStele!! + room, g.nextJoker)
    }

    /** Over a long walk, with every item bought one by one, the bubble of a joker never shows with the popup of a stone. */
    @Test
    fun theBubbleAndThePopupNeverMeet() {
        for (seed in 1..3) {
            val g = game(store("effort" to day3, "obols" to 100_000.0), seed)
            var touch = 0.0
            g.knob.grab(touch)
            repeat(24_000) { frame ->
                if (frame % 1500 == 0) Line.entries.firstOrNull { g.buy(it) }
                touch += 0.1
                g.knob.drag(touch)
                g.update(DT)
                assertFalse(g.telling != null && g.jesting != null, "seed $seed: both at ${g.position}")
            }
            assertTrue(JOKERS.sumOf { g.met(it) } >= 2, "seed $seed: ${JOKERS.sumOf { g.met(it) }} jokers")
            assertTrue(g.stelesTold >= 6, "seed $seed: ${g.stelesTold} stones")
        }
    }

    /** A vision stops him, so the bubble keeps its text while the vision shows. */
    @Test
    fun aVisionStopsTheWalkAndTheBubbleWaits() {
        val g = game(store("position" to 1000.0, "nextJoker" to 1000.0 + near - 1))
        val text = g.jesting
        assertNotNull(text)
        g.summonVision()
        g.turn(4 * PI)
        assertEquals(1000.0, g.position)
        assertEquals(text, g.jesting)
    }

    @Test
    fun noBubbleAtTheTop() {
        val g = game(store("position" to 5000.0, "summit" to 5000.0, "nextJoker" to 5000.0))
        assertTrue(g.atTop)
        assertNull(g.jesting)
    }

    @Test
    fun theGodsDoNotWalkHimToAJoker() {
        val g = game(store("position" to 1000.0, "nextJoker" to 1000.0 + near + 24, "shop.alone" to 1.0))
        repeat(3600) { g.update(1.0) }
        assertEquals(1000.0, g.position)
        assertNull(g.jesting)
    }

    @Test
    fun aNewClimbPutsANewJokerAheadAndKeepsTheMeetings() {
        val g = game(store("position" to 1000.0, "nextJoker" to 1000.0 + near, "met.9" to 1.0))
        assertTrue(g.turnUntil { g.metJoker != null })
        g.startOver()
        assertTrue(g.nextJoker!! in ahead)
        assertNull(g.metJoker)
        assertEquals(1, g.met(JOKERS[0]))
        assertEquals(1, g.met(JOKERS[9]))
        g.turn(2 * PI)
        g.moveTo(Place.ETNA)
        assertTrue(g.nextJoker!! in ahead)
        assertEquals(1, g.met(JOKERS[0]))
    }

    @Test
    fun theJokerAheadAndTheMeetingsSurviveARestart() {
        val s = store("nextJoker" to 700.0, "joker" to 5.0, "met.5" to 2.0, "jokersSeen" to 32.0)
        for (g in listOf(game(s).also { it.save() }, game(s))) {
            assertEquals(700.0, g.nextJoker)
            assertSame(JOKERS[5], g.joker)
            assertEquals(2, g.met(JOKERS[5]))
            assertEquals(32, g.jokersSeen)
        }
    }

    @Test
    fun anUnknownJokerIdIsDiogenes() {
        assertSame(JOKERS[0], game(store("joker" to 42.0, "nextJoker" to 700.0)).joker)
    }

    @Test
    fun anOldSaveHasMetNoJokerAndOneStandsAhead() {
        val g = game(store("position" to 5000.0))
        assertTrue(g.nextJoker!! - g.position in ahead)
        assertTrue(JOKERS.all { g.met(it) == 0 })
        assertFalse(g.newJokers)
    }

    /** The jokers take their own random numbers: a joker met does not change the wait for the first vision of the next climb. */
    @Test
    fun theChoiceOfAJokerTakesNoNumberFromTheGame() {
        fun walk(nextJoker: Double): MemoryStore {
            val s = store("nextJoker" to nextJoker)
            game(s).apply {
                turn(30 * 2 * PI)
                startOver()
            }
            return s
        }
        val met = walk(100.0)
        val none = walk(1e7)
        assertEquals(1.0, met.load("met.0"))
        assertEquals(0.0, none.load("met.0"))
        assertEquals(none.load("nextVision"), met.load("nextVision"))
    }

    @Test
    fun aNewCardIsNewsUntilTheTabShowsIt() {
        val g = game(store("position" to 1000.0, "nextJoker" to 1000.0 + near))
        assertFalse(g.newJokers)
        assertTrue(g.turnUntil { g.met(JOKERS[0]) == 1 })
        assertTrue(g.newJokers)
        g.seeJokers()
        assertFalse(g.newJokers)
        assertEquals(1, g.jokersSeen)
    }

    /** The dev screen's Joker brings the jokers in the order of the cards, each a turn into his bubble. */
    @Test
    fun theDevJokerBringsEachJokerInTurn() {
        val g = game(store("position" to 1000.0, "nextJoker" to 5000.0))
        for (joker in JOKERS + JOKERS[0]) {
            g.nearTheNextJoker()
            assertSame(joker, g.joker)
            assertEquals(joker.jokes[0].texts[0], g.jesting)
        }
    }

    /** The dev Stone and Joker, one after the other: the one pressed last talks, and the other keeps its room. */
    @Test
    fun theDevStoneAndJokerKeepTheirRoom() {
        val g = game(store("effort" to day3, "position" to 1000.0))
        g.nearTheNextJoker()
        g.nearTheNextStone()
        assertTrue(abs(g.nextJoker!! - g.nextStele!!) >= room)
        assertNotNull(g.telling)
        assertNull(g.jesting)
        g.nearTheNextJoker()
        assertTrue(abs(g.nextJoker!! - g.nextStele!!) >= room)
        assertNotNull(g.jesting)
        assertNull(g.telling)
    }
}
