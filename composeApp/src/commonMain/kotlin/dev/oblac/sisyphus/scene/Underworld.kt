package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.bayer
import dev.oblac.sisyphus.gfx.hash
import dev.oblac.sisyphus.gfx.hash01
import dev.oblac.sisyphus.gfx.lerpColor
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.gfx.sprite
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/*
 * The Underworld: Sisyphus pushes in Tartarus, where Homer saw him. A rock roof hangs over a misty cave,
 * the pale sun of Elysium crosses it, and the dark Styx runs below, before the gates of Hades.
 */

private const val GATE_HEIGHT = 15

private val UNDERWORLD_PROP_COLORS = mapOf(
    'v' to rgb(0x3E4E40),
    'V' to rgb(0x28342C),
    'w' to rgb(0x7E8E70),
    'W' to rgb(0x5A6A50),
    'c' to rgb(0xDCE0D8),
    'C' to rgb(0xA8B0A8),
    'g' to rgb(0x4E6E3E),
    'G' to rgb(0x324A2C),
    'r' to rgb(0xC03A30),
    'a' to rgb(0xF2EEE2),
    'A' to rgb(0xC8C4B4),
    'e' to rgb(0x7A8A6A),
    's' to rgb(0xB0ACA4),
    'S' to rgb(0x827E76),
    'k' to rgb(0x4E4A46),
    'o' to rgb(0xA85A3A),
    'O' to rgb(0x7A3E28),
    'b' to rgb(0x5A5A62),
    'B' to rgb(0x7E7E86),
    'd' to rgb(0x8E8E96),
    'D' to rgb(0x62626A),
    't' to rgb(0x3E322A),
    'T' to rgb(0x5A4A3C),
)

