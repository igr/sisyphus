package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.ITEMS
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.PX_PER_TURN
import dev.oblac.sisyphus.gfx.PixelFont
import dev.oblac.sisyphus.ui.DISABLED
import dev.oblac.sisyphus.ui.Rect
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ScreenTest {
    private fun screen(store: Store = MemoryStore()) =
        Screen(Game(store, seed = 1), seed = 2).apply { resize(1179, 2556, 177, 102) }

    /** The narrowest phone: 135 virtual pixels wide. */
    private fun smallest() = Screen(Game(MemoryStore(), seed = 1), seed = 2).apply { resize(1080, 2340, 144, 102) }

    private fun Screen.tap(x: Float, y: Float) = press(x * scale + offsetX, y * scale + offsetY)

    private fun Screen.tap(r: Rect) = tap(r.x + r.w / 2f, r.y + r.h / 2f)

    @Test
    fun theKnobIsGrabbedAroundItsCenter() {
        val s = screen()
        val l = s.layout!!
        assertEquals(Touch.KNOB, s.tap(l.knobX + l.knobR * 0.6f, l.knobY + 0.5f))
        assertTrue(s.game.knob.held)
    }

    @Test
    fun aFingerCirclingEitherWayOnlyClimbs() {
        val s = screen()
        val l = s.layout!!
        val r = l.knobR * 0.7f
        var a = 0.0
        s.tap(l.knobX + 0.5f + r, l.knobY + 0.5f)
        var last = s.game.position
        repeat(240) { frame ->
            a += if (frame < 120) -0.1 else 0.1
            val x = l.knobX + 0.5f + r * cos(a).toFloat()
            val y = l.knobY + 0.5f + r * sin(a).toFloat()
            s.move(x * s.scale + s.offsetX, y * s.scale + s.offsetY)
            s.update(1 / 60.0)
            assertTrue(s.game.position >= last, "walked down at frame $frame")
            last = s.game.position
        }
        s.release()
        assertTrue(s.game.position > 3 * PX_PER_TURN)
    }

    @Test
    fun theDisplayIgnoresTouches() {
        val s = screen()
        assertEquals(Touch.NONE, s.tap(s.layout!!.scene))
        assertFalse(s.game.knob.held)
    }

    @Test
    fun theCoinOpensAndClosesTheAgora() {
        val s = screen()
        val l = s.layout!!
        assertEquals(Touch.SHOP, s.tap(l.coinIcon))
        assertTrue(s.agoraOpen)
        assertEquals(Touch.SHOP, s.tap(l.coinIcon))
        assertFalse(s.agoraOpen)
    }

    @Test
    fun theObolsSitAcrossFromTheDay() {
        val l = screen().layout!!
        val c = l.coinIcon
        assertTrue(c.x > l.scene.x + l.scene.w / 2 && c.right <= l.scene.right, "in the right half of the display")
        assertTrue(c.y >= l.scene.y && c.y <= l.scene.y + 6, "at the top, level with the day")
    }

    @Test
    fun aRowYouCannotAffordDoesNothing() {
        val s = screen(MemoryStore(mapOf("obols" to 1.0)))
        val l = s.layout!!
        s.tap(l.coinIcon)
        assertEquals(Touch.NONE, s.tap(l.agoraRow(Line.ROCK.ordinal)))
        assertEquals(0, s.game.shop.tier(Line.ROCK))
        assertEquals(1.0, s.game.shop.obols, 1e-9)
    }

    @Test
    fun rowsYouCannotAffordAreGrayedOut() {
        fun grayIn(obols: Double): Int {
            val s = screen(MemoryStore(mapOf("obols" to obols)))
            val l = s.layout!!
            s.tap(l.coinIcon)
            val px = s.render()
            val r = l.agoraRow(Line.ROCK.ordinal)
            return (r.y until r.bottom).sumOf { y -> (r.x until r.right).count { x -> px[x, y] == DISABLED } }
        }
        assertTrue(grayIn(0.0) > 0)
        assertEquals(0, grayIn(100.0))
    }

    @Test
    fun backClosesTheAgora() {
        val s = screen()
        assertFalse(s.back())
        s.tap(s.layout!!.coinIcon)
        assertTrue(s.back())
        assertFalse(s.agoraOpen)
    }

    @Test
    fun theSpeakerLivesInTheAgora() {
        val store = MemoryStore()
        val s = screen(store)
        val l = s.layout!!
        s.tap(l.coinIcon)
        assertEquals(Touch.SOUND, s.tap(l.agoraSound))
        assertFalse(s.game.soundOn)
        assertFalse(Game(store, seed = 1).soundOn)
        assertEquals(Touch.SOUND, s.tap(l.agoraSound))
        assertTrue(s.game.soundOn)
    }

    @Test
    fun tappingARowBuysTheNextItem() {
        val s = screen(MemoryStore(mapOf("obols" to 2.0)))
        val l = s.layout!!
        s.tap(l.coinIcon)
        assertEquals(Touch.BUY, s.tap(l.agoraRow(Line.ALONE.ordinal)))
        assertEquals(1, s.game.shop.tier(Line.ALONE))
        assertEquals(1.0, s.game.shop.obols, 1e-9)
    }

    @Test
    fun oneTapBuysOneItem() {
        val s = screen(MemoryStore(mapOf("obols" to 100.0)))
        val l = s.layout!!
        s.tap(l.coinIcon)
        s.tap(l.agoraRow(Line.ROCK.ordinal))
        assertEquals(1, s.game.shop.tier(Line.ROCK))
    }

    @Test
    fun theKnobRestsWhileTheAgoraIsOpen() {
        val s = screen()
        val l = s.layout!!
        s.tap(l.coinIcon)
        assertNotEquals(Touch.KNOB, s.tap(l.knobX + l.knobR * 0.6f, l.knobY + 0.5f))
        assertFalse(s.game.knob.held)
    }

    @Test
    fun theCoinWinsOverTheKnobOnTheSmallestPhone() {
        val s = smallest()
        assertEquals(Touch.SHOP, s.tap(s.layout!!.coinIcon))
        assertFalse(s.game.knob.held)
    }

    @Test
    fun everyItemFitsItsRowOnTheSmallestPhone() {
        val l = smallest().layout!!
        val row = l.agoraRow(0)
        for (item in ITEMS) {
            for (ch in item.name + item.note) assertTrue(PixelFont.hasGlyph(ch), "missing '$ch' in ${item.name}")
            assertTrue(PixelFont.width(item.name) + 24 <= row.w, item.name)
            assertTrue(PixelFont.width(item.note) + 16 <= row.w, item.note)
        }
        assertTrue(row.h >= 3 * PixelFont.LINE_HEIGHT)
        assertTrue(l.agoraRow(4).bottom <= l.agoraSound.y)
    }
}
