package dev.oblac.sisyphus

import dev.oblac.sisyphus.gfx.hash
import dev.oblac.sisyphus.scene.CAIRO_PROPS
import dev.oblac.sisyphus.scene.ETNA_PROPS
import dev.oblac.sisyphus.scene.PROPS
import dev.oblac.sisyphus.scene.UNDERWORLD_PROPS
import dev.oblac.sisyphus.scene.propFor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PropsTest {
    @Test
    fun everyKindOfTreeGrowsOnTheHill() {
        val seen = (0 until 2000).map { propFor(hash(it, 0, 61) ushr 8) }.toSet()
        assertEquals(PROPS.toSet(), seen)
    }

    @Test
    fun everyKindOfPlantAndStoneShowsUpOnEtna() {
        val seen = (0 until 2000).map { propFor(hash(it, 0, 61) ushr 8, ETNA_PROPS) }.toSet()
        assertEquals(ETNA_PROPS.toSet(), seen)
        assertTrue(ETNA_PROPS.none { it in PROPS })
    }

    @Test
    fun everyKindOfPlantAndStoneShowsUpInTheUnderworld() {
        val seen = (0 until 2000).map { propFor(hash(it, 0, 61) ushr 8, UNDERWORLD_PROPS) }.toSet()
        assertEquals(UNDERWORLD_PROPS.toSet(), seen)
        assertTrue(UNDERWORLD_PROPS.none { it in PROPS || it in ETNA_PROPS })
    }

    @Test
    fun everyKindOfStoneAndThingShowsUpInCairo() {
        val seen = (0 until 2000).map { propFor(hash(it, 0, 61) ushr 8, CAIRO_PROPS) }.toSet()
        assertEquals(CAIRO_PROPS.toSet(), seen)
        assertTrue(CAIRO_PROPS.none { it in PROPS || it in ETNA_PROPS || it in UNDERWORLD_PROPS })
    }
}
