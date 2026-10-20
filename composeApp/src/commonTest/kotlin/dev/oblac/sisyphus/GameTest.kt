package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.Knob
import dev.oblac.sisyphus.game.PX_PER_TURN
import dev.oblac.sisyphus.game.START_TIME
import dev.oblac.sisyphus.game.Weather
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
    fun walkingBackDoesNotMoveTheSun() {
        val game = Game(seed = 1)
        game.turn(-2 * PI)
        assertTrue(game.position < -PX_PER_TURN + 0.5)
        assertEquals(0.0, game.effort)
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
}
