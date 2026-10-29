package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.bayer
import dev.oblac.sisyphus.gfx.hash
import dev.oblac.sisyphus.gfx.hash01
import dev.oblac.sisyphus.gfx.lerpColor
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.gfx.shade
import dev.oblac.sisyphus.gfx.sprite
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

/*
 * Cairo: Sisyphus pushes up the side of the pyramid of Cheops, the hill that men built. The other two pyramids of Giza
 * stand on the desert horizon with the Sphinx at their feet, the green valley of the Nile lies below,
 * and on the days it rains in Corinth, a sandstorm blows.
 */

/** The rows under the horizon where the Nile runs, and the row where the hippopotamus comes out of it. */
private const val NILE_TOP = 5
private const val NILE_BOTTOM = 8
internal const val NILE_WATER = 7

/** The pyramid of Chephren on the horizon: how high it stands, and how far in. So far away, it stays put while he walks. */
private const val FAR_HEIGHT = 21
private const val FAR_X = 0.18

/** The courses of the pyramid are this many pixels high, of blocks this many pixels long. */
private const val COURSE = 5
private const val BLOCK = 11

private const val SAND_LIGHT = 0
private const val SAND = 1
private const val SAND_DARK = 2
private const val STONE_LIT = 3
private const val STONE = 4
private const val STONE_SHADE = 5
private const val JOINT = 6
private const val WORN = 7

private val CAIRO_PROP_COLORS = mapOf(
    'l' to rgb(0xE8D4A2),
    'L' to rgb(0xCBB07E),
    'd' to rgb(0xA48858),
    'c' to rgb(0xF6F0E2),
    'C' to rgb(0xD6CCB4),
    'r' to rgb(0xB8462C),
    't' to rgb(0x8A6440),
    'T' to rgb(0x5E422A),
    'p' to rgb(0xD2AA66),
    'P' to rgb(0x9C7A44),
    'o' to rgb(0xC0703E),
    'O' to rgb(0x8E4C2A),
    'k' to rgb(0x3A2418),
    'n' to rgb(0xC89E66),
    'N' to rgb(0x7A5A3A),
    'e' to rgb(0xD8E040),
    'v' to rgb(0xC2B060),
    'V' to rgb(0x8E8040),
    'a' to rgb(0x86964E),
    'A' to rgb(0x5E6E3A),
    'y' to rgb(0xF2C83A),
)

/** A block of limestone that the builders left on the way up. */
val BUILDERS_BLOCK = Prop(
    """
    ..lllllll
    .lllllllL
    lllllllLd
    LLLLLLLdd
    LLLLLLLdd
    LLLLLLLd.
    """,
    weight = 10,
    colors = CAIRO_PROP_COLORS,
)

/** A block with the red marks of the gang that hauled it. */
val MARKED_BLOCK = Prop(
    """
    ..lllllll
    .lllllllL
    lllllllLd
    LrLrrLLdd
    LLrLrLLdd
    LLLLLLLd.
    """,
    weight = 6,
    colors = CAIRO_PROP_COLORS,
)

/** A block on the wooden sledge that carried it. */
val SLEDGE = Prop(
    """
    ...lllllll..
    ..lllllllL..
    .lllllllLd..
    .LLLLLLLdd..
    .LLLLLLLdd..
    .LLLLLLLd...
    tttttttttttt
    TTTTTTTTTTT.
    """,
    weight = 4,
    colors = CAIRO_PROP_COLORS,
)

/** A lever of short beams, as Herodotus says the stones went up from step to step. */
val LEVER = Prop(
    """
    tttttttttt.
    T...T.....p
    ....T.....p
    ....T.....P
    ....T......
    ...TTT.....
    ..TTTTT....
    """,
    weight = 4,
    colors = CAIRO_PROP_COLORS,
)

/** A jar of water, to wet the sand in front of the sledges. */
val WATER_JAR = Prop(
    """
    ..kkk..
    ..ooo..
    ...o...
    .ooooo.
    oOooooo
    oOooooo
    oOooooo
    .oOooo.
    ..ooo..
    """,
    weight = 6,
    colors = CAIRO_PROP_COLORS,
)

/** A coil of rope. */
val ROPE = Prop(
    """
    .pPpPp..
    pPpPpPp.
    PpPpPpPp
    .PpPpPp.
    """,
    weight = 6,
    colors = CAIRO_PROP_COLORS,
)

/** A white casing stone, fallen from higher up. */
val CASING_STONE = Prop(
    """
    .......cc.
    .....ccccC
    ...ccccccC
    .ccccccccC
    CCCCCCCCC.
    """,
    weight = 8,
    colors = CAIRO_PROP_COLORS,
)

