package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.NARROWEST_TEXT
import dev.oblac.sisyphus.game.PX_PER_TURN
import dev.oblac.sisyphus.game.QUOTES
import dev.oblac.sisyphus.game.Quote
import dev.oblac.sisyphus.game.QuoteBox
import dev.oblac.sisyphus.game.TUTORIAL
import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class QuotesTest {
    private val all = QUOTES + TUTORIAL

    @Test
    fun everyCharacterHasAGlyph() {
        for (q in all) for (ch in q.text + q.author) assertTrue(PixelFont.hasGlyph(ch), "missing '$ch' in \"${q.text}\"")
    }

    @Test
    fun everyQuoteFitsTheNarrowestPanel() {
        for (q in all) {
            val lines = PixelFont.wrap(q.text, NARROWEST_TEXT)
            assertTrue(lines.size <= 3, "too long: \"${q.text}\" -> $lines")
            assertTrue(lines.all { PixelFont.width(it) <= NARROWEST_TEXT }, "word too wide in \"${q.text}\"")
            assertTrue(PixelFont.width("— ${q.author}") <= NARROWEST_TEXT, "author too wide: ${q.author}")
        }
    }

    @Test
    fun everyQuoteIsThereOnce() {
        val twice = QUOTES.groupBy { it.text }.filterValues { it.size > 1 }.keys
        assertTrue(twice.isEmpty(), "twice: $twice")
    }

    @Test
    fun wrapKeepsAllWords() {
        val text = "The way up and the way down are one and the same."
        assertEquals(text, PixelFont.wrap(text, 60).joinToString(" "))
    }

    @Test
    fun theTutorialLeavesAfterTheFirstTurn() {
        val box = QuoteBox(seed = 1, drawn = 0, firstTime = true)
        assertSame(TUTORIAL, box.quote)
        repeat(300) { box.update(1 / 60.0, 0.0, 0.0) }
        assertEquals(QuoteBox.Phase.SHOWING, box.phase)
        repeat(120) { box.update(1 / 60.0, PX_PER_TURN / 60, 0.0) }
        assertNotSame(TUTORIAL, box.quote)
    }

    @Test
    fun aReturningPlayerGetsWisdomRightAway() {
        assertNotSame(TUTORIAL, QuoteBox(seed = 1, drawn = 0, firstTime = false).quote)
    }

    @Test
    fun aRoundShowsEveryQuoteOnce() {
        val round = (1..QUOTES.size).map { QuoteBox(seed = 7, drawn = it, firstTime = false).quote }
        assertEquals(QUOTES.toSet(), round.toSet())
    }

    @Test
    fun anAnnouncementComesFirstAndTheRoundGoesOn() {
        val box = QuoteBox(seed = 3, drawn = 5, firstTime = false)
        box.announce(Quote("Amalthea pushed 6 times while you were away.", "Hermes"))
        assertEquals("Amalthea pushed 6 times while you were away.", box.quote.text)
        assertEquals(QuoteBox.Phase.TYPING, box.phase)
        assertEquals(0.0, box.typed)
        assertEquals(5, box.drawn)
    }

    @Test
    fun aRestartShowsTheLastQuoteAgainFullyTyped() {
        val box = QuoteBox(seed = 3, drawn = 0, firstTime = false)
        val again = QuoteBox(seed = 3, drawn = box.drawn, firstTime = false)
        assertSame(box.quote, again.quote)
        assertEquals(QuoteBox.Phase.SHOWING, again.phase)
        assertEquals(again.length.toDouble(), again.typed)
    }
}
