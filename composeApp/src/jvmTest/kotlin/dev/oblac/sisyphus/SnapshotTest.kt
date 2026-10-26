package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.ITEMS
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.START_TIME
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.audio.SAMPLE_RATE
import dev.oblac.sisyphus.audio.loadClick
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.scene.Ending
import dev.oblac.sisyphus.scene.PROPS
import dev.oblac.sisyphus.scene.SceneGeometry
import dev.oblac.sisyphus.scene.SceneRenderer
import dev.oblac.sisyphus.scene.World
import dev.oblac.sisyphus.ui.Rect
import dev.oblac.sisyphus.ui.itemIcon
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlinx.coroutines.runBlocking
import kotlin.math.abs
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

    private fun write(px: Pixels, name: String, scale: Int, width: Int = px.width * scale, height: Int = px.height * scale) {
        val img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
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
    fun agora() {
        val store = MemoryStore(mapOf("effort" to effortAt(3, 0.4), "obols" to 3.0, "shop.rock" to 1.0, "shop.alone" to 3.0))
        val s = Screen(Game(store, seed = 3), seed = 5, dev = true).apply { resize(1179, 2556, 177, 102) }
        s.idle(1.0)
        val l = s.layout!!
        s.press((l.coinIcon.x + 2f) * s.scale + s.offsetX, (l.coinIcon.y + 2f) * s.scale + s.offsetY)
        write(s.render(), "8-agora", 3)
        s.press((l.agoraTitle.x + 2f) * s.scale + s.offsetX, (l.agoraTitle.y + 2f) * s.scale + s.offsetY)
        write(s.render(), "8-dev", 3)
    }

    /** The offer for the river stone, on a big phone and on the narrowest one. */
    @Test
    fun offer() {
        for ((name, size) in listOf("8-offer" to listOf(1179, 2556, 177, 102), "8-offer-small" to listOf(1080, 2340, 144, 102))) {
            val s = Screen(Game(MemoryStore(mapOf("effort" to effortAt(3, 0.4), "obols" to 12.0)), seed = 3), seed = 5)
            s.resize(size[0], size[1], size[2], size[3])
            val l = s.layout!!
            fun tap(r: Rect) = s.press((r.x + r.w / 2f) * s.scale + s.offsetX, (r.y + r.h / 2f) * s.scale + s.offsetY)
            tap(l.coinIcon)
            tap(l.agoraRow(0))
            write(s.render(), name, 3)
        }
    }

    /** Merope and Amalthea, the first sidekick and the first god, in the display's bottom-right corner. */
    @Test
    fun helpers() {
        val store = MemoryStore(mapOf("effort" to effortAt(2, 0.45), "shop.beside" to 1.0, "shop.alone" to 1.0))
        val s = Screen(Game(store, seed = 3), seed = 5).apply { resize(1179, 2556, 177, 102) }
        s.idle(1.0)
        write(s.render(), "9-helpers", 3)
    }

    /** The top: the flat top ahead, standing on it, the rock rolling down, the black screen, and the first morning again. */
    @Test
    fun summit() {
        val bought = Line.entries.associate { "shop." + it.id to 3.0 }
        val store = MemoryStore(bought + mapOf("effort" to effortAt(40, 0.42), "position" to 37_740.0, "summit" to 37_800.0))
        val s = Screen(Game(store, seed = 3), seed = 5).apply { resize(1179, 2556, 177, 102) }
        s.idle(0.5)
        write(s.render(), "10-near-top", 3)
        while (s.ending == null) s.push(10)
        s.idle(1.0)
        write(s.render(), "10-top", 3)
        s.idle(2.3)
        write(s.render(), "10-rolling", 3)
        s.idle(Ending.BLACK_AT + Ending.READ - 3.0)
        write(s.render(), "10-black", 3)
        s.press(s.offsetX + 10f, s.offsetY + 10f)
        s.idle(2.0)
        write(s.render(), "10-again", 3)
    }

    /** The Worlds screen, and its question before going to Mount Etna, on a big phone and on the narrowest one. */
    @Test
    fun worlds() {
        for ((name, size) in listOf("11-worlds" to listOf(1179, 2556, 177, 102), "11-worlds-small" to listOf(1080, 2340, 144, 102))) {
            val s = Screen(Game(MemoryStore(mapOf("effort" to effortAt(3, 0.4))), seed = 3), seed = 5)
            s.resize(size[0], size[1], size[2], size[3])
            val l = s.layout!!
            fun tap(r: Rect) = s.press((r.x + r.w / 2f) * s.scale + s.offsetX, (r.y + r.h / 2f) * s.scale + s.offsetY)
            tap(l.dayLabel(s.game.day))
            write(s.render(), name, 3)
            tap(l.worldRow(1))
            write(s.render(), name.replace("worlds", "move"), 3)
        }
    }

    /** The pictures of all 15 items, a line per row, at the size the offer shows them. */
    @Test
    fun iconSheet() {
        val sheet = Pixels(3 * 36 + 2, 5 * 36 + 2)
        sheet.clear(rgb(0x17110D))
        for (item in ITEMS) sheet.drawScaled(itemIcon(item), 2 + (item.tier - 1) * 36, 2 + item.line.ordinal * 36, 2)
        write(sheet, "icons", 4)
    }

    /** The help over the main screen, on a big phone and on the narrowest one. */
    @Test
    fun help() {
        for ((name, size) in listOf("0-help" to listOf(1179, 2556, 177, 102), "0-help-small" to listOf(1080, 2340, 144, 102))) {
            val s = Screen(Game(MemoryStore(mapOf("effort" to effortAt(2, 0.45), "obols" to 12.0)), seed = 3), seed = 5)
            s.resize(size[0], size[1], size[2], size[3])
            s.idle(1.0)
            val l = s.layout!!
            s.press((l.helpIcon.x + 2f) * s.scale + s.offsetX, (l.helpIcon.y + 2f) * s.scale + s.offsetY)
            write(s.render(), name, 3)
        }
    }

    /** A slow turn of the knob, to listen to: afplay composeApp/build/snapshots/clicks.wav */
    @Test
    fun clicks() {
        val wave = runBlocking { loadClick() }
        val gap = SAMPLE_RATE / 14
        val sound = FloatArray(gap * 11 + wave.size)
        for (k in 0 until 12) wave.forEachIndexed { i, s -> sound[k * gap + i] += s }
        val pcm = pcm16(sound)
        val format = AudioFormat(SAMPLE_RATE.toFloat(), 16, 1, true, false)
        AudioInputStream(ByteArrayInputStream(pcm), format, sound.size.toLong()).use {
            AudioSystem.write(it, AudioFileFormat.Type.WAVE, File(out, "clicks.wav"))
        }
        val plot = Pixels(wave.size / 2, 101)
        plot.clear(rgb(0x101010))
        plot.hline(0, plot.width - 1, 50, rgb(0x404040))
        for (x in 0 until plot.width) {
            val v = maxOf(abs(wave[2 * x]), abs(wave[2 * x + 1]))
            plot.vline(x, 50 - (v * 50).toInt(), 50 + (v * 50).toInt(), rgb(0xF29A62))
        }
        write(plot, "click-wave", 2)
    }

    @Test
    fun propSheet() {
        val gap = 4
        val width = PROPS.sumOf { it.sprite.width + gap } + gap
        val height = PROPS.maxOf { it.sprite.height } + 6
        val sheet = Pixels(width, height)
        sheet.clear(rgb(0x9CC8EE))
        sheet.fillRect(0, height - 4, width, 4, rgb(0x82A048))
        var x = gap
        for (prop in PROPS) {
            sheet.draw(prop.sprite, x, height - 4 - prop.sprite.height + 1)
            x += prop.sprite.width + gap
        }
        write(sheet, "props", 6)
    }

    @Test
    fun appIcon() {
        val geo = SceneGeometry(64, 64)
        val game = Game(MemoryStore(mapOf("effort" to effortAt(1, 0.705), "position" to 962.0)), seed = 3)
        val world = World(9).apply { reset(geo, Weather.CLEAR) }
        val icon = Pixels(64, 64)
        SceneRenderer(geo).render(icon, game, world, banner = 0.0, hud = false)
        write(icon, "icon-1024", 16)
    }

    /** The Google Play feature graphic: a wide view of the hill, 1024 x 500. */
    @Test
    fun featureGraphic() {
        val geo = SceneGeometry(205, 100)
        val game = Game(MemoryStore(mapOf("effort" to effortAt(1, 0.745), "position" to 900.0)), seed = 3)
        val world = World(9).apply { reset(geo, Weather.FAIR) }
        val px = Pixels(205, 100)
        SceneRenderer(geo).render(px, game, world, banner = 0.0, hud = false)
        write(px, "feature-graphic", 5, width = 1024)
    }

    /** The beauty items of the shop: a dolphin mid-leap, Pegasus flying by, the Pleiades at night. */
    @Test
    fun beauty() {
        fun shot(time: Double, name: String, ready: (Screen) -> Boolean) {
            val store = MemoryStore(mapOf("effort" to effortAt(1, time), "shop.beauty" to 3.0))
            val s = Screen(Game(store, seed = 3), seed = 5).apply { resize(1179, 2556, 177, 102) }
            var frames = 0
            while (!ready(s) && frames++ < 3000) s.update(1 / 20.0)
            write(s.render(), name, 3)
        }
        shot(0.32, "9-dolphins") { s -> s.world.dolphins.any { it.t in 0.5..0.7 } }
        shot(0.5, "9-pegasus") { s -> s.world.pegasus?.let { it.x in 40.0..90.0 } == true }
        shot(0.95, "9-pleiades") { s -> s.game.clock > 2.0 }
    }

    /** The four rocks of the shop, side by side: today's rock, the river stone, the pumice, the sphere of Daedalus. */
    @Test
    fun rockSheet() {
        val geo = SceneGeometry(64, 64)
        val sheet = Pixels(64 * 4, 64)
        for (tier in 0..3) {
            val game = Game(MemoryStore(mapOf("effort" to effortAt(1, 0.45), "position" to 962.0, "shop.rock" to tier.toDouble())), seed = 3)
            val px = Pixels(64, 64)
            SceneRenderer(geo).render(px, game, World(9).apply { reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
            sheet.draw(px, tier * 64, 0)
        }
        write(sheet, "rocks", 6)
    }

    @Test
    fun heroSheet() {
        val frames = (0 until 12).map { p ->
            val s = screen(1, 0.4, position = p.toDouble())
            s.push(1, step = 0.0001)
            val l = s.layout!!
            val px = s.render()
            val x0 = l.scene.x + l.scene.w / 2 - 30
            val y0 = l.scene.y + SceneGeometry(l.scene.w, l.scene.h).baseY - 36
            Pixels(52, 44).also { crop ->
                for (y in 0 until crop.height) for (x in 0 until crop.width) crop[x, y] = px[x0 + x, y0 + y]
            }
        }
        val sheet = Pixels(52 * 6, 44 * 2)
        frames.forEachIndexed { i, f -> sheet.draw(f, (i % 6) * 52, (i / 6) * 44) }
        write(sheet, "hero-sheet", 6)
    }
}
