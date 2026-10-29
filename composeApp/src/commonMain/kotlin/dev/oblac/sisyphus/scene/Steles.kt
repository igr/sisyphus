package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.gfx.sprite

/** A slab with a rounded top, a panel for its relief at (1, 1), and carved letters below. */
private val SLAB =
    """
    ..wwwwW..
    .wpppppW.
    wpppppppW
    wpppppppW
    wpppppppW
    wpppppppW
    wpppppppW
    wpppppppW
    wwwwwwwWW
    wCwCCwCwW
    wwwwwwwWW
    wCCwCwCCW
    wwwwwwwWW
    wwwwwwwWW
    WWWWWWWWD
    """

/**
 * A stone of the tales in the colors of one world: `w W D p C` for the slab, and `c k` for the relief carved in it.
 * Its [reliefs] are by the id of the tale, then in the order of its parts.
 */
internal class Stone(colors: Map<Char, Int>) {
    val slab: Pixels = sprite(colors, SLAB)
    val reliefs: List<List<Pixels>> = RELIEF_ART.map { tale -> tale.map { sprite(colors, it) } }
}

/** Pale limestone, in Corinth. */
internal val LIMESTONE = Stone(
    mapOf(
        'w' to rgb(0xECE6D8), 'W' to rgb(0xB8B0A0), 'D' to rgb(0x5A4636), 'p' to rgb(0xD8D0BE), 'C' to rgb(0x9C9482),
        'c' to rgb(0xA8A08E), 'k' to rgb(0x7E7666),
    ),
)

/** Dark lava rock, on Mount Etna. */
internal val LAVA_STONE = Stone(
    mapOf(
        'w' to rgb(0x7E746C), 'W' to rgb(0x5A524C), 'D' to rgb(0x241E1C), 'p' to rgb(0x6C635D), 'C' to rgb(0x4A423D),
        'c' to rgb(0x4E4642), 'k' to rgb(0x302A27),
    ),
)

/** Black stone, in The Underworld. The relief is cut lighter, so that it shows in the dark. */
internal val BLACK_STONE = Stone(
    mapOf(
        'w' to rgb(0x5E5E6E), 'W' to rgb(0x3E3E4C), 'D' to rgb(0x121218), 'p' to rgb(0x383846), 'C' to rgb(0x7A7A8E),
        'c' to rgb(0x66667A), 'k' to rgb(0x9898AE),
    ),
)

/** Sandstone, in Cairo. */
internal val SANDSTONE = Stone(
    mapOf(
        'w' to rgb(0xF0DAAA), 'W' to rgb(0xCDAE78), 'D' to rgb(0x6A4E2A), 'p' to rgb(0xE2C690), 'C' to rgb(0xB0905C),
        'c' to rgb(0xBA985E), 'k' to rgb(0x8C6A3A),
    ),
)
