package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.JOKERS
import dev.oblac.sisyphus.game.Joker
import dev.oblac.sisyphus.game.Line
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.TALES
import dev.oblac.sisyphus.game.Tale
import dev.oblac.sisyphus.game.Vision
import dev.oblac.sisyphus.game.godsOf
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.scene.Ending
import dev.oblac.sisyphus.scene.SceneGeometry
import dev.oblac.sisyphus.scene.SceneRenderer
import dev.oblac.sisyphus.scene.World
import dev.oblac.sisyphus.scene.atmosphere
import dev.oblac.sisyphus.scene.drawGods
import dev.oblac.sisyphus.scene.drawVision
import dev.oblac.sisyphus.ui.DEV_STEPS
import dev.oblac.sisyphus.ui.GAIN_TIME
import dev.oblac.sisyphus.ui.Layout
import dev.oblac.sisyphus.ui.SETTINGS
import dev.oblac.sisyphus.ui.supportTag
import dev.oblac.sisyphus.ui.Rect
import dev.oblac.sisyphus.ui.Tab
import dev.oblac.sisyphus.ui.UiRenderer
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * What a finger touched. [LIST] is a finger on the list of the Worlds screen: a drag scrolls it, and on the worlds a tap chooses one when the finger lifts.
 * [MOVE] is the Yes that goes to another world. [COPY] copies the player's id for support. [SETTINGS] opens or closes the settings.
 */
enum class Touch { NONE, KNOB, SOUND, SHOP, BUY, DEV, HELP, AGAIN, WORLDS, LIST, MOVE, COPY, SETTINGS }

/** New obols on the main screen: how many came together, and the seconds since. */
class Gain(val obols: Int, val age: Double)

/** The whole screen is one small framebuffer, scaled up by a whole number so every pixel stays sharp. */
private const val TARGET_WIDTH = 144f

/**
 * The game is at most as wide as a 16:9 phone of the same height. A tablet, an unfolded phone or a wide window shows it
 * with bars at the sides: a wider screen would leave the scene too short for the popups.
 */
private const val WIDEST = 9.0 / 16

/** Extra virtual pixels around small icons, so they are easy to hit with a finger. */
private const val TOUCH_SLOP = 6

/** A finger on a list that moves less than this, in virtual pixels, taps; more scrolls. */
private const val TAP_SLOP = 3f

/** Seconds that the settings say "Copied" after a tap copied the player's id. */
private const val COPIED_FOR = 2.0

/** A coasting list slows down by this much a second, like the knob. */
private const val LIST_FRICTION = 3.0

/** Below this speed, in virtual pixels a second, a touch on the list does not count as stopping it. */
private const val STOP_SPEED = 20.0

/**
 * Owns the framebuffer and maps real screen pixels to virtual ones.
 * Platform code only forwards touches and shows [pixels]. The settings show [build], the build number of the app, after the player's id.
 */
