package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.scene.SceneGeometry
import dev.oblac.sisyphus.scene.SceneRenderer
import dev.oblac.sisyphus.scene.World
import kotlin.test.Test
import kotlin.test.assertEquals

class RockLookTest {
    private fun rockPixels(tier: Int): List<Int> {
        val geo = SceneGeometry(64, 64)
        val game = Game(MemoryStore(mapOf("shop.rock" to tier.toDouble(), "effort" to 300.0)), seed = 3)
        val px = Pixels(64, 64)
        SceneRenderer(geo).render(px, game, World(9).apply { reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
        val cx = (geo.heroX + geo.rockDx).toInt()
        val cy = (geo.baseY + geo.rockDy).toInt()
        val r = geo.rockR.toInt()
        return (cy - r..cy + r).flatMap { y -> (cx - r..cx + r).map { x -> px[x, y] } }
    }

    @Test
    fun eachRockTierLooksDifferent() {
        assertEquals(4, (0..3).map { rockPixels(it) }.toSet().size)
    }
}
