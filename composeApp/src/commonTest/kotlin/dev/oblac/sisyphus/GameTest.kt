package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.Knob
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.PX_PER_TURN
import dev.oblac.sisyphus.game.START_TIME
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

private const val DT = 1 / 60.0

/** Drags the knob by [radians] in small steps, one step per frame. */
private fun Game.turn(radians: Double, step: Double = 0.1) {
    var touch = 0.0
    knob.grab(touch)
    val steps = (abs(radians) / step).toInt()
    repeat(steps) {
        touch += if (radians > 0) step else -step
        knob.drag(touch)
        update(DT)
    }
}

class KnobTest {
    @Test
    fun followsTheFinger() {
        val knob = Knob()
        knob.grab(0.0)
        knob.drag(0.5)
        assertEquals(0.5, knob.update(DT), 1e-9)
        assertEquals(0.5, knob.angle, 1e-9)
    }

    @Test
    fun crossingTheBackOfTheCircleIsASmallStep() {
        val knob = Knob()
        knob.grab(3.0)
        knob.drag(-3.0)
        assertEquals(2 * PI - 6.0, knob.update(DT), 1e-9)
    }

    @Test
    fun ignoresTheDeadZoneAtTheCenter() {
        val knob = Knob()
        knob.grab(0.0)
        knob.drag(null)
        knob.drag(2.0)
        assertEquals(0.0, knob.update(DT), 1e-9)
    }

    @Test
    fun coastsAfterAFlickAndStops() {
        val knob = Knob()
        knob.grab(0.0)
        var touch = 0.0
        repeat(10) {
            touch += 0.2
            knob.drag(touch)
            knob.update(DT)
        }
        knob.release()
        assertTrue(knob.velocity > 0)
        repeat(600) { knob.update(DT) }
        assertEquals(0.0, knob.velocity)
        assertTrue(knob.angle > touch)
    }
}

class GameTest {
    @Test
    fun oneTurnWalksOneStride() {
        val game = Game(seed = 1)
        game.turn(2 * PI)
        assertEquals(PX_PER_TURN, game.position, 0.3)
        assertEquals(game.position, game.effort, 1e-9)
    }

    @Test
    fun turningBackAlsoPushesUp() {
        val game = Game(seed = 1)
        game.turn(-2 * PI)
        assertEquals(PX_PER_TURN, game.position, 0.3)
        assertEquals(game.position, game.effort, 1e-9)
    }

    @Test
    fun effortMakesTheDays() {
        val game = Game(MemoryStore(mapOf("effort" to DAY_LENGTH * (1 - START_TIME))), seed = 1)
        assertEquals(2, game.day)
        assertEquals(0.0, game.timeOfDay, 1e-9)
        assertEquals(1, Game(seed = 1).day)
        assertEquals(START_TIME, Game(seed = 1).timeOfDay, 1e-9)
    }

    @Test
    fun theFirstDayIsFair() {
        assertEquals(Weather.FAIR, Weather.of(1))
        assertTrue(Weather.entries.all { w -> (2..400).any { Weather.of(it) == w } })
    }

    @Test
    fun theKnobClicksWhileTurning() {
        val game = Game(seed = 1)
        var ticks = 0
        game.onTick = { ticks++ }
        game.turn(2 * PI)
        assertTrue(ticks in 22..24, "ticks: $ticks")
    }

    @Test
    fun aRestartedGameIsWhereItEnded() {
        val store = MemoryStore()
        val game = Game(store, seed = 1)
        game.turn(40 * PI)
        repeat(60) { game.update(DT) }
        game.save()

        val again = Game(store, seed = 2)
        assertEquals(game.day, again.day)
        assertEquals(game.timeOfDay, again.timeOfDay, 1e-9)
        assertEquals(game.position, again.position, 1e-9)
        assertEquals(game.knob.angle, again.knob.angle, 1e-9)
        assertTrue(game.quotes.drawn > 0)
        assertSame(game.quotes.quote, again.quotes.quote)
    }

    @Test
    fun progressIsSaved() {
        val store = MemoryStore()
        val game = Game(store, seed = 1)
        game.turn(PI)
        game.save()
        assertEquals(game.effort, store.load("effort"))
        assertEquals(game.effort, Game(store, seed = 1).effort)
    }

    @Test
    fun oneTurnEarnsItsShareOfAnObol() {
        val game = Game(seed = 1)
        game.turn(2 * PI)
        assertEquals(1.0 / 720, game.shop.obols, 1e-4)
    }

    @Test
    fun aStrongRockEarnsMoreButTheSunKeepsItsPace() {
        val game = Game(MemoryStore(mapOf("shop.rock" to 3.0, "shop.beside" to 3.0)), seed = 1)
        game.turn(2 * PI)
        assertEquals(PX_PER_TURN * 1.5, game.effort, 0.5)
        assertEquals(PX_PER_TURN * 1.5, game.position, 0.5)
        assertEquals(6.0 / 720, game.shop.obols, 1e-3)
    }

