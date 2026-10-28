package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Category
import dev.oblac.sisyphus.game.Counts
import dev.oblac.sisyphus.game.LABORS
import dev.oblac.sisyphus.game.NARROWEST_TEXT
import dev.oblac.sisyphus.game.done
import dev.oblac.sisyphus.game.laborNews
import dev.oblac.sisyphus.game.progress
import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LaborsTest {
    private fun of(category: Category) = LABORS.filter { it.category == category }

    @Test
    fun twentyLaborsInFourCategories() {
        assertEquals(20, LABORS.size)
        assertEquals(listOf<Long>(1, 10, 25, 50, 100), of(Category.HILLS).map { it.goal })
        assertEquals(listOf<Long>(100, 1_000, 10_000, 100_000, 1_000_000), of(Category.TURNS).map { it.goal })
        assertEquals(listOf<Long>(10, 100, 1_000, 10_000, 100_000), of(Category.DAYS).map { it.goal })
        assertEquals(listOf<Long>(1, 10, 50, 100, 500), of(Category.ANSWERS).map { it.goal })
        assertEquals(
            listOf(
                "Top of the Morning", "Rolling Stone", "King of the Hill", "Over the Hill", "Imagine Him Happy",
                "Turn On", "Spin Doctor", "Tina Turner", "Revolutionary", "Perpetual Motion",
                "Carpe Diem", "The Hundred Days", "Days of Our Lives", "Groundhog Day", "Eternal Sunshine",
                "Lucky Guess", "Teacher's Pet", "The Oracle", "Know-It-All", "Know Thyself",
            ),
            LABORS.map { it.name },
        )
    }

    @Test
    fun thePlainLinesSayWhatToDo() {
        assertEquals("Reach the top once.", of(Category.HILLS)[0].line)
        assertEquals("Reach the top 10 times.", of(Category.HILLS)[1].line)
        assertEquals("Turn the knob 1,000,000 times.", of(Category.TURNS)[4].line)
        assertEquals("Push through 10 days.", of(Category.DAYS)[0].line)
        assertEquals("Answer a vision right.", of(Category.ANSWERS)[0].line)
        assertEquals("Answer 500 visions right.", of(Category.ANSWERS)[4].line)
    }

    @Test
    fun progressIsShort() {
        assertEquals("7/10", progress(7.0, 10))
        assertEquals("850/1k", progress(850.0, 1_000))
        assertEquals("12k/100k", progress(12_345.0, 100_000))
        assertEquals("123k/1M", progress(123_456.0, 1_000_000))
        assertEquals("0/100", progress(0.6, 100))
    }

    @Test
    fun aLaborIsDoneAtItsGoal() {
        val spin = LABORS.first { it.name == "Spin Doctor" }
        assertFalse(spin.done(Counts(hills = 0, turns = 999.9, days = 0, answers = 0)))
        assertTrue(spin.done(Counts(hills = 0, turns = 1000.0, days = 0, answers = 0)))
    }

    @Test
    fun theNewsNamesTheLabor() {
        val news = laborNews(LABORS.first { it.name == "Tina Turner" })
        assertEquals("A labor is done: Tina Turner.", news.text)
        assertEquals("Hermes", news.author)
    }

    @Test
    fun everyLaborTextHasGlyphs() {
        val texts = LABORS.flatMap { listOf(it.name, it.line, laborNews(it).text) } + Category.entries.map { it.title }
        for (t in texts) for (ch in t) assertTrue(PixelFont.hasGlyph(ch), "missing '$ch' in \"$t\"")
        for (l in LABORS) {
            val wrapped = PixelFont.wrap(laborNews(l).text, NARROWEST_TEXT)
            assertTrue(wrapped.size <= 3 && wrapped.all { PixelFont.width(it) <= NARROWEST_TEXT }, "too long: ${laborNews(l).text}")
        }
    }
}
