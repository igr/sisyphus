package dev.oblac.sisyphus

import dev.oblac.sisyphus.audio.SAMPLE_RATE
import dev.oblac.sisyphus.audio.loadClick
import kotlinx.coroutines.runBlocking
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class ClickFileTest {
    @Test
    fun theClickFilePlaysAtOnce() {
        val click = runBlocking { loadClick() }
        assertTrue(click.take(SAMPLE_RATE / 1000).any { abs(it) > 0.01f })
    }
}
