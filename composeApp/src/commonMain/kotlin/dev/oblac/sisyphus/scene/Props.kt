package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.gfx.sprite

private val PROP_COLORS = mapOf(
    'l' to rgb(0xA8B878),
    'g' to rgb(0x6E8250),
    'G' to rgb(0x4E6238),
    'c' to rgb(0x4E7038),
    'C' to rgb(0x34502C),
    't' to rgb(0x5A4030),
    'T' to rgb(0x7A5A40),
    'w' to rgb(0xECE6D8),
    'W' to rgb(0xB8B0A0),
    'o' to rgb(0x2A221E),
)

/** Things by the path, placed along the hill. [base] is the row that touches the ground. */
class Prop(val sprite: Pixels, val base: Int)

val OLIVE = Prop(
    sprite(
        PROP_COLORS,
        """
        .....llll.....
        ...llgggglg...
        ..lggGgggggl..
        .lgggggGgggGl.
        lggGgggggggGg.
        .gggggGgggGgg.
        ..gGg.ggggGg..
        ...gg.gtgg....
        .......tt.....
        ......tt......
        ......tT......
        .....tTt......
        ....tt.tt.....
        """,
    ),
    base = 12,
)

val CYPRESS = Prop(
    sprite(
        PROP_COLORS,
        """
        ..C..
        ..C..
        .CcC.
        .CcC.
        .ccC.
        CcCcC
        CcccC
        CCcCC
        CcCcC
        .CcC.
        CCcCC
        .CCC.
        ..t..
        ..t..
        """,
    ),
    base = 13,
)

val COLUMN = Prop(
    sprite(
        PROP_COLORS,
        """
        .ww....
        .wwwW..
        .wWwWw.
        .wWwWw.
        .wWwWw.
        .wWwWw.
        .wWwWw.
        wwwwwww
        WWWWWWW
        """,
    ),
    base = 8,
)

val BUSH = Prop(
    sprite(
        PROP_COLORS,
        """
        ...lgl...
        .lgggGgl.
        lgGgggGgg
        .gggGggg.
        """,
    ),
    base = 3,
)

private val BIRD_COLOR = mapOf('#' to rgb(0x2E3442))

val BIRD_UP = sprite(BIRD_COLOR, "#...#\n.#.#.\n..#..")
val BIRD_FLAT = sprite(BIRD_COLOR, ".....\n##.##\n..#..")
