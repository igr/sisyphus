package dev.oblac.sisyphus

import dev.oblac.sisyphus.gfx.hash
import dev.oblac.sisyphus.scene.PROPS
import dev.oblac.sisyphus.scene.propFor
import kotlin.test.Test
import kotlin.test.assertEquals

class PropsTest {
    @Test
    fun everyKindOfTreeGrowsOnTheHill() {
        val seen = (0 until 2000).map { propFor(hash(it, 0, 61) ushr 8) }.toSet()
        assertEquals(PROPS.toSet(), seen)
    }
}