/** The black poplars of Persephone's grove. */
val BLACK_POPLAR = Prop(
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
    ..VvV..
    ...t...
    ...t...
    ...T...
    """,
    weight = 12,
    colors = UNDERWORLD_PROP_COLORS,
)

/** A willow of the same grove, weeping over the path. */
val WILLOW = Prop(
    """
    ....wWwWw....
    ..wWwWwWwWw..
    .wWwWwWwWwWw.
    wWw.wWtWw.wWw
    w.w.w.t.w.w.w
    w.w.w.t.w.w.w
    w...w.t.w...w
    ....w.t.w....
    ......tT.....
    ......Tt.....
    .....tTt.....
    """,
    weight = 10,
    colors = UNDERWORLD_PROP_COLORS,
)

/** The white cypress, which grows by the spring of forgetting. */
val WHITE_CYPRESS = Prop(
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
    weight = 10,
    colors = UNDERWORLD_PROP_COLORS,
)

/** A pomegranate tree, whose seeds kept Persephone here. */
val POMEGRANATE = Prop(
    """
    ...gGgGg...
    .gGgrGgGg..
    gGgGgGgrGg.
    GgrGgGgGgG.
    .gGgGrgGg..
    ..GgGgGg...
    ....gTg....
    .....t.....
    .....Tt....
    ....tTt....
    """,
    weight = 8,
    colors = UNDERWORLD_PROP_COLORS,
)

/** Asphodels, the pale flowers of the meadow of the dead. */
val ASPHODELS = Prop(
    """
    .a...a...
    .a.a.A.a.
    .A.a.a.a.
    .e.A.e.A.
    .e.e.e.e.
    ..eeeee..
    ...eee...
    """,
    weight = 14,
    colors = UNDERWORLD_PROP_COLORS,
)

/** A grave stone with a carved band. */
val STELE = Prop(
    """
    ..sss..
    .sSsSs.
    sssssss
    sSkkkSs
    sssssss
    sSsssSs
    sssssss
    sSsssSs
    sssssss
    SSSSSSS
    """,
    weight = 5,
    colors = UNDERWORLD_PROP_COLORS,
)

/** An amphora, left for the dead. */
val AMPHORA = Prop(
    """
    .o...o.
    .oOoOo.
    ..oOo..
    .oOoOo.
    oOoOoOo
    oOkkkOo
    .oOoOo.
    ..oOo..
    ...o...
    """,
    weight = 4,
    colors = UNDERWORLD_PROP_COLORS,
)

val GRAY_BOULDER = Prop(
    """
    ..BBB...
    .BbbbB..
    BbbbbbB.
    bbbbbbbB
    .bbbbbb.
    """,
    weight = 8,
    colors = UNDERWORLD_PROP_COLORS,
)

/** A tree with no leaves, pale as the shades. */
val BARE_TREE = Prop(
    """
    d.....d....
    .d..d.d..d.
    ..d.dDd.d..
    ...dDDdd...
    ....DdD....
    ....dDD....
    .....Dd....
    .....dD....
    ....dDd....
    ...dD.Dd...
    """,
    weight = 6,
    colors = UNDERWORLD_PROP_COLORS,
)

/** The props of The Underworld. */
val UNDERWORLD_PROPS = listOf(BLACK_POPLAR, WILLOW, WHITE_CYPRESS, POMEGRANATE, ASPHODELS, STELE, AMPHORA, GRAY_BOULDER, BARE_TREE)

private val BAT_COLOR = mapOf('#' to rgb(0x1A1620))

val BAT_UP = sprite(BAT_COLOR, "#.....#\n##...##\n.#####.\n...#...")
val BAT_FLAT = sprite(BAT_COLOR, "...#...\n.#####.\n##.#.##\n#.....#")

private val PSYCHE_COLORS = mapOf('w' to rgb(0xA8D8F4), 'W' to rgb(0x5A8AC8), 'b' to rgb(0x2A2034))
private val PSYCHE_OUTLINE = rgb(0x1A1620)

/** Psyche on butterfly wings, open and half closed. */
val PSYCHE_UP = sprite(
    PSYCHE_COLORS,
    """
    .WW...WW.
    WwwW.WwwW
    WwwwbwwwW
    .WwwbwwW.
    ..WwbwW..
    ...W.W...
    """,
).outlined(PSYCHE_OUTLINE)

val PSYCHE_DOWN = sprite(
    PSYCHE_COLORS,
    """
    .........
    ...W.W...
    ..WwbwW..
    .WwwbwwW.
    ..WwbwW..
    ...W.W...
    """,
).outlined(PSYCHE_OUTLINE)

private val FERRY_COLORS = mapOf('h' to rgb(0x3A3640), 'H' to rgb(0x56525E), 'b' to rgb(0x1E1814), 'B' to rgb(0x3A2E26), 'p' to rgb(0x6A5A4A))

/** Charon in his boat, facing right: he poles from the stern, and a lantern hangs at the bow. */
val FERRY = sprite(
    FERRY_COLORS,
    """
    ...H.......
    ..hHh......
    ..hhh....p.
    .phhhh...p.
    pbBBBBBBBBb
    ..bbbbbbbb.
    """,
)

/** The rock for each tier of the shop in The Underworld: the first rock, the stone of Lethe, the rock of Tantalus, the adamant sphere. */
private val UNDERWORLD_ROCKS = listOf(
    FIRST_ROCK,
    Rock(intArrayOf(rgb(0x2E4C58), rgb(0x446A76), rgb(0x5E8894), rgb(0x7AA4AE), rgb(0x9CC0C6), rgb(0xC4DEE0)), ::letheMark),
    Rock(intArrayOf(rgb(0x26221E), rgb(0x383430), rgb(0x4C4640), rgb(0x625A52), rgb(0x7A7068), rgb(0x94897E)), ::tantalusMark),
    Rock(intArrayOf(rgb(0x1E2228), rgb(0x30363E), rgb(0x464E58), rgb(0x606A76), rgb(0x7E8A96), rgb(0xF4F8FF)), polished = true),
)

/** The river of forgetting wore soft bands into the stone. */
private fun letheMark(u: Double, v: Double): Int = if (abs(sin(v * 0.9 + sin(u * 0.5))) < 0.22) 2 else 0

/** Deep cracks in the rock that hangs over Tantalus. */
private val TANTALUS_CRACKS = listOf(
    doubleArrayOf(-7.0, 1.0, -3.0, 0.0, 0.0, 2.5, 3.5, 1.5, 7.0, 3.0),
    doubleArrayOf(-2.0, -7.0, -1.0, -3.5, -3.0, 0.0),
    doubleArrayOf(2.0, 6.5, 0.0, 2.5),
)

private fun tantalusMark(u: Double, v: Double): Int = when {
    TANTALUS_CRACKS.any { crack(u, v, it) } -> -3
    (hash(floor(u / 3).toInt(), floor(v / 3).toInt(), 77) and 0xFF) < 40 -> -1
    else -> 0
}

internal val UNDERWORLD_LOOK = Look(
    sky = UNDERWORLD_KEYS,
    sun = intArrayOf(rgb(0xD0DCD0), rgb(0xF0F6EE), rgb(0x7A9890), rgb(0xB8D0C8), rgb(0xA8C8C0), rgb(0xE0F0EC)),
    ground = intArrayOf(
        rgb(0x8A8890), rgb(0x6A6872), rgb(0x524E5A),
        rgb(0x403C48), rgb(0x34303C), rgb(0x2A2632),
        rgb(0x928E88), rgb(0x5E5A58),
    ),
    blades = intArrayOf(rgb(0xA4B098), rgb(0x74806A)),
    grass = 0.5f,
    flowers = intArrayOf(rgb(0xF0ECE0), rgb(0xF0E4A0), rgb(0xB4A4D4), rgb(0xF8F8F0)),
    props = UNDERWORLD_PROPS,
    rocks = UNDERWORLD_ROCKS,
    birdUp = BAT_UP,
    birdFlat = BAT_FLAT,
    dust = rgb(0x7A7680),
    fall = Fall.RAIN,
    stone = BLACK_STONE,
)

/** The roof of the cave, with stalactites, over the misty air. */
internal fun drawRoof(px: Pixels, atm: Atmosphere) {
    val rock = lerpColor(atm.skyTop, rgb(0x000000), 0.6f)
    for (x in 0 until px.width) {
        var h = 2 + (hash01(x.floorDiv(3), 1, 45) * 4).toInt()
        val c = x.floorDiv(11) * 11 + 5
        if (hash01(c, 2, 45) < 0.6f) h = max(h, (6 + hash01(c, 3, 45) * 10).toInt() - abs(x - c) * 3)
        for (y in 0 until h) px[x, y] = rock
    }
}

/** Where the gates of Hades stand on the far shore: a fifth of the way in, so far away that they stay put. */
private fun gateX(geo: SceneGeometry): Int = (geo.width * 0.22).toInt()

/** The far shore of the Styx, and on it the gates of the house of Hades, with a fire burning on each side. */
internal fun drawFarShore(px: Pixels, geo: SceneGeometry, atm: Atmosphere, clock: Double) {
    val shore = lerpColor(atm.skyHorizon, atm.sea, 0.7f)
    val gate = lerpColor(atm.skyHorizon, atm.sea, 0.9f)
    for (x in 0 until px.width) {
        val h = 1 + (hash01(x.floorDiv(3), 0, 43) * 3).toInt()
        for (k in 1..h) px[x, geo.horizon - k] = shore
    }
    val gx = gateX(geo)
    val top = geo.horizon - GATE_HEIGHT
    for (x in gx - 2..gx + 2) px[x, top - 1] = gate
    for (x in gx - 5..gx + 5) px[x, top] = gate
    for (x in gx - 8..gx + 8) for (y in top + 1..top + 2) px[x, y] = gate
    for (x in gx - 6..gx + 6) for (y in top + 3 until geo.horizon) px[x, y] = if (abs(x - gx) <= 3) lerpColor(gate, rgb(0x000000), 0.5f) else gate
    for (side in intArrayOf(-11, 11)) {
        val fx = gx + side
        for (y in geo.horizon - 3 until geo.horizon) px[fx, y] = gate
        flame(px, fx, geo.horizon - 4, (floor(clock * 8).toInt() + side) % 2 == 0)
    }
}

/** A small fire at ([x], [y]) with its glow; [high] makes it flicker. */
private fun flame(px: Pixels, x: Int, y: Int, high: Boolean) {
    for (j in y - 3..y + 1) for (i in x - 2..x + 2) {
        if (abs(i - x) + abs(j - (y - 1)) <= 2 && bayer(i, j) < 0.5f) px[i, j] = lerpColor(px[i, j], rgb(0xF0A050), 0.35f)
    }
    px[x, y] = rgb(0xF07A2A)
    px[x, y - 1] = if (high) rgb(0xFFE070) else rgb(0xF8B040)
}

/** The Styx below the horizon: dark, still water with slow ripples. */
internal fun drawStyx(px: Pixels, geo: SceneGeometry, atm: Atmosphere, p: Int, clock: Double) {
    val tick = floor(clock * 1.2).toInt()
    val ripple = lerpColor(atm.sea, atm.skyHorizon, 0.3f)
    for (x in 0 until px.width) {
        val bottom = min(px.height, geo.surfaceY(x, p))
        for (y in geo.horizon until bottom) {
            val k = y - geo.horizon
            var c = lerpColor(atm.skyHorizon, atm.sea, min(1f, k / 4f + 0.45f))
            if (hash01((x + tick * (1 + k % 2)).floorDiv(4), k, 47) < 0.06f) c = ripple
            px.data[y * px.width + x] = c
        }
    }
}

/** Charon's boat on the Styx, with its lantern lit. */
internal fun drawFerry(px: Pixels, world: World, atm: Atmosphere) {
    val f = world.ferry ?: return
    val x = floor(f.x).toInt() - FERRY.width / 2
    val y = floor(f.y).toInt() - FERRY.height + 1
    px.draw(FERRY, x, y, light = atm.light)
    flame(px, x + 9, y + 1, true)
}

/** At night the blessed souls of Elysium drift over the meadow as small lights. */
internal fun drawLights(px: Pixels, geo: SceneGeometry, atm: Atmosphere, p: Int, clock: Double) {
    if (atm.darkness < 0.5f) return
    for (i in 0 until 9) {
        val x = floor(geo.width * (i + 0.5) / 9 + sin(clock * 0.3 + i * 2.1) * 6).toInt()
        val y = geo.surfaceY(x, p) - 4 - floor((sin(clock * 0.5 + i * 1.7) + 1) * 5).toInt()
        for (j in y - 2..y + 2) for (k in x - 2..x + 2) {
            if (abs(k - x) + abs(j - y) <= 2 && bayer(k, j) < 0.4f) px[k, j] = lerpColor(px[k, j], rgb(0xE8F0A0), 0.35f)
        }
        px[x, y] = if (sin(clock * 2 + i) > 0) rgb(0xFFFFD8) else rgb(0xE8E8A0)
    }
}
