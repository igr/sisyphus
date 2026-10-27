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
class Prop(art: String, val weight: Int, colors: Map<Char, Int> = PROP_COLORS) {
    val sprite = sprite(colors, art)
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

/** A herm: the stone marker of Greek roads, a pillar with the head of Hermes. One stands after every 100 turns of the final climb. */
val HERM: Pixels = sprite(
    PROP_COLORS,
    """
    ..wwW..
    .wwwwW.
    .wwwWW.
    .wwWWW.
    ..WWD..
    ...wW..
    wwwwWWW
    .wwwWW.
    .wwwW..
    .wwwW..
    .wwwW..
    .wwwW..
    .wwwW..
    .wwwW..
    .wwwW..
    wwwwwWW
    WWWWWWW
    """,
)

/** The props of Corinth. */
val PROPS = listOf(OLIVE, YOUNG_OLIVE, CYPRESS, STONE_PINE, POPLAR, ALMOND, LEMON, DEAD_TREE, COLUMN, BUSH)

/** Picks one of [props] for a random [roll]: the heavier the weight, the more often it comes up. */
fun propFor(roll: Int, props: List<Prop> = PROPS): Prop {
    var r = roll.mod(props.sumOf { it.weight })
    for (prop in props) {
        if (r < prop.weight) return prop
        r -= prop.weight
    }
    return props.last()
}

private val BIRD_COLOR = mapOf('#' to rgb(0x2E3442))

val BIRD_UP = sprite(BIRD_COLOR, "#...#\n.#.#.\n..#..")
val BIRD_FLAT = sprite(BIRD_COLOR, ".....\n##.##\n..#..")

private val DOLPHIN_COLORS = mapOf('s' to rgb(0x3E4C5E), 'b' to rgb(0x8C9AAE))

/** A dolphin's leap in three frames, facing right: rising, at the top, diving back in. */
val DOLPHIN_RISE = sprite(
    DOLPHIN_COLORS,
    """
    ....ss
    ...sss
    ..ssb.
    .ssb..
    ss....
    """,
)

val DOLPHIN_TOP = sprite(
    DOLPHIN_COLORS,
    """
    ...s...
    .ssssss
    s.bbbb.
    """,
)

val DOLPHIN_DIVE = sprite(
    DOLPHIN_COLORS,
    """
    ss....
    .ssb..
    ..ssb.
    ...sss
    ....ss
    """,
)

private val PEGASUS_COLORS = mapOf('w' to rgb(0xF4F2EE), 'g' to rgb(0xC9C6C0))
private val PEGASUS_OUTLINE = rgb(0x2E3442)

/** Pegasus, wings up and wings down. Both face right. */
val PEGASUS_UP = sprite(
    PEGASUS_COLORS,
    """
    ....gg.......
    ...ggg....w..
    ..ggg....wwww
    .ggg.....ww..
    ..wwwwwwwww..
    .wwwwwwwwww..
    w.w.w...w.w..
    ..w.w...w.w..
    """,
).outlined(PEGASUS_OUTLINE)

val PEGASUS_DOWN = sprite(
    PEGASUS_COLORS,
    """
    .............
    ..........w..
    .........wwww
    .........ww..
    ..wwwwwwwww..
    .wwgggggwww..
    w.wgggg.w.w..
    ..w.gg..w.w..
    """,
).outlined(PEGASUS_OUTLINE)