/** A cat that sits on the warm stone and watches him. */
val CAT = Prop(
    """
    .n.n....
    .nnn....
    eNnn....
    .nnnn...
    ..nnnn..
    ..nNnnn.
    ..nnNnn.
    .nnnnnnN
    """,
    weight = 3,
    colors = CAIRO_PROP_COLORS,
)

/** A tuft of dry grass in a crack. */
val DESERT_GRASS = Prop(
    """
    v..V..v
    .v.V.v.
    .VvVvV.
    ..vVv..
    ..VVV..
    """,
    weight = 12,
    colors = CAIRO_PROP_COLORS,
)

/** A young acacia, with thorns and yellow flowers, that grew in a joint. */
val THORN_BUSH = Prop(
    """
    .aAaAaAaAa.
    aAyAaAaAyAa
    .AaAa.aAaA.
    .....t.....
    .....t.....
    ....tT.....
    .....T.....
    ....tTt....
    """,
    weight = 8,
    colors = CAIRO_PROP_COLORS,
)

/** The props of Cairo. */
val CAIRO_PROPS = listOf(BUILDERS_BLOCK, MARKED_BLOCK, SLEDGE, LEVER, WATER_JAR, ROPE, CASING_STONE, CAT, DESERT_GRASS, THORN_BUSH)

private val FALCON_COLOR = mapOf('#' to rgb(0x3A2A20))

val FALCON_UP = sprite(FALCON_COLOR, "#.......#\n.#.....#.\n..#...#..\n...###...\n....#....")
val FALCON_FLAT = sprite(FALCON_COLOR, ".........\n.........\n####.####\n...###...\n....#....")

private val PHOENIX_COLORS = mapOf('r' to rgb(0xC8382A), 'R' to rgb(0x8A2018), 'o' to rgb(0xF07A2A), 'y' to rgb(0xF8C840), 'z' to rgb(0xFFF0A0))
private val PHOENIX_OUTLINE = rgb(0x3A1410)

/** The Phoenix in red and gold, with a crest and a long tail of gold plumes, wings up and wings down. Both face right. */
val PHOENIX_UP = sprite(
    PHOENIX_COLORS,
    """
    ..........RRR.....
    .........RrrR.....
    ........RrrR......
    .......RrrR...yy..
    ......orrR...yrrz.
    yyy..orrrrrrrrrr..
    ...yyorrrrrrrr....
    yy...yoo..........
    ..yyy.............
    ..................
    """,
).outlined(PHOENIX_OUTLINE)

val PHOENIX_DOWN = sprite(
    PHOENIX_COLORS,
    """
    ..................
    ..................
    ..............yy..
    .............yrrz.
    yyy..orrrrrrrrrr..
    ...yyorrrrrrrr....
    yy...yooRrrR......
    ..yyy....RrrR.....
    ..........RrrR....
    ...........RR.....
    """,
).outlined(PHOENIX_OUTLINE)

private val HIPPO_COLORS = mapOf('p' to rgb(0x9A8A90), 'P' to rgb(0x6A5A62), 'e' to rgb(0x1A1214), 'k' to rgb(0xE07A86), 't' to rgb(0xFFFFF0))

/** A hippopotamus in the Nile, facing right: its eyes and ears over the water, its head out, and its yawn. */
private val HIPPO_EYES = sprite(HIPPO_COLORS, ".p.p...\npPpPp..")
private val HIPPO_HEAD = sprite(
    HIPPO_COLORS,
    """
    .p.p.......
    pPpPppppp..
    ppePpppppp.
    pppppppppPp
    .pppppppppp
    """,
)
private val HIPPO_YAWN = sprite(
    HIPPO_COLORS,
    """
    ........pp.
    .......pkkt
    .p.p..pkkk.
    pPpPppkkkk.
    ppePpppkkkt
    pppppppppPp
    .pppppppppp
    """,
)

/** The rock for each tier of the shop in Cairo: the first rock, the desert stone, the egg of myrrh, the alabaster sphere. */
private val CAIRO_ROCKS = listOf(
    FIRST_ROCK,
    Rock(intArrayOf(rgb(0x5A4632), rgb(0x7A6248), rgb(0x9A8060), rgb(0xB89E7A), rgb(0xD2BA94), rgb(0xEAD8B4)), ::windMark),
    Rock(intArrayOf(rgb(0x5A2A14), rgb(0x7A3A1C), rgb(0x9A5228), rgb(0xB86E3A), rgb(0xD48E52), rgb(0xF0B878)), ::myrrhMark),
    Rock(intArrayOf(rgb(0x8A7A5E), rgb(0xAE9C7C), rgb(0xCCBC9C), rgb(0xE2D6BC), rgb(0xF2EAD8), rgb(0xFFFFFF)), ::veinMark, polished = true),
)

/** The wind of the desert cut fine grooves across the stone. */
private fun windMark(u: Double, v: Double): Int = if ((v + 0.35 * u).mod(3.5) < 0.7) -1 else 0

