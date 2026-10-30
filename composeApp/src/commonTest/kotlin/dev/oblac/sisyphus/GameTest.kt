package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.CLIMB
import dev.oblac.sisyphus.game.Config
import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.FIRST_OBOL
import dev.oblac.sisyphus.game.GOAL
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.Knob
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.PX_PER_TURN
import dev.oblac.sisyphus.game.Quote
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.START_TIME
import dev.oblac.sisyphus.game.TUTORIAL
import dev.oblac.sisyphus.game.Vision
import dev.oblac.sisyphus.game.godsOf
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

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
        assertEquals(1.0 / FIRST_OBOL, game.shop.obols, 0.02 / FIRST_OBOL)
    }

    @Test
    fun aStrongRockEarnsMoreButTheSunKeepsItsPace() {
        val game = Game(MemoryStore(mapOf("shop.rock" to 3.0, "shop.beside" to 3.0)), seed = 1)
        game.turn(2 * PI)
        assertEquals(PX_PER_TURN * 1.5, game.effort, 0.5)
        assertEquals(PX_PER_TURN * 1.5, game.position, 0.5)
        assertEquals(6.0 / Config.WORK_PER_OBOL, game.shop.obols, 1e-3)
    }

    @Test
    fun theWorkForTheNextObolIsSavedAsSoonAsTheObolComes() {
        val store = MemoryStore(mapOf("shop.alone" to 3.0, "obols" to 0.99, "nextObol" to 150.0))
        val game = Game(store, seed = 1, wall = { 0.0 })
        assertEquals(150.0, game.shop.nextObol)
        // Thanatos pushes the 1.5 turns that are left in 225 seconds; the knob does not move.
        repeat(1060) { game.update(1.0) }
        assertEquals(1, game.shop.obols.toInt())
        assertTrue(game.shop.nextObol in Config.WORK_PER_OBOL * (1 - Config.OBOL_SPREAD)..Config.WORK_PER_OBOL * (1 + Config.OBOL_SPREAD))
        assertEquals(game.shop.nextObol, store.load("nextObol"))
        assertEquals(game.shop.nextObol, Game(store, seed = 2, wall = { 0.0 }).shop.nextObol)
    }

    @Test
    fun aNewObolIsTold() {
        val game = Game(MemoryStore(mapOf("obols" to 0.99, "nextObol" to 100.0)), seed = 1, wall = { 0.0 })
        var coins = 0
        game.onObol = { coins++ }
        game.turn(4 * PI)
        assertEquals(1, game.shop.obols.toInt())
        assertEquals(1, coins)
        game.addObols(10)
        assertEquals(1, coins)
    }

    @Test
    fun nearTheNextObolThreeClicksBringIt() {
        val game = Game(MemoryStore(mapOf("obols" to 0.5, "nextObol" to 300.0)), seed = 1, wall = { 0.0 })
        var coins = 0
        game.onObol = { coins++ }
        game.nearTheNextObol()
        game.turn(0.7)
        assertEquals(0, coins)
        game.turn(0.2)
        assertEquals(1, coins)
    }

    /** All the obols that came while the app was away are told at once, with their number. */
    @Test
    fun obolsThatCameWhileAwayAreToldOnReturn() {
        var now = 0.0
        val game = Game(MemoryStore(mapOf("shop.alone" to 3.0, "nextObol" to 100.0)), seed = 1, wall = { now })
        val told = mutableListOf<Int>()
        game.onObol = { told += it }
        game.pause()
        now += 80 * 3600.0
        game.resume()
        assertTrue(game.shop.obols.toInt() >= 2)
        assertEquals(listOf(game.shop.obols.toInt()), told)
    }

    /** The first purchase of a climb tells the goal: all 15 items bring the top in sight. */
    @Test
    fun theFirstPurchaseOfAClimbTellsTheGoal() {
        val game = Game(MemoryStore(mapOf("obols" to 10.0)), seed = 1, wall = { 0.0 })
        assertTrue(game.buy(Line.GODS))
        assertSame(GOAL, game.quotes.quote)
        assertTrue(PixelFont.wrap(GOAL.text, 119).size <= 3)
        val later = Game(MemoryStore(mapOf("obols" to 10.0, "shop.rock" to 1.0)), seed = 1, wall = { 0.0 })
        assertTrue(later.buy(Line.GODS))
        assertFalse(later.quotes.quote === GOAL)
    }

    @Test
    fun theShopIsSavedAndComesBack() {
        val store = MemoryStore(mapOf("obols" to 5.0))
        val game = Game(store, seed = 1, wall = { 0.0 })
        assertTrue(game.buy(Line.GODS))
        val again = Game(store, seed = 1, wall = { 0.0 })
        assertEquals(1, again.shop.tier(Line.GODS))
        assertEquals(4.0, again.shop.obols, 1e-9)
    }

    @Test
    fun thanatosPushesTwentyFourTimesAnHour() {
        val game = Game(MemoryStore(mapOf("shop.alone" to 3.0)), seed = 1, wall = { 0.0 })
        repeat(3600) { game.update(1.0) }
        assertEquals(24 * PX_PER_TURN, game.effort, 1e-6)
        assertEquals(0.0, game.position)
        assertEquals(24.0 / Config.WORK_PER_OBOL, game.shop.obols, 1e-9)
    }

    @Test
    fun helpersWorkWhileTheAppIsClosed() {
        var now = 1000.0
        val store = MemoryStore(mapOf("shop.alone" to 3.0))
        Game(store, seed = 1, wall = { now }).save()
        now += 3 * 3600
        val again = Game(store, seed = 1, wall = { now })
        assertEquals(72 * PX_PER_TURN, again.effort, 1e-6)
        assertEquals(72.0 / Config.WORK_PER_OBOL, again.shop.obols, 1e-9)
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

    private val allBought = Line.entries.associate { "shop." + it.id to 3.0 }

    /** In the final climb, a herm stands by the path every 100 turns before the top. As he passes one, Hermes says how far the top is. */
    @Test
    fun hermsMarkEveryHundredTurnsOfTheClimbAndHermesCountsDown() {
        val start = 1000.0
        val game = Game(MemoryStore(allBought + mapOf("position" to start, "summit" to start + CLIMB)), seed = 1, wall = { 0.0 })
        val step = Config.HERM_TURNS * Config.SUN_CAP * PX_PER_TURN
        assertEquals((1 until Config.CLIMB_TURNS / Config.HERM_TURNS).map { start + it * step }, game.herms)
        game.turn((Config.HERM_TURNS - 1) * 2 * PI, step = 1.0)
        assertFalse(game.quotes.quote.text.endsWith("turns to the top."))
        game.turn(2 * 2 * PI, step = 1.0)
        assertEquals("${Config.CLIMB_TURNS - Config.HERM_TURNS} turns to the top.", game.quotes.quote.text)
        assertEquals("Hermes", game.quotes.quote.author)
        game.turn(Config.HERM_TURNS * 2 * PI, step = 1.0)
        assertEquals("${Config.CLIMB_TURNS - 2 * Config.HERM_TURNS} turns to the top.", game.quotes.quote.text)
        assertTrue(Game(seed = 1).herms.isEmpty())
    }

    /** During the final climb there is nothing more to buy: obols stop coming, from the knob and from the gods. */
    @Test
    fun duringTheFinalClimbObolsStopComing() {
        var now = 0.0
        val game = Game(MemoryStore(allBought + mapOf("obols" to 2.5, "position" to 1000.0, "summit" to 1000.0 + CLIMB)), seed = 1, wall = { now })
        var told = 0
        game.onObol = { told++ }
        game.turn(20 * PI)
        repeat(60) { game.update(1.0) }
        assertTrue(game.position > 1000.0)
        game.pause()
        now += 24 * 3600.0
        game.resume()
        assertEquals(2.5, game.shop.obols, 1e-9)
        assertEquals(0, told)
    }

    /** The final climb counts from 0 when it begins to 1 at the top. Before it begins, there is nothing to count. */
    @Test
    fun theFinalClimbCountsHowFarHeHasClimbed() {
        assertNull(Game(seed = 1).climbed)
        fun at(position: Double) = Game(MemoryStore(allBought + mapOf("position" to position, "summit" to 1000.0 + CLIMB)), seed = 1, wall = { 0.0 }).climbed!!
        assertEquals(0.0, at(1000.0), 1e-9)
        assertEquals(0.5, at(1000.0 + CLIMB / 2), 1e-9)
        assertEquals(1.0, at(1000.0 + CLIMB), 1e-9)
    }

    @Test
    fun buyingTheLastItemBeginsTheFinalClimb() {
        val store = MemoryStore(allBought + mapOf("shop.beauty" to 2.0, "obols" to 11.0, "position" to 500.0))
        val game = Game(store, seed = 1, wall = { 0.0 })
        assertNull(game.summit)
        assertTrue(game.buy(Line.BEAUTY))
        assertEquals(500.0 + CLIMB, game.summit!!, 1e-9)
        assertEquals("Hermes", game.quotes.quote.author)
        assertEquals(500.0 + CLIMB, Game(store, seed = 1, wall = { 0.0 }).summit!!, 1e-9)
    }

    @Test
    fun anOldSaveWithEverythingBoughtBeginsTheClimb() {
        val game = Game(MemoryStore(allBought + mapOf("position" to 500.0)), seed = 1, wall = { 0.0 })
        assertEquals(500.0 + CLIMB, game.summit!!, 1e-9)
    }

    /** From the 15th purchase, the top is the turns of the final climb away. Hermes says that it is in sight. */
    @Test
    fun theTopIsTheFinalClimbAway() {
        val game = Game(MemoryStore(allBought + mapOf("shop.beauty" to 2.0, "obols" to 20.0)), seed = 1, wall = { 0.0 })
        assertTrue(game.buy(Line.BEAUTY))
        assertEquals("The top is in sight. Push on.", game.quotes.quote.text)
        game.turn((Config.CLIMB_TURNS - 1) * 2 * PI)
        assertFalse(game.atTop)
        game.turn(2 * 2 * PI)
        assertTrue(game.atTop)
    }

    /** A top saved with other numbers is no farther than the final climb is now. */
    @Test
    fun aSavedTopIsNoFartherThanTheFinalClimb() {
        val game = Game(MemoryStore(allBought + mapOf("position" to 1000.0, "summit" to 1000.0 + 3 * CLIMB)), seed = 1, wall = { 0.0 })
        assertEquals(1000.0 + CLIMB, game.summit)
    }

    @Test
    fun theTopStopsHim() {
        val game = Game(MemoryStore(allBought + mapOf("position" to 90.0, "summit" to 100.0)), seed = 1, wall = { 0.0 })
        game.turn(2 * 2 * PI)
        assertTrue(game.atTop)
        assertEquals(100.0, game.position)
        game.turn(2 * 2 * PI)
        assertEquals(100.0, game.position)
    }

    @Test
    fun startingOverKeepsOnlyTheClimbsAndTheSound() {
        val store = MemoryStore(allBought + mapOf("position" to 100.0, "summit" to 100.0, "effort" to 50_000.0, "obols" to 40.0, "sound" to 0.0))
        val game = Game(store, seed = 1, wall = { 0.0 })
        game.startOver()
        for (g in listOf(game, Game(store, seed = 1, wall = { 0.0 }))) {
            assertEquals(1, g.climbs)
            assertEquals(1, g.day)
            assertEquals(0.0, g.position)
            assertEquals(0.0, g.shop.obols)
            assertTrue(Line.entries.all { g.shop.tier(it) == 0 })
            assertNull(g.summit)
            assertFalse(g.soundOn)
        }
    }

    @Test
    fun movingToAnotherWorldStartsOver() {
        val store = MemoryStore(allBought + mapOf("position" to 100.0, "effort" to 50_000.0, "obols" to 40.0, "climbs" to 2.0))
        val game = Game(store, seed = 1, wall = { 0.0 })
        assertEquals(Place.CORINTH, game.place)
        game.moveTo(Place.ETNA)
        for (g in listOf(game, Game(store, seed = 1, wall = { 0.0 }))) {
            assertEquals(Place.ETNA, g.place)
            assertEquals(1, g.day)
            assertEquals(0.0, g.position)
            assertEquals(0.0, g.shop.obols)
            assertTrue(Line.entries.all { g.shop.tier(it) == 0 })
            assertNull(g.summit)
            assertEquals(2, g.climbs)
        }
    }

    /** The climb begins with the first push, and a move to another world takes him back to the foot of the hill. */
    @Test
    fun theClimbBeginsWithTheFirstPush() {
        val game = Game(MemoryStore(), seed = 1, wall = { 0.0 })
        assertFalse(game.begun)
        game.turn(PI)
        assertTrue(game.begun)
        game.moveTo(Place.ETNA)
        assertFalse(game.begun)
    }

    @Test
    fun onEtnaTheShopSellsTheItemsOfEtna() {
        assertEquals("Empedocles", Game(MemoryStore(mapOf("place" to 1.0)), seed = 1, wall = { 0.0 }).shop.next(Line.SIDEKICKS)?.name)
        val game = Game(MemoryStore(), seed = 1, wall = { 0.0 })
        game.moveTo(Place.ETNA)
        assertEquals("Aetna", game.shop.next(Line.GODS)?.name)
    }

    @Test
    fun inTheUnderworldTheShopSellsTheItemsOfTheUnderworld() {
        assertEquals("Orpheus", Game(MemoryStore(mapOf("place" to 2.0)), seed = 1, wall = { 0.0 }).shop.next(Line.SIDEKICKS)?.name)
        val game = Game(MemoryStore(), seed = 1, wall = { 0.0 })
        game.moveTo(Place.UNDERWORLD)
        assertEquals("Cerberus", game.shop.next(Line.GODS)?.name)
    }

    @Test
    fun inCairoTheShopSellsTheItemsOfCairo() {
        assertEquals("Io", Game(MemoryStore(mapOf("place" to 3.0)), seed = 1, wall = { 0.0 }).shop.next(Line.SIDEKICKS)?.name)
        val game = Game(MemoryStore(), seed = 1, wall = { 0.0 })
        game.moveTo(Place.CAIRO)
        assertEquals("Scarab", game.shop.next(Line.GODS)?.name)
    }

    @Test
    fun onMountFujiTheShopSellsTheItemsOfMountFuji() {
        assertEquals("Kaguya", Game(MemoryStore(mapOf("place" to 4.0)), seed = 1, wall = { 0.0 }).shop.next(Line.SIDEKICKS)?.name)
        val game = Game(MemoryStore(), seed = 1, wall = { 0.0 })
        game.moveTo(Place.FUJI)
        assertEquals("Tengu", game.shop.next(Line.GODS)?.name)
    }

    @Test
    fun hermesNamesTheGodOfTheWorld() {
        var now = 1000.0
        val store = MemoryStore(mapOf("shop.alone" to 3.0, "place" to 1.0))
        Game(store, seed = 1, wall = { now }).save()
        now += 3600
        assertEquals("Typhon pushed 24 times while you were away.", Game(store, seed = 1, wall = { now }).quotes.quote.text)
    }

    /** Every saved game holds these ids. Changing one loses the players' purchases or their world. */
    @Test
    fun theSaveIdsNeverChange() {
        assertEquals(listOf("rock", "beside", "alone", "blessing", "beauty"), Line.entries.map { it.id })
        assertEquals(listOf(0, 1, 2, 3, 4), Place.entries.map { it.id })
    }

    /** RevenueCat and the stores know a world by its product. Changing one locks the worlds the players bought. Corinth is free. */
    @Test
    fun theProductIdsNeverChange() {
        assertEquals(listOf(null, "world_etna", "world_underworld", "world_cairo", "world_fuji"), Place.entries.map { it.product })
    }

    @Test
    fun anOldSaveLoadsItsSidekickGodAndWorld() {
        val game = Game(MemoryStore(mapOf("shop.beside" to 2.0, "shop.alone" to 3.0, "place" to 2.0)), seed = 1, wall = { 0.0 })
        assertEquals(2, game.shop.tier(Line.SIDEKICKS))
        assertEquals(3, game.shop.tier(Line.GODS))
        assertEquals(Place.UNDERWORLD, game.place)
    }

    @Test
    fun aYearAwayStaysSane() {
        var now = 0.0
        val store = MemoryStore(mapOf("shop.alone" to 3.0, "shop.blessing" to 3.0))
        Game(store, seed = 1, wall = { now }).save()
        now += 365 * 24 * 3600.0
        val game = Game(store, seed = 1, wall = { now })
        val plutus = Config.WORK_PER_OBOL * Config.ITEMS.getValue(Line.BLESSING)[2].second
        assertEquals(24 * 24 * 365.0 / plutus, game.shop.obols, 24 * 24 * 365.0 / plutus / 100)
        val q = game.quotes.quote
        assertEquals("Hermes · ${game.shop.obols.toInt()} obols", q.author)
        assertTrue(PixelFont.wrap(q.text, 119).size <= 3)
        assertTrue(PixelFont.width("— ${q.author}") <= 119)
    }

    /** A game whose vision came and asks: the question is typed out, and the answers show. */
    private fun asking(store: MemoryStore = MemoryStore(mapOf("nextVision" to 0.5))): Game {
        val game = Game(store, seed = 1, wall = { 0.0 })
        game.turn(2 * PI)
        repeat(600) { if (game.vision?.phase == Vision.Phase.COMING) game.update(DT) }
        assertEquals(Vision.Phase.ASKING, game.vision?.phase)
        return game
    }

    /** A new touch turns a quarter turn toward the right answer, or toward the wrong one. */
    private fun Game.answer(right: Boolean) {
        val toLeft = vision!!.rightOnLeft == right
        turn(if (toLeft) -PI / 2 else PI / 2)
    }

    @Test
    fun aVisionComesWhenTheCountRunsOut() {
        val game = Game(MemoryStore(mapOf("nextVision" to 2.0)), seed = 1, wall = { 0.0 })
        game.turn(2.5 * 2 * PI)
        val vision = assertNotNull(game.vision)
        assertEquals(vision.question.text, game.quotes.quote.text)
        assertEquals(vision.god.title, game.quotes.quote.author)
    }

    @Test
    fun theBonusDoesNotCountTowardAVision() {
        val game = Game(MemoryStore(mapOf("nextVision" to 10.0, "shop.rock" to 3.0)), seed = 1, wall = { 0.0 })
        game.turn(5 * 2 * PI)
        assertEquals(5.0, game.voices.count, 0.01)
    }

    @Test
    fun theGodsDoNotBringAVision() {
        var now = 0.0
        val game = Game(MemoryStore(mapOf("shop.alone" to 3.0, "nextVision" to 1.0)), seed = 1, wall = { now })
        game.pause()
        now += 86400.0
        game.resume()
        repeat(60) { game.update(DT) }
        assertNull(game.vision)
        assertEquals(1.0, game.voices.count, 1e-9)
    }

    @Test
    fun noVisionInTheFinalClimb() {
        val game = Game(MemoryStore(mapOf("nextVision" to 1.0)), seed = 1, wall = { 0.0 })
        game.nearTheTop()
        game.turn(1.5 * 2 * PI)
        assertNull(game.vision)
        assertEquals(1.0, game.voices.count, 1e-9)
    }

    @Test
    fun aVisionHoldsSisyphus() {
        val game = Game(MemoryStore(mapOf("nextVision" to 0.5)), seed = 1, wall = { 0.0 })
        game.turn(2 * PI)
        assertNotNull(game.vision)
        val position = game.position
        val effort = game.effort
        val obols = game.shop.obols
        game.turn(3 * 2 * PI)
        assertEquals(position, game.position)
        assertEquals(effort, game.effort)
        assertEquals(obols, game.shop.obols)
    }

    @Test
    fun aFlickBeforeAVisionPicksNoAnswer() {
        val game = Game(MemoryStore(mapOf("nextVision" to 1.0)), seed = 1, wall = { 0.0 })
        var touch = 0.0
        game.knob.grab(touch)
        repeat(6) {
            touch += 0.5
            game.knob.drag(touch)
            game.update(DT)
        }
        game.knob.release()
        repeat(240) { if (game.vision == null) game.update(DT) }
        assertNotNull(game.vision)
        assertEquals(0.0, game.knob.velocity)
        val angle = game.knob.angle
        repeat(300) { game.update(DT) }
        assertEquals(angle, game.knob.angle)
        assertEquals(Vision.Phase.ASKING, game.vision?.phase)
    }

    @Test
    fun anAnswerNeedsANewTouch() {
        val game = Game(MemoryStore(mapOf("nextVision" to 0.5)), seed = 1, wall = { 0.0 })
        var touch = 0.0
        game.knob.grab(touch)
        repeat(60) {
            touch += 0.06
            game.knob.drag(touch)
            game.update(DT)
        }
        repeat(600) { if (game.vision?.phase == Vision.Phase.COMING) game.update(DT) }
        assertEquals(Vision.Phase.ASKING, game.vision?.phase)
        repeat(16) {
            touch += 0.1
            game.knob.drag(touch)
            game.update(DT)
        }
        assertEquals(Vision.Phase.ASKING, game.vision?.phase)
        game.turn(PI / 2)
        assertEquals(Vision.Phase.LEAVING, game.vision?.phase)
    }

    @Test
    fun aRightAnswerGivesOneObol() {
        val game = asking()
        var told = 0
        game.onObol = { told += it }
        val obols = game.shop.obols
        val question = game.vision!!.question
        game.answer(right = true)
        assertEquals(obols + 1, game.shop.obols, 0.01)
        assertEquals(1, told)
        assertEquals(1, game.voices.rightAnswers)
        assertEquals("Correct. ${question.yes}", game.quotes.quote.text)
    }

    @Test
    fun aWrongAnswerGivesNothing() {
        val game = asking()
        var told = 0
        game.onObol = { told += it }
        val obols = game.shop.obols
        val question = game.vision!!.question
        game.answer(right = false)
        assertEquals(obols, game.shop.obols, 0.01)
        assertEquals(0, told)
        assertEquals(0, game.voices.rightAnswers)
        assertEquals("Wrong. ${question.no}", game.quotes.quote.text)
    }

    @Test
    fun noAnswerSaysFarewell() {
        val game = asking()
        val god = game.vision!!.god
        repeat(25 * 60) { if (game.vision?.phase == Vision.Phase.ASKING) game.update(DT) }
        assertEquals(Vision.Phase.LEAVING, game.vision?.phase)
        assertEquals(god.farewell, game.quotes.quote.text)
    }

    @Test
    fun theKnobPushesAgainAfterTheAnswer() {
        val game = asking()
        game.answer(right = true)
        val position = game.position
        game.turn(2 * PI)
        assertTrue(game.position > position)
    }

    @Test
    fun pausingEndsTheVision() {
        val game = asking()
        game.pause()
        assertNull(game.vision)
        game.resume()
        val position = game.position
        game.turn(2 * PI)
        assertTrue(game.position > position)
    }

    @Test
    fun theFinalClimbEndsTheVision() {
        val all = Line.entries.filter { it != Line.BEAUTY }.associate { "shop." + it.id to 3.0 }
        val game = asking(MemoryStore(all + mapOf("shop.beauty" to 2.0, "obols" to 100.0, "nextVision" to 0.5)))
        assertTrue(game.buy(Line.BEAUTY))
        assertNull(game.vision)
        assertTrue(game.summit != null)
    }

    @Test
    fun movingEndsTheVision() {
        val game = asking()
        game.moveTo(Place.ETNA)
        assertNull(game.vision)
    }

    @Test
    fun theVoicesAreSaved() {
        val store = MemoryStore(mapOf("nextVision" to 0.5))
        val game = Game(store, seed = 1, wall = { 0.0 })
        game.turn(2 * PI)
        assertNotNull(game.vision)
        val again = Game(store, seed = 2, wall = { 0.0 })
        assertEquals(game.voices.drawn, again.voices.drawn)
        assertEquals(game.voices.count, again.voices.count, 1e-9)
        assertEquals(game.voices.visionAt(5, Place.CORINTH).question.text, again.voices.visionAt(5, Place.CORINTH).question.text)
        assertNull(again.vision)
    }

    /** The visions go on where they were, but each climb waits for its first one only until day 2. */
    @Test
    fun theVisionsGoOnAtTheTopAndInAnotherWorld() {
        val game = asking()
        game.answer(right = true)
        repeat(180) { game.update(DT) }
        assertNull(game.vision)
        val drawn = game.voices.drawn
        game.startOver()
        assertTrue(game.voices.count in Config.FIRST_VISION)
        game.moveTo(Place.ETNA)
        assertTrue(game.voices.count in Config.FIRST_VISION)
        assertEquals(drawn, game.voices.drawn)
        assertEquals(1, game.voices.rightAnswers)
    }

    @Test
    fun theDevScreenBringsAVisionAtTheNextClick() {
        val game = Game(MemoryStore(mapOf("nextVision" to 900.0)), seed = 1, wall = { 0.0 })
        game.nearTheNextVision()
        repeat(60) { game.update(DT) }
        assertNull(game.vision)
        game.turn(0.5)
        assertNotNull(game.vision)
    }

    @Test
    fun aComingVisionAsksAgainWhenItsQuestionIsGone() {
        val game = Game(MemoryStore(mapOf("obols" to 5.0)), seed = 1, wall = { 0.0 })
        game.summonVision()
        repeat(30) { game.update(DT) }
        assertEquals(Vision.Phase.COMING, game.vision?.phase)
        assertTrue(game.buy(Line.GODS))
        repeat(600) { if (game.vision?.phase == Vision.Phase.COMING) game.update(DT) }
        assertEquals(Vision.Phase.ASKING, game.vision?.phase)
        assertEquals(game.vision!!.question.text, game.quotes.quote.text)
    }

    /** The first vision of each climb comes on day 2, also when the rock makes the sun move as fast as it can. */
    @Test
    fun theFirstVisionOfEachClimbComesOnDay2() {
        for (seed in 1..10) {
            val game = Game(MemoryStore(), seed = seed, wall = { 0.0 })
            repeat(2) { climb ->
                var quarters = 0
                while (game.vision == null && quarters++ < 4000) {
                    game.turn(PI / 2)
                    game.buy(Line.ROCK)
                }
                assertNotNull(game.vision, "seed $seed, climb $climb")
                assertEquals(2, game.day, "seed $seed, climb $climb")
                game.endVision()
                repeat(600) { game.update(DT) }
                assertNull(game.vision)
                game.startOver()
            }
        }
    }

    @Test
    fun anOldSaveMeetsTheVoicesSoon() {
        val game = Game(MemoryStore(mapOf("effort" to 5000.0)), seed = 1, wall = { 0.0 })
        assertTrue(game.voices.count in Config.FIRST_VISION)
    }

    @Test
    fun turnsPushedCountTheOwnTurns() {
        val game = Game(MemoryStore(mapOf("nextVision" to 1000.0)), seed = 1, wall = { 0.0 })
        game.turn(2 * PI)
        assertEquals(6.2 / (2 * PI), game.turnsPushed, 1e-6)
        var now = 0.0
        val store = MemoryStore(mapOf("shop.alone" to 3.0))
        Game(store, seed = 1, wall = { now }).save()
        now += 24 * 3600.0
        assertEquals(0.0, Game(store, seed = 1, wall = { now }).turnsPushed)
        val held = asking()
        val before = held.turnsPushed
        held.turn(0.3)
        assertEquals(before, held.turnsPushed, 1e-9)
    }

    @Test
    fun daysAddUpOverTheClimbs() {
        val game = Game(MemoryStore(mapOf("effort" to (4.5 - START_TIME) * DAY_LENGTH)), seed = 1, wall = { 0.0 })
        assertEquals(5, game.day)
        assertEquals(4, game.days)
        game.startOver()
        assertEquals(1, game.day)
        assertEquals(4, game.daysBefore)
        assertEquals(4, game.days)
    }

    @Test
    fun aMoveOnTheFirstDayAddsNoDay() {
        val game = Game(MemoryStore(), seed = 1, wall = { 0.0 })
        game.moveTo(Place.ETNA)
        game.moveTo(Place.CORINTH)
        assertEquals(0, game.days)
    }

    @Test
    fun theCountsStay() {
        val store = MemoryStore(
            mapOf("climbs" to 2.0, "rightAnswers" to 5.0, "turnsPushed" to 300.0, "daysBefore" to 7.0, "effort" to (2.5 - START_TIME) * DAY_LENGTH),
        )
        val game = Game(store, seed = 1, wall = { 0.0 })
        assertEquals(9, game.days)
        game.startOver()
        game.moveTo(Place.ETNA)
        game.resetClimb()
        assertEquals(3, game.climbs)
        assertEquals(300.0, game.turnsPushed)
        assertEquals(9, game.days)
        assertEquals(5, game.voices.rightAnswers)
    }

    @Test
    fun theCountsAreSaved() {
        val store = MemoryStore(mapOf("nextVision" to 1000.0, "effort" to (3.5 - START_TIME) * DAY_LENGTH))
        val game = Game(store, seed = 1, wall = { 0.0 })
        game.turn(2 * PI)
        game.startOver()
        game.seeLabors()
        game.save()
        val again = Game(store, seed = 1, wall = { 0.0 })
        assertEquals(game.turnsPushed, again.turnsPushed, 1e-9)
        assertEquals(3, again.daysBefore)
        assertEquals(1, again.laborsSeen)
    }

    @Test
    fun anOlderSaveShowsTheDot() {
        val store = MemoryStore(mapOf("climbs" to 1.0))
        val game = Game(store, seed = 1, wall = { 0.0 })
        assertTrue(game.newLabors)
        game.seeLabors()
        assertFalse(game.newLabors)
        assertFalse(Game(store, seed = 1, wall = { 0.0 }).newLabors)
    }

    /** Pushes on for about [seconds], and returns the wise words in the order they came. */
    private fun Game.wiseWords(seconds: Double): List<Quote> {
        val seen = mutableListOf(quotes.quote)
        repeat((seconds * 2).toInt()) {
            turn(PI)
            if (quotes.quote !== seen.last()) seen += quotes.quote
        }
        return seen
    }

    private val turnOn = "A labor is done: Turn On."

    @Test
    fun aLaborBringsItsNewsOnce() {
        val game = Game(MemoryStore(mapOf("turnsPushed" to 99.9, "nextVision" to 10_000.0, "effort" to DAY_LENGTH)), seed = 1, wall = { 0.0 })
        val words = game.wiseWords(120.0)
        assertEquals(turnOn, words[1].text)
        assertEquals("Hermes", words[1].author)
        assertEquals(1, words.count { it.text == turnOn })
    }

    @Test
    fun laborsDoneAtTheStartBringNoNews() {
        val game = Game(MemoryStore(mapOf("climbs" to 1.0, "rightAnswers" to 3.0, "nextVision" to 10_000.0, "effort" to DAY_LENGTH)), seed = 1, wall = { 0.0 })
        val words = game.wiseWords(60.0)
        assertTrue(words.size > 3)
        assertTrue(words.none { it.text.startsWith("A labor") })
    }

    @Test
    fun theNewsWaitsWhileThePanelIsCovered() {
        val game = Game(MemoryStore(mapOf("turnsPushed" to 99.9, "nextVision" to 10_000.0, "effort" to DAY_LENGTH)), seed = 1, wall = { 0.0 })
        game.covered = true
        val covered = game.wiseWords(30.0)
        assertTrue(covered.size > 1)
        assertTrue(covered.none { it.text == turnOn })
        game.covered = false
        assertEquals(turnOn, game.wiseWords(30.0)[1].text)
    }

    @Test
    fun theNewsWaitsForTheReply() {
        val game = asking(MemoryStore(mapOf("nextVision" to 0.5, "effort" to DAY_LENGTH)))
        val reply = game.vision!!.question.reply(right = true)
        game.answer(right = true)
        val words = game.wiseWords(60.0)
        assertEquals(reply, words[0].text)
        assertEquals("A labor is done: Lucky Guess.", words[1].text)
    }

    @Test
    fun theNewsOfATopSurvivesTheNewClimb() {
        val game = Game(MemoryStore(mapOf("nextVision" to 10_000.0, "effort" to DAY_LENGTH)), seed = 1, wall = { 0.0 })
        game.startOver()
        val words = game.wiseWords(60.0)
        assertSame(TUTORIAL, words[0])
        assertEquals("A labor is done: Top of the Morning.", words[1].text)
    }

    @Test
    fun theDevScreenBringsALaborAtTheNextClick() {
        val game = Game(MemoryStore(mapOf("turnsPushed" to 150.0, "nextVision" to 10_000.0, "effort" to DAY_LENGTH)), seed = 1, wall = { 0.0 })
        game.nearTheNextLabor()
        val done = game.laborsDone
        repeat(60) { game.update(DT) }
        assertEquals(done, game.laborsDone)
        game.turn(0.5)
        assertEquals(done + 1, game.laborsDone)
        assertEquals("A labor is done: Spin Doctor.", game.wiseWords(30.0)[1].text)
    }

    @Test
    fun aMoveBringsTheGodsOfTheNewWorld() {
        val game = Game(MemoryStore(), seed = 1, wall = { 0.0 })
        game.moveTo(Place.ETNA)
        repeat(6) {
            game.summonVision()
            assertTrue(game.vision!!.god in godsOf(Place.ETNA))
        }
    }

    @Test
    fun anOlderSaveGoesOnAsking() {
        val store = MemoryStore(mapOf("visionsDrawn" to 7.0, "visionSeed" to 42.0, "place" to Place.ETNA.id.toDouble(), "nextVision" to 0.5))
        val game = Game(store, seed = 1, wall = { 0.0 })
        game.turn(2 * PI)
        assertTrue(game.vision!!.god in godsOf(Place.ETNA))
    }
}
