package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.START_TIME
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.scene.SceneGeometry
import dev.oblac.sisyphus.scene.SceneRenderer
import dev.oblac.sisyphus.scene.World
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test

/** Renders a few moments of the game to build/snapshots, to look at the pixel art. */
class SnapshotTest {
    private val out = File("build/snapshots").apply { mkdirs() }

    private fun effortAt(day: Int, time: Double) = ((day - 1) + time - START_TIME) * DAY_LENGTH

    private fun dayWith(weather: Weather) = (2..500).first { Weather.of(it) == weather }

    private fun screen(day: Int, time: Double, position: Double = 0.0): Screen {
        val game = Game(MemoryStore(mapOf("effort" to effortAt(day, time), "position" to position)), seed = 3)
        return Screen(game, seed = 5).apply { resize(1179, 2556, 177, 102) }
    }

    private fun Screen.push(frames: Int, step: Double = 0.12) {
        val l = layout!!
        val cx = (l.knobX + 0.5f) * scale + offsetX
        val cy = (l.knobY + 0.5f) * scale + offsetY
        val r = l.knobR * 0.7f * scale
        var a = 0.0
        press(cx + r, cy)
        repeat(frames) {
            a += step
            move(cx + r * cos(a).toFloat(), cy + r * sin(a).toFloat())
            update(1 / 60.0)
        }
        release()
    }

    private fun Screen.idle(seconds: Double) = repeat((seconds * 60).toInt()) { update(1 / 60.0) }

    private fun write(px: Pixels, name: String, scale: Int) {
        val img = BufferedImage(px.width * scale, px.height * scale, BufferedImage.TYPE_INT_RGB)
        for (y in 0 until img.height) for (x in 0 until img.width) img.setRGB(x, y, px[x / scale, y / scale])
        ImageIO.write(img, "png", File(out, "$name.png"))
    }

    @Test
    fun moments() {
        listOf(
            Triple("1-morning", 1, 0.30),
            Triple("2-noon-clear", dayWith(Weather.CLEAR), 0.5),
            Triple("3-sunset-windy", dayWith(Weather.WINDY), 0.745),
            Triple("4-night", dayWith(Weather.FAIR), 0.95),
            Triple("5-rain", dayWith(Weather.RAIN), 0.45),
            Triple("6-dawn-cloudy", dayWith(Weather.CLOUDY), 0.262),
        ).forEach { (name, day, time) ->
            val s = screen(day, time)
            s.idle(3.0)
            s.push(150)
            write(s.render(), name, 3)
        }
    }

    @Test
    fun windyNoonWithBirds() {
        val s = screen(dayWith(Weather.WINDY), 0.5)
        repeat(60) {
            s.idle(1.0)
            if (s.world.birds.size > 1 && s.world.birds.all { it.x in 20.0..110.0 }) return@repeat
        }
        write(s.render(), "7-windy-birds", 3)
    }

    @Test
    fun firstLaunch() {
        val s = Screen(Game(MemoryStore(), seed = 3), seed = 5).apply { resize(1179, 2556, 177, 102) }
        s.idle(3.25)
        write(s.render(), "0-first-launch", 3)
    }

    @Test
    fun appIcon() {
        val geo = SceneGeometry(64, 64)
        val game = Game(MemoryStore(mapOf("effort" to effortAt(1, 0.705), "position" to 3.0)), seed = 3)
        val world = World(9).apply { reset(geo, Weather.CLEAR) }
        val icon = Pixels(64, 64)
        SceneRenderer(geo).render(icon, game, world, banner = 0.0, hud = false)
        write(icon, "icon-1024", 16)
    }

    @Test
    fun heroSheet() {
        val frames = (0 until 12).map { p ->
            val s = screen(1, 0.4, position = p.toDouble())
            s.push(1, step = 0.0001)
            val l = s.layout!!
            val px = s.render()
            val x0 = l.scene.x + l.scene.w / 2 - 30
            val y0 = l.scene.y + (l.scene.h * 0.70).toInt() - 36
            Pixels(52, 44).also { crop ->
                for (y in 0 until crop.height) for (x in 0 until crop.width) crop[x, y] = px[x0 + x, y0 + y]
            }
        }
        val sheet = Pixels(52 * 6, 44 * 2)
        frames.forEachIndexed { i, f -> sheet.draw(f, (i % 6) * 52, (i / 6) * 44) }
        write(sheet, "hero-sheet", 6)
    }
}
