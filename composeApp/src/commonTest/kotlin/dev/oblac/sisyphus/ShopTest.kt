package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.FIRST_OBOL
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.OBOL_SPREAD
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.Shop
import dev.oblac.sisyphus.game.WORK_PER_OBOL
import dev.oblac.sisyphus.game.items
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ShopTest {
    private val corinth = items(Place.CORINTH)

    @Test
    fun theShopHasFiveLinesOfThree() {
        assertEquals(15, corinth.size)
        for (line in Line.entries) assertEquals(listOf(1, 2, 3), corinth.filter { it.line == line }.map { it.tier })
        assertEquals(87, corinth.sumOf { it.price })
    }

    @Test
    fun pricesAndEffectsFollowTheDesign() {
        fun of(line: Line) = corinth.filter { it.line == line }.map { it.price to it.effect }
        assertEquals(listOf(2 to 1.5, 5 to 2.0, 9 to 3.0), of(Line.ROCK))
        assertEquals(listOf(3 to 1.25, 6 to 1.5, 10 to 2.0), of(Line.SIDEKICKS))
        assertEquals(listOf(1 to 6.0, 4 to 12.0, 8 to 24.0), of(Line.GODS))
        assertEquals(listOf(2 to 504.0, 6 to 360.0, 10 to 216.0), of(Line.BLESSING))
        assertEquals(listOf(3, 7, 11), corinth.filter { it.line == Line.BEAUTY }.map { it.price })
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
    fun theFirstObolComesSoon() {
        val shop = Shop()
        shop.addWork(FIRST_OBOL - 1)
        assertEquals(0, shop.obols.toInt())
        shop.addWork(1.0)
        assertEquals(1.0, shop.obols, 1e-9)
    }

    @Test
    fun eachObolAfterTheFirstNeedsTheWorkPerObolGiveOrTakeHalfADay() {
        for ((tiers, perObol) in listOf(emptyMap<Line, Int>() to WORK_PER_OBOL, mapOf(Line.BLESSING to 3) to 216.0)) {
            val shop = Shop(tiers, obols = 1.0, random = Random(1))
            val needs = List(200) {
                shop.addWork(shop.nextObol)
                shop.nextObol
            }
            assertEquals(201.0, shop.obols, 1e-6)
            assertTrue(needs.all { it in perObol - OBOL_SPREAD..perObol + OBOL_SPREAD }, "$needs")
            assertTrue(needs.min() < perObol - OBOL_SPREAD / 2 && needs.max() > perObol + OBOL_SPREAD / 2, "$needs")
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
        val shop = Shop(obols = 2.4, nextObol = 700.0)
        assertTrue(shop.buy(Line.BLESSING))
        assertEquals(0.4, shop.obols, 1e-9)
        assertEquals(700.0 * 504 / 720, shop.nextObol, 1e-9)
        val goat = Shop(obols = 1.4, nextObol = 700.0)
        assertTrue(goat.buy(Line.GODS))
        assertEquals(700.0, goat.nextObol, 1e-9)
    }

    @Test
    fun onlyANewShopWaitsForTheFirstObol() {
        assertEquals(FIRST_OBOL, Shop().nextObol)
        assertEquals(WORK_PER_OBOL, Shop(obols = 3.5).nextObol)
        assertEquals(216.0, Shop(mapOf(Line.BLESSING to 3), obols = 0.5).nextObol)
        assertEquals(216.0, Shop(mapOf(Line.BLESSING to 3)).nextObol)
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
}
