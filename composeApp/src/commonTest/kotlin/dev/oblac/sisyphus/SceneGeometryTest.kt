package dev.oblac.sisyphus

import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.TRANSPARENT
import dev.oblac.sisyphus.scene.HERO_EYE
import dev.oblac.sisyphus.scene.HeroSprites
import dev.oblac.sisyphus.scene.SceneGeometry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SceneGeometryTest {
    private val geo = SceneGeometry(133, 133)
    private val hero = HeroSprites(geo.rockDx, geo.rockDy, geo.rockR)

    /** At the top he holds the rock a moment, stands up when it slips back, and turns around once it rolls past him. */
    @Test
    fun atTheTopHeStandsUpAndTurnsToWatchTheRockGo() {
        assertNull(hero.atTop(rolled = 0.0))
        assertSame(hero.stand, hero.atTop(rolled = 1.0))
        assertSame(hero.stand, hero.atTop(rolled = geo.rockDx - 1))
        assertSame(hero.turned, hero.atTop(rolled = geo.rockDx + 1))
    }

    @Test
    fun standingUpHeIsTallerThanWhenHePushes() {
        fun top(px: Pixels) = (0 until px.height).first { y -> (0 until px.width).any { px[it, y] != TRANSPARENT } }
        assertTrue(top(hero.stand) < top(hero.idle[0]) - 3, "${top(hero.stand)} and ${top(hero.idle[0])}")
    }

    @Test
    fun turnedAroundHeLooksDownTheHill() {
        fun eye(px: Pixels) = (0 until px.width).first { x -> (0 until px.height).any { px[x, it] == HERO_EYE } }
        assertTrue(eye(hero.stand) > HeroSprites.ORIGIN_X)
        assertTrue(eye(hero.turned) < HeroSprites.ORIGIN_X)
    }

    @Test
    fun theSlopeStartsAnEighthOfTheWayIn() {
        for (p in listOf(0, 1, 7, 1000)) {
            val foot = (0 until geo.width).first { geo.surfaceY(it, p) < geo.height }
            assertEquals(geo.width / 8.0, foot.toDouble(), 2.0)
        }
    }

    @Test
    fun sisyphusStandsAgainstTheSky() {
        assertTrue(geo.horizon > geo.baseY, "the sea line is below his feet")
        assertTrue(geo.horizon < geo.height - 10, "and there is still sea to see")
    }
}
