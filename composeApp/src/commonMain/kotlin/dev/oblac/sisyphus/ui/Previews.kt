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

/** The picture of a world on the Worlds screen, [w] x [h]. A drawn world is cut from its scene; The Underworld is a sketch until it is drawn. */
fun worldPreview(place: Place, w: Int, h: Int): Pixels = when (place) {
    Place.CORINTH, Place.ETNA -> scene(place, w, h)
    Place.UNDERWORLD -> underworld(w, h)
}

/** The scene of [place] at noon, cut to the band from the top of the rock down to the far view. */
private fun scene(place: Place, w: Int, h: Int): Pixels {
    val geo = SceneGeometry(w, w)
    val game = Game(MemoryStore(mapOf("effort" to (0.5 - START_TIME) * DAY_LENGTH, "place" to place.id.toDouble())), seed = 3)
    val scene = Pixels(w, w)
    SceneRenderer(geo).render(scene, game, World(9).apply { reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
    val top = geo.baseY - 31
    return Pixels(w, h).apply { for (y in 0 until h) for (x in 0 until w) this[x, y] = scene[x, top + y] }
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
