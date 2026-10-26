package dev.oblac.sisyphus.ui

import dev.oblac.sisyphus.MemoryStore
import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.START_TIME
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.bayer
import dev.oblac.sisyphus.gfx.hash01
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.scene.SceneGeometry
import dev.oblac.sisyphus.scene.SceneRenderer
import dev.oblac.sisyphus.scene.World

/** The picture of a world on the Worlds screen, [w] x [h]. Corinth is the real scene; the others are sketches until they are drawn. */
fun worldPreview(place: Place, w: Int, h: Int): Pixels = when (place) {
    Place.CORINTH -> corinth(w, h)
    Place.ETNA -> etna(w, h)
    Place.UNDERWORLD -> underworld(w, h)
}

/** The scene at noon, cut to the band from the top of the rock down to the sea. */
private fun corinth(w: Int, h: Int): Pixels {
    val geo = SceneGeometry(w, w)
    val game = Game(MemoryStore(mapOf("effort" to (0.5 - START_TIME) * DAY_LENGTH)), seed = 3)
    val scene = Pixels(w, w)
    SceneRenderer(geo).render(scene, game, World(9).apply { reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
    val top = geo.baseY - 31
    return Pixels(w, h).apply { for (y in 0 until h) for (x in 0 until w) this[x, y] = scene[x, top + y] }
}

/** Dusk over a dark volcano: a glowing crater, a stream of lava, smoke, and a slope of ash in front. */
private fun etna(w: Int, h: Int) = Pixels(w, h).apply {
    gradient(listOf(rgb(0x2A1830), rgb(0x6A2A3A), rgb(0xC8502E), rgb(0xF0A050)), h)
    val cx = w * 0.62
    val top = h / 5
    for (y in top until h) {
        val half = 5 + (y - top) * 1.4
        for (x in (cx - half).toInt()..(cx + half).toInt()) this[x, y] = if (bayer(x, y) < 0.15f) rgb(0x3A2A2E) else rgb(0x241A1E)
    }
    for (x in (cx - 4).toInt()..(cx + 4).toInt()) this[x, top] = rgb(0xF07A30)
    var lava = cx + 1
    for (y in top + 1 until h) {
        this[lava.toInt(), y] = rgb(0xE0602A)
        lava += if (hash01(y, 1, 5) < 0.5f) 0.7 else 0.2
    }
    for (k in 0..2) disc(cx - 5 - k * 8, top - 3.0 - k * 2, 3.0 + k, if (k == 0) rgb(0x8A7A86) else rgb(0x6A5C6A))
    slope(rgb(0x2E2224), rgb(0x4A3A3A))
}

/** Under a cave roof: the river Styx, Charon's boat with its lantern, and pale asphodels on the near bank. */
private fun underworld(w: Int, h: Int) = Pixels(w, h).apply {
    val water = h - 12
    gradient(listOf(rgb(0x0C0A12), rgb(0x1A1628), rgb(0x2A3A44)), water)
    for (x in 0 until w) for (y in 0..(hash01(x / 3, 2, 7) * 7).toInt()) this[x, y] = rgb(0x06050A)
    for (y in water until h) for (x in 0 until w) this[x, y] = if (bayer(x + y * 3, y) < 0.18f) rgb(0x3A6A70) else rgb(0x16242E)
    val bx = w * 3 / 10
    fillRect(bx - 7, water - 2, 15, 2, rgb(0x0A0808))
    hline(bx - 9, bx - 7, water - 3, rgb(0x0A0808))
    hline(bx + 8, bx + 10, water - 3, rgb(0x0A0808))
    fillRect(bx - 1, water - 9, 3, 7, rgb(0x0A0808))
    line(bx + 3, water - 13, bx - 4, water + 3, rgb(0x2A2020))
    this[bx + 4, water - 8] = rgb(0xF0C050)
    for ((ox, oy) in listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)) this[bx + 4 + ox, water - 8 + oy] = rgb(0x8A6A30)
    slope(rgb(0x1E1820), rgb(0x2E2630))
    for (x in w / 2 until w step 5) {
        val y = h - 2 - (x - w / 3) / 2 + (hash01(x, 4, 7) * 3).toInt()
        this[x, y] = rgb(0x4A6A3A)
        this[x, y - 1] = rgb(0xE6E0F0)
    }
}

/** The near hill, rising to the right like the one he climbs. */
private fun Pixels.slope(ground: Int, speck: Int) {
    val x0 = width / 3
    for (x in x0 until width) {
        val s = height - 1 - (x - x0) / 2
        for (y in s until height) this[x, y] = if (hash01(x, y, 3) < 0.12f) speck else ground
    }
}

/** Dithered bands of [colors] from the top down to row [to]. */
private fun Pixels.gradient(colors: List<Int>, to: Int) {
    for (y in 0 until to) {
        val t = y.toFloat() / (to - 1) * (colors.size - 1)
        val k = t.toInt().coerceAtMost(colors.size - 2)
        for (x in 0 until width) this[x, y] = if (bayer(x, y) < t - k) colors[k + 1] else colors[k]
    }
}
