package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.God
import dev.oblac.sisyphus.game.NARROWEST_ANSWER
import dev.oblac.sisyphus.game.NARROWEST_TEXT
import dev.oblac.sisyphus.game.PEOPLE
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.RIDDLES
import dev.oblac.sisyphus.game.SAYINGS
import dev.oblac.sisyphus.game.Vision
import dev.oblac.sisyphus.game.Voices
import dev.oblac.sisyphus.game.godsOf
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
    private val all = RIDDLES + SAYINGS.map { hermes(it, "Seneca") }
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
    fun eachWorldHasHermesAndTwoGodsOfItsOwn() {
        assertEquals(setOf(God.SPHINX, God.HERMES, God.HECATE), godsOf(Place.CORINTH).toSet())
        assertEquals(setOf(God.HEPHAESTUS, God.HERMES, God.EMPEDOCLES), godsOf(Place.ETNA).toSet())
        assertEquals(setOf(God.CHARON, God.HERMES, God.PERSEPHONE), godsOf(Place.UNDERWORLD).toSet())
        for (place in Place.entries) assertEquals(3, godsOf(place).size)
    }

    @Test
    fun hermesAsksTheQuotesAndTheOthersTheRiddles() {
        val v = Voices(1, 0, null, 0, Random(2))
        for (place in Place.entries) for (k in 0 until 30) {
            val vision = v.visionAt(k, place)
            assertTrue(vision.god in godsOf(place))
            if (vision.god == God.HERMES) assertTrue(vision.question.text.startsWith("Who said this?"))
            else assertTrue(vision.question in RIDDLES, vision.question.text)
        }
    }

    @Test
    fun noQuestionComesTwice() {
        val texts = RIDDLES.map { it.text }
        assertEquals(texts.size, texts.toSet().size)
        assertTrue(RIDDLES.all { it.right != it.wrong })
    }

    @Test
    fun aNewGameMeetsTheFirstVoiceSoon() {
        assertTrue(Voices(1, 0, null, 0, Random(2)).count in 300.0..600.0)
    }

    @Test
    fun theCountRunsDownWithTurns() {
        val v = Voices(1, 0, 10.0, 0, Random(2))
        assertNull(v.pushed(9.5, Place.CORINTH))
        assertNotNull(v.pushed(1.0, Place.CORINTH))
        assertEquals(1, v.drawn)
        assertTrue(v.count in 600.0..1800.0)
    }

    @Test
    fun theGodsComeInTurn() {
        val v = Voices(1, 0, null, 0, Random(2))
        for (place in Place.entries) for (r in 0..9) assertEquals(godsOf(place).toSet(), (0..2).map { v.visionAt(3 * r + it, place).god }.toSet())
    }

    /** The world changes every 7 visions, also in the middle of a round: still no riddle comes twice before all have come. */
    @Test
    fun noRiddleComesAgainBeforeAllHaveComeInAllWorlds() {
        val v = Voices(1, 0, null, 0, Random(2))
        val riddles = generateSequence(0) { it + 1 }.map { k -> v.visionAt(k, Place.entries[k / 7 % Place.entries.size]) }
            .filter { it.god != God.HERMES }.map { it.question }.take(RIDDLES.size).toList()
        assertEquals(RIDDLES.size, riddles.toSet().size)
    }

    @Test
    fun theSameDrawGivesTheSameVision() {
        val a = Voices(5, 0, null, 0, Random(1))
        val b = Voices(5, 0, null, 0, Random(2))
        for (k in 0..30) {
            assertEquals(a.visionAt(k, Place.CORINTH).question.text, b.visionAt(k, Place.CORINTH).question.text)
            assertEquals(a.visionAt(k, Place.CORINTH).rightOnLeft, b.visionAt(k, Place.CORINTH).rightOnLeft)
        }
    }

    @Test
    fun hermesNamesAnotherPerson() {
        val v = Voices(1, 0, null, 0, Random(2))
        val asked = (0..60).map { v.visionAt(it, Place.CORINTH) }.filter { it.god == God.HERMES }.map { it.question }
        assertTrue(asked.size >= 20)
        for (q in asked) {
            assertNotEquals(q.right, q.wrong)
            assertTrue(q.wrong in PEOPLE)
        }
    }

    @Test
    fun aVisionComesInAsksAndLeaves() {
        val v = Vision(RIDDLES[0], God.SPHINX, rightOnLeft = true)
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

    @Test
    fun sixtyRiddles() {
        assertEquals(60, RIDDLES.size)
        assertEquals(60, RIDDLES.map { it.text }.toSet().size)
    }

    /** Any god of any world can ask any riddle, so no riddle names one of them. */
    @Test
    fun noRiddleNamesAGod() {
        val names = God.entries.filter { it.place != null }.map { it.title.removePrefix("the ") }
        for (q in RIDDLES) for (t in listOf(q.text, q.right, q.wrong, q.yes, q.no)) {
            assertTrue(names.none { it in t }, "names a god: \"$t\"")
        }
        assertTrue(RIDDLES.none { "I have two" in it.yes || "I have two" in it.no })
    }

    /** The name of the god stands under its question, as the author under the wise words. */
    @Test
    fun everyGodsNameFitsThePanel() {
        for (g in God.entries) assertTrue(PixelFont.width("— ${g.title}") <= NARROWEST_TEXT, g.title)
    }
}
