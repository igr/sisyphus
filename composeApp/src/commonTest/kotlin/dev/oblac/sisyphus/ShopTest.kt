package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Config
import dev.oblac.sisyphus.game.FIRST_OBOL
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.Shop
import dev.oblac.sisyphus.game.items
import dev.oblac.sisyphus.game.plain
import dev.oblac.sisyphus.game.workDays
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ShopTest {
    private val corinth = items(Place.CORINTH)
    private val hermes = Config.ITEMS.getValue(Line.BLESSING)[0].second
    private val plutus = Config.ITEMS.getValue(Line.BLESSING)[2].second

    @Test
    fun theShopHasFiveLinesOfThree() {
        assertEquals(15, corinth.size)
        for (line in Line.entries) assertEquals(listOf(1, 2, 3), corinth.filter { it.line == line }.map { it.tier })
    }

    /** The items have the prices and the effects of [Config.ITEMS]. Each tier costs more, and does more, than the one before. */
    @Test
    fun eachTierCostsMoreAndDoesMoreThanTheOneBefore() {
        for (line in Line.entries) {
            val tiers = corinth.filter { it.line == line }
            assertEquals(Config.ITEMS.getValue(line), tiers.map { it.price to it.effect })
            for ((a, b) in tiers.zipWithNext()) {
                assertTrue(b.price > a.price, b.name)
                when (line) {
                    Line.BLESSING -> assertTrue(b.effect < a.effect && b.effect > 0, b.name)
                    Line.BEAUTY -> assertEquals(0.0, b.effect)
                    else -> assertTrue(b.effect > a.effect, b.name)
                }
            }
        }
        assertTrue(corinth.first { it.line == Line.BLESSING }.effect < 1)
    }

    @Test
    fun eachLineIsBoughtInOrder() {
        val shop = Shop(obols = 20.0)
        assertEquals(1, shop.next(Line.ROCK)?.tier)
        assertTrue(shop.buy(Line.ROCK))
        assertEquals(1, shop.tier(Line.ROCK))
        assertEquals(18.0, shop.obols, 1e-9)
        assertEquals(2, shop.next(Line.ROCK)?.tier)
    }

    @Test
    fun nothingIsBoughtWithoutEnoughObols() {
        val shop = Shop(obols = 1.99)
        assertFalse(shop.buy(Line.ROCK))
        assertEquals(0, shop.tier(Line.ROCK))
        assertEquals(1.99, shop.obols, 1e-9)
    }

    @Test
    fun aCompleteLineSellsNothingMore() {
        val shop = Shop(tiers = mapOf(Line.ROCK to 3), obols = 100.0)
        assertNull(shop.next(Line.ROCK))
        assertFalse(shop.buy(Line.ROCK))
    }

    @Test
    fun theRockAndTheHelperBesideHimMultiply() {
        assertEquals(1.0, Shop().bonus, 1e-9)
        assertEquals(1.5 * 1.25, Shop(tiers = mapOf(Line.ROCK to 1, Line.SIDEKICKS to 1)).bonus, 1e-9)
        assertEquals(6.0, Shop(tiers = mapOf(Line.ROCK to 3, Line.SIDEKICKS to 3)).bonus, 1e-9)
    }

    @Test
    fun theFirstFourObolsComeSoon() {
        val shop = Shop(random = Random(1))
        shop.addWork(FIRST_OBOL - 1)
        assertEquals(0, shop.obols.toInt())
        shop.addWork(1.0)
        assertEquals(1.0, shop.obols, 1e-9)
        shop.addWork((Config.FAST_OBOLS - 1) * FIRST_OBOL)
        assertEquals(Config.FAST_OBOLS.toDouble(), shop.obols, 1e-9)
        assertTrue(shop.nextObol in Config.WORK_PER_OBOL * (1 - Config.OBOL_SPREAD)..Config.WORK_PER_OBOL * (1 + Config.OBOL_SPREAD))
    }

    /** The fast obols are the first four of the climb, also when some of them are spent. */
    @Test
    fun spentObolsCountAmongTheFastOnes() {
        val shop = Shop(random = Random(1))
        shop.addWork(FIRST_OBOL)
        assertTrue(shop.buy(Line.GODS))
        shop.addWork(2 * FIRST_OBOL)
        assertTrue(shop.buy(Line.ROCK))
        shop.addWork(FIRST_OBOL)
        assertEquals(1.0, shop.obols, 1e-9)
        assertTrue(shop.nextObol in Config.WORK_PER_OBOL * (1 - Config.OBOL_SPREAD)..Config.WORK_PER_OBOL * (1 + Config.OBOL_SPREAD))
    }

    @Test
    fun aBlessingMakesTheFastObolsSmallerToo() {
        val shop = Shop(random = Random(1))
        shop.addWork(2 * FIRST_OBOL)
        assertTrue(shop.buy(Line.BLESSING))
        assertEquals(FIRST_OBOL * hermes, shop.nextObol, 1e-9)
        shop.addWork(shop.nextObol)
        assertEquals(FIRST_OBOL * hermes, shop.nextObol, 1e-9)
    }

    @Test
    fun eachObolAfterTheFastOnesNeedsTheWorkPerObolGiveOrTakeAPartOfIt() {
        for ((tiers, perObol) in listOf(emptyMap<Line, Int>() to Config.WORK_PER_OBOL, mapOf(Line.BLESSING to 3) to Config.WORK_PER_OBOL * plutus)) {
            val shop = Shop(tiers, obols = Config.FAST_OBOLS.toDouble(), random = Random(1))
            val needs = List(200) {
                shop.addWork(shop.nextObol)
                shop.nextObol
            }
            assertEquals(Config.FAST_OBOLS + 200.0, shop.obols, 1e-6)
            assertTrue(needs.all { it in perObol * (1 - Config.OBOL_SPREAD)..perObol * (1 + Config.OBOL_SPREAD) }, "$needs")
            assertTrue(needs.min() < perObol * (1 - Config.OBOL_SPREAD / 2) && needs.max() > perObol * (1 + Config.OBOL_SPREAD / 2), "$needs")
        }
    }

    @Test
    fun workLeftOverGoesToTheNextObol() {
        val shop = Shop(random = Random(1))
        shop.addWork(FIRST_OBOL + 100)
        assertEquals(1 + 100 / shop.nextObol, shop.obols, 1e-9)
    }

    @Test
    fun aBlessingMakesTheNextObolSmallerAndKeepsWhatWasEarned() {
        val shop = Shop(obols = 2.4, nextObol = 150.0)
        assertTrue(shop.buy(Line.BLESSING))
        assertEquals(0.4, shop.obols, 1e-9)
        assertEquals(150.0 * hermes, shop.nextObol, 1e-9)
        val goat = Shop(obols = 1.4, nextObol = 150.0)
        assertTrue(goat.buy(Line.GODS))
        assertEquals(150.0, goat.nextObol, 1e-9)
    }

    /** Without the saved work, the shop counts the obols of the climb, the spent ones too. */
    @Test
    fun withoutTheSavedWorkTheShopCountsTheObolsOfTheClimb() {
        assertEquals(FIRST_OBOL, Shop().nextObol)
        assertEquals(FIRST_OBOL, Shop(obols = 3.5).nextObol)
        assertEquals(Config.WORK_PER_OBOL, Shop(obols = 4.5).nextObol)
        assertEquals(Config.WORK_PER_OBOL, Shop(mapOf(Line.ROCK to 1, Line.GODS to 1), obols = 1.5).nextObol)
        assertEquals(Config.WORK_PER_OBOL * plutus, Shop(mapOf(Line.BLESSING to 3), obols = 0.5).nextObol)
        assertEquals(Config.WORK_PER_OBOL * plutus, Shop(mapOf(Line.BLESSING to 3)).nextObol)
    }

    /** The pace the numbers set: 5 minutes a day at a turn a second, the cheapest item bought once it is affordable, the gods pushing all day. */
    @Test
    fun atFiveMinutesADayTheTopComesInAboutAWeek() {
        val shop = Shop(random = Random(1))
        var climb = Config.CLIMB_TURNS.toDouble()
        var days = 0
        while (climb > 0) {
            days++
            repeat(5 * 60) {
                if (shop.allBought) climb-- else shop.addWork(shop.bonus)
                while (true) {
                    val item = Line.entries.mapNotNull { shop.next(it) }.minByOrNull { it.price } ?: break
                    if (!shop.buy(item.line)) break
                }
            }
            if (!shop.allBought) shop.addWork(shop.helpersPerHour * 24)
        }
        assertTrue(days in 6..9, "the top on day $days")
    }

    /** An obol saved with other numbers needs no more work than an obol can need now. What was earned of it is kept. */
    @Test
    fun aSavedObolNeedsNoMoreWorkThanAnObolCanNeedNow() {
        val shop = Shop(obols = 5.5, nextObol = 10_000.0)
        assertEquals(Config.WORK_PER_OBOL * (1 + Config.OBOL_SPREAD), shop.nextObol)
        assertEquals(5.5, shop.obols)
    }

    @Test
    fun boughtCountsTheItemsOfEveryLine() {
        assertEquals(0, Shop().bought)
        assertEquals(3, Shop(mapOf(Line.ROCK to 2, Line.GODS to 1)).bought)
        assertEquals(15, Shop(Line.entries.associateWith { 3 }).bought)
    }

    @Test
    fun helpersOnTheirOwnPushByTheHour() {
        assertEquals(0.0, Shop().helpersPerHour)
        assertNull(Shop().helper)
        val shop = Shop(tiers = mapOf(Line.GODS to 3))
        assertEquals(24.0, shop.helpersPerHour)
        assertEquals("Thanatos", shop.helper?.name)
    }

    @Test
    fun theBlessingsTellTheirDaysOfPushing() {
        for (place in Place.entries) for (item in items(place).filter { it.line == Line.BLESSING }) {
            val days = workDays(Config.WORK_PER_OBOL * item.effect)
            assertEquals("obol per $days", item.note, item.name)
            assertTrue("every $days of pushing" in item.details, item.name)
            if (item.tier == 1) assertTrue("not ${plain(Config.WORK_PER_OBOL / Config.TURNS_PER_DAY)}." in item.details, item.name)
        }
    }

    @Test
    fun numbersAndDaysReadWell() {
        assertEquals("1.5", plain(1.5))
        assertEquals("2", plain(2.0))
        assertEquals("1.25", plain(1.25))
        assertEquals("1.5 days", workDays(108.0))
        assertEquals("day", workDays(72.0))
        assertEquals("half day", workDays(36.0))
        assertEquals("7 days", workDays(720 * 0.7))
    }

    /** Every text that shows a number is made from the number of its item, the same in every world. */
    @Test
    fun theTextsOfTheItemsShowTheirNumbers() {
        for (place in Place.entries) for (item in items(place)) {
            assertFalse('{' in item.details, item.name)
            val x = plain(item.effect)
            when (item.line) {
                Line.ROCK -> assertEquals("turns count ${x}x", item.note, item.name)
                Line.SIDEKICKS -> assertEquals("helps push ${x}x", item.note, item.name)
                Line.GODS -> assertEquals("$x pushes an hour", item.note, item.name)
                else -> {}
            }
            if (item.line == Line.ROCK || item.line == Line.SIDEKICKS) assertTrue("counts $x times" in item.details, item.name)
            if (item.line == Line.GODS) assertTrue("$x times an hour" in item.details, item.name)
        }
    }

    @Test
    fun everyWorldHasThePricesAndEffectsOfCorinth() {
        for (place in Place.entries) {
            val world = items(place)
            assertEquals(corinth.map { listOf(it.line, it.tier, it.price, it.effect) }, world.map { listOf(it.line, it.tier, it.price, it.effect) }, place.title)
            assertEquals(corinth.filter { it.line != Line.BEAUTY }.map { it.note }, world.filter { it.line != Line.BEAUTY }.map { it.note }, place.title)
        }
    }

    @Test
    fun etnaHasNamesOfItsOwn() {
        assertEquals(
            listOf(
                "Lava stone", "Volcanic bomb", "Obsidian sphere", "Empedocles", "Odysseus", "Polyphemus", "Aetna", "Hephaestus", "Typhon",
                "Sheaf of Demeter", "Wine of Dionysus", "Silver of Arethusa", "Lava fountain", "Eagle of Zeus", "Torches of Demeter",
            ),
            items(Place.ETNA).map { it.name },
        )
    }

    @Test
    fun aShopSellsTheItemsOfItsWorld() {
        assertEquals("Merope", Shop().next(Line.SIDEKICKS)?.name)
        assertEquals("Empedocles", Shop(place = Place.ETNA).next(Line.SIDEKICKS)?.name)
        assertEquals("Odysseus", Shop(mapOf(Line.SIDEKICKS to 2), place = Place.ETNA).owned(Line.SIDEKICKS)?.name)
        assertEquals("Orpheus", Shop(place = Place.UNDERWORLD).next(Line.SIDEKICKS)?.name)
        assertEquals("Io", Shop(place = Place.CAIRO).next(Line.SIDEKICKS)?.name)
        assertEquals("Kaguya", Shop(place = Place.FUJI).next(Line.SIDEKICKS)?.name)
    }

    @Test
    fun theUnderworldHasNamesOfItsOwn() {
        assertEquals(
            listOf(
                "Stone of Lethe", "Rock of Tantalus", "Adamant sphere", "Orpheus", "Theseus", "Achilles", "Cerberus", "Persephone", "Hades",
                "Obol for the ferry", "Golden bough", "Riches of Plouton", "Boat of Charon", "Psyche", "Lights of Elysium",
            ),
            items(Place.UNDERWORLD).map { it.name },
        )
    }

    @Test
    fun cairoHasNamesOfItsOwn() {
        assertEquals(
            listOf(
                "Desert stone", "Egg of the Phoenix", "Alabaster sphere", "Io", "Epaphus", "Memnon", "Scarab", "Isis", "Osiris",
                "Bread and onions", "Gift of the Nile", "Gold of Cheops", "Hippopotamus", "Phoenix", "The Milky Way",
            ),
            items(Place.CAIRO).map { it.name },
        )
    }

    @Test
    fun mountFujiHasNamesOfItsOwn() {
        assertEquals(
            listOf(
                "Mossy stone", "Daruma", "Crystal sphere", "Kaguya", "En no Gyoja", "Kintaro", "Tengu", "Fujin", "Izanagi",
                "Bowl of rice", "Lucky cat", "Mallet of Daikoku", "Koi", "Dragon", "Fireflies",
            ),
            items(Place.FUJI).map { it.name },
        )
    }
}
