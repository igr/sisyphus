package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Config
import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.godsOf
import dev.oblac.sisyphus.game.God
import dev.oblac.sisyphus.game.PX_PER_TURN
import dev.oblac.sisyphus.game.CLIMB
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.items
import dev.oblac.sisyphus.game.JOKERS
import dev.oblac.sisyphus.game.START_TIME
import dev.oblac.sisyphus.game.TALES
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.audio.SAMPLE_RATE
import dev.oblac.sisyphus.audio.loadClick
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.gfx.WHITE
import dev.oblac.sisyphus.scene.CAIRO_PROPS
import dev.oblac.sisyphus.scene.ETNA_PROPS
import dev.oblac.sisyphus.scene.Ending
import dev.oblac.sisyphus.scene.JOKER_SPRITES
import dev.oblac.sisyphus.scene.atmosphere
import dev.oblac.sisyphus.scene.PROPS
import dev.oblac.sisyphus.scene.SceneGeometry
import dev.oblac.sisyphus.scene.SceneRenderer
import dev.oblac.sisyphus.scene.UNDERWORLD_PROPS
import dev.oblac.sisyphus.scene.World
import dev.oblac.sisyphus.scene.lookOf
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

    private fun screen(day: Int, time: Double, position: Double = 0.0, place: Place = Place.CORINTH): Screen {
        val game = Game(MemoryStore(mapOf("effort" to effortAt(day, time), "position" to position, "place" to place.id.toDouble())), seed = 3)
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

    /** Six moments of the day and the weather in each world. The images of the worlds after Corinth start with their names. */
    @Test
    fun moments() {
        for ((place, prefix) in listOf(Place.CORINTH to "", Place.ETNA to "etna-", Place.UNDERWORLD to "underworld-", Place.CAIRO to "cairo-")) listOf(
            Triple("1-morning", 1, 0.30),
            Triple("2-noon-clear", dayWith(Weather.CLEAR), 0.5),
            Triple("3-sunset-windy", dayWith(Weather.WINDY), 0.745),
            Triple("4-night", dayWith(Weather.FAIR), 0.95),
            Triple("5-rain", dayWith(Weather.RAIN), 0.45),
            Triple("6-dawn-cloudy", dayWith(Weather.CLOUDY), 0.262),
        ).forEach { (name, day, time) ->
            val s = screen(day, time, place = place)
            s.idle(3.0)
            s.push(150)
            write(s.render(), prefix + name, 3)
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

    /**
     * The 9th stone ahead of the rock, with its part in a popup over it: in Corinth on both phones, and in each other world with
     * another tale. Then the stone of each world, in a row, with the relief of each tale.
     */
    @Test
    fun steles() {
        val big = listOf(1179, 2556, 177, 102)
        val shots = listOf(
            Triple("12-stele", big, Place.CORINTH), Triple("12-stele-small", listOf(1080, 2340, 144, 102), Place.CORINTH),
            Triple("12-stele-etna", big, Place.ETNA), Triple("12-stele-underworld", big, Place.UNDERWORLD), Triple("12-stele-cairo", big, Place.CAIRO),
        )
        for ((name, size, place) in shots) {
            val store = MemoryStore(
                mapOf(
                    "effort" to effortAt(3, 0.5), "position" to 1000.0, "shop.alone" to 3.0, "shop.rock" to 3.0, "shop.beside" to 3.0,
                    "stelesTold" to 8.0, "nextStele" to 1070.0, "nextVision" to 600.0, "place" to place.id.toDouble(), "tale" to place.id.toDouble(),
                ),
            )
            val s = Screen(Game(store, seed = 3), seed = 5).apply { resize(size[0], size[1], size[2], size[3]) }
            s.push(144)
            s.idle(4.0)
            write(s.render(), name, 3)
        }
        val sheet = Pixels(TALES.size * 12, Place.entries.size * 18).apply { clear(rgb(0x8AA65A)) }
        for (place in Place.entries) for (tale in TALES) {
            val stone = lookOf(place).stone
            sheet.draw(stone.slab, tale.id * 12 + 1, place.ordinal * 18 + 1)
            sheet.draw(stone.reliefs[tale.id], tale.id * 12 + 2, place.ordinal * 18 + 2)
        }
        write(sheet, "steles", 6)
    }

    /**
     * Diogenes ahead of the rock, with the first text of his first line in a bubble over him, on both phones; then behind him,
     * with the second text. Then the 10 jokers in a row, as they are and in the light of the night.
     */
    @Test
    fun jokers() {
        val big = listOf(1179, 2556, 177, 102)
        val shots = listOf(
            Triple("13-joker", big, 1080.0), Triple("13-joker-small", listOf(1080, 2340, 144, 102), 1080.0), Triple("13-joker-behind", big, 990.0),
        )
        for ((name, size, at) in shots) {
            val store = MemoryStore(
                mapOf(
                    "effort" to effortAt(1, 0.5), "position" to 1000.0, "shop.alone" to 3.0, "shop.rock" to 3.0, "shop.beside" to 3.0,
                    "nextJoker" to at, "joker" to 0.0, "nextVision" to 600.0,
                ),
            )
            val s = Screen(Game(store, seed = 3), seed = 5).apply { resize(size[0], size[1], size[2], size[3]) }
            s.push(144)
            s.idle(4.0)
            write(s.render(), name, 3)
        }
        val night = atmosphere(0.0, 0f).light
        val sheet = Pixels(JOKERS.size * 18, 2 * 28).apply { clear(rgb(0x8AA65A)) }
        for ((row, light) in listOf(WHITE, night).withIndex()) for (joker in JOKERS) {
            val sprite = JOKER_SPRITES[joker.id]
            sheet.draw(sprite, joker.id * 18 + (18 - sprite.width) / 2, row * 28 + 26 - sprite.height, light = light)
        }
        write(sheet, "jokers", 6)
    }

    /** A herm of the final climb by the path ahead, in each world, at noon. */
    @Test
    fun herms() {
        val bought = Line.entries.associate { "shop." + it.id to 3.0 }
        for (place in Place.entries) {
            val position = 20_000.0
            val summit = position + 40 - Config.HERM_TURNS * Config.SUN_CAP * PX_PER_TURN + CLIMB
            val store = MemoryStore(bought + mapOf("effort" to effortAt(30, 0.5), "position" to position, "summit" to summit, "place" to place.id.toDouble()))
            val s = Screen(Game(store, seed = 3), seed = 5).apply { resize(1179, 2556, 177, 102) }
            val prefix = if (place == Place.CORINTH) "" else place.name.lowercase() + "-"
            write(s.render(), "${prefix}10-herm", 3)
        }
    }

    /** A new obol: the coin of the count glints, and "+1" rises under the count and fades. */
    @Test
    fun newObol() {
        val s = Screen(Game(MemoryStore(mapOf("obols" to 2.5, "nextObol" to 300.0)), seed = 3), seed = 5).apply { resize(1179, 2556, 177, 102) }
        s.game.nearTheNextObol()
        s.idle(1.0)
        s.game.knob.grab(0.0)
        s.game.knob.drag(1.0)
        s.update(1 / 60.0)
        write(s.render(), "12-obol-1", 3)
        s.update(0.4)
        write(s.render(), "12-obol-2", 3)
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
        val store = MemoryStore(mapOf("effort" to effortAt(2, 0.45), "shop.beside" to 1.0, "shop.alone" to 1.0, "shop.blessing" to 1.0, "obols" to 3.6))
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
        s.idle(0.8)
        write(s.render(), "10-standing", 3)
        s.idle(1.5)
        write(s.render(), "10-rolling", 3)
        s.idle(Ending.BLACK_AT + Ending.READ - 3.0)
        write(s.render(), "10-black", 3)
        s.press(s.offsetX + 10f, s.offsetY + 10f)
        write(s.render(), "10-worlds", 3)
        s.back()
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
            s.release()
            write(s.render(), name.replace("worlds", "move"), 3)
        }
    }

    /** The Labors tab with none done on the narrowest phone, and with some done on a big one; and the dot after the day. */
    @Test
    fun labors() {
        val some = mapOf("climbs" to 1.0, "turnsPushed" to 12_345.0, "rightAnswers" to 12.0, "daysBefore" to 150.0, "laborsSeen" to 20.0)
        for ((name, size, values) in listOf(
            Triple("12-labors-small", listOf(1080, 2340, 144, 102), emptyMap()),
            Triple("12-labors", listOf(1179, 2556, 177, 102), some),
        )) {
            val s = Screen(Game(MemoryStore(values + ("effort" to effortAt(3, 0.4))), seed = 3), seed = 5)
            s.resize(size[0], size[1], size[2], size[3])
            val l = s.layout!!
            fun tap(r: Rect) = s.press((r.x + r.w / 2f) * s.scale + s.offsetX, (r.y + r.h / 2f) * s.scale + s.offsetY)
            tap(l.dayLabel(s.game.day))
            tap(l.laborsTab)
            write(s.render(), name, 3)
        }
        val s = Screen(Game(MemoryStore(mapOf("climbs" to 1.0, "effort" to effortAt(3, 0.4))), seed = 3), seed = 5)
        s.resize(1179, 2556, 177, 102)
        write(s.render(), "12-day-dot", 3)
    }

    /** The Jokers tab with four jokers met, one of them new, on both phones; then the open card of Sherlock Holmes. */
    @Test
    fun jokerCards() {
        val met = mapOf("met.0" to 12.0, "met.1" to 5.0, "met.3" to 1.0, "met.6" to 2.0, "jokersSeen" to 11.0, "effort" to effortAt(3, 0.4))
        for ((name, size) in listOf("14-jokers-small" to listOf(1080, 2340, 144, 102), "14-jokers" to listOf(1179, 2556, 177, 102))) {
            val s = Screen(Game(MemoryStore(met), seed = 3), seed = 5)
            s.resize(size[0], size[1], size[2], size[3])
            val l = s.layout!!
            fun tap(r: Rect) = s.press((r.x + r.w / 2f) * s.scale + s.offsetX, (r.y + r.h / 2f) * s.scale + s.offsetY)
            write(s.render(), "$name-dot", 3)
            tap(l.dayLabel(s.game.day))
            write(s.render(), name, 3)
            if (name == "14-jokers") {
                tap(l.jokerCards[6])
                write(s.render(), "14-card", 3)
            }
        }
    }

    /** Before a world is bought: the locks, the prices and Restore, then the question before buying Mount Etna, on both phones. */
    @Test
    fun buyWorlds() {
        val notBought = object : Billing {
            override fun owns(place: Place) = place.product == null
            override fun price(place: Place) = "\$${Config.WORLD_PRICE}"
            override val userId = "\$RCAnonymousID:9c4e2b7a51d84f0e8a3b6c2d1f7e3f9a"
            override fun buy(place: Place, done: (Boolean) -> Unit) {}
            override fun restore(done: () -> Unit) {}
        }
        for ((name, size) in listOf("11-locked" to listOf(1179, 2556, 177, 102), "11-locked-small" to listOf(1080, 2340, 144, 102))) {
            val s = Screen(Game(MemoryStore(mapOf("effort" to effortAt(3, 0.4))), seed = 3), seed = 5, billing = notBought, build = "41")
            s.resize(size[0], size[1], size[2], size[3])
            val l = s.layout!!
            fun tap(r: Rect) = s.press((r.x + r.w / 2f) * s.scale + s.offsetX, (r.y + r.h / 2f) * s.scale + s.offsetY)
            tap(l.dayLabel(s.game.day))
            write(s.render(), name, 3)
            tap(l.worldRow(1))
            s.release()
            write(s.render(), name.replace("locked", "buy"), 3)
        }
    }

    /** On a short screen the worlds do not fit: the list scrolls halfway, cut clean at its edges, with a scrollbar. */
    @Test
    fun worldsScrolled() {
        val s = Screen(Game(MemoryStore(mapOf("effort" to effortAt(3, 0.4))), seed = 3), seed = 5).apply { resize(1179, 1800) }
        val l = s.layout!!
        fun at(x: Float, y: Float) = x * s.scale + s.offsetX to y * s.scale + s.offsetY
        val day = l.dayLabel(s.game.day)
        at(day.x + 2f, day.y + 2f).let { (x, y) -> s.press(x, y) }
        val row = l.worldRow(0)
        at(row.x + 10f, row.y + 30f).let { (x, y) -> s.press(x, y) }
        at(row.x + 10f, row.y + 30f - l.worldScroll(Place.entries.size) / 2f).let { (x, y) -> s.move(x, y) }
        s.release()
        write(s.render(), "11-worlds-scrolled", 3)
    }

    /** The pictures of the 15 items of each world, a line per row, at the size the offer shows them. */
    @Test
    fun iconSheet() {
        for ((place, name) in listOf(Place.CORINTH to "icons", Place.ETNA to "icons-etna", Place.UNDERWORLD to "icons-underworld", Place.CAIRO to "icons-cairo")) {
            val sheet = Pixels(3 * 36 + 2, 5 * 36 + 2)
            sheet.clear(rgb(0x17110D))
            for (item in items(place)) sheet.drawScaled(itemIcon(item), 2 + (item.tier - 1) * 36, 2 + item.line.ordinal * 36, 2)
            write(sheet, name, 4)
        }
    }

    /** The help over the main screen, on a big phone and on the narrowest one. */
    @Test
    fun help() {
        for ((name, size) in listOf("0-help" to listOf(1179, 2556, 177, 102), "0-help-small" to listOf(1080, 2340, 144, 102))) {
            val s = Screen(Game(MemoryStore(mapOf("effort" to effortAt(2, 0.45), "obols" to 12.4)), seed = 3), seed = 5)
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

    /** The props of each world, on its own sky and ground. */
    @Test
    fun propSheet() {
        for ((props, name, colors) in listOf(
            Triple(PROPS, "props", rgb(0x9CC8EE) to rgb(0x82A048)),
            Triple(ETNA_PROPS, "props-etna", rgb(0xC4D0DC) to rgb(0x4A4440)),
            Triple(UNDERWORLD_PROPS, "props-underworld", rgb(0x46606A) to rgb(0x524E5A)),
            Triple(CAIRO_PROPS, "props-cairo", rgb(0xB8D4EC) to rgb(0xC8A878)),
        )) {
            val gap = 4
            val width = props.sumOf { it.sprite.width + gap } + gap
            val height = props.maxOf { it.sprite.height } + 6
            val sheet = Pixels(width, height)
            sheet.clear(colors.first)
            sheet.fillRect(0, height - 4, width, 4, colors.second)
            var x = gap
            for (prop in props) {
                sheet.draw(prop.sprite, x, height - 4 - prop.sprite.height + 1)
                x += prop.sprite.width + gap
            }
            write(sheet, name, 6)
        }
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

    /** The beauty items of the shop in each world: in Corinth, a dolphin mid-leap, Pegasus flying by, the Pleiades at night. */
    @Test
    fun beauty() {
        fun shot(time: Double, name: String, place: Place = Place.CORINTH, ready: (Screen) -> Boolean) {
            val store = MemoryStore(mapOf("effort" to effortAt(1, time), "shop.beauty" to 3.0, "place" to place.id.toDouble()))
            val s = Screen(Game(store, seed = 3), seed = 5).apply { resize(1179, 2556, 177, 102) }
            var frames = 0
            while (!ready(s) && frames++ < 3000) s.update(1 / 20.0)
            write(s.render(), name, 3)
        }
        shot(0.32, "9-dolphins") { s -> s.world.dolphins.any { it.t in 0.5..0.7 } }
        shot(0.5, "9-pegasus") { s -> s.world.flyer?.let { it.x in 40.0..90.0 } == true }
        shot(0.95, "9-pleiades") { s -> s.game.clock > 2.0 }
        shot(0.8, "etna-9-fountain", Place.ETNA) { s -> s.world.sparks.size > 60 }
        shot(0.5, "etna-9-eagle", Place.ETNA) { s -> s.world.flyer?.let { it.x in 40.0..90.0 } == true }
        shot(0.95, "etna-9-torches", Place.ETNA) { s -> s.game.clock > 2.0 }
        shot(0.5, "underworld-9-boat", Place.UNDERWORLD) { s -> s.world.ferry?.let { it.x in 15.0..30.0 } == true }
        shot(0.5, "underworld-9-psyche", Place.UNDERWORLD) { s -> s.world.flyer?.let { it.x in 40.0..90.0 } == true }
        shot(0.95, "underworld-9-lights", Place.UNDERWORLD) { s -> s.game.clock > 2.0 }
        shot(0.32, "cairo-9-hippo", Place.CAIRO) { s -> s.world.hippo?.let { it.t in 1.2..1.6 } == true }
        shot(0.5, "cairo-9-phoenix", Place.CAIRO) { s -> s.world.flyer?.let { it.x in 40.0..90.0 } == true }
        shot(0.95, "cairo-9-milky-way", Place.CAIRO) { s -> s.game.clock > 2.0 }
    }

    /** The four rocks of the shop, side by side, in each world. */
    @Test
    fun rockSheet() {
        val geo = SceneGeometry(64, 64)
        for ((place, name) in listOf(Place.CORINTH to "rocks", Place.ETNA to "rocks-etna", Place.UNDERWORLD to "rocks-underworld", Place.CAIRO to "rocks-cairo")) {
            val sheet = Pixels(64 * 4, 64)
            for (tier in 0..3) {
                val store = MemoryStore(mapOf("effort" to effortAt(1, 0.45), "position" to 962.0, "shop.rock" to tier.toDouble(), "place" to place.id.toDouble()))
                val px = Pixels(64, 64)
                SceneRenderer(geo).render(px, Game(store, seed = 3), World(9).apply { reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
                sheet.draw(px, tier * 64, 0)
            }
            write(sheet, name, 6)
        }
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

    /** A vision at noon in each world, each world with another god; one on the smallest phone, and one with the three helpers. */
    @Test
    fun voices() {
        fun asked(s: Screen, gods: Int) {
            repeat(gods) { s.game.summonVision() }
            s.idle(4.0)
        }
        for (place in Place.entries) {
            val s = screen(1, 0.5, place = place)
            val seen = mutableSetOf<God>()
            while (seen.size < godsOf(place).size) {
                asked(s, 1)
                val god = s.game.vision!!.god
                if (seen.add(god)) write(s.render(), "voice-${place.id}-${god.name.lowercase()}", 3)
            }
        }
        val small = Screen(Game(MemoryStore(mapOf("effort" to effortAt(1, 0.5))), seed = 3), seed = 5).apply { resize(1080, 2340, 144, 102) }
        asked(small, 1)
        write(small.render(), "voice-small", 3)
        val helped = mapOf("effort" to effortAt(1, 0.5), "shop.beside" to 3.0, "shop.alone" to 3.0, "shop.blessing" to 3.0)
        val helpers = Screen(Game(MemoryStore(helped), seed = 3), seed = 5).apply { resize(1179, 2556, 177, 102) }
        asked(helpers, 1)
        write(helpers.render(), "voice-helpers", 3)
    }
}
