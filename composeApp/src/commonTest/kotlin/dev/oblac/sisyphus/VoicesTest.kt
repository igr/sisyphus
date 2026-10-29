package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Config
import dev.oblac.sisyphus.game.God
import dev.oblac.sisyphus.game.NARROWEST_ANSWER
import dev.oblac.sisyphus.game.NARROWEST_TEXT
import dev.oblac.sisyphus.game.PEOPLE
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.SAYINGS
import dev.oblac.sisyphus.game.Vision
import dev.oblac.sisyphus.game.Voices
import dev.oblac.sisyphus.game.godsOf
import dev.oblac.sisyphus.game.whoSaid
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
    private val all = SAYINGS.map { whoSaid(it, "Seneca") }
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
    fun theGodsAskOnlyAboutPeople() {
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
        assertEquals(setOf(God.THOTH, God.HERMES, God.ANUBIS), godsOf(Place.CAIRO).toSet())
        for (place in Place.entries) assertEquals(3, godsOf(place).size)
    }

    @Test
    fun everyGodAsksWhoSaidTheWords() {
        val v = Voices(1, 0, null, 0, Random(2))
        for (place in Place.entries) for (k in 0 until 30) {
            val vision = v.visionAt(k, place)
            assertTrue(vision.god in godsOf(place))
            assertTrue(vision.question.text.startsWith("Who said this?"), vision.question.text)
        }
    }

    @Test
    fun theGodWhoAsksIsChosenAtRandom() {
        val v = Voices(1, 0, null, 0, Random(2))
        for (place in Place.entries) {
            val gods = (0 until 300).map { v.visionAt(it, place).god }
            for (god in godsOf(place)) assertTrue(gods.count { it == god } in 60..140, "$god in $place")
        }
    }

    /** Empedocles is a god of Mount Etna and a man of wise words too: no god asks about its own words, or names itself. */
    @Test
    fun noGodAsksAboutItsOwnWords() {
        val v = Voices(1, 0, null, 0, Random(2))
        for (place in Place.entries) for (k in SAYINGS.indices) {
            val vision = v.visionAt(k, place)
            assertTrue(vision.god.title != vision.question.right && vision.god.title != vision.question.wrong, vision.question.text)
        }
    }

    @Test
    fun aNewGameMeetsTheFirstVoiceSoon() {
        assertTrue(Voices(1, 0, null, 0, Random(2)).count in Config.FIRST_VISION)
    }

    /** A count saved with other numbers waits no longer than the next vision can. */
    @Test
    fun aSavedCountWaitsNoLongerThanTheNextVisionCan() {
        assertEquals(Config.NEXT_VISION.endInclusive, Voices(1, 0, 10_000.0, 0, Random(2)).count)
    }

    @Test
    fun theCountRunsDownWithTurns() {
        val v = Voices(1, 0, 10.0, 0, Random(2))
        assertNull(v.pushed(9.5, Place.CORINTH))
        assertNotNull(v.pushed(1.0, Place.CORINTH))
        assertEquals(1, v.drawn)
        assertTrue(v.count in Config.NEXT_VISION)
    }

    /** The world changes every 7 visions: still no saying comes twice before all have come. */
    @Test
    fun noSayingComesAgainBeforeAllHaveComeInAllWorlds() {
        val v = Voices(1, 0, null, 0, Random(2))
        val texts = SAYINGS.indices.map { k -> v.visionAt(k, Place.entries[k / 7 % Place.entries.size]).question.text }
        assertEquals(SAYINGS.size, texts.toSet().size)
    }

    @Test
    fun theSameDrawGivesTheSameVision() {
        val a = Voices(5, 0, null, 0, Random(1))
        val b = Voices(5, 0, null, 0, Random(2))
        for (k in 0..30) {
            assertEquals(a.visionAt(k, Place.CORINTH).question.text, b.visionAt(k, Place.CORINTH).question.text)
            assertEquals(a.visionAt(k, Place.CORINTH).rightOnLeft, b.visionAt(k, Place.CORINTH).rightOnLeft)
            assertEquals(a.visionAt(k, Place.CORINTH).god, b.visionAt(k, Place.CORINTH).god)
        }
    }

    @Test
    fun everyGodNamesAnotherPerson() {
        val v = Voices(1, 0, null, 0, Random(2))
        for (k in 0..60) {
            val q = v.visionAt(k, Place.CORINTH).question
            assertNotEquals(q.right, q.wrong)
            assertTrue(q.right in PEOPLE && q.wrong in PEOPLE)
        }
    }

    @Test
    fun aVisionComesInAsksAndLeaves() {
        val q = whoSaid(SAYINGS[0], PEOPLE.first { it != SAYINGS[0].author })
        val v = Vision(q, God.SPHINX, rightOnLeft = true)
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
        assertEquals(q.right, v.left)
        assertEquals(q.wrong, v.right)
    }

    /** The name of the god stands under its question, as the author under the wise words. */
    @Test
    fun everyGodsNameFitsThePanel() {
        for (g in God.entries) assertTrue(PixelFont.width("— ${g.title}") <= NARROWEST_TEXT, g.title)
    }
}
