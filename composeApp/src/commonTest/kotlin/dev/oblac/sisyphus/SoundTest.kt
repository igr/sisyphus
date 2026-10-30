package dev.oblac.sisyphus

import kotlin.test.Test
import kotlin.test.assertEquals

/** Writes down what it was asked to play. */
private class Recorder : Sound {
    val played = mutableListOf<String>()
    /** How loud the rock rolled, at each call. */
    val levels = mutableListOf<Float>()
    override fun prepare() { played += "prepare" }
    override fun click() { played += "click" }
    override fun ui() { played += "ui" }
    override fun win() { played += "win" }
    override fun fanfare() { played += "fanfare" }
    override fun coin() { played += "coin" }
    override fun spell() { played += "spell" }
    override fun popup() { played += "popup" }
    override fun victory() { played += "victory" }
    override fun roll(level: Float) { levels += level }
    override fun pause() { played += "pause" }
}

class SoundTest {
    @Test
    fun theKnobClicksTheScreenClicksBuyingWinsAndANewWorldHasAFanfare() {
        val sound = Recorder()
        sound.tick(on = true)
        for (touch in Touch.entries) sound.play(touch, on = true)
        assertEquals(listOf("click", "prepare", "ui", "ui", "win", "ui", "ui", "ui", "ui", "fanfare", "ui", "ui", "ui"), sound.played)
    }

    @Test
    fun theRockRollsLouderAsHePushesFasterUpToABriskPush() {
        val sound = Recorder()
        for (turns in listOf(0.0, 0.375, 1.5, 3.0)) sound.rock(turns, on = true)
        assertEquals(listOf(0f, 0.5f, 1f, 1f), sound.levels)
    }

    @Test
    fun theRockIsSilentWhileItsSoundIsOff() {
        val sound = Recorder()
        sound.rock(1.5, on = false)
        assertEquals(listOf(0f), sound.levels)
    }

    @Test
    fun aNewObolPlaysTheCoin() {
        val sound = Recorder()
        sound.obol(on = true)
        assertEquals(listOf("coin"), sound.played)
    }

    @Test
    fun nothingPlaysWhileTheSoundIsOff() {
        val sound = Recorder()
        sound.tick(on = false)
        sound.obol(on = false)
        for (touch in Touch.entries) sound.play(touch, on = false)
        assertEquals(listOf("pause"), sound.played)
    }

    @Test
    fun theVisionIsSilentWhenTheSoundIsOff() {
        val sound = Recorder()
        sound.vision(on = false)
        assertEquals(emptyList(), sound.played)
        sound.vision(on = true)
        assertEquals(listOf("spell"), sound.played)
    }

    @Test
    fun aBubblePlaysThePopupUnlessTheSoundIsOff() {
        val sound = Recorder()
        sound.bubble(on = false)
        assertEquals(emptyList(), sound.played)
        sound.bubble(on = true)
        assertEquals(listOf("popup"), sound.played)
    }

    @Test
    fun theTopPlaysTheVictoryUnlessTheSoundIsOff() {
        val sound = Recorder()
        sound.top(on = false)
        assertEquals(emptyList(), sound.played)
        sound.top(on = true)
        assertEquals(listOf("victory"), sound.played)
    }
}
