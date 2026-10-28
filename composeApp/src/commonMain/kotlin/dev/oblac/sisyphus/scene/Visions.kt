package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.game.God
import dev.oblac.sisyphus.game.Vision
import dev.oblac.sisyphus.gfx.PixelFont
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.TRANSPARENT
import dev.oblac.sisyphus.gfx.bayer
import dev.oblac.sisyphus.gfx.lerpColor
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.gfx.sprite
import kotlin.math.pow
import kotlin.math.sqrt

/** The dark that a vision brings: the whole scene moves toward it. */
private val VISION_DARK = rgb(0x101828)
/** The light around a god in a vision, and along its edge. */
private val VISION_LIGHT = rgb(0xFFF2C8)
/** The answers of a vision, in the bottom corners of the scene. */
internal val ANSWER = rgb(0xF1E4C8)
private val ANSWER_OUTLINE = rgb(0x0A0705)
private val ANSWER_MARK = rgb(0xF29A62)

/** A god shows twice the size of its art. */
private const val VISION_SCALE = 2

private val SPHINX = sprite(
    mapOf(
        'h' to rgb(0x5E3C1E), 'f' to rgb(0xE8C08A), 'e' to rgb(0x3A2412), 'w' to rgb(0xF0CE8C), 'W' to rgb(0xD2A462),
        'b' to rgb(0xD2A462), 's' to rgb(0xAE7C42),
    ),
    """
    ....hhhh.................
    ...hhhhhh.......www......
    ...hffffh......wwwww.....
    ...hfeffh.....wwwWww.....
    ...hffffh....wwwwWww.....
    ....fffh....wwwwWww......
    .....ff....wwwwwwww......
    ....bbbbbbbbbbbbbbbbbb...
    ...bbbbbbbbbbbbbbbbbbbb..
    ...bbbbbbbbbbbbbbbbbbbbb.
    ...bbbbbbbbbbbbbbbbbbbbbs
    ..bbbbsbbbbbbbbbbbbbbbb.s
    .bbbb.sbbbbbbbbbbbbbbb..s
    bbbbb..sssssssssbbbbbb.ss
    sssss..........ssssss....
    """,
)

private val HERMES = sprite(
    mapOf(
        'W' to rgb(0xF4ECDA), 'p' to rgb(0xB08850), 'h' to rgb(0x3C2618), 'f' to rgb(0xE0A070), 'e' to rgb(0x2A1A10),
        'c' to rgb(0xB4482A), 'C' to rgb(0x7A2E1A), 'r' to rgb(0xF4ECDA), 'y' to rgb(0xE0B040), 'Y' to rgb(0xF4ECDA),
    ),
    """
    .W.........ppp.........W.
    WWW......ppppppp......WWW
    .WWWWppppppppppppppWWWW..
    ........hffffffh.....Y.Y.
    ........hfefffeh......y..
    ........hffffffh.....yyy.
    .........ffffff.......y..
    ..........ffff........y..
    .......cccrrrrccc.....y..
    ......ccccrrrrcccc...fy..
    .....cccccrrrrccccccffy..
    .....ccccCrrrrCccc....y..
    .....cccCC.rr.CCccc...y..
    ......ccC..rr..Ccc....y..
    .......C...rr...C.....y..
    """,
)

private val HECATE = sprite(
    mapOf(
        'o' to rgb(0xF29A62), 'O' to rgb(0xF0CE8C), 't' to rgb(0x6A4024), 'h' to rgb(0x2A1A10), 'f' to rgb(0xE2B088),
        'e' to rgb(0x2A1A10), 'g' to rgb(0x5A4A8A), 'G' to rgb(0x3E3266),
    ),
    """
    .o.....................o.
    oOo........hhh........oOo
    .o........hhhhh........o.
    .t.......hhffffh.......t.
    .t.......hfeffeh.......t.
    .t........ffffff.......t.
    .t.........ffff........t.
    ff.......ggggggg.......ff
    .ffgg...ggggggggg...ggff.
    ...gggggggggggggggggggg..
    ........gggggggggg.......
    ........ggGggggGgg.......
    .......gggGggggGggg......
    .......ggGgggggGggg......
    ......gggGggggggGggg.....
    """,
)

private fun art(god: God) = when (god) {
    God.SPHINX -> SPHINX
    God.HERMES -> HERMES
    God.HECATE -> HECATE
}

/**
 * A god comes over the scene as a vision: the scene darkens, and the god glows high in the sky.
 * While it asks, its two answers stand in the bottom corners, each with a mark that points to its side.
 */
fun drawVision(px: Pixels, vision: Vision) {
    val light = vision.light.toFloat()
    if (light <= 0f) return
    for (i in px.data.indices) px.data[i] = lerpColor(px.data[i], VISION_DARK, 0.6f * light)

    val glow = art(vision.question.god).outlined(VISION_LIGHT)
    val w = glow.width * VISION_SCALE
    val h = glow.height * VISION_SCALE
    val x0 = (px.width - w) / 2
    val y0 = (px.height * 0.3).toInt() - h / 2
    val cx = x0 + w / 2.0
    val cy = y0 + h / 2.0
    val r = w * 0.75
    for (y in 0 until px.height) for (x in 0 until px.width) {
        val dx = x - cx
        val dy = (y - cy) * 1.3
        val d = sqrt(dx * dx + dy * dy) / r
        if (d < 1 && bayer(x, y) > d.pow(0.7) + 0.15) px[x, y] = lerpColor(px[x, y], VISION_LIGHT, 0.55f * light)
    }
    for (sy in 0 until glow.height) for (sx in 0 until glow.width) {
        val c = glow[sx, sy]
        if (c == TRANSPARENT) continue
        val k = if (c == VISION_LIGHT) light else 0.8f * light
        for (j in 0 until VISION_SCALE) for (i in 0 until VISION_SCALE) {
            val x = x0 + sx * VISION_SCALE + i
            val y = y0 + sy * VISION_SCALE + j
            px[x, y] = lerpColor(px[x, y], c, k)
        }
    }

    if (vision.phase != Vision.Phase.ASKING) return
    val wide = (px.width - 12) / 2 - 8
    answer(px, vision.left, wide, left = true)
    answer(px, vision.right, wide, left = false)
}

/** An answer in its bottom corner, 6 pixels from the edges, in [wide] pixels of at most 2 lines. */
private fun answer(px: Pixels, text: String, wide: Int, left: Boolean) {
    val lines = PixelFont.wrap(text, wide).take(2)
    if (lines.isEmpty()) return
    val top = px.height - 6 - PixelFont.CAP_HEIGHT - (lines.size - 1) * PixelFont.LINE_HEIGHT
    lines.forEachIndexed { i, line ->
        val x = if (left) 12 else px.width - 12 - PixelFont.width(line)
        PixelFont.drawOutlined(px, line, x, top + i * PixelFont.LINE_HEIGHT, ANSWER, ANSWER_OUTLINE)
    }
    val tip = if (left) 6 else px.width - 7
    for (k in 0..3) for (d in -k..k) px[if (left) tip + k else tip - k, top + 3 + d] = ANSWER_MARK
}
