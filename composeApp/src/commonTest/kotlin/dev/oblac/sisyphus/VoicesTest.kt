package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.God
import dev.oblac.sisyphus.game.NARROWEST_ANSWER
import dev.oblac.sisyphus.game.NARROWEST_TEXT
import dev.oblac.sisyphus.game.PEOPLE
import dev.oblac.sisyphus.game.RIDDLES
import dev.oblac.sisyphus.game.ROADS
import dev.oblac.sisyphus.game.SAYINGS
import dev.oblac.sisyphus.game.Vision
import dev.oblac.sisyphus.game.Voices
import dev.oblac.sisyphus.game.hermes
import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VoicesTest {
    private val all = RIDDLES + ROADS + SAYINGS.map { hermes(it, "Seneca") }
    private val farewells = God.entries.map { it.farewell }

    private fun fits(text: String, width: Int, lines: Int): Boolean {
        val wrapped = PixelFont.wrap(text, width)
        return wrapped.size <= lines && wrapped.all { PixelFont.width(it) <= width }
    }

    @Test
    fun everyVoiceHasGlyphs() {
        val texts = all.flatMap { listOf(it.text, it.right, it.wrong, it.yes, it.no) } + farewells + God.entries.map { it.title }
        for (t in texts) for (ch in t) assertTrue(PixelFont.hasGlyph(ch), "missing '$ch' in \"$t\"")
    }

    @Test
    fun everyQuestionAndReplyFitsThePanel() {
        for (t in all.flatMap { listOf(it.text, it.reply(right = true), it.reply(right = false)) } + farewells) {
            assertTrue(fits(t, NARROWEST_TEXT, 3), "too long: \"$t\" -> ${PixelFont.wrap(t, NARROWEST_TEXT)}")
        }
    }

    @Test
    fun aReplySaysFirstWhetherTheAnswerWasRight() {
        for (q in all) {
            assertTrue(q.reply(right = true).startsWith("Correct. "))
            assertTrue(q.reply(right = false).startsWith("Wrong. "))
        }
        val again = Regex("^(Yes|No)\\b")
        for (t in all.flatMap { listOf(it.yes, it.no) }) assertFalse(again.containsMatchIn(t), "says it twice: \"$t\"")
    }

    @Test
    fun everyAnswerFitsItsCorner() {
        for (t in all.flatMap { listOf(it.right, it.wrong) } + PEOPLE) {
            assertTrue(fits(t, NARROWEST_ANSWER, 2), "too wide: \"$t\" -> ${PixelFont.wrap(t, NARROWEST_ANSWER)}")
        }
    }

    @Test
    fun hermesAsksOnlyAboutPeople() {
        assertTrue(SAYINGS.none { it.author == "Sisyphus" || it.author == "Proverb" || it.author == "the Rock" })
        assertTrue(SAYINGS.all { it.author in PEOPLE })
        assertTrue(SAYINGS.size >= 400, "only ${SAYINGS.size} sayings")
        assertEquals(PEOPLE.size, PEOPLE.toSet().size)
    }

    @Test
    fun riddlesBelongToTheSphinxAndRoadsToHecate() {
        assertTrue(RIDDLES.all { it.god == God.SPHINX })
        assertTrue(ROADS.all { it.god == God.HECATE })
    }

    @Test
    fun noQuestionComesTwice() {
        val texts = (RIDDLES + ROADS).map { it.text }
        assertEquals(texts.size, texts.toSet().size)
        assertTrue((RIDDLES + ROADS).all { it.right != it.wrong })
    }

    @Test
    fun aNewGameMeetsTheFirstVoiceSoon() {
        assertTrue(Voices(1, 0, null, 0, Random(2)).count in 300.0..600.0)
    }

    @Test
    fun theCountRunsDownWithTurns() {
        val v = Voices(1, 0, 10.0, 0, Random(2))
        assertNull(v.pushed(9.5))
        assertNotNull(v.pushed(1.0))
        assertEquals(1, v.drawn)
        assertTrue(v.count in 600.0..1800.0)
    }

    @Test
    fun theGodsComeInTurn() {
        val v = Voices(1, 0, null, 0, Random(2))
        for (r in 0..9) assertEquals(God.entries.toSet(), (0..2).map { v.visionAt(3 * r + it).question.god }.toSet())
    }

    @Test
    fun aRiddleComesAgainOnlyAfterAllRiddles() {
        val v = Voices(1, 0, null, 0, Random(2))
        val riddles = generateSequence(0) { it + 1 }.map { v.visionAt(it).question }.filter { it.god == God.SPHINX }.take(RIDDLES.size).toList()
        assertEquals(RIDDLES.size, riddles.map { it.text }.toSet().size)
    }

    @Test
    fun theSameDrawGivesTheSameVision() {
        val a = Voices(5, 0, null, 0, Random(1))
        val b = Voices(5, 0, null, 0, Random(2))
        for (k in 0..30) {
            assertEquals(a.visionAt(k).question.text, b.visionAt(k).question.text)
            assertEquals(a.visionAt(k).rightOnLeft, b.visionAt(k).rightOnLeft)
        }
    }

    @Test
    fun hermesNamesAnotherPerson() {
        val v = Voices(1, 0, null, 0, Random(2))
        val asked = (0..60).map { v.visionAt(it).question }.filter { it.god == God.HERMES }
        assertTrue(asked.size >= 20)
        for (q in asked) {
            assertNotEquals(q.right, q.wrong)
            assertTrue(q.wrong in PEOPLE)
        }
    }

    @Test
    fun aVisionComesInAsksAndLeaves() {
        val v = Vision(RIDDLES[0], rightOnLeft = true)
        v.update(0.5)
        assertEquals(0.5, v.light, 1e-9)
        v.update(1.0)
        assertEquals(1.0, v.light, 1e-9)
        v.ask()
        v.update(3.0)
        assertEquals(3.0, v.asked, 1e-9)
        v.leave()
        v.update(1.5)
        assertTrue(v.over)
        assertEquals(RIDDLES[0].right, v.left)
        assertEquals(RIDDLES[0].wrong, v.right)
    }
}
