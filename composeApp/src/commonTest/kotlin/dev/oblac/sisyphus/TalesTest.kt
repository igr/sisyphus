package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.ALCESTIS
import dev.oblac.sisyphus.game.ARION
import dev.oblac.sisyphus.game.BELLEROPHON
import dev.oblac.sisyphus.game.DAEDALUS_AND_ICARUS
import dev.oblac.sisyphus.game.DEMETER_AND_PERSEPHONE
import dev.oblac.sisyphus.game.DEUCALION_AND_PYRRHA
import dev.oblac.sisyphus.game.EMPEDOCLES
import dev.oblac.sisyphus.game.HERMES
import dev.oblac.sisyphus.game.IO
import dev.oblac.sisyphus.game.ISIS_AND_OSIRIS
import dev.oblac.sisyphus.game.IZANAGI_AND_IZANAMI
import dev.oblac.sisyphus.game.KAGUYA
import dev.oblac.sisyphus.game.ORPHEUS
import dev.oblac.sisyphus.game.PHAETHON
import dev.oblac.sisyphus.game.SISYPHUS
import dev.oblac.sisyphus.game.TALES
import dev.oblac.sisyphus.game.TYPHON
import dev.oblac.sisyphus.gfx.PixelFont
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TalesTest {
    @Test
    fun thePoolHasTalesOfFifteenParts() {
        assertEquals((0..15).toList(), TALES.map { it.id })
        assertEquals(
            listOf(
                SISYPHUS, TYPHON, ORPHEUS, ISIS_AND_OSIRIS, BELLEROPHON, EMPEDOCLES, DEMETER_AND_PERSEPHONE, HERMES, PHAETHON, ARION,
                DAEDALUS_AND_ICARUS, IO, KAGUYA, IZANAGI_AND_IZANAMI, DEUCALION_AND_PYRRHA, ALCESTIS,
            ),
            TALES,
        )
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
        assertEquals(
            "Bellerophon was the grandson of Sisyphus. The gods gave him beauty and courage." to
                "Pegasus found a home on Olympus. Now he carries the thunder and lightning of Zeus.",
            ends(BELLEROPHON.parts.map { it.texts.first() }),
        )
        assertEquals(
            "Empedocles was born in Acragas, in Sicily. His grandfather won a horse race at Olympia." to
                "Others say he leapt into the crater of Etna. It threw back one of his bronze sandals.",
            ends(EMPEDOCLES.parts.map { it.texts.first() }),
        )
        assertEquals(
            "Demeter was the first to plow, and to give men grain. Her daughter was Persephone." to
                "So for a third of each year, she is queen below. Each spring, she comes back up.",
            ends(DEMETER_AND_PERSEPHONE.parts.map { it.texts.first() }),
        )
        assertEquals(
            "Hermes, the son of Zeus and Maia, was born at dawn, in a cave on Mount Cyllene." to
                "Later, Hermes gave his skill to his son Autolycus, who stole the cattle of Sisyphus.",
            ends(HERMES.parts.map { it.texts.first() }),
        )
        assertEquals(
            "Phaethon was proud that the Sun was his father. Epaphus, son of Zeus, laughed at him." to
                "\"My work never ends,\" said the Sun. But the gods begged him, so he drove again.",
            ends(PHAETHON.parts.map { it.texts.first() }),
        )
        assertEquals(
            "Arion was the finest lyre player of his day. He lived with Periander, ruler of Corinth." to
                "For its kindness, Zeus set the dolphin among the stars. It shines there still.",
            ends(ARION.parts.map { it.texts.first() }),
        )
        assertEquals(
            "Daedalus of Athens was the great inventor. Men said his statues could see and walk." to
                "So Minos knew Daedalus was there. But Minos died in his bath, and Daedalus was safe.",
            ends(DAEDALUS_AND_ICARUS.parts.map { it.texts.first() }),
        )
        assertEquals(
            "Io was the daughter of the river god Inachus. Zeus saw her, and he loved her." to
                "In Egypt, men called her Isis: a woman with the horns of a cow.",
            ends(IO.parts.map { it.texts.first() }),
        )
        assertEquals(
            "Long ago, an old bamboo cutter saw a stalk that shone. Inside it was a tiny girl." to
                "The mountain was named Fuji, and its smoke still rises into the clouds, they say.",
            ends(KAGUYA.parts.map { it.texts.first() }),
        )
        assertEquals(
            "Long ago, the gods gave Izanagi and Izanami a jewelled spear, to make the land." to
                "He washed in a river, and the Sun goddess, the Moon god and Susanoo were born.",
            ends(IZANAGI_AND_IZANAMI.parts.map { it.texts.first() }),
        )
        assertEquals(
            "Deucalion was the son of Prometheus, and the great-grandfather of Sisyphus." to
                "His stones became men, and hers women. So people are a hard race, used to toil.",
            ends(DEUCALION_AND_PYRRHA.parts.map { it.texts.first() }),
        )
        assertEquals(
            "Zeus punished Apollo. For a year, Apollo had to serve a mortal: Admetus, king of Pherae." to
                "Alcestis could not speak until the third day. \"Now I am a happy man,\" said Admetus.",
            ends(ALCESTIS.parts.map { it.texts.first() }),
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
