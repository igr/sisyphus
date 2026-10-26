package dev.oblac.sisyphus.ui

import dev.oblac.sisyphus.MemoryStore
import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.START_TIME
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.scene.SceneGeometry
import dev.oblac.sisyphus.scene.SceneRenderer
import dev.oblac.sisyphus.scene.World

/** The picture of a world on the Worlds screen, [w] x [h]: its scene at noon, cut to the band from the top of the rock down to the far view. */
fun worldPreview(place: Place, w: Int, h: Int): Pixels {
    val geo = SceneGeometry(w, w)
    val game = Game(MemoryStore(mapOf("effort" to (0.5 - START_TIME) * DAY_LENGTH, "place" to place.id.toDouble())), seed = 3)
    val scene = Pixels(w, w)
    SceneRenderer(geo).render(scene, game, World(9).apply { reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
    val top = geo.baseY - 31
    return Pixels(w, h).apply { for (y in 0 until h) for (x in 0 until w) this[x, y] = scene[x, top + y] }
}
