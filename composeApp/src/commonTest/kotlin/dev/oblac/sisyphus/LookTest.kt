package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.START_TIME
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.scene.SceneGeometry
import dev.oblac.sisyphus.scene.SceneRenderer
import dev.oblac.sisyphus.scene.World
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LookTest {
    private val geo = SceneGeometry(133, 133)

    private fun noon(place: Place): IntArray {
        val game = Game(MemoryStore(mapOf("effort" to (0.5 - START_TIME) * DAY_LENGTH, "place" to place.id.toDouble())), seed = 3)
        val px = Pixels(geo.width, geo.height)
        SceneRenderer(geo).render(px, game, World(4).also { it.reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
        return px.data.copyOf()
    }

    @Test
    fun etnaLooksLikeItself() {
        assertFalse(noon(Place.ETNA).contentEquals(noon(Place.CORINTH)))
    }

    @Test
    fun theUnderworldLooksLikeCorinthUntilItIsDrawn() {
        assertTrue(noon(Place.UNDERWORLD).contentEquals(noon(Place.CORINTH)))
    }

    /** Over a second of rain, how far the drops fall, and how many splashes they make. */
    private fun rain(place: Place): Pair<Double, Int> {
        val world = World(4).also { it.reset(geo, Weather.RAIN) }
        var fall = 0.0
        var splashes = 0
        repeat(10) {
            val before = world.drops.map { it.y }
            world.update(0.1, 0.0, 0.0, 0, Weather.RAIN, 0.5, 0f, 0, place)
            fall += world.drops.zip(before).filter { (d, y) -> d.y > y }.map { (d, y) -> d.y - y }.average()
            splashes += world.particles.size
        }
        return fall to splashes
    }

    @Test
    fun ashFallsSlowlyOnEtnaWhereRainFallsInCorinth() {
        val (rainFall, rainSplashes) = rain(Place.CORINTH)
        val (ashFall, ashSplashes) = rain(Place.ETNA)
        assertTrue(rainFall > 60, "rain falls $rainFall")
        assertTrue(ashFall < 30, "ash falls $ashFall")
        assertTrue(rainSplashes > 0)
        assertEquals(0, ashSplashes)
    }
}
