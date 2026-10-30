package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.bayer
import dev.oblac.sisyphus.gfx.hash
import dev.oblac.sisyphus.gfx.hash01
import dev.oblac.sisyphus.gfx.lerpColor
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.gfx.shade
import dev.oblac.sisyphus.gfx.sprite
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/*
 * Mount Fuji: Sisyphus pushes up a side cone of the mountain. The white peak stands on the horizon over the woods,
 * and a lake below shows it upside down.
 */

/** The peak on the horizon: how high it stands, how far its foot reaches, the half of its flat top, and how far in it stands. */
private const val PEAK_HEIGHT = 27
private const val PEAK_HALF = 40.0
private const val PEAK_TOP = 2.5
private const val PEAK_X = 0.2

/** The snow comes down this far, in pixels above the horizon, and lower in the gullies. */
private const val SNOW_LINE = 15

/** The reflection in the lake is this much shorter than the peak. */
private const val REFLECTION = 0.5

/** The row under the horizon where the koi leap out of the lake. */
internal const val LAKE_KOI = 7

private const val FIREFLY_SLOT = 7

private val FUJI_PROP_COLORS = mapOf(
    'n' to rgb(0x3A6A3E),
    'N' to rgb(0x24482C),
    'm' to rgb(0x5A8A4A),
    'r' to rgb(0xB0603E),
    'R' to rgb(0x7A3E28),
    't' to rgb(0x4A3C34),
    'T' to rgb(0x2E2622),
    'a' to rgb(0xFFF2F6),
    'b' to rgb(0xF8C6D8),
    'B' to rgb(0xE896B4),
    'd' to rgb(0x5E3E34),
    'D' to rgb(0x3E2822),
    'o' to rgb(0xE0502E),
    'O' to rgb(0xA8321E),
    'q' to rgb(0xF09040),
    'c' to rgb(0x3E6A40),
    'C' to rgb(0x284A30),
    'v' to rgb(0x9CC060),
    'V' to rgb(0x6A9A40),
    'l' to rgb(0x7AAE4E),
    'w' to rgb(0xCFCFC6),
    'W' to rgb(0x9A9A92),
    'k' to rgb(0x4A4A46),
    'e' to rgb(0xC8302A),
    'E' to rgb(0x8E2420),
    'g' to rgb(0x4E7E3C),
    'G' to rgb(0x365E2C),
    'p' to rgb(0xF0609E),
)

/** A red pine, leaning, with flat pads of needles. */
val RED_PINE = Prop(
    """
    .......nmnnm....
    .....nnnnnNnnn..
    ....NNnnNNNnnNN.
    ..........rr....
    ..mnnm...rR.....
    .nnnnnnn.r......
    .NNnnNNNrR......
    ......rRr.......
    .......rR.......
    .......Rr.......
    ......rR........
    ......rRr.......
    .....rr.Rr......
    """,
    weight = 14,
    colors = FUJI_PROP_COLORS,
)

/** A black pine, as the gardeners shape them, with its crown in clouds. */
val GARDEN_PINE = Prop(
    """
    .....NnnnN.....
    ...NnnmnnnnN...
    ..NnnnnnNnnnN..
    ....NNtNNN.....
    .NnnnnnT.......
    NnnmnnnnTt.....
    .NNNNN..tT.nnN.
    .........TtnnnN
    .........tTNNN.
    .........Tt....
    ........tTt....
    .......tt.Tt...
    """,
    weight = 12,
    colors = FUJI_PROP_COLORS,
)

/** A cherry tree in bloom. */
val CHERRY = Prop(
    """
    ....abBba......
    ..abBbbaBbBa...
    .bBabBbbBabBba.
    abBbBabBbBabBbB
    .BbaBbBbBabBbB.
    ..bBb.dbBbBb...
    ....b..dD.b....
    .......dD......
    ......Dd.......
    ......dD.......
    ......dDd......
    .....dD.Dd.....
    """,
    weight = 10,
    colors = FUJI_PROP_COLORS,
)

val YOUNG_CHERRY = Prop(
    """
    ..abBa..
    .bBbBab.
    bBabBbBb
    .bBbaBb.
    ...dD...
    ...Dd...
    ...dD...
    ..dDd...
    """,
    weight = 8,
    colors = FUJI_PROP_COLORS,
)

/** A maple, red and orange. */
val MAPLE = Prop(
    """
    .....oqo......
    ...oOoqoOoq...
    ..qoOooOoqoO..
    .oOqooOqooOqo.
    .OooOqoOooOoO.
    ..oOo.toOoq...
    .....tT.......
    ......tT......
    ......Tt......
    .....tTt......
    """,
    weight = 8,
    colors = FUJI_PROP_COLORS,
)

