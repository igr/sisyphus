package dev.oblac.sisyphus

import dev.oblac.sisyphus.scene.SceneGeometry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SceneGeometryTest {
    private val geo = SceneGeometry(133, 133)

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