    @Test
    fun theShopIsSavedAndComesBack() {
        val store = MemoryStore(mapOf("obols" to 5.0))
        val game = Game(store, seed = 1, wall = { 0.0 })
        assertTrue(game.buy(Line.ALONE))
        val again = Game(store, seed = 1, wall = { 0.0 })
        assertEquals(1, again.shop.tier(Line.ALONE))
        assertEquals(4.0, again.shop.obols, 1e-9)
    }

    @Test
    fun thanatosPushesTwentyFourTimesAnHour() {
        val game = Game(MemoryStore(mapOf("shop.alone" to 3.0)), seed = 1, wall = { 0.0 })
        repeat(3600) { game.update(1.0) }
        assertEquals(24 * PX_PER_TURN, game.effort, 1e-6)
        assertEquals(0.0, game.position)
        assertEquals(24.0 / 720, game.shop.obols, 1e-9)
    }

    @Test
    fun helpersWorkWhileTheAppIsClosed() {
        var now = 1000.0
        val store = MemoryStore(mapOf("shop.alone" to 3.0))
        Game(store, seed = 1, wall = { now }).save()
        now += 3 * 3600
        val again = Game(store, seed = 1, wall = { now })
        assertEquals(72 * PX_PER_TURN, again.effort, 1e-6)
        assertEquals(72.0 / 720, again.shop.obols, 1e-9)
        assertEquals("Thanatos pushed 72 times while you were away.", again.quotes.quote.text)
        assertEquals("Hermes", again.quotes.quote.author)
    }

    @Test
    fun oneTurnAwayIsTold() {
        var now = 0.0
        val store = MemoryStore(mapOf("shop.alone" to 3.0))
        Game(store, seed = 1, wall = { now }).save()
        now += 150.0
        assertEquals("Thanatos pushed once while you were away.", Game(store, seed = 1, wall = { now }).quotes.quote.text)
    }

    @Test
    fun helpersWorkWhileTheAppIsInTheBackground() {
        var now = 0.0
        val game = Game(MemoryStore(mapOf("shop.alone" to 1.0)), seed = 1, wall = { now })
        game.pause()
        now += 3600.0
        game.resume()
        game.resume()
        assertEquals(6 * PX_PER_TURN, game.effort, 1e-6)
    }

    @Test
    fun aSecondPauseKeepsTheFirstTime() {
        var now = 0.0
        val store = MemoryStore(mapOf("shop.alone" to 1.0))
        val game = Game(store, seed = 1, wall = { now })
        game.pause()
        game.pause()
        now += 3600.0
        game.pause()
        assertEquals(0.0, store.load("savedAt"))
        game.resume()
        assertEquals(6 * PX_PER_TURN, game.effort, 1e-6)
    }

    @Test
    fun framesWhilePausedDoNotCountTwice() {
        var now = 0.0
        val game = Game(MemoryStore(mapOf("shop.alone" to 3.0)), seed = 1, wall = { now })
        game.pause()
        repeat(600) {
            now += 1.0
            game.update(1.0)
        }
        game.resume()
        assertEquals(4 * PX_PER_TURN, game.effort, 1e-6)
    }

    @Test
    fun anOldSaveWithoutATimeGetsNoAwayTime() {
        val game = Game(MemoryStore(mapOf("effort" to 500.0, "shop.alone" to 3.0)), seed = 1, wall = { 1.0e9 })
        assertEquals(500.0, game.effort)
        assertEquals(0.0, game.shop.obols)
    }

    @Test
    fun aClockMovedBackGivesNothing() {
        var now = 5000.0
        val store = MemoryStore(mapOf("shop.alone" to 3.0))
        Game(store, seed = 1, wall = { now }).save()
        now = 1000.0
        assertEquals(0.0, Game(store, seed = 1, wall = { now }).effort)
    }

    @Test
    fun aYearAwayStaysSane() {
        var now = 0.0
        val store = MemoryStore(mapOf("shop.alone" to 3.0, "shop.blessing" to 3.0))
        Game(store, seed = 1, wall = { now }).save()
        now += 365 * 24 * 3600.0
        val game = Game(store, seed = 1, wall = { now })
        assertEquals(24 * 24 * 365.0 / 216, game.shop.obols, 1e-6)
        val q = game.quotes.quote
        assertEquals("Hermes · 973 obols", q.author)
        assertTrue(PixelFont.wrap(q.text, 119).size <= 3)
        assertTrue(PixelFont.width("— ${q.author}") <= 119)
    }
}
