package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.taleOf
import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TalesTest {
    @Test
    fun corinthTellsTheTaleOfSisyphusInFifteenParts() {
        val tale = taleOf(Place.CORINTH)!!
        assertEquals(15, tale.size)
        assertEquals("Peirene's spring", tale[8].place)
        assertEquals(2, tale[13].texts.size)
        assertTrue(tale.filterIndexed { i, _ -> i != 13 }.all { it.texts.size == 1 })
        assertEquals("Long ago, this city was Ephyra. Sisyphus built it, and he became its king.", tale.first().texts.single())
        assertEquals("For his tricks, the gods gave him a rock. He must push it to the top, forever.", tale.last().texts.single())
    }

    @Test
    fun otherWorldsTellNoTaleYet() {
        for (place in listOf(Place.ETNA, Place.UNDERWORLD, Place.CAIRO)) assertNull(taleOf(place))
    }

    @Test
    fun everyTextHasOnlyLettersOfTheFont() {
        for (part in taleOf(Place.CORINTH)!!) for (text in part.texts) {
            for (ch in text) assertTrue(PixelFont.hasGlyph(ch), "missing '$ch' in \"$text\"")
        }
    }
}
