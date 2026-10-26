package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.Shop
import dev.oblac.sisyphus.game.items
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
    fun workTurnsIntoObols() {
        val shop = Shop()
        shop.addWork(720.0)
        assertEquals(1.0, shop.obols, 1e-9)
        val blessed = Shop(tiers = mapOf(Line.BLESSING to 3))
        blessed.addWork(216.0)
        assertEquals(1.0, blessed.obols, 1e-9)
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
        assertEquals("Merope", Shop(place = Place.UNDERWORLD).next(Line.SIDEKICKS)?.name)
    }
}
