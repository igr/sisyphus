package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.JOKERS
import dev.oblac.sisyphus.game.chance
import dev.oblac.sisyphus.game.pickJoker
import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JokersTest {
    @Test
    fun theJokersAreTenInTheOrderOfTheirIds() {
        assertEquals((0..9).toList(), JOKERS.map { it.id })
        assertEquals(
            listOf(
                "Diogenes", "Humpty Dumpty", "Red Riding Hood", "Don Quixote", "Pinocchio",
                "Cleopatra", "Sherlock Holmes", "Hamlet", "Count Dracula", "Cthulhu",
            ),
            JOKERS.map { it.name },
        )
        assertEquals(listOf("5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"), JOKERS.map { it.rank })
    }

    /** Each weight is out of 500: Diogenes comes 1 time in 2, Cthulhu 1 time in 500. */
    @Test
    fun theWeightsAddUpToFiveHundred() {
        assertEquals(listOf(250, 124, 62, 31, 15, 8, 4, 3, 2, 1), JOKERS.map { it.weight })
        assertEquals(500, JOKERS.sumOf { it.weight })
        assertEquals(listOf(2, 4, 8, 16, 33, 63, 125, 167, 250, 500), JOKERS.map { it.chance })
    }

    @Test
    fun everyJokerHasLinesOfOneOrTwoTexts() {
        assertEquals(listOf(10, 8, 6, 5, 4, 3, 3, 3, 2, 2), JOKERS.map { it.jokes.size })
        assertTrue(JOKERS.all { joker -> joker.jokes.all { it.texts.size in 1..2 } })
        fun ends(i: Int) = JOKERS[i].jokes.let { it.first().texts.first() to it.last().texts.last() }
        assertEquals("I carry a lamp at noon. I am looking for an honest man." to "Do not worry. At you, I wag.", ends(0))
        assertEquals(
            "Ph'nglui mglw'nafh Cthulhu R'lyeh wgah'nagl fhtagn." to "You push, it rolls down, you push again. We should talk.",
            ends(9),
        )
        assertEquals("The Cynic of Corinth. He lived in a jar, and with a lamp by day he looked for an honest man.", JOKERS[0].about)
    }

    @Test
    fun everyTextHasOnlyLettersOfTheFont() {
        for (joker in JOKERS) for (text in joker.jokes.flatMap { it.texts.toList() } + joker.name + joker.about + joker.rank) {
            for (ch in text) assertTrue(PixelFont.hasGlyph(ch), "missing '$ch' in \"$text\"")
        }
    }

    @Test
    fun eachJokerComesAsOftenAsHisWeightSays() {
        val random = Random(1)
        val counts = IntArray(JOKERS.size)
        repeat(500_000) { counts[pickJoker(random).id]++ }
        for (joker in JOKERS) {
            val expected = joker.weight * 1000
            assertTrue(abs(counts[joker.id] - expected) <= expected / 10, "${joker.name} came ${counts[joker.id]} times, not about $expected")
        }
    }
}
