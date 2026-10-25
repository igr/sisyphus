package dev.oblac.sisyphus

import dev.oblac.sisyphus.audio.SAMPLE_RATE
import dev.oblac.sisyphus.audio.loadClick
import dev.oblac.sisyphus.audio.loadUi
import dev.oblac.sisyphus.audio.loadWin
import kotlinx.coroutines.runBlocking
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class SoundFileTest {
    @Test
    fun theClickFilePlaysAtOnce() {
        val click = runBlocking { loadClick() }
        assertTrue(click.take(SAMPLE_RATE / 1000).any { abs(it) > 0.01f })
    }

    @Test
    fun theUiClickPlaysAtOnce() {
        val ui = runBlocking { loadUi() }
        assertTrue(ui.take(SAMPLE_RATE / 1000).any { abs(it) > 0.01f })
    }

    @Test
    fun theWinFilePlaysAtOnceAndEndsWithinASecond() {
        val win = runBlocking { loadWin() }
        assertTrue(win.take(SAMPLE_RATE / 1000).any { abs(it) > 0.01f })
        assertTrue(win.size < SAMPLE_RATE)
    }
}
