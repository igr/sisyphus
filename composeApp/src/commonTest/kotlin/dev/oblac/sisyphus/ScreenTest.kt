package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.ui.Rect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScreenTest {
    private fun screen(store: Store = MemoryStore()) =
        Screen(Game(store, seed = 1), seed = 2).apply { resize(1179, 2556, 177, 102) }

    private fun Screen.tap(x: Float, y: Float) = press(x * scale + offsetX, y * scale + offsetY)

    private fun Screen.tap(r: Rect) = tap(r.x + r.w / 2f, r.y + r.h / 2f)

    @Test
    fun theSpeakerTurnsTheSoundOffAndOn() {
        val store = MemoryStore()
        val s = screen(store)
        val icon = s.layout!!.soundIcon
        assertTrue(s.game.soundOn)

        assertEquals(Touch.SOUND, s.tap(icon))
        assertFalse(s.game.soundOn)
        assertFalse(s.game.knob.held)
        assertFalse(Game(store, seed = 1).soundOn)

        assertEquals(Touch.SOUND, s.tap(icon))
        assertTrue(s.game.soundOn)
    }

    @Test
    fun theKnobIsGrabbedAroundItsCenter() {
        val s = screen()
        val l = s.layout!!
        assertEquals(Touch.KNOB, s.tap(l.knobX + l.knobR * 0.6f, l.knobY + 0.5f))
        assertTrue(s.game.knob.held)
    }

    @Test
    fun theDisplayIgnoresTouches() {
        val s = screen()
        assertEquals(Touch.NONE, s.tap(s.layout!!.scene))
        assertFalse(s.game.knob.held)
    }
}
