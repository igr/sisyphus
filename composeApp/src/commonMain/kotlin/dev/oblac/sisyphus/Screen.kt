package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.scene.SceneGeometry
import dev.oblac.sisyphus.scene.SceneRenderer
import dev.oblac.sisyphus.scene.World
import dev.oblac.sisyphus.scene.atmosphere
import dev.oblac.sisyphus.ui.DEV_STEPS
import dev.oblac.sisyphus.ui.Layout
import dev.oblac.sisyphus.ui.Rect
import dev.oblac.sisyphus.ui.UiRenderer
import kotlin.math.atan2
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

enum class Touch { NONE, KNOB, SOUND, SHOP, BUY, DEV, HELP }

/** The whole screen is one small framebuffer, scaled up by a whole number so every pixel stays sharp. */
private const val TARGET_WIDTH = 144f

/** Extra virtual pixels around small icons, so they are easy to hit with a finger. */
private const val TOUCH_SLOP = 6

/**
 * Owns the framebuffer and maps real screen pixels to virtual ones.
 * Platform code only forwards touches and shows [pixels].
 */
class Screen(val game: Game, seed: Int = Random.nextInt(), private val dev: Boolean = false) {
    var scale = 1
        private set
    var offsetX = 0
        private set
    var offsetY = 0
        private set
    var pixels = Pixels(1, 1)
        private set
    var layout: Layout? = null
        private set
    /** The Agora, the shop, covers the whole screen while it is open. */
    var agoraOpen = false
        private set
    /** In dev builds, tapping the Agora's title shows the dev screen in its place: it sets the obols and resets the shop. */
    var devOpen = false
        private set
    /** The help lies over the main screen and points at the day, the obols and the knob. Any touch closes it. */
    var helpOpen = false
        private set

    val world = World(seed)
    private val ui = UiRenderer()
    private var scene = Pixels(1, 1)
    private var sceneRenderer: SceneRenderer? = null
    private var lastResize = emptyList<Int>()
    private var lastDay = game.day
    private var banner = 0.0

    fun resize(widthPx: Int, heightPx: Int, safeTop: Int = 0, safeBottom: Int = 0, safeLeft: Int = 0, safeRight: Int = 0) {
        val args = listOf(widthPx, heightPx, safeTop, safeBottom, safeLeft, safeRight)
        if (widthPx <= 0 || heightPx <= 0 || args == lastResize) return
        lastResize = args
        val s = max(1, (widthPx / TARGET_WIDTH).roundToInt())
        val w = widthPx / s
        val h = heightPx / s
        scale = s
        offsetX = (widthPx - w * s) / 2
        offsetY = (heightPx - h * s) / 2
        pixels = Pixels(w, h)
        val l = Layout(w, h, safeTop.ceilDiv(s), safeBottom.ceilDiv(s), safeLeft.ceilDiv(s), safeRight.ceilDiv(s))
        layout = l
        val geo = SceneGeometry(l.scene.w, l.scene.h)
        scene = Pixels(geo.width, geo.height)
        sceneRenderer = SceneRenderer(geo)
        world.reset(geo, game.weather)
    }

    fun update(dt: Double) {
        game.update(dt)
        if (layout != null) {
            val stars = atmosphere(game.timeOfDay, world.overcast).stars
            world.update(dt, game.moved, game.speed, floor(game.position).toInt(), game.weather, game.timeOfDay, stars, game.shop.tier(Line.BEAUTY))
        }
        if (game.day > lastDay) banner = 3.0
        lastDay = game.day
        banner = max(0.0, banner - dt)
    }

    fun render(): Pixels {
        val l = layout ?: return pixels
        val renderer = sceneRenderer ?: return pixels
        ui.background(pixels)
        if (agoraOpen) {
            ui.agora(pixels, l, game.shop, game.soundOn, devOpen)
        } else {
            renderer.render(scene, game, world, banner)
            pixels.draw(scene, l.scene.x, l.scene.y)
            ui.bezel(pixels, l.display)
            ui.band(pixels, l.band)
            ui.quote(pixels, l.quote, game.quotes, game.clock)
            ui.knob(pixels, l, game)
            ui.helpButton(pixels, l.helpIcon)
        }
        ui.coins(pixels, l.coinIcon, game.shop.obols.toInt())
        if (helpOpen) ui.help(pixels, l, game.day, game.shop.obols.toInt())
        return pixels
    }

    /** Handles a finger touching the screen and tells what it touched. */
    fun press(xPx: Float, yPx: Float): Touch {
        val l = layout ?: return Touch.NONE
        val x = (xPx - offsetX) / scale
        val y = (yPx - offsetY) / scale
        if (helpOpen) {
            helpOpen = false
            return Touch.HELP
        }
        if (l.coinIcon.near(x, y, TOUCH_SLOP)) {
            agoraOpen = !agoraOpen
            devOpen = false
            return Touch.SHOP
        }
        if (agoraOpen) {
            if (l.agoraSound.near(x, y, TOUCH_SLOP)) {
                game.toggleSound()
                return Touch.SOUND
            }
            if (dev && l.agoraTitle.near(x, y, 0)) {
                devOpen = !devOpen
                return Touch.DEV
            }
            if (devOpen) {
                val step = DEV_STEPS.indices.firstOrNull { l.devStep(it).near(x, y, 0) }
                when {
                    step != null -> game.addObols(DEV_STEPS[step])
                    l.devReset.near(x, y, 0) -> game.resetShop()
                    else -> return Touch.NONE
                }
                return Touch.DEV
            }
            val row = Line.entries.indices.firstOrNull { l.agoraRow(it).near(x, y, 0) } ?: return Touch.NONE
            return if (game.buy(Line.entries[row])) Touch.BUY else Touch.NONE
        }
        if (l.helpIcon.near(x, y, TOUCH_SLOP)) {
            helpOpen = true
            return Touch.HELP
        }
        val dx = x - (l.knobX + 0.5)
        val dy = y - (l.knobY + 0.5)
        if (y < l.quote.bottom && sqrt(dx * dx + dy * dy) > l.knobR * 1.4) return Touch.NONE
        game.knob.grab(touchAngle(dx, dy, l))
        return Touch.KNOB
    }

    fun move(xPx: Float, yPx: Float) {
        val l = layout ?: return
        val x = (xPx - offsetX) / scale
        val y = (yPx - offsetY) / scale
        game.knob.drag(touchAngle(x - (l.knobX + 0.5), y - (l.knobY + 0.5), l))
    }

    fun release() = game.knob.release()

    /** Back closes the help or the Agora. Tells whether there was something to close. */
    fun back(): Boolean {
        if (helpOpen) {
            helpOpen = false
            return true
        }
        if (!agoraOpen) return false
        agoraOpen = false
        devOpen = false
        return true
    }

    /** Near the center the direction of the finger is noise, so it does not count. */
    private fun touchAngle(dx: Double, dy: Double, l: Layout): Double? =
        if (sqrt(dx * dx + dy * dy) < l.knobR * 0.2) null else atan2(dy, dx)

    private fun Int.ceilDiv(d: Int) = (this + d - 1) / d

    private fun Rect.near(x: Float, y: Float, slop: Int) =
        x >= this.x - slop && x < right + slop && y >= this.y - slop && y < bottom + slop
}
