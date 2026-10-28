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

class BeautyTest {
    private val geo = SceneGeometry(133, 133)

    private fun live(beauty: Int, seconds: Int, place: Place = Place.CORINTH, check: (World) -> Unit) {
        val world = World(4).also { it.reset(geo, Weather.CLEAR) }
        repeat(seconds * 10) {
            world.update(0.1, 0.0, 0.0, 0, Weather.CLEAR, 0.5, 0f, beauty, place)
            check(world)
        }
    }

    private fun frame(beauty: Int, time: Double, place: Place = Place.CORINTH): IntArray {
        val effort = (time - START_TIME) * DAY_LENGTH
        val game = Game(MemoryStore(mapOf("shop.beauty" to beauty.toDouble(), "effort" to effort, "place" to place.id.toDouble())), seed = 3)
        val px = Pixels(geo.width, geo.height)
        SceneRenderer(geo).render(px, game, World(4).also { it.reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
        return px.data.copyOf()
    }

    @Test
    fun dolphinsLeapOnlyOnceBought() {
        var leaps = 0
        live(0, 120) { leaps += it.dolphins.size }
        assertEquals(0, leaps)
        live(1, 120) { leaps += it.dolphins.size }
        assertTrue(leaps > 0)
    }

    @Test
    fun pegasusFliesByOnceBought() {
        var seen = false
        live(1, 240) { if (it.flyer != null) seen = true }
        assertFalse(seen)
        live(2, 240) { if (it.flyer != null) seen = true }
        assertTrue(seen)
    }

    @Test
    fun thePleiadesShineOnlyAtNight() {
        assertFalse(frame(3, 0.95).contentEquals(frame(2, 0.95)))
        assertTrue(frame(3, 0.5).contentEquals(frame(2, 0.5)))
    }

    @Test
    fun theCraterThrowsFireOnlyOnceBought() {
        var sparks = 0
        live(0, 120, Place.ETNA) { sparks += it.sparks.size }
        assertEquals(0, sparks)
        live(1, 120, Place.ETNA) {
            sparks += it.sparks.size
            assertTrue(it.dolphins.isEmpty())
        }
        assertTrue(sparks > 0)
    }

    @Test
    fun theEagleFliesByOnceBought() {
        var seen = false
        live(1, 240, Place.ETNA) { if (it.flyer != null) seen = true }
        assertFalse(seen)
        live(2, 240, Place.ETNA) { if (it.flyer != null) seen = true }
        assertTrue(seen)
    }

    @Test
    fun theBoatOfCharonCrossesOnlyOnceBought() {
        var seen = false
        live(0, 120, Place.UNDERWORLD) { if (it.ferry != null) seen = true }
        assertFalse(seen)
        live(1, 120, Place.UNDERWORLD) {
            if (it.ferry != null) seen = true
            assertTrue(it.dolphins.isEmpty() && it.sparks.isEmpty())
        }
        assertTrue(seen)
    }

    @Test
    fun psycheFliesByOnceBought() {
        var seen = false
        live(1, 240, Place.UNDERWORLD) { if (it.flyer != null) seen = true }
        assertFalse(seen)
        live(2, 240, Place.UNDERWORLD) { if (it.flyer != null) seen = true }
        assertTrue(seen)
    }

    @Test
    fun theHippopotamusRisesOnlyOnceBought() {
        var seen = false
        live(0, 120, Place.CAIRO) { if (it.hippo != null) seen = true }
        assertFalse(seen)
        live(1, 120, Place.CAIRO) {
            if (it.hippo != null) seen = true
            assertTrue(it.dolphins.isEmpty() && it.sparks.isEmpty() && it.ferry == null)
        }
        assertTrue(seen)
    }

    @Test
    fun thePhoenixFliesByOnceBought() {
        var seen = false
        live(1, 240, Place.CAIRO) { if (it.flyer != null) seen = true }
        assertFalse(seen)
        live(2, 240, Place.CAIRO) { if (it.flyer != null) seen = true }
        assertTrue(seen)
    }

    @Test
    fun theMilkyWayShinesOnlyAtNight() {
        assertFalse(frame(3, 0.95, Place.CAIRO).contentEquals(frame(2, 0.95, Place.CAIRO)))
        assertTrue(frame(3, 0.5, Place.CAIRO).contentEquals(frame(2, 0.5, Place.CAIRO)))
    }

    @Test
    fun theLightsOfElysiumGlowOnlyAtNight() {
        assertFalse(frame(3, 0.95, Place.UNDERWORLD).contentEquals(frame(2, 0.95, Place.UNDERWORLD)))
        assertTrue(frame(3, 0.5, Place.UNDERWORLD).contentEquals(frame(2, 0.5, Place.UNDERWORLD)))
    }

    @Test
    fun theTorchesOfDemeterBurnOnlyAtNight() {
        assertFalse(frame(3, 0.95, Place.ETNA).contentEquals(frame(2, 0.95, Place.ETNA)))
        assertTrue(frame(3, 0.5, Place.ETNA).contentEquals(frame(2, 0.5, Place.ETNA)))
    }
}
