package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.scene.SceneGeometry
import dev.oblac.sisyphus.scene.SceneRenderer
import dev.oblac.sisyphus.scene.World
import dev.oblac.sisyphus.scene.rockIcon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RockLookTest {
    private fun rockPixels(tier: Int, place: Place = Place.CORINTH): List<Int> {
        val geo = SceneGeometry(64, 64)
        val game = Game(MemoryStore(mapOf("shop.rock" to tier.toDouble(), "effort" to 300.0, "place" to place.id.toDouble())), seed = 3)
        val px = Pixels(64, 64)
        SceneRenderer(geo).render(px, game, World(9).apply { reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
        val cx = (geo.heroX + geo.rockDx).toInt()
        val cy = (geo.baseY + geo.rockDy).toInt()
        val r = geo.rockR.toInt()
        return (cy - r..cy + r).flatMap { y -> (cx - r..cx + r).map { x -> px[x, y] } }
    }

    @Test
    fun eachRockTierLooksDifferent() {
        assertEquals(4, (0..3).map { rockPixels(it) }.toSet().size)
    }

    @Test
    fun eachRockTierLooksDifferentOnEtna() {
        assertEquals(4, (0..3).map { rockPixels(it, Place.ETNA) }.toSet().size)
    }

    @Test
    fun eachRockTierLooksDifferentInTheUnderworld() {
        assertEquals(4, (0..3).map { rockPixels(it, Place.UNDERWORLD) }.toSet().size)
    }

    @Test
    fun theUnderworldHasRocksOfItsOwnAfterTheFirst() {
        fun icon(place: Place, tier: Int) = rockIcon(place, tier).data.toList()
        assertEquals(icon(Place.CORINTH, 0), icon(Place.UNDERWORLD, 0))
        for (tier in 1..3) {
            assertNotEquals(icon(Place.CORINTH, tier), icon(Place.UNDERWORLD, tier))
            assertNotEquals(icon(Place.ETNA, tier), icon(Place.UNDERWORLD, tier))
        }
    }

    @Test
    fun etnaHasRocksOfItsOwnAfterTheFirst() {
        fun icon(place: Place, tier: Int) = rockIcon(place, tier).data.toList()
        assertEquals(icon(Place.CORINTH, 0), icon(Place.ETNA, 0))
        for (tier in 1..3) assertNotEquals(icon(Place.CORINTH, tier), icon(Place.ETNA, tier))
    }
}