/** Myrrh is a resin: drops of it shine where the light goes in. */
private fun myrrhMark(u: Double, v: Double): Int =
    if ((hash(floor(u / 1.6).toInt(), floor(v / 1.6).toInt(), 79) and 0xFF) < 34) 1 else 0

/** Soft veins run through the alabaster. */
private fun veinMark(u: Double, v: Double): Int = if (abs(sin(u * 0.7 + sin(v * 0.6) * 1.5)) < 0.12) -1 else 0

/** The courses of the pyramid: level rows of blocks, with sand on the top and in the joints. */
private fun courses(wx: Int, wy: Int, d: Int): Int {
    if (d == 0) return if (hash01(wx, 1, 89) < 0.4f) SAND_LIGHT else SAND
    if (d == 1) return if (bayer(wx, d) < 0.5f) SAND else SAND_DARK
    val course = wy.floorDiv(COURSE)
    val row = wy.mod(COURSE)
    val bx = wx + course * 7
    if (row == 0 || bx.mod(BLOCK) == 0) return if (d < 4 && bayer(wx, wy) < 0.5f) SAND_DARK else JOINT
    val h = hash(bx.floorDiv(BLOCK), course, 91)
    if ((h and 0xFF) < 36 && hash01(wx, wy, 92) < 0.5f) return WORN
    return when {
        row == COURSE - 1 -> STONE_LIT
        row == 1 -> STONE_SHADE
        else -> STONE
    }
}

internal val CAIRO_LOOK = Look(
    sky = CAIRO_KEYS,
    sun = intArrayOf(rgb(0xFFB060), rgb(0xFFFFF0), rgb(0xF07A38), rgb(0xFFE488), rgb(0xFFC878), rgb(0xFFF2C8)),
    ground = intArrayOf(
        rgb(0xF2DDA8), rgb(0xE0C48E), rgb(0xC4A470),
        rgb(0xE2CA96), rgb(0xCDB07C), rgb(0xB49664),
        rgb(0x8E7046), rgb(0xBCA070),
    ),
    blades = intArrayOf(rgb(0xE0C48E), rgb(0xC4A470)),
    grass = 0f,
    flowers = intArrayOf(rgb(0xE0C48E), rgb(0xE0C48E), rgb(0xE0C48E), rgb(0xE0C48E)),
    props = CAIRO_PROPS,
    rocks = CAIRO_ROCKS,
    birdUp = FALCON_UP,
    birdFlat = FALCON_FLAT,
    dust = rgb(0xD8BC88),
    fall = Fall.SAND,
    stone = SANDSTONE,
    layers = ::courses,
)

/** A far pyramid [height] pixels over the [horizon] at column [cx]: its left face in the sun, its right face in shade, and its top in white [casing]. */
private fun farPyramid(px: Pixels, cx: Double, horizon: Int, height: Int, lit: Int, shadow: Int, casing: Int?) {
    val half = height / 1.27
    for (x in floor(cx - half).toInt()..floor(cx + half).toInt()) {
        val d = abs(x + 0.5 - cx)
        val h = (height - d * 1.27).roundToInt()
        for (k in 1..h) {
            val top = height - k < 4
            px[x, horizon - k] = if (casing != null && top) casing else if (x + 0.5 < cx) lit else shadow
        }
    }
}

/** The Sphinx lies before the pyramid of Chephren, facing the sunrise on the left. */
private val SPHINX_SHAPE = listOf(".##.......", "###.......", ".###......", ".#########", "##########")

/** On the desert horizon: low dunes, the pyramids of Chephren and Mycerinus, and the Sphinx at their feet. */
internal fun drawPyramids(px: Pixels, geo: SceneGeometry, atm: Atmosphere) {
    val dune = lerpColor(atm.skyHorizon, shade(rgb(0xD8B880), atm.light), 0.45f)
    for (x in 0 until px.width) {
        val h = (1.6 + sin(x * 0.09) * 0.9 + sin(x * 0.23 + 1) * 0.5).roundToInt()
        for (k in 1..h) px[x, geo.horizon - k] = dune
    }
    val lit = lerpColor(atm.skyHorizon, shade(rgb(0xE8CC94), atm.light), 0.6f)
    val shadow = lerpColor(atm.skyHorizon, shade(rgb(0xA88A5C), atm.light), 0.6f)
    val casing = lerpColor(atm.skyHorizon, shade(rgb(0xF8F0DC), atm.light), 0.6f)
    val cx = geo.width * FAR_X
    farPyramid(px, cx + 27, geo.horizon, 12, lit, shadow, null)
    farPyramid(px, cx, geo.horizon, FAR_HEIGHT, lit, shadow, casing)
    val sphinx = lerpColor(atm.skyHorizon, shade(rgb(0x8A6A42), atm.light), 0.75f)
    val sx = floor(cx).toInt() - 14
    SPHINX_SHAPE.forEachIndexed { j, row ->
        for (i in row.indices) if (row[i] == '#') px[sx + i, geo.horizon - SPHINX_SHAPE.size + j] = sphinx
    }
}