/** A tall cedar, as they stand by the shrines. */
val CEDAR = Prop(
    """
    ...c...
    ...c...
    ..cCc..
    ..cCc..
    .ccCcc.
    ..cCc..
    .cCcCc.
    ccCccCc
    .cCcCc.
    ccCcCcc
    cCccCcC
    CcCcCcC
    ...t...
    ...t...
    ...T...
    """,
    weight = 12,
    colors = FUJI_PROP_COLORS,
)

/** Three stalks of bamboo. */
val BAMBOO = Prop(
    """
    .l.l..l.l
    ll.vl.lll
    .lvl.vl..
    ..v..v.V.
    ..V..v.v.
    ..v..V.v.
    ..v..v.v.
    ..V..v.V.
    ..v..v.v.
    ..v..V.v.
    ..v..v.v.
    .vV.vVvVv
    """,
    weight = 8,
    colors = FUJI_PROP_COLORS,
)

/** A stone lantern by the path. */
val STONE_LANTERN = Prop(
    """
    ...w...
    ..wwW..
    wwwwwWW
    .WWWWW.
    .wkkkW.
    .wwwwW.
    ..wwW..
    ..wwW..
    ..wWW..
    .wwwWW.
    wwwwwWW
    """,
    weight = 5,
    colors = FUJI_PROP_COLORS,
)

/** Jizo, who keeps travelers safe, in stone, with a red cap and a red bib. */
val JIZO = Prop(
    """
    ..eee..
    .eEeee.
    .wwwwW.
    .wkwkW.
    ..wwW..
    .eeeee.
    eeeEeee
    .wwwwW.
    .wwwwW.
    WWWWWWW
    """,
    weight = 4,
    colors = FUJI_PROP_COLORS,
)

/** An azalea in flower. */
val AZALEA = Prop(
    """
    ...gpg...
    .gpgGgpg.
    pgGgpgGgp
    .gGpgGgg.
    """,
    weight = 10,
    colors = FUJI_PROP_COLORS,
)

/** The props of Mount Fuji. */
val FUJI_PROPS = listOf(RED_PINE, GARDEN_PINE, CHERRY, YOUNG_CHERRY, MAPLE, CEDAR, BAMBOO, STONE_LANTERN, JIZO, AZALEA)

private val CRANE_COLORS = mapOf('w' to rgb(0xF4F4F0), 'k' to rgb(0x1E1E22), 'r' to rgb(0xC83A2A))

/** A crane, white with black wing tips and a red crown, its neck stretched out ahead and its legs behind. */
val CRANE_UP = sprite(CRANE_COLORS, "..kkk......\n...www.....\nkkwwwwwwwwr\n...........\n...........")
val CRANE_FLAT = sprite(CRANE_COLORS, "...........\n...........\nkkwwwwwwwwr\n...www.....\n..kkk......")

private val DRAGON_COLORS = mapOf('g' to rgb(0x3A9A6A), 'y' to rgb(0xE8C050), 'r' to rgb(0xD8402E), 'w' to rgb(0xFFF8E0))
private val DRAGON_OUTLINE = rgb(0x0E2A1E)

/** The dragon, green and gold with red fins, its long body in waves. Both frames face right. */
val DRAGON_UP = sprite(
    DRAGON_COLORS,
    """
    .......................
    .........r.............
    .........ggg.r.........
    ........ggyggg.........
    gg...rgggy.yygg...rr...
    .ggg.ggyy....ygg.rgggg.
    ..ygggy.......yyrggwggg
    ...yyy...........gggggw
    ..................yyy..
    """,
).outlined(DRAGON_OUTLINE)

val DRAGON_DOWN = sprite(
    DRAGON_COLORS,
    """
    ..................rr...
    .....r...........rgggg.
    ...ggg..........rggwggg
    .gggygg......rgg.gggggw
    ggyy.ygggr...ggy..yyy..
    ......yygg.gggy........
    ........ygggyy.........
    .........yyy...........
    .......................
    """,
).outlined(DRAGON_OUTLINE)

private val KOI_COLORS = mapOf('o' to rgb(0xF06A2A), 'w' to rgb(0xFFF4E8))

/** A koi's leap in three frames, facing right: rising, at the top, diving back in. */
private val KOI_RISE = sprite(KOI_COLORS, "...o\n..wo\n.oo.\noo..")
private val KOI_TOP = sprite(KOI_COLORS, "o.ow.\n.oooo")
private val KOI_DIVE = sprite(KOI_COLORS, "oo..\n.oo.\n..ow\n...o")

