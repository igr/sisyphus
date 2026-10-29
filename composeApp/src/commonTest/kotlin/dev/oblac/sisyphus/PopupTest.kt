package dev.oblac.sisyphus

import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.drawPopup
import dev.oblac.sisyphus.gfx.rgb
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PopupTest {
    private val sky = rgb(0x6FA8DC)
    private val text = "Long ago, this city was Ephyra. Sisyphus built it, and he became its king."

    private fun popup(x: Int, y: Int, note: String? = null) = Pixels(121, 122).apply {
        clear(sky)
        drawPopup(this, text, x, y, note)
    }

    /** The tail ends on the point that the popup shows, and the bubble stands above it. */
    @Test
    fun aPopupPointsDownAtItsPoint() {
        val px = popup(60, 70)
        assertNotEquals(sky, px[60, 70])
        for (y in 71 until px.height) for (x in 0 until px.width) assertEquals(sky, px[x, y], "($x, $y)")
        assertTrue((0 until 60).count { y -> px[60, y] != sky } > 20)
    }

    /** A long text wraps into lines. Near an edge, the bubble moves in so that all of it shows, and the tail still points at the point. */
    @Test
    fun aPopupStaysInsideThePicture() {
        for (x in listOf(3, 117)) {
            val px = popup(x, 70)
            assertNotEquals(sky, px[x, 70])
            for (y in 0 until px.height) {
                assertEquals(sky, px[0, y])
                assertEquals(sky, px[px.width - 1, y])
            }
        }
    }

    @Test
    fun aPopupShowsItsNote() {
        assertFalse(popup(60, 70, note = "3/15").data.contentEquals(popup(60, 70).data))
    }
}