/**
 * The valley of the Nile below the horizon: the far bank, fields with palms, the river, and the near fields with their palms.
 * It slides by slowly as he walks.
 */
internal fun drawNile(px: Pixels, geo: SceneGeometry, atm: Atmosphere, p: Int, clock: Double) {
    val shift = floor(p * 0.04).toInt()
    val tick = floor(clock * 1.5).toInt()
    val bank = lerpColor(atm.skyHorizon, shade(rgb(0xC8B080), atm.light), 0.5f)
    val field = shade(rgb(0x6E9E3C), atm.light)
    val furrow = shade(rgb(0x4E7E30), atm.light)
    val palm = shade(rgb(0x2E5426), atm.light)
    val ripple = lerpColor(atm.sea, atm.skyHorizon, 0.45f)
    for (x in 0 until px.width) {
        val u = x + shift
        val bottom = min(px.height, geo.surfaceY(x, p))
        for (y in geo.horizon until bottom) {
            val k = y - geo.horizon
            val c = when {
                k < 2 -> bank
                k < NILE_TOP -> lerpColor(atm.skyHorizon, if (hash01(u.floorDiv(2), k, 95) < 0.35f) palm else furrow, 0.55f)
                k <= NILE_BOTTOM -> {
                    val water = lerpColor(atm.skyHorizon, atm.sea, min(1f, (k - NILE_TOP) / 4f + 0.4f))
                    if (hash01((x + tick * (1 + k % 2)).floorDiv(3), k, 97) < 0.12f) ripple else water
                }
                else -> lerpColor(atm.skyHorizon, if (k % 3 == 0 || hash01(u, k, 96) < 0.15f) furrow else field, min(1f, k / 12f + 0.5f))
            }
            px.data[y * px.width + x] = c
        }
    }
    for (slot in (shift - 8).floorDiv(7)..(shift + px.width + 8).floorDiv(7)) {
        if (hash01(slot, 0, 98) < 0.4f) continue
        val x = slot * 7 + (hash(slot, 1, 98) and 3) - shift
        val base = geo.horizon + NILE_BOTTOM + 3
        if (base >= geo.surfaceY(x, p)) continue
        palmTree(px, x, base, palm, shade(rgb(0x5A4030), atm.light))
    }
}

/** A date palm standing on the near bank at ([x], [base]), its crown over the river. */
private fun palmTree(px: Pixels, x: Int, base: Int, crown: Int, trunk: Int) {
    for (k in 1..4) px[x, base - k] = trunk
    for (i in -2..2) px[x + i, base - 5] = crown
    for (i in intArrayOf(-3, -1, 1, 3)) px[x + i, base - 4] = crown
    px[x - 1, base - 6] = crown
    px[x + 1, base - 6] = crown
}

/** The hippopotamus of the first beauty item, out of the Nile. */
internal fun drawHippo(px: Pixels, world: World, atm: Atmosphere) {
    val h = world.hippo ?: return
    val f = h.t / HIPPO_TIME
    val frame = when {
        f < 0.12 || f > 0.88 -> HIPPO_EYES
        f < 0.3 || f > 0.7 -> HIPPO_HEAD
        else -> HIPPO_YAWN
    }
    val x = floor(h.x).toInt() - frame.width / 2
    px.draw(frame, x, world.geo.horizon + NILE_WATER - frame.height + 1, flipX = h.dir < 0, light = atm.light)
}

/** At night the Milky Way crosses the sky, from low on the left to high on the right: a band of faint light and many small stars. */
internal fun drawMilkyWay(px: Pixels, geo: SceneGeometry, atm: Atmosphere) {
    if (atm.stars <= 0.15f) return
    val glow = lerpColor(atm.skyMid, rgb(0xC8D0F0), 0.35f)
    val star = rgb(0xE8ECFF)
    val dim = rgb(0x8890B8)
    for (y in 0 until geo.horizon) for (x in 0 until px.width) {
        val center = geo.horizon * (0.95 - 0.85 * x / px.width) + 4 * sin(x * 0.06)
        val d = abs(y - center) / 9
        if (d > 1) continue
        val k = (1 - d).toFloat() * atm.stars
        if (bayer(x, y) < k * 0.55f && hash01(x.floorDiv(2), y.floorDiv(2), 93) > 0.25f) px[x, y] = lerpColor(px[x, y], glow, 0.3f)
        val s = hash01(x, y, 94)
        if (s < k * 0.1f) px[x, y] = if (s < k * 0.02f) star else dim
    }
}
