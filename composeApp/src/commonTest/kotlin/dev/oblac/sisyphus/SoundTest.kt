package dev.oblac.sisyphus

import kotlin.test.Test
import kotlin.test.assertEquals

/** Writes down what it was asked to play. */
private class Recorder : Sound {
    val played = mutableListOf<String>()
    override fun prepare() { played += "prepare" }
    override fun click() { played += "click" }
    override fun ui() { played += "ui" }
    override fun win() { played += "win" }
    override fun pause() { played += "pause" }
}

class SoundTest {
    @Test
    fun theKnobClicksTheScreenClicksAndBuyingWins() {
        val sound = Recorder()
        sound.tick(on = true)
        for (touch in Touch.entries) sound.play(touch, on = true)
        assertEquals(listOf("click", "prepare", "ui", "ui", "win", "ui", "ui", "ui"), sound.played)
    }

    @Test
    fun nothingPlaysWhileTheSoundIsOff() {
        val sound = Recorder()
        sound.tick(on = false)
        for (touch in Touch.entries) sound.play(touch, on = false)
        assertEquals(listOf("pause"), sound.played)
    }
}