/** The rock for each tier of the shop on Mount Fuji: the first rock, the mossy stone, the daruma, the crystal sphere. */
private val FUJI_ROCKS = listOf(
    FIRST_ROCK,
    Rock(intArrayOf(rgb(0x2E3430), rgb(0x46504A), rgb(0x5E6A62), rgb(0x7A867C), rgb(0x98A296), rgb(0xB8C0B2)), ::mossMark, accent = rgb(0x5E8A34)),
    Rock(intArrayOf(rgb(0x4A0E0C), rgb(0x741814), rgb(0x9E221A), rgb(0xC43424), rgb(0xE05038), rgb(0xF4806A)), ::darumaMark, accent = rgb(0xF6E6CC)),
    Rock(intArrayOf(rgb(0x5E6E80), rgb(0x8296A8), rgb(0xA6B8C8), rgb(0xC6D6E2), rgb(0xE2ECF4), rgb(0xFFFFFF)), ::crystalMark, polished = true),
)

/** Moss grows on the stone in soft patches, and a hollow stays bare. */
private fun mossMark(u: Double, v: Double): Int {
    fun near(x: Double, y: Double, r: Double) = (u - x) * (u - x) + (v - y) * (v - y) < r * r
    if (near(-2.0, -4.5, 3.2) || near(3.5, -2.5, 2.2) || near(-5.0, 2.0, 1.8)) return ACCENT
    if (near(2.0, 4.0, 2.0)) return -1
    return 0
}

/** The daruma has a white face with two round eyes, and it tumbles as it rolls. */
private fun darumaMark(u: Double, v: Double): Int {
    fun near(x: Double, y: Double, r: Double) = (u - x) * (u - x) + (v - y) * (v - y) < r * r
    if (near(-1.6, -2.0, 0.9) || near(1.6, -2.0, 0.9)) return -6
    if (u * u / 16 + (v + 1.5) * (v + 1.5) / 9 < 1) return ACCENT
    return 0
}

/** The light bends in the crystal and gathers in a bright ring. */
private fun crystalMark(u: Double, v: Double): Int = if (abs(sqrt(u * u + v * v) - 5.0) < 0.6) 1 else 0

internal val FUJI_LOOK = Look(
    sky = FUJI_KEYS,
    sun = intArrayOf(rgb(0xF0503A), rgb(0xFFF8EC), rgb(0xC82A2A), rgb(0xFFEAB0), rgb(0xFF9A8A), rgb(0xFFF6EA)),
    ground = intArrayOf(
        rgb(0x9CC85A), rgb(0x6AA03E), rgb(0x4A7C32),
        rgb(0x463830), rgb(0x382C26), rgb(0x2A221E),
        rgb(0x7A6E66), rgb(0x342A26),
    ),
    blades = intArrayOf(rgb(0xA8D064), rgb(0x6AA03E)),
    grass = 1f,
    flowers = intArrayOf(rgb(0xF29AC8), rgb(0xE070AA), rgb(0xFAD2E4), rgb(0xC85A9A)),
    props = FUJI_PROPS,
    rocks = FUJI_ROCKS,
    birdUp = CRANE_UP,
    birdFlat = CRANE_FLAT,
    dust = rgb(0x4E4642),
    fall = Fall.RAIN,
    stone = GRANITE,
)

private fun peakX(geo: SceneGeometry): Double = geo.width * PEAK_X

/** How high the peak stands over column [x], in pixels above the horizon: steep at the top, wide at the foot. */
private fun peakHeight(geo: SceneGeometry, x: Int): Int {
    val d = abs(x + 0.5 - peakX(geo))
    if (d < PEAK_TOP) return PEAK_HEIGHT
    if (d >= PEAK_HALF) return 0
    return (PEAK_HEIGHT * ((PEAK_HALF - d) / (PEAK_HALF - PEAK_TOP)).pow(1.5)).roundToInt()
}

/** Where the snow of column [x] ends, in pixels above the horizon: it reaches down the gullies. */
private fun snowLine(x: Int): Int = SNOW_LINE + (2.2 * sin(x * 1.3) + 1.4 * sin(x * 0.47 + 1)).roundToInt()

/** How high the woods on the far shore stand over column [x]. */
private fun woods(x: Int): Int = 2 + (hash(x.floorDiv(2), 0, 85) and 1) + if (hash01(x, 1, 85) < 0.2f) 1 else 0

/** The four colors of the peak, as the light falls on it: its lit side, its shaded side, and its snow in light and in shade. */
private fun peakColors(atm: Atmosphere) = intArrayOf(
    lerpColor(atm.skyHorizon, shade(rgb(0x5A6A9A), atm.light), 0.6f),
    lerpColor(atm.skyHorizon, shade(rgb(0x3A4674), atm.light), 0.7f),
    lerpColor(atm.skyHorizon, shade(rgb(0xFFFFFF), atm.light), 0.8f),
    lerpColor(atm.skyHorizon, shade(rgb(0xC4D0EA), atm.light), 0.75f),
)

