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
    'q' to rgb(0x8AA65A),
    'p' to rgb(0x5E7E3C),
    'P' to rgb(0x3E5C2C),
    'v' to rgb(0x8AB050),
    'V' to rgb(0x5E8A3A),
    'e' to rgb(0x3C6A2E),
    'E' to rgb(0x56883A),
    'y' to rgb(0xF4D23E),
    'a' to rgb(0xFFF2F6),
    'b' to rgb(0xF6C6D6),
    'B' to rgb(0xE494B0),
    't' to rgb(0x5A4030),
    'T' to rgb(0x7A5A40),
    'd' to rgb(0x8A7058),
    'D' to rgb(0x5A4636),
    'w' to rgb(0xECE6D8),
    'W' to rgb(0xB8B0A0),
)

/** Something by the path. Its last row touches the ground. [weight] sets how often it shows up. */
class Prop(art: String, val weight: Int) {
    val sprite = sprite(PROP_COLORS, art)
}

val OLIVE = Prop(
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
    weight = 16,
)

val YOUNG_OLIVE = Prop(
    """
    ..llgl...
    .lgGgggl.
    lgggGgGgl
    .gGgggGg.
    ..gg.gg..
    ....t....
    ....tt...
    ...tTt...
    """,
    weight = 8,
)

val CYPRESS = Prop(
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
    weight = 13,
)

/** The umbrella pine of Mediterranean hills: a flat crown on a tall, bare trunk. */
val STONE_PINE = Prop(
    """
    ......qqpqqpq......
    ...qqppqpppppqq....
    .qqpppPpppPpppppq..
    qppPpppPPpppPppPpq.
    pPpPPppPPpPPpPPpPp.
    .PPpPPPPpPPPPPpPP..
    ..PP.PPP.tPP..PP...
    .........tT........
    .........Tt........
    ..........tT.......
    ..........tT.......
    ..........Tt.......
    .........tT........
    .........tTt.......
    ........tt.Tt......
    """,
    weight = 12,
)

val POPLAR = Prop(
    """
    ...v...
    ..vVv..
    ..VvV..
    .vVvVv.
    .VvVvV.
    .vVvvV.
    vVvVvVv
    .VvVvV.
    vVvvVvV
    .vVvVv.
    vVvVvVv
    .VvVvV.
    vVvVvVv
    .vVvVv.
    ..VvV..
    ...t...
    ...t...
    ...T...
    """,
    weight = 8,
)

val ALMOND = Prop(
    """
    ....aBbab......
    ..abBbaBbbBa...
    .abBbaBbbBabBa.
    aBbBbabBbaBbBb.
    .bBabBbBbBabB..
    ..bBb.tbBbBb...
    ....b..tt.b....
    .......tT......
    ......tt.......
    ......tT.......
    .....tTtt......
    """,
    weight = 8,
)

val LEMON = Prop(
    """
    ...eEEEe...
    .eEEyEEEEe.
    eEEEEEeEyEe
    EEyEEEEEEEE
    eEEEEyEEEEe
    .eEEEEEEyE.
    ..eEyEEEe..
    ....eTe....
    .....t.....
    .....Tt....
    ....tTt....
    """,
    weight = 7,
)

/** Not everything on this hill made it. */
val DEAD_TREE = Prop(
    """
    .d.......d...
    ..d..d..d..d.
    d..d.d.dd.d..
    .d..dDdd..d..
    ..d.DdD..d...
    ...dDDd.dd...
    ....dDDdd....
    .....DDd.....
    .....dDD.....
    ......DD.....
    ......Dd.....
    .....dDD.....
    ....dD.DDd...
    """,
    weight = 6,
)

val COLUMN = Prop(
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
    weight = 5,
)

val BUSH = Prop(
    """
    ...lgl...
    .lgggGgl.
    lgGgggGgg
    .gggGggg.
    """,
    weight = 10,
)

val PROPS = listOf(OLIVE, YOUNG_OLIVE, CYPRESS, STONE_PINE, POPLAR, ALMOND, LEMON, DEAD_TREE, COLUMN, BUSH)
private val TOTAL_WEIGHT = PROPS.sumOf { it.weight }

/** Picks a prop for a random [roll]: the heavier the weight, the more often it comes up. */
fun propFor(roll: Int): Prop {
    var r = roll.mod(TOTAL_WEIGHT)
    for (prop in PROPS) {
        if (r < prop.weight) return prop
        r -= prop.weight
    }
    return PROPS.last()
}

private val BIRD_COLOR = mapOf('#' to rgb(0x2E3442))

val BIRD_UP = sprite(BIRD_COLOR, "#...#\n.#.#.\n..#..")
val BIRD_FLAT = sprite(BIRD_COLOR, ".....\n##.##\n..#..")