class Screen(
    val game: Game,
    seed: Int = Random.nextInt(),
    private val dev: Boolean = false,
    private val billing: Billing = NoBilling,
    private val build: String? = null,
) {
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
    /** The line whose next item the Agora offers in a window, asking Yes or No before it is bought. */
    var offer: Line? = null
        private set
    /** The help lies over the main screen and points at the day, the obols and the knob. Any touch closes it. */
    var helpOpen = false
        private set
    /** The Worlds screen covers the whole screen while it is open; tapping the day opens it. */
    var worldsOpen = false
        private set
    /** The settings cover the whole screen while they are open; the gear across from the help opens them. */
    var settingsOpen = false
        private set
    /** The tab that the Worlds screen shows. It stays as it was shown last, until the app closes. */
    var tab = Tab.WORLDS
        private set
    val laborsShown get() = tab == Tab.LABORS
    val cardsShown get() = tab == Tab.CARDS
    /** The card of a joker open over the Cards tab. Any tap closes it. */
    var openCard: Joker? = null
        private set
    /** The tale open over the Cards tab, at its [talePage]. A tap on it turns the page; a tap off it closes it. */
    var openTale: Tale? = null
        private set
    var talePage = 0
        private set
    /** The world the player asked to go to, waiting for Yes or No: there the climb starts over. */
    var moving: Place? = null
        private set
    /** A world not bought yet that the player asked to go to, waiting for Yes or No: Yes opens the store's payment sheet. */
    var buying: Place? = null
        private set
    /** The store's payment sheet is open: the screen waits for its answer and takes no touches. */
    var paying = false
        private set
    /** The store looks for the worlds bought before. */
    var restoring = false
        private set
    /** In dev builds, the dev screen opens every world without the store, until it is tapped again or the app restarts. */
    var openWorlds = false
        private set
    /** How far the list of the shown tab of the Worlds screen is scrolled, in virtual pixels. */
    val worldsScroll get() = listScroll.roundToInt()
    private var listScroll = 0.0
    /** The speed of the list, in virtual pixels a second: it follows the finger, and coasts after a swipe. */
    private var listSpeed = 0.0
    private var lastListScroll = 0.0
    private var drag: ListDrag? = null

    /** The top: from the moment he reaches it until the first morning is back, after the Worlds screen. */
    var ending: Ending? = null
        private set

    /** The player's id at the store's service: a tap in the settings copies it, for support. Null without a store. */
    val supportId: String? get() = billing.userId
    /** Seconds that the settings still say that the id is copied. */
    private var copied = 0.0

    /** New obols that just came: the coin of the count glints, and their number rises under the count and fades. */
    var gain: Gain? = null
        private set
    /** Called when new obols come. */
    var onCoin: () -> Unit = {}
    /** Called when he reaches the top. */
    var onTop: () -> Unit = {}
    /** Called when the store's answer, not a touch, begins the climb in a new world: a world just bought. */
    var onMove: () -> Unit = {}
    /** The coin rings in the next frame: the obols of the time away come before the sounds are ready. */
    private var ring = false

    init {
        game.onObol = { obols ->
            gain = Gain(obols + (gain?.obols ?: 0), 0.0)
            ring = true
        }
    }

    val world = World(seed)
    private var geo: SceneGeometry? = null
    private val ui = UiRenderer()
    /** The worlds as the screen sees them: what the store says, or every world while the dev screen opens them. */
    private val worlds = object : Billing by billing {
        override fun owns(place: Place) = openWorlds || billing.owns(place)
    }
    private var scene = Pixels(1, 1)
    private var sceneRenderer: SceneRenderer? = null
    private var lastResize = emptyList<Int>()
    private var lastDay = game.day
    private var banner = 0.0

    fun resize(widthPx: Int, heightPx: Int, safeTop: Int = 0, safeBottom: Int = 0, safeLeft: Int = 0, safeRight: Int = 0) {
        val args = listOf(widthPx, heightPx, safeTop, safeBottom, safeLeft, safeRight)
        if (widthPx <= 0 || heightPx <= 0 || args == lastResize) return
        lastResize = args
        val used = min(widthPx, (heightPx * WIDEST).toInt())
        val s = max(1, (used / TARGET_WIDTH).roundToInt())
        val w = used / s
        val h = heightPx / s
        scale = s
        offsetX = (widthPx - w * s) / 2
        offsetY = (heightPx - h * s) / 2
        pixels = Pixels(w, h)
        fun side(inset: Int) = max(0, inset - offsetX).ceilDiv(s)
        val l = Layout(w, h, safeTop.ceilDiv(s), safeBottom.ceilDiv(s), side(safeLeft), side(safeRight))
        layout = l
        val geo = SceneGeometry(l.scene.w, l.scene.h)
        this.geo = geo
        scene = Pixels(geo.width, geo.height)
        sceneRenderer = SceneRenderer(geo)
        world.reset(geo, game.weather)
    }

    fun update(dt: Double) {
        geo?.top = game.summit?.let { floor(it).toInt() }
        game.covered = agoraOpen || worldsOpen || settingsOpen || helpOpen || ending != null
        game.update(dt)
        if (ring) {
            ring = false
            onCoin()
        }
        gain = gain?.let { Gain(it.obols, it.age + dt) }?.takeIf { it.age < GAIN_TIME }
        copied = max(0.0, copied - dt)
        val end = ending ?: if (game.atTop) reachTop() else null
        if (end != null) {
            val black = end.time >= Ending.BLACK_AT
            end.update(dt)
            if (!black && end.time >= Ending.BLACK_AT) {
                game.knob.release()
                game.knob.stop()
                game.startOver()
                geo?.let { world.reset(it, game.weather) }
            }
            if (end.choosing && !worldsOpen) end.wake()
            if (end.over) ending = null
        }
        if (layout != null) {
            val stars = atmosphere(game.timeOfDay, world.overcast, game.place).stars
            world.update(dt, game.moved, game.speed, floor(game.position).toInt(), game.weather, game.timeOfDay, stars, game.shop.tier(Line.BEAUTY), game.place)
        }
        if (worldsOpen) moveList(dt)
        if (worldsOpen && laborsShown && game.newLabors) game.seeLabors()
        if (worldsOpen && cardsShown && game.newJokers) game.seeJokers()
        if (worldsOpen && cardsShown && game.newTales) game.seeTales()
        if (game.day > lastDay) banner = 3.0
        lastDay = game.day
        banner = max(0.0, banner - dt)
    }

    /** He reaches the top: two of the three gods of the world come, a pair of each climb's own, and cheer him. */
    private fun reachTop(): Ending {
        val end = Ending(godsOf(game.place).shuffled(Random(game.climbs)).take(2))
        ending = end
        game.cheer()
        onTop()
        return end
    }

    fun render(): Pixels {
        val l = layout ?: return pixels
        val renderer = sceneRenderer ?: return pixels
        ui.background(pixels)
        if (agoraOpen) {
            ui.agora(pixels, l, game.shop, game.soundOn, devOpen, openWorlds)
            offer?.let { game.shop.next(it) }?.let { ui.offer(pixels, l, it) }
        } else if (settingsOpen) {
            ui.settings(pixels, l, listOf(game.soundOn, game.ticksOn, game.rollOn))
            supportTag(supportId, build)?.let { ui.supportId(pixels, l.supportId, it, copied > 0) }
        } else if (worldsOpen) {
            ui.tabs(pixels, l, tab, game.newLabors, game.newJokers || game.newTales)
            when (tab) {
                Tab.WORLDS -> ui.worlds(pixels, l, game.place, worldsScroll, worlds)
                Tab.LABORS -> ui.labors(pixels, l, game.counts, worldsScroll)
                Tab.CARDS -> ui.cards(pixels, l, worldsScroll, game::met, game::partsHeard)
            }
            if (canRestore) ui.restore(pixels, l.restore, restoring)
            openCard?.let { ui.openCard(pixels, l, it, game.met(it)) }
            openTale?.let { ui.openTale(pixels, l, it, talePage, game.partsHeard(it)) }
            moving?.let { ui.confirm(pixels, l, it) }
            buying?.let { ui.buy(pixels, l, it, billing.price(it), paying, climbing = game.begun && ending == null) }
        } else {
            renderer.render(scene, game, world, banner, rolled = ending?.rolled)
            game.vision?.let { drawVision(scene, it) }
            ending?.let { end ->
                drawGods(scene, end.gods, end.godLight)
                end.sparkles?.let { renderer.sparkle(scene, it) }
            }
            pixels.draw(scene, l.scene.x, l.scene.y)
            ui.bezel(pixels, l.display)
            ui.obolBar(pixels, l.obolBar, game.shop.obols, game.climbed)
            if (game.vision == null) {
                val corner = listOfNotNull(game.shop.owned(Line.SIDEKICKS), game.shop.owned(Line.GODS), game.shop.owned(Line.BLESSING))
                corner.forEachIndexed { i, item -> ui.helper(pixels, l.helper(corner.size - 1 - i), item) }
            }
            ui.band(pixels, l.band)
            val voice = game.vision?.let { it.phase != Vision.Phase.LEAVING } == true || (ending?.godLight ?: 0f) > 0f
            ui.quote(pixels, l.quote, game.quotes, game.clock, voice)
            ui.knob(pixels, l, game)
            ui.helpButton(pixels, l.helpIcon)
            ui.settingsButton(pixels, l.settingsIcon)
        }
        val count = game.shop.obols.toInt()
        val covered = agoraOpen || worldsOpen || settingsOpen
        if (!worldsOpen && !settingsOpen) ui.coins(pixels, l.coinIcon, if (game.climbed != null) null else count)
        if (!covered && !helpOpen) gain?.let { ui.gain(pixels, l.coinIcon, it.obols, it.age, count) }
        if (helpOpen) ui.help(pixels, l, game.day, game.shop.obols, game.climbed)
        if (!covered) ending?.let { ui.ending(pixels, l, it, game.climbs) }
        return pixels
    }

    /** Handles a finger touching the screen and tells what it touched. */
    fun press(xPx: Float, yPx: Float): Touch {
        val l = layout ?: return Touch.NONE
        if (paying) return Touch.NONE
        val x = (xPx - offsetX) / scale
        val y = (yPx - offsetY) / scale
        if (!worldsOpen && !agoraOpen && !settingsOpen) ending?.let {
            if (!it.waiting) return Touch.NONE
            it.choose()
            showWorlds(Tab.WORLDS)
            return Touch.AGAIN
        }
        if (helpOpen) {
            helpOpen = false
            return Touch.HELP
        }
        if (settingsOpen) return pressSettings(x, y, l)
        if (!worldsOpen && l.coinIcon.near(x, y, TOUCH_SLOP)) {
            cover()
            agoraOpen = !agoraOpen
            devOpen = false
            offer = null
            return Touch.SHOP
        }
        if (worldsOpen) return pressWorlds(x, y, l)
        if (agoraOpen) {
            val offered = offer
            if (offered != null) {
                if (l.offerYes.near(x, y, 0)) {
                    offer = null
                    return if (game.buy(offered)) Touch.BUY else Touch.NONE
                }
                if (l.offer.near(x, y, 0) && !l.offerNo.near(x, y, 0)) return Touch.NONE
                offer = null
                return Touch.SHOP
            }
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
                    l.devResetShop.near(x, y, 0) -> game.resetShop()
                    l.devResetClimb.near(x, y, 0) -> {
                        game.resetClimb()
                        geo?.let { world.reset(it, game.weather) }
                    }
                    l.devNextObol.near(x, y, 0) -> game.nearTheNextObol()
                    l.devTop.near(x, y, 0) -> game.nearTheTop()
                    l.devStone.near(x, y, 0) -> {
                        agoraOpen = false
                        devOpen = false
                        game.nearTheNextStone()
                    }
                    l.devWorlds.near(x, y, 0) -> openWorlds = !openWorlds
                    l.devJoker.near(x, y, 0) -> {
                        agoraOpen = false
                        devOpen = false
                        game.nearTheNextJoker()
                    }
                    l.devVision.near(x, y, 0) -> {
                        agoraOpen = false
                        devOpen = false
                        game.summonVision()
                    }
                    l.devNextVision.near(x, y, 0) -> {
                        agoraOpen = false
                        devOpen = false
                        game.nearTheNextVision()
                    }
                    l.devNextLabor.near(x, y, 0) -> {
                        agoraOpen = false
                        devOpen = false
                        game.nearTheNextLabor()
                    }
                    else -> return Touch.NONE
                }
                return Touch.DEV
            }
            val row = Line.entries.indices.firstOrNull { l.agoraRow(it).near(x, y, 0) } ?: return Touch.NONE
            val line = Line.entries[row]
            val item = game.shop.next(line) ?: return Touch.NONE
            if (game.shop.obols < item.price) return Touch.NONE
            offer = line
            return Touch.SHOP
        }
        if (l.dayLabel(game.day).near(x, y, TOUCH_SLOP)) {
            showWorlds(
                when {
                    game.newLabors -> Tab.LABORS
                    game.newJokers || game.newTales -> Tab.CARDS
                    else -> tab
                },
            )
            return Touch.WORLDS
        }
        if (l.helpIcon.near(x, y, TOUCH_SLOP)) {
            cover()
            helpOpen = true
            return Touch.HELP
        }
        if (l.settingsIcon.near(x, y, TOUCH_SLOP)) {
            cover()
            settingsOpen = true
            return Touch.SETTINGS
        }
        val dx = x - (l.knobX + 0.5)
        val dy = y - (l.knobY + 0.5)
        if (y < l.quote.bottom && sqrt(dx * dx + dy * dy) > l.knobR * 1.4) return Touch.NONE
        game.knob.grab(touchAngle(dx, dy, l))
        return Touch.KNOB
    }

    /**
     * On the Worlds screen: a tap on a tab, or else near one, goes to the nearest tab. One that is not shown shows; a tap on the
     * shown tab, or anywhere else on the title row, closes the screen.
     * The list scrolls or chooses a world, and Yes goes there, or first buys it. An open card closes at any tap; an open tale turns
     * its page, or closes. Restore asks the store for the worlds bought before.
     */
    private fun pressWorlds(x: Float, y: Float, l: Layout): Touch {
        val to = moving
        if (to != null) {
            if (l.confirmYes.near(x, y, 0)) return goTo(to)
            if (l.confirm.near(x, y, 0) && !l.confirmNo.near(x, y, 0)) return Touch.NONE
            moving = null
            return Touch.WORLDS
        }
        val wanted = buying
        if (wanted != null) {
            if (l.confirmYes.near(x, y, 0)) {
                paying = true
                billing.buy(wanted) { bought ->
                    paying = false
                    buying = null
                    if (bought) {
                        goTo(wanted)
                        onMove()
                    }
                }
                return Touch.WORLDS
            }
            if (l.confirm.near(x, y, 0) && !l.confirmNo.near(x, y, 0)) return Touch.NONE
            buying = null
            return Touch.WORLDS
        }
        openTale?.let { return turnTale(it, x, y, l) }
        if (openCard != null) {
            openCard = null
            return Touch.WORLDS
        }
        val tabs = listOf(Tab.WORLDS to l.worldsTab, Tab.LABORS to l.laborsTab, Tab.CARDS to l.cardsTab)
        val touched = tabs.firstOrNull { (_, r) -> r.near(x, y, 0) }
            ?: tabs.filter { (_, r) -> r.near(x, y, TOUCH_SLOP) }.minByOrNull { (_, r) -> r.gap(x, y) }
        if (touched != null && touched.first != tab) {
            show(touched.first)
            return Touch.WORLDS
        }
        if (l.agoraTitle.near(x, y, 0)) {
            worldsOpen = false
            return Touch.WORLDS
        }
        if (canRestore && !restoring && l.restore.near(x, y, TOUCH_SLOP)) {
            restoring = true
            billing.restore { restoring = false }
            return Touch.WORLDS
        }
        if (!l.worldList.near(x, y, 0)) return Touch.NONE
        drag = ListDrag(x, y, listScroll, stopped = abs(listSpeed) > STOP_SPEED)
        listSpeed = 0.0
        return Touch.LIST
    }

    /** In the settings, a tap on a row turns its sound off or on, a tap on the id copies it, and a tap on the title row closes them. */
    private fun pressSettings(x: Float, y: Float, l: Layout): Touch {
        if (l.agoraTitle.near(x, y, 0)) {
            settingsOpen = false
            return Touch.SETTINGS
        }
        when (SETTINGS.indices.firstOrNull { l.settingsRow(it).near(x, y, 0) }) {
            0 -> game.toggleSound()
            1 -> game.toggleTicks()
            2 -> game.toggleRoll()
            else -> {
                if (supportId == null || !l.supportId.near(x, y, TOUCH_SLOP)) return Touch.NONE
                copied = COPIED_FOR
                return Touch.COPY
            }
        }
        return Touch.SOUND
    }

    fun move(xPx: Float, yPx: Float) {
        val l = layout ?: return
        val x = (xPx - offsetX) / scale
        val y = (yPx - offsetY) / scale
        drag?.let {
            it.moved = max(it.moved, abs(y - it.y))
            listScroll = (it.scroll - (y - it.y)).coerceIn(0.0, mostScroll(l).toDouble())
            return
        }
        game.knob.drag(touchAngle(x - (l.knobX + 0.5), y - (l.knobY + 0.5), l))
    }

    /** While a world is not bought, the Worlds screen can ask the store for the worlds bought before. */
    private val canRestore get() = Place.entries.any { !worlds.owns(it) }

    /**
     * The finger lifts: the knob coasts on, or a tap on the list goes back to the world he is in, or asks to go to another world,
     * or to buy it. Before the climb has begun, as after the top, nothing is lost, so a world already bought begins at once.
     * On the Cards tab, a tap opens the card of a joker met, or a tale heard. Tells what the lift did.
     */
    fun release(): Touch {
        val d = drag ?: return Touch.NONE.also { game.knob.release() }
        drag = null
        val l = layout ?: return Touch.NONE
        if (d.moved > TAP_SLOP) return Touch.NONE
        listSpeed = 0.0
        if (d.stopped) return Touch.NONE
        if (tab == Tab.CARDS) return openAt(d.x, d.y + worldsScroll, l)
        if (tab != Tab.WORLDS) return Touch.NONE
        val row = Place.entries.indices.firstOrNull { l.worldRow(it).near(d.x, d.y + worldsScroll, 0) } ?: return Touch.NONE
        val place = Place.entries[row]
        if (place == game.place) {
            worldsOpen = false
            return Touch.WORLDS
        }
        when {
            !worlds.owns(place) -> buying = place
            game.begun && ending == null -> moving = place
            else -> return goTo(place)
        }
        return Touch.WORLDS
    }

    /** On the list of cards, before it scrolls, at ([x], [y]): the card of a joker met opens, or a tale heard opens at its first page. */
    private fun openAt(x: Float, y: Float, l: Layout): Touch {
        JOKERS.firstOrNull { l.jokerCards[it.id].near(x, y, 0) && game.met(it) > 0 }?.let {
            openCard = it
            return Touch.WORLDS
        }
        TALES.firstOrNull { l.taleCards[it.id].near(x, y, 0) && game.partsHeard(it) > 0 }?.let {
            openTale = it
            talePage = 0
            return Touch.WORLDS
        }
        return Touch.NONE
    }

    /** A tap on the left half of the open [tale] shows the page before, on its right half the next page heard. A tap off it closes it. */
    private fun turnTale(tale: Tale, x: Float, y: Float, l: Layout): Touch {
        val r = l.openTale
        if (!r.near(x, y, 0)) {
            openTale = null
            return Touch.WORLDS
        }
        val page = talePage + if (x < r.x + r.w / 2f) -1 else 1
        if (page !in 0 until tale.pagesOf(game.partsHeard(tale))) return Touch.NONE
        talePage = page
        return Touch.WORLDS
    }

    /** The climb starts over in [place], and the Worlds screen closes. */
    private fun goTo(place: Place): Touch {
        game.moveTo(place)
        geo?.let { world.reset(it, game.weather) }
        moving = null
        worldsOpen = false
        return Touch.MOVE
    }

    /** While a finger drags the list, its speed follows; after the finger lifts, the list coasts and slows down. */
    private fun moveList(dt: Double) {
        val most = (layout?.let(::mostScroll) ?: 0).toDouble()
        if (drag != null) {
            if (dt > 0) listSpeed += ((listScroll - lastListScroll) / dt - listSpeed) * (1 - exp(-dt * 18))
        } else if (listSpeed != 0.0) {
            listScroll = (listScroll + listSpeed * dt).coerceIn(0.0, most)
            listSpeed *= exp(-LIST_FRICTION * dt)
            if (abs(listSpeed) < STOP_SPEED / 4 || listScroll == 0.0 || listScroll == most) listSpeed = 0.0
        }
        lastListScroll = listScroll
    }

    /** How far the list on the shown tab of the Worlds screen can scroll. */
    private fun mostScroll(l: Layout) = when (tab) {
        Tab.WORLDS -> l.worldScroll(Place.entries.size)
        Tab.LABORS -> l.laborScroll
        Tab.CARDS -> l.cardScroll
    }

    /** A finger on the list, from where it landed. A finger that [stopped] the list from coasting chooses nothing. */
    private class ListDrag(val x: Float, val y: Float, val scroll: Double, val stopped: Boolean) {
        var moved = 0f
    }

    /**
     * Back closes the help, the offer, a question, an open card or tale, the Worlds screen, the settings or the Agora. Tells whether there was something to close.
     * While the store's payment sheet is open, back waits for it.
     */
    fun back(): Boolean {
        if (paying) return true
        if (helpOpen) {
            helpOpen = false
            return true
        }
        if (offer != null) {
            offer = null
            return true
        }
        if (moving != null) {
            moving = null
            return true
        }
        if (buying != null) {
            buying = null
            return true
        }
        if (openCard != null) {
            openCard = null
            return true
        }
        if (openTale != null) {
            openTale = null
            return true
        }
        if (worldsOpen) {
            worldsOpen = false
            return true
        }
        if (settingsOpen) {
            settingsOpen = false
            return true
        }
        if (!agoraOpen) return false
        agoraOpen = false
        devOpen = false
        return true
    }

    /** A screen covers the game: a vision ends, and the knob stops, so that no vision comes unseen behind the screen. */
    private fun cover() {
        game.endVision()
        game.knob.stop()
    }

    /** Opens the Worlds screen on [tab]. The store is asked again for the prices that it could not send before. */
    private fun showWorlds(tab: Tab) {
        cover()
        billing.refresh()
        worldsOpen = true
        show(tab)
    }

    /** Shows [tab] at the top of its list, with no card open. A tab with news has been seen. */
    private fun show(tab: Tab) {
        this.tab = tab
        openCard = null
        openTale = null
        if (laborsShown) game.seeLabors()
        if (cardsShown) {
            game.seeJokers()
            game.seeTales()
        }
        listScroll = 0.0
        listSpeed = 0.0
        lastListScroll = 0.0
    }

    /** Near the center the direction of the finger is noise, so it does not count. */
    private fun touchAngle(dx: Double, dy: Double, l: Layout): Double? =
        if (sqrt(dx * dx + dy * dy) < l.knobR * 0.2) null else atan2(dy, dx)

    private fun Int.ceilDiv(d: Int) = (this + d - 1) / d

    private fun Rect.near(x: Float, y: Float, slop: Int) =
        x >= this.x - slop && x < right + slop && y >= this.y - slop && y < bottom + slop

    /** How far ([x], [y]) is from the rectangle, across and down: 0 inside it. */
    private fun Rect.gap(x: Float, y: Float) = max(max(this.x - x, x - right), 0f) + max(max(this.y - y, y - bottom), 0f)
}
