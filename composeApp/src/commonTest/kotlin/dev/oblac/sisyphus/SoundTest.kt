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
    override fun fanfare() { played += "fanfare" }
    override fun coin() { played += "coin" }
    override fun pause() { played += "pause" }
}

class SoundTest {
    @Test
    fun theKnobClicksTheScreenClicksBuyingWinsAndANewWorldHasAFanfare() {
        val sound = Recorder()
        sound.tick(on = true)
        for (touch in Touch.entries) sound.play(touch, on = true)
        assertEquals(listOf("click", "prepare", "ui", "ui", "win", "ui", "ui", "ui", "ui", "fanfare", "ui"), sound.played)
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
}
