package dev.oblac.sisyphus

import dev.oblac.sisyphus.audio.SAMPLE_RATE
import dev.oblac.sisyphus.audio.loadClick
import dev.oblac.sisyphus.audio.loadCoin
import dev.oblac.sisyphus.audio.loadFanfare
import dev.oblac.sisyphus.audio.loadSpell
import dev.oblac.sisyphus.audio.loadUi
import dev.oblac.sisyphus.audio.loadWin
import kotlinx.coroutines.runBlocking
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

/** The mean square of the loudest half second. */
private fun loudest(samples: FloatArray): Double {
    val n = SAMPLE_RATE / 2
    var sum = (0 until n).sumOf { samples[it].toDouble() * samples[it] }
    var best = sum
    for (i in n until samples.size) {
        sum += samples[i].toDouble() * samples[i] - samples[i - n].toDouble() * samples[i - n]
        best = maxOf(best, sum)
    }
    return best / n
}

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

    @Test
    fun theFanfarePlaysAtOnceAndLastsAFewSeconds() {
        val fanfare = runBlocking { loadFanfare() }
        assertTrue(fanfare.take(SAMPLE_RATE / 1000).any { abs(it) > 0.01f })
        assertTrue(fanfare.size in 2 * SAMPLE_RATE..5 * SAMPLE_RATE, "${fanfare.size} samples")
    }

    @Test
    fun theCoinRingsWithinTenMillisecondsAndEndsWithinThreeSeconds() {
        val coin = runBlocking { loadCoin() }
        assertTrue(coin.take(SAMPLE_RATE / 100).any { abs(it) > 0.1f })
        assertTrue(coin.size in SAMPLE_RATE / 2..3 * SAMPLE_RATE, "${coin.size} samples")
    }

    @Test
    fun theSpellStartsWithinTenMillisecondsAndLastsAFewSeconds() {
        val spell = runBlocking { loadSpell() }
        val peak = spell.maxOf { abs(it) }
        assertTrue(spell.take(SAMPLE_RATE / 100).any { abs(it) > peak / 50 })
        assertTrue(spell.size in 2 * SAMPLE_RATE..5 * SAMPLE_RATE, "${spell.size} samples")
    }

    @Test
    fun theSpellIsSofterThanTheFanfare() {
        val spell = runBlocking { loadSpell() }
        val fanfare = runBlocking { loadFanfare() }
        assertTrue(loudest(spell) < loudest(fanfare), "${loudest(spell)} against ${loudest(fanfare)}")
    }
}
