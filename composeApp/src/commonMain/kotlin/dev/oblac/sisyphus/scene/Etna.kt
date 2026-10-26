package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.bayer
import dev.oblac.sisyphus.gfx.hash
import dev.oblac.sisyphus.gfx.hash01
import dev.oblac.sisyphus.gfx.lerpColor
import dev.oblac.sisyphus.gfx.luminance
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.gfx.sprite
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

/*
 * Mount Etna: Sisyphus pushes up a side cone of the volcano. The summit smokes on the horizon,
 * black lava fields lie below it, and on the days it rains in Corinth, ash falls here.
 */

private const val SUMMIT_HEIGHT = 22
private const val SUMMIT_HALF = 30.0
private const val CRATER_HALF = 3.0
private const val PUFFS = 7

private val ETNA_PROP_COLORS = mapOf(
    'n' to rgb(0xA8C468),
    'N' to rgb(0x6E9040),
    'w' to rgb(0xEEEAE0),
    'k' to rgb(0x2A2626),
    'y' to rgb(0xF2C83A),
    'Y' to rgb(0xC8962A),
    'g' to rgb(0x6E8A3A),
    'h' to rgb(0x7E9C44),
    'c' to rgb(0x5A7A30),
    'C' to rgb(0x3A5626),
    't' to rgb(0x5A4030),
    'T' to rgb(0x7A5A40),
    'q' to rgb(0x466A3E),
    'p' to rgb(0x2E4A2E),
    'P' to rgb(0x1E3420),
    's' to rgb(0x8A9A78),
    'S' to rgb(0x5E6E54),
    'f' to rgb(0xE8C8D0),
    'b' to rgb(0x2A2424),
    'B' to rgb(0x4A403E),
    'r' to rgb(0x9A4E36),
    'v' to rgb(0x1A1414),
    'o' to rgb(0xE0602A),
    'm' to rgb(0xF2F0EC),
    'M' to rgb(0xC8C4C0),
    'd' to rgb(0x241E1C),
    'D' to rgb(0x3E3634),
    'l' to rgb(0x2E2826),
    'L' to rgb(0x4E4642),
)

/** The birch that grows only on Etna, white among the black stones. */
val ETNA_BIRCH = Prop(
    """
    ...nNn...
    ..nNnNn..
    .nNnnNnN.
    nNnNnNnNn
    .NnNnnNn.
    ..nNwNn..
    ...nw....
    ....wk...
    ....kw...
    ....ww...
    ....wk...
    ....kw...
    ....ww...
    ...wkww..
    """,
    weight = 12,
    colors = ETNA_PROP_COLORS,
)

/** The broom of Etna, yellow with flowers. */
val BROOM = Prop(
    """
    ...yYy.y...
    .yYgyYgyYy.
    yYgyyYyYgyy
    .ygYyygYyY.
    ..gyYgyYg..
    ....ggg....
    .....g.....
    """,
    weight = 14,
    colors = ETNA_PROP_COLORS,
)

/** An old chestnut, wide enough to shade a hundred horses. */
val CHESTNUT = Prop(
    """
    .....hcchch......
    ...hcCcchcCch....
    .hcCccCchccCcch..
    hcCcchcCcCchcCcch
    cCchcCcccCcchcCcc
    .CcCcCchcCcCcCcC.
    ..CCcCTtCCcCC....
    ......TttT.......
    ......tTTt.......
    .....TtttTt......
    .....tTt.tT......
    ....tTt...tTt....
    """,
    weight = 8,
    colors = ETNA_PROP_COLORS,
)

val BLACK_PINE = Prop(
    """
    .....q.....
    ....qpq....
    ...qpPpq...
    ....pPp....
    ...qpPppq..
    ..qpPpPpPq.
    ....pPp....
    ...qpPpPq..
    ..qpPppPpq.
    .qpPpPpPpPq
    ....pPt....
    ..qpPtPpq..
    .qpPpPpPpPq
    qpPpPpPpPpq
    .....t.....
    .....T.....
    .....t.....
    """,
    weight = 12,
    colors = ETNA_PROP_COLORS,
)