/** The color of the peak at height [k] over column [x], whose middle is [cx]. */
private fun peakAt(colors: IntArray, x: Int, k: Int, cx: Double): Int {
    val lit = x + 0.5 < cx
    return if (k > snowLine(x)) colors[if (lit) 2 else 3] else colors[if (lit) 0 else 1]
}

/** Mount Fuji on the horizon, with snow on its top, and the dark woods along the far shore. So far away, it stays put while he walks. */
internal fun drawFuji(px: Pixels, geo: SceneGeometry, atm: Atmosphere) {
    val cx = peakX(geo)
    val colors = peakColors(atm)
    for (x in floor(cx - PEAK_HALF).toInt()..ceil(cx + PEAK_HALF).toInt()) {
        for (k in 1..peakHeight(geo, x)) px[x, geo.horizon - k] = peakAt(colors, x, k, cx)
    }
    val wood = lerpColor(atm.skyHorizon, shade(rgb(0x2A4A34), atm.light), 0.75f)
    for (x in 0 until px.width) for (k in 1..woods(x)) px[x, geo.horizon - k] = wood
}

/**
 * The lake below the horizon: the woods and the peak upside down in it, broken by ripples, and the sky in the rest.
 * The ripples move, but the reflection stays put, as the peak does.
 */
internal fun drawLake(px: Pixels, geo: SceneGeometry, atm: Atmosphere, p: Int, clock: Double) {
    val cx = peakX(geo)
    val colors = peakColors(atm)
    val wood = lerpColor(atm.sea, shade(rgb(0x2A4A34), atm.light), 0.6f)
    val ripple = lerpColor(atm.sea, atm.skyHorizon, 0.55f)
    val tick = floor(clock * 1.2).toInt()
    for (x in 0 until px.width) {
        val bottom = min(px.height, geo.surfaceY(x, p))
        val shore = woods(x)
        val peak = peakHeight(geo, x)
        for (y in geo.horizon until bottom) {
            val k = y - geo.horizon
            val water = lerpColor(atm.skyHorizon, atm.sea, min(1f, k / 10f + 0.45f))
            val m = ((k - shore + 1) / REFLECTION).toInt()
            var c = when {
                k < shore -> wood
                m < peak -> lerpColor(water, peakAt(colors, x, m, cx), 0.55f)
                else -> water
            }
            if (hash01((x + tick * (1 + k % 2)).floorDiv(3), k, 86) < 0.1f) c = lerpColor(c, ripple, 0.6f)
            px.data[y * px.width + x] = c
        }
    }
}

/** The koi of the first beauty item, out of the lake. */
internal fun drawKoi(px: Pixels, world: World, atm: Atmosphere) {
    val koi = world.koi ?: return
    val f = koi.t / KOI_TIME
    val x = koi.x + koi.dir * 5 * f
    val y = world.geo.horizon + LAKE_KOI - sin(PI * f) * 4
    val frame = when {
        f < 0.35 -> KOI_RISE
        f < 0.65 -> KOI_TOP
        else -> KOI_DIVE
    }
    px.draw(frame, floor(x).toInt() - frame.width / 2, floor(y).toInt() - frame.height / 2, flipX = koi.dir < 0, light = atm.light)
}

/** At night fireflies glow in the grass by the path. They stay where they are on the hill, and each blinks in its own time. */
internal fun drawFireflies(px: Pixels, geo: SceneGeometry, atm: Atmosphere, p: Int, clock: Double) {
    if (atm.darkness < 0.45f) return
    val from = geo.worldX(0, p).floorDiv(FIREFLY_SLOT)
    val to = geo.worldX(px.width, p).floorDiv(FIREFLY_SLOT)
    for (slot in from..to) {
        if (hash01(slot, 0, 88) < 0.2f) continue
        val blink = sin(clock * (1.2 + hash01(slot, 1, 88)) + hash01(slot, 2, 88) * 2 * PI)
        if (blink < -0.5) continue
        val wx = slot * FIREFLY_SLOT + (hash01(slot, 3, 88) * FIREFLY_SLOT).toInt() + floor(sin(clock * 0.4 + slot) * 2).toInt()
        val x = wx - p + geo.heroX
        val y = geo.surfaceY(x, p) - 3 - floor((sin(clock * 0.6 + slot * 1.7) + 1) * 3.5).toInt()
        if (blink > 0.2) {
            for (j in y - 1..y + 1) for (i in x - 1..x + 1) px[i, j] = lerpColor(px[i, j], rgb(0xC8F070), if (i == x || j == y) 0.45f else 0.2f)
        }
        px[x, y] = if (blink > 0.2) rgb(0xF4FFA0) else rgb(0xA8C860)
    }
}
