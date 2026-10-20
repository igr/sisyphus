package dev.oblac.sisyphus

import dev.oblac.sisyphus.audio.CLICKS
import dev.oblac.sisyphus.audio.SAMPLE_RATE
import dev.oblac.sisyphus.audio.click
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ClickTest {
    private val samples = click(0)

    @Test
    fun isShortAndNotTooLoud() {
        assertTrue(samples.size < SAMPLE_RATE / 30)
        assertEquals(0.6f, samples.maxOf { abs(it) }, 0.01f)
    }

    @Test
    fun startsAndEndsWithoutAPop() {
        assertTrue(abs(samples.first()) < 0.1f)
        assertTrue(samples.takeLast(10).all { abs(it) < 0.01f })
    }

    @Test
    fun mostOfTheSoundIsInTheFirstTenMilliseconds() {
        val energy = samples.sumOf { it.toDouble() * it }
        val early = samples.take(SAMPLE_RATE / 100).sumOf { it.toDouble() * it }
        assertTrue(early / energy > 0.9, "early energy ${early / energy}")
    }

    @Test
    fun variantsDiffer() {
        val all = (0 until CLICKS).map { click(it).toList() }
        assertEquals(CLICKS, all.toSet().size)
        assertFalse(click(0).contentEquals(click(1)))
    }
}
