package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.AUTHORS
import dev.oblac.sisyphus.game.Config
import dev.oblac.sisyphus.game.God
import dev.oblac.sisyphus.game.NARROWEST_ANSWER
import dev.oblac.sisyphus.game.NARROWEST_TEXT
import dev.oblac.sisyphus.game.PEOPLE
import dev.oblac.sisyphus.game.PROVERBS
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.QUOTES
import dev.oblac.sisyphus.game.Quote
import dev.oblac.sisyphus.game.SAYINGS
import dev.oblac.sisyphus.game.TRIVIA
import dev.oblac.sisyphus.game.VOICES
import dev.oblac.sisyphus.game.Vision
import dev.oblac.sisyphus.game.Voices
import dev.oblac.sisyphus.game.WHO_SAID
import dev.oblac.sisyphus.game.answerLines
import dev.oblac.sisyphus.game.godsOf
import dev.oblac.sisyphus.game.kin
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
    private val all = SAYINGS.map { saying -> whoSaid(saying, kin(saying.author).first { it != saying.author }) } + TRIVIA
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

    /** On the smallest phone, a name too wide for its corner breaks with a hyphen, as La Roche-foucauld. */
    @Test
    fun everyAnswerFitsItsCorner() {
        for (t in all.flatMap { listOf(it.right, it.wrong) } + AUTHORS) {
            val lines = answerLines(t, NARROWEST_ANSWER)
            assertTrue(lines.size <= 2 && lines.all { PixelFont.width(it) <= NARROWEST_ANSWER }, "too wide: \"$t\" -> $lines")
        }
        assertEquals(listOf("La Roche-", "foucauld"), answerLines("La Rochefoucauld", NARROWEST_ANSWER))
        assertEquals(listOf("La Rochefoucauld"), answerLines("La Rochefoucauld", 100))
    }

    /** The gods ask about all the wise words: of people, of the voices of Sisyphus's world, and the proverbs. */
    @Test
    fun theGodsAskAboutAllTheWiseWords() {
        assertEquals(QUOTES, SAYINGS)
        assertTrue("Sisyphus" in VOICES && "the Rock" in VOICES && "Japanese proverb" in PROVERBS)
        assertEquals(AUTHORS.toSet(), (PEOPLE + VOICES.filter { it in AUTHORS } + PROVERBS.filter { it in AUTHORS }).toSet())
        assertTrue(VOICES.none { it in PEOPLE } && PROVERBS.none { it in PEOPLE })
        for (author in AUTHORS) assertTrue(kin(author).count { it != author } > 0, author)
    }

    /** The words show as the wise words do, with "Who said this?" under them; the other answer is of the same kind as the right one. */
    @Test
    fun whoSaidThisAsksAboutTheWordsAsTheyAre() {
        val heraclitus = QUOTES.first { it.author == "Heraclitus" }
        val q = whoSaid(heraclitus, "Pindar")
        assertEquals(heraclitus.text, q.text)
        assertEquals(WHO_SAID, q.prompt)
        assertEquals("Correct. These are the words of Heraclitus.", q.reply(right = true))
        assertEquals("Wrong. This is a Japanese proverb.", whoSaid(Quote("Fall seven times, stand up eight.", "Japanese proverb"), "Latin proverb").reply(right = false))
        assertEquals("Correct. This is a proverb.", whoSaid(Quote("Rome was not built in a day.", "Proverb"), "Zen saying").reply(right = true))
        assertTrue(kin("the Rock").all { it in VOICES } && kin("Zen saying").all { it in PROVERBS } && kin("Seneca").all { it in PEOPLE })
    }

    @Test
    fun eachWorldHasHermesAndTwoGodsOfItsOwn() {
        assertEquals(setOf(God.SPHINX, God.HERMES, God.HECATE), godsOf(Place.CORINTH).toSet())
        assertEquals(setOf(God.HEPHAESTUS, God.HERMES, God.EMPEDOCLES), godsOf(Place.ETNA).toSet())
        assertEquals(setOf(God.CHARON, God.HERMES, God.PERSEPHONE), godsOf(Place.UNDERWORLD).toSet())
        assertEquals(setOf(God.THOTH, God.HERMES, God.ANUBIS), godsOf(Place.CAIRO).toSet())
        assertEquals(setOf(God.SAKUYA, God.HERMES, God.IWANAGA), godsOf(Place.FUJI).toSet())
        for (place in Place.entries) assertEquals(3, godsOf(place).size)
    }

    @Test
    fun aGodOfTheWorldAsks() {
        val v = Voices(1, 0, null, 0, Random(2))
        for (place in Place.entries) for (k in 0 until 30) assertTrue(v.visionAt(k, place).god in godsOf(place))
    }

    /** Each round of two visions asks who said some wise words, and a question of general knowledge or pop culture. */
    @Test
    fun theTwoKindsOfQuestionsComeInTurn() {
        val v = Voices(1, 0, null, 0, Random(2))
        for (r in 0..40) {
            val questions = (0..1).map { v.visionAt(2 * r + it, Place.CORINTH).question }
            assertEquals(1, questions.count { it.prompt == WHO_SAID }, questions.joinToString { it.text })
            assertEquals(1, questions.count { it in TRIVIA }, questions.joinToString { it.text })
        }
    }

    /** A question of general knowledge or pop culture asks with a question mark, and its reply tells one fact, right or wrong. */
    @Test
    fun theOtherQuestionsAreOfGeneralKnowledgeAndPopCulture() {
        assertTrue(TRIVIA.size >= 400, "only ${TRIVIA.size}")
        assertEquals(TRIVIA.size, TRIVIA.map { it.text }.toSet().size)
        for (q in TRIVIA) {
            assertTrue(q.text.endsWith("?"), q.text)
            assertNotEquals(q.right, q.wrong, q.text)
            assertEquals(q.yes, q.no, q.text)
            assertNull(q.prompt, q.text)
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

    /** The world changes every 7 visions: still no question comes twice before all of its kind have come. */
    @Test
    fun noQuestionComesAgainBeforeAllOfItsKindHaveComeInAllWorlds() {
        val v = Voices(1, 0, null, 0, Random(2))
        val asked = (0 until 2 * maxOf(SAYINGS.size, TRIVIA.size)).map { k -> v.visionAt(k, Place.entries[k / 7 % Place.entries.size]).question }
        val said = asked.filter { it.prompt == WHO_SAID }.take(SAYINGS.size).map { it.text to it.right }
        assertEquals(SAYINGS.size, said.toSet().size)
        val trivia = asked.filter { it.prompt == null }.take(TRIVIA.size).map { it.text }
        assertEquals(TRIVIA.size, trivia.toSet().size)
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
    fun theTwoAnswersDiffer() {
        val v = Voices(1, 0, null, 0, Random(2))
        for (k in 0..90) {
            val q = v.visionAt(k, Place.CORINTH).question
            assertNotEquals(q.right, q.wrong)
            if (q.prompt == WHO_SAID) assertTrue(q.wrong in kin(q.right), q.text)
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

    /** The name of the god stands under its question, as the author under the wise words; under the wise words it asks about, "Who said this?". */
    @Test
    fun everyGodsNameFitsThePanel() {
        for (g in God.entries) assertTrue(PixelFont.width("— ${g.title}") <= NARROWEST_TEXT, g.title)
        assertTrue(PixelFont.width("— $WHO_SAID") <= NARROWEST_TEXT)
        val v = Vision(whoSaid(SAYINGS[0], "Seneca"), God.SPHINX, rightOnLeft = true)
        assertEquals(WHO_SAID, v.byline)
        assertEquals(God.SPHINX.title, Vision(TRIVIA[0], God.SPHINX, rightOnLeft = true).byline)
    }
}
