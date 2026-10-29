package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.PX_PER_TURN
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.START_TIME
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.scene.SceneGeometry
import dev.oblac.sisyphus.scene.SceneRenderer
import dev.oblac.sisyphus.scene.World
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LookTest {
    private val geo = SceneGeometry(133, 133)

    private fun noon(place: Place, geo: SceneGeometry = this.geo): IntArray {
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
    fun theUnderworldLooksLikeItself() {
        assertFalse(noon(Place.UNDERWORLD).contentEquals(noon(Place.CORINTH)))
        assertFalse(noon(Place.UNDERWORLD).contentEquals(noon(Place.ETNA)))
    }

    @Test
    fun cairoLooksLikeItself() {
        for (other in listOf(Place.CORINTH, Place.ETNA, Place.UNDERWORLD)) assertFalse(noon(Place.CAIRO).contentEquals(noon(other)))
    }

    /** A stone of the tale stands by the path ahead. */
    @Test
    fun theStoneAheadIsDrawn() {
        fun noon(stone: Boolean): IntArray {
            val keys = mutableMapOf("effort" to (0.5 - START_TIME) * DAY_LENGTH, "shop.alone" to 1.0, "stelesTold" to 0.0)
            if (stone) keys["nextStele"] = 2 * PX_PER_TURN
            val px = Pixels(geo.width, geo.height)
            SceneRenderer(geo).render(px, Game(MemoryStore(keys), seed = 3), World(4).also { it.reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
            return px.data.copyOf()
        }
        assertFalse(noon(stone = true).contentEquals(noon(stone = false)))
    }

    /** On the flat top, a joint between two courses runs straight across, under the sand. */
    @Test
    fun thePyramidCoursesLieLevelOnTheFlatTop() {
        val summit = SceneGeometry(133, 133).also { it.top = 10 }
        val px = noon(Place.CAIRO, summit)
        val columns = 90 until summit.width
        assertEquals(summit.surfaceY(columns.first, 0), summit.surfaceY(columns.last, 0))
        val level = (summit.surfaceY(columns.first, 0) + 4 until summit.height).filter { y ->
            columns.all { px[y * summit.width + it] == px[y * summit.width + columns.first] }
        }
        assertTrue(level.isNotEmpty())
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

    @Test
    fun sandBlowsByInCairoWhereRainFallsInCorinth() {
        val (sandFall, sandSplashes) = rain(Place.CAIRO)
        val world = World(4).also { it.reset(geo, Weather.RAIN) }
        val before = world.drops.map { it.x }
        world.update(0.1, 0.0, 0.0, 0, Weather.RAIN, 0.5, 0f, 0, Place.CAIRO)
        val blown = world.drops.zip(before).map { (d, x) -> abs(d.x - x) }.filter { it < 50 }.average() * 10
        assertTrue(sandFall < 30, "sand falls $sandFall")
        assertTrue(blown > 60, "sand blows $blown")
        assertEquals(0, sandSplashes)
    }
}
