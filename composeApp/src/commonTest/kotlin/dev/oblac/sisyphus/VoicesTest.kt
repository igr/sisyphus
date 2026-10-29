package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.BIRTH_YEARS
import dev.oblac.sisyphus.game.Config
import dev.oblac.sisyphus.game.God
import dev.oblac.sisyphus.game.LIVED_FIRST
import dev.oblac.sisyphus.game.NARROWEST_ANSWER
import dev.oblac.sisyphus.game.NARROWEST_TEXT
import dev.oblac.sisyphus.game.PEOPLE
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.QUOTES
import dev.oblac.sisyphus.game.ROMANS
import dev.oblac.sisyphus.game.SAYINGS
import dev.oblac.sisyphus.game.Vision
import dev.oblac.sisyphus.game.Voices
import dev.oblac.sisyphus.game.godsOf
import dev.oblac.sisyphus.game.romanName
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
    private val all = SAYINGS.map { whoSaid(it, "Seneca") } + LIVED_FIRST + ROMANS.map { romanName(it, "Mars") }
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
    fun aGodOfTheWorldAsks() {
        val v = Voices(1, 0, null, 0, Random(2))
        for (place in Place.entries) for (k in 0 until 30) assertTrue(v.visionAt(k, place).god in godsOf(place))
    }

    /** Each round of three visions asks who said some wise words, who lived first, and what the Romans call a god. */
    @Test
    fun theThreeKindsOfQuestionsComeInTurn() {
        val v = Voices(1, 0, null, 0, Random(2))
        for (r in 0..20) {
            val texts = (0..2).map { v.visionAt(3 * r + it, Place.CORINTH).question.text }
            assertEquals(1, texts.count { it.startsWith("Who said this?") }, "$texts")
            assertEquals(1, texts.count { it == "Who lived first?" }, "$texts")
            assertEquals(1, texts.count { it.startsWith("What do the Romans call ") }, "$texts")
        }
    }

    /** Both lived in antiquity, or both in modern times, so that the era does not give the answer away. */
    @Test
    fun whoLivedFirstAsksAboutTwoPeopleOfOneEraACenturyApart() {
        assertTrue(LIVED_FIRST.size >= 500, "only ${LIVED_FIRST.size}")
        for (q in LIVED_FIRST) {
            assertTrue(q.right in PEOPLE && q.wrong in PEOPLE, q.yes)
            val (first, second) = BIRTH_YEARS.getValue(q.right) to BIRTH_YEARS.getValue(q.wrong)
            assertTrue(first + 100 <= second, q.yes)
            assertEquals(first < 1000, second < 1000, q.yes)
        }
        for (name in BIRTH_YEARS.keys) assertTrue(QUOTES.any { it.author == name }, name)
    }

    @Test
    fun theReplyTellsWhenBothWereBorn() {
        val ancient = LIVED_FIRST.first { it.right == "Plato" && it.wrong == "Plutarch" }
        assertEquals("Correct. Plato was born about 428 BC, Plutarch about 46 AD.", ancient.reply(right = true))
        val modern = LIVED_FIRST.first { it.right == "Goethe" && it.wrong == "Albert Camus" }
        assertEquals("Wrong. Goethe was born in 1749, Albert Camus in 1913.", modern.reply(right = false))
    }

    /** The wrong answer is a god if the right one is a god, and a goddess if it is a goddess, so that only knowing helps. */
    @Test
    fun theRomansNameAGodOfTheSameKind() {
        assertTrue(ROMANS.size >= 20, "only ${ROMANS.size}")
        val v = Voices(1, 0, null, 0, Random(2))
        val asked = (0 until 300).map { v.visionAt(it, Place.CORINTH).question }.filter { it.text.startsWith("What do the Romans call ") }
        assertEquals(100, asked.size)
        for (q in asked) {
            val god = ROMANS.first { it.roman == q.right }
            val other = ROMANS.first { it.roman == q.wrong }
            assertNotEquals(god, other)
            assertEquals(god.goddess, other.goddess, q.text)
            assertEquals("What do the Romans call ${god.greek}?", q.text)
            assertEquals("Correct. ${god.greek} is ${god.roman} in Rome.", q.reply(right = true))
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
        val asked = (0 until 3 * maxOf(SAYINGS.size, LIVED_FIRST.size)).map { k -> v.visionAt(k, Place.entries[k / 7 % Place.entries.size]).question }
        val said = asked.filter { it.text.startsWith("Who said this?") }.take(SAYINGS.size).map { it.text }
        assertEquals(SAYINGS.size, said.toSet().size)
        val first = asked.filter { it.text == "Who lived first?" }.map { it.right to it.wrong }
        assertEquals(LIVED_FIRST.size, first.toSet().size)
        val roman = asked.filter { it.text.startsWith("What do the Romans call ") }.take(ROMANS.size).map { it.text }
        assertEquals(ROMANS.size, roman.toSet().size)
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
            if (q.text.startsWith("Who said this?")) assertTrue(q.right in PEOPLE && q.wrong in PEOPLE)
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