/** A cushion of spines, with small pink flowers, as on the high slopes. */
val SPINY_CUSHION = Prop(
    """
    ...sSs...
    .sSfsSss.
    sSsSsfSsS
    .SsSsSsS.
    """,
    weight = 10,
    colors = ETNA_PROP_COLORS,
)

val LAVA_BOULDER = Prop(
    """
    ...BBB...
    .BbbbBB..
    BbbrbbbB.
    bbbbbbrbB
    bbrbbbbbb
    .bbbbbbb.
    """,
    weight = 10,
    colors = ETNA_PROP_COLORS,
)

/** A crack in the ground that breathes steam, with fire deep inside. */
val STEAM_VENT = Prop(
    """
    ..m...
    .mM...
    ..mM..
    ...mM.
    ..Mm..
    .mM...
    ..m...
    ..vo..
    .vovv.
    vvovvv
    """,
    weight = 5,
    colors = ETNA_PROP_COLORS,
)

/** A tree that the lava reached. */
val BURNT_TREE = Prop(
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
    ....DdD....
    ...dD.Dd...
    """,
    weight = 6,
    colors = ETNA_PROP_COLORS,
)

/** A dry wall of lava stones, the kind that holds the vineyards. */
val LAVA_WALL = Prop(
    """
    .LLl.LLl.
    LLlLLlLLl
    lLLlLLlLL
    LlLLlLLlL
    """,
    weight = 5,
    colors = ETNA_PROP_COLORS,
)

/** The props of Etna. */
val ETNA_PROPS = listOf(ETNA_BIRCH, BROOM, CHESTNUT, BLACK_PINE, SPINY_CUSHION, LAVA_BOULDER, STEAM_VENT, BURNT_TREE, LAVA_WALL)

private val RAVEN_COLOR = mapOf('#' to rgb(0x141012))

val RAVEN_UP = sprite(RAVEN_COLOR, "##...##\n..#.#..\n...#...")
val RAVEN_FLAT = sprite(RAVEN_COLOR, ".......\n###.###\n...#...")

private val EAGLE_COLORS = mapOf('b' to rgb(0x5A3A20), 'B' to rgb(0x8A6034), 'g' to rgb(0xD0A040), 'y' to rgb(0xF8D040), 'z' to rgb(0xFFF0A0))
private val EAGLE_OUTLINE = rgb(0x2A1C10)

/** The eagle of Zeus with a thunderbolt in its claws, wings up and wings down. Both face right. */
val EAGLE_UP = sprite(
    EAGLE_COLORS,
    """
    ....BBB........
    ...BBbB........
    ..BBbB.........
    .BBbB......gg..
    bbbbbbbbbbbggy.
    ..bbbbbbbbb....
    .....z.........
    ....zyz........
    .....z.........
    """,
).outlined(EAGLE_OUTLINE)

val EAGLE_DOWN = sprite(
    EAGLE_COLORS,
    """
    ...............
    ...............
    ...........gg..
    bbbbbbbbbbbggy.
    ..bbbbbbbbb....
    ...BbbB.z......
    ....BbB.yz.....
    .....BB..z.....
    ...............
    """,
).outlined(EAGLE_OUTLINE)

/** The rock for each tier of the shop on Etna: the first rock, the lava stone, the volcanic bomb, the obsidian sphere. */
private val ETNA_ROCKS = listOf(
    FIRST_ROCK,
    Rock(intArrayOf(rgb(0x1E1C1E), rgb(0x2E2B2C), rgb(0x403C3C), rgb(0x56504E), rgb(0x6E6864), rgb(0x8C8680)), ::lavaMark),
    Rock(intArrayOf(rgb(0x2E2422), rgb(0x44342E), rgb(0x5A463E), rgb(0x72584C), rgb(0x8A6C5E), rgb(0xA28474)), ::bombMark, accent = rgb(0xB04A2A)),
    Rock(intArrayOf(rgb(0x0C0A12), rgb(0x16141E), rgb(0x22202E), rgb(0x34304A), rgb(0x4C4670), rgb(0xE0DCF4)), polished = true),
)

/** Lava stone is full of small round holes, where gas bubbled out as it cooled. */
private fun lavaMark(u: Double, v: Double): Int =
    if ((hash(floor(u / 1.3).toInt(), floor(v / 1.3).toInt(), 73) and 0xFF) < 26) -2 else 0

/** The crust of the bomb cracked as it flew, and the red inside shows. */
private val BOMB_CRACKS = listOf(
    doubleArrayOf(-7.0, -2.0, -2.5, -1.0, 0.5, -4.5, 2.5, -7.0),
    doubleArrayOf(-2.5, -1.0, -0.5, 3.0, 4.0, 4.5, 7.0, 3.0),
    doubleArrayOf(-0.5, 3.0, -4.0, 6.0),
    doubleArrayOf(0.5, -4.5, 5.0, -2.0, 6.5, 1.0),
)

private fun bombMark(u: Double, v: Double): Int = if (BOMB_CRACKS.any { crack(u, v, it) }) ACCENT else 0

internal val ETNA_LOOK = Look(
    sky = ETNA_KEYS,
    sun = intArrayOf(rgb(0xFF7A48), rgb(0xFFE8B8), rgb(0xD8402A), rgb(0xF4A84A)),
    ground = intArrayOf(
        rgb(0x6A625A), rgb(0x4A4440), rgb(0x383230),
        rgb(0x2A2422), rgb(0x221C1C), rgb(0x1A1616),
        rgb(0x6E4A3C), rgb(0x40302A),
    ),
    blades = intArrayOf(rgb(0xB4B850), rgb(0x7A8A3A)),
    grass = 0.4f,
    flowers = intArrayOf(rgb(0xF2C640), rgb(0x9A6ACC), rgb(0xD8402A), rgb(0xF6D850)),
    props = ETNA_PROPS,
    rocks = ETNA_ROCKS,
    birdUp = RAVEN_UP,
    birdFlat = RAVEN_FLAT,
    dust = rgb(0x6A625C),
    ash = true,
)

/** The crater of the summit, a quarter of the way in, on the horizon. So far away, it stays put while he walks. */
internal fun craterX(geo: SceneGeometry): Double = geo.width * 0.24

internal fun craterY(geo: SceneGeometry): Double = (geo.horizon - SUMMIT_HEIGHT).toDouble()

/** How dark it is: about 0 at noon and 0.7 at midnight. The lava glows brighter in the dark. */
private fun glow(atm: Atmosphere): Float = 1f - luminance(atm.light) / 255f

/** How high the summit stands over column [x], in pixels above the horizon. */
private fun summitHeight(geo: SceneGeometry, x: Int): Int {
    val d = abs(x + 0.5 - craterX(geo))
    if (d < CRATER_HALF) return SUMMIT_HEIGHT - 1
    if (d >= SUMMIT_HALF) return 0
    return (SUMMIT_HEIGHT * ((SUMMIT_HALF - d) / (SUMMIT_HALF - CRATER_HALF)).pow(1.35)).toInt()
}

/** The summit of Etna on the horizon: a hazy cone, lava in its crater and down its side, and smoke drifting away. */
internal fun drawSummit(px: Pixels, geo: SceneGeometry, atm: Atmosphere, clock: Double) {
    val cx = craterX(geo)
    val glow = glow(atm)
    val lit = lerpColor(atm.skyHorizon, atm.sea, 0.6f)
    val shadow = lerpColor(atm.skyHorizon, atm.sea, 0.75f)
    val lava = lerpColor(rgb(0x6A2A1E), rgb(0xF07030), glow)
    for (x in floor(cx - SUMMIT_HALF).toInt()..ceil(cx + SUMMIT_HALF).toInt()) {
        val h = summitHeight(geo, x)
        for (k in 1..h) px[x, geo.horizon - k] = if (x + 0.5 < cx) lit else shadow
        if (abs(x + 0.5 - cx) < CRATER_HALF) px[x, geo.horizon - h] = lava
    }
    var sx = cx + 1.5
    for (y in geo.horizon - SUMMIT_HEIGHT + 1 until geo.horizon) {
        val x = floor(sx).toInt()
        if (summitHeight(geo, x) >= geo.horizon - y) px[x, y] = lava
        sx += 0.55 + 0.35 * sin(y * 0.9)
    }
    val smoke = lerpColor(atm.cloudShadow, atm.sea, 0.3f)
    for (n in 0 until PUFFS) {
        val age = (clock * 0.04 + n.toDouble() / PUFFS) % 1.0
        val x = cx + 1 + age * 24 + sin(age * 6 + n) * 1.2
        val y = craterY(geo) - 2 - age * 18
        val r = 1.5 + age * 3.5
        val color = lerpColor(smoke, rgb(0xE8602A), glow * (1 - age).toFloat() * 0.35f)
        for (j in floor(y - r).toInt()..ceil(y + r).toInt()) for (i in floor(x - r).toInt()..ceil(x + r).toInt()) {
            val dx = i + 0.5 - x
            val dy = j + 0.5 - y
            if (dx * dx + dy * dy <= r * r && bayer(i, j) < (1 - age) * 0.85) px[i, j] = color
        }
    }
}

/**
 * The black lava fields below the horizon: rough, with a flow of lava winding through them and a few hot spots,
 * which glow at night. They slide by slowly as he walks.
 */
internal fun drawFields(px: Pixels, geo: SceneGeometry, atm: Atmosphere, p: Int) {
    val lava = lerpColor(rgb(0x4A1A14), rgb(0xF06A2A), glow(atm))
    val shift = floor(p * 0.04).toInt()
    for (x in 0 until px.width) {
        val u = x + shift
        val flow = 6 + (sin(u * 0.21) * 1.6 + sin(u * 0.07 + 2) * 2.2).roundToInt()
        val bottom = min(px.height, geo.surfaceY(x, p))
        for (y in geo.horizon until bottom) {
            val k = y - geo.horizon
            var c = atm.sea
            if (hash01(u.floorDiv(2), k, 27) < 0.25f) c = lerpColor(c, atm.skyHorizon, 0.12f)
            if (k == flow || (k == flow + 1 && hash01(u, 0, 33) < 0.35f) || (k >= 3 && hash01(u, k, 29) < 0.02f)) c = lava
            px.data[y * px.width + x] = lerpColor(atm.skyHorizon, c, min(1f, k / 5f + 0.35f))
        }
    }
}

/** Demeter's two torches, lit in the crater, cross the far fields at night while she looks for her daughter. */
internal fun drawTorches(px: Pixels, geo: SceneGeometry, atm: Atmosphere, clock: Double) {
    if (glow(atm) < 0.45f) return
    val span = geo.heroX - 10.0
    val x0 = (clock * 1.5 + span * 0.3) % span - 4
    val y = geo.horizon + 3
    for (k in 0..1) {
        val x = floor(x0 + k * 3).toInt()
        for (j in y - 3..y + 1) for (i in x - 2..x + 2) {
            if (abs(i - x) + abs(j - (y - 1)) <= 2 && bayer(i, j) < 0.5f) px[i, j] = lerpColor(px[i, j], rgb(0xF0A050), 0.3f)
        }
        px[x, y] = rgb(0x5A3A24)
        px[x, y - 1] = rgb(0xF07A2A)
        px[x, y - 2] = if ((floor(clock * 7).toInt() + k) % 2 == 0) rgb(0xFFE070) else rgb(0xF8B040)
    }
}

/** The fire that the crater throws up, bright by day and by night. */
internal fun drawSparks(px: Pixels, world: World) {
    for (q in world.sparks) {
        val x = floor(q.x).toInt()
        val y = floor(q.y).toInt()
        if (bayer(x, y) > q.life / q.maxLife + 0.25) continue
        px[x, y] = q.color
    }
}
