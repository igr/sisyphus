package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.ISIS_AND_OSIRIS
import dev.oblac.sisyphus.game.ORPHEUS
import dev.oblac.sisyphus.game.SISYPHUS
import dev.oblac.sisyphus.game.TALES
import dev.oblac.sisyphus.game.TYPHON
import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TalesTest {
    @Test
    fun thePoolHasFourTalesOfFifteenParts() {
        assertEquals(listOf(0, 1, 2, 3), TALES.map { it.id })
        assertEquals(listOf(SISYPHUS, TYPHON, ORPHEUS, ISIS_AND_OSIRIS), TALES)
        assertTrue(TALES.all { it.parts.size == 15 })
        assertEquals(2, SISYPHUS.parts[13].texts.size)
        assertTrue(TALES.all { tale -> tale.parts.all { it.texts.size == 1 || it === SISYPHUS.parts[13] } })
        fun ends(tale: List<String>) = tale.first() to tale.last()
        assertEquals(
            "Long ago, Sisyphus built the city of Ephyra, and he became its king." to
                "For his tricks, the gods gave him a rock. He must push it to the top, forever.",
            ends(SISYPHUS.parts.map { it.texts.first() }),
        )
        assertEquals(
            "The gods won their war with the Giants. The Earth, mother of the Giants, was angry." to
                "He lies there still. When he turns, the earth shakes, and fire comes out of Etna.",
            ends(TYPHON.parts.map { it.texts.first() }),
        )
        assertEquals(
            "Orpheus was the son of a Muse. When he sang, trees and stones came near to hear." to
                "After his death, he found her below. Now they walk together, and he can look back.",
            ends(ORPHEUS.parts.map { it.texts.first() }),
        )
        assertEquals(
            "Osiris was the king of Egypt. He taught men to sow wheat, and he gave them laws." to
                "Osiris became the king of the dead. Before him, each heart is weighed against a feather.",
            ends(ISIS_AND_OSIRIS.parts.map { it.texts.first() }),
        )
        assertEquals("The dead wept. The wheel of Ixion stood still, and Sisyphus sat down on his rock.", ORPHEUS.parts[8].texts.single())
    }

    @Test
    fun everyTextHasOnlyLettersOfTheFont() {
        for (tale in TALES) for (part in tale.parts) for (text in part.texts) {
            for (ch in text) assertTrue(PixelFont.hasGlyph(ch), "missing '$ch' in \"$text\"")
        }
    }
}
