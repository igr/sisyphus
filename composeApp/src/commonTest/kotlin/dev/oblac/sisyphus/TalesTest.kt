package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.NARROWEST_TEXT
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.taleOf
import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TalesTest {
    private fun fits(text: String, width: Int, lines: Int): Boolean {
        val wrapped = PixelFont.wrap(text, width)
        return wrapped.size <= lines && wrapped.all { PixelFont.width(it) <= width }
    }

    @Test
    fun corinthTellsTheTaleOfSisyphusInFifteenParts() {
        val tale = taleOf(Place.CORINTH)!!
        assertEquals(15, tale.size)
        assertEquals("Peirene's spring", tale[8].place)
        assertEquals(2, tale[13].quotes.size)
        assertTrue(tale.filterIndexed { i, _ -> i != 13 }.all { it.quotes.size == 1 })
        assertEquals("Long ago, this city was Ephyra. Sisyphus built it, and he became its king.", tale.first().quotes.single().text)
        assertEquals("For his tricks, the gods gave him a rock. He must push it to the top, forever.", tale.last().quotes.single().text)
        assertTrue(tale.all { part -> part.quotes.all { it.author == part.place } })
    }

    @Test
    fun otherWorldsTellNoTaleYet() {
        for (place in listOf(Place.ETNA, Place.UNDERWORLD, Place.CAIRO)) assertNull(taleOf(place))
    }

    @Test
    fun everyPartFitsTheSmallestPhone() {
        for (part in taleOf(Place.CORINTH)!!) {
            assertTrue(PixelFont.width("— ${part.place}") <= NARROWEST_TEXT, part.place)
            for (quote in part.quotes) {
                assertTrue(fits(quote.text, NARROWEST_TEXT, 3), "too long: \"${quote.text}\" -> ${PixelFont.wrap(quote.text, NARROWEST_TEXT)}")
                for (ch in quote.text + part.place) assertTrue(PixelFont.hasGlyph(ch), "missing '$ch' in \"${quote.text}\"")
            }
        }
    }
}
