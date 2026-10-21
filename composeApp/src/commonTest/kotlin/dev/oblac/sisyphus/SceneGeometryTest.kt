package dev.oblac.sisyphus

import dev.oblac.sisyphus.scene.SceneGeometry
import kotlin.test.Test
import kotlin.test.assertEquals

class SceneGeometryTest {
    @Test
    fun theHillStaysInTheLowerRightThird() {
        val geo = SceneGeometry(133, 133)
        for (p in listOf(0, 1, 7, 1000)) {
            val foot = (0 until geo.width).first { geo.surfaceY(it, p) < geo.height }
            assertEquals(geo.width / 3.0, foot.toDouble(), 2.0)
            assertEquals(geo.height * 2 / 3.0, geo.surfaceY(geo.width - 1, p).toDouble(), 2.0)
        }
    }
}
