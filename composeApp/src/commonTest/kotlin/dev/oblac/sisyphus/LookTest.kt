package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Config
import dev.oblac.sisyphus.game.DAY_LENGTH
import dev.oblac.sisyphus.game.Game
import dev.oblac.sisyphus.game.JOKERS
import dev.oblac.sisyphus.game.Joker
import dev.oblac.sisyphus.game.PX_PER_TURN
import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.game.START_TIME
import dev.oblac.sisyphus.game.TALES
import dev.oblac.sisyphus.game.Vision
import dev.oblac.sisyphus.game.Weather
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.popupRows
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.scene.SceneGeometry
import dev.oblac.sisyphus.scene.SceneRenderer
import dev.oblac.sisyphus.scene.BLACK_STONE
import dev.oblac.sisyphus.scene.JOKER_SPRITES
import dev.oblac.sisyphus.scene.GRANITE
import dev.oblac.sisyphus.scene.LAVA_STONE
import dev.oblac.sisyphus.scene.LIMESTONE
import dev.oblac.sisyphus.scene.SANDSTONE
import dev.oblac.sisyphus.scene.lookOf
import dev.oblac.sisyphus.scene.World
import dev.oblac.sisyphus.ui.Layout
import kotlin.math.abs
import kotlin.math.floor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The narrow phones, in virtual pixels: the narrowest, a 1080 × 1920 Android phone, the shortest, and the iPhone 8 Plus. */
private val NARROW_PHONES = listOf(Layout(135, 292, 18, 13, 0, 0), Layout(135, 240, 0, 0, 0, 0), Layout(138, 245, 0, 0, 0, 0))

class LookTest {
    private val geo = SceneGeometry(133, 133)

    private fun noon(place: Place, geo: SceneGeometry = this.geo): IntArray {
        val game = Game(MemoryStore(mapOf("effort" to (0.5 - START_TIME) * DAY_LENGTH, "place" to place.id.toDouble())), seed = 3)
        val px = Pixels(geo.width, geo.height)
        SceneRenderer(geo).render(px, game, World(4).also { it.reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
        return px.data.copyOf()
    }

    @Test
    fun etnaLooksLikeItself() {
        assertFalse(noon(Place.ETNA).contentEquals(noon(Place.CORINTH)))
    }

    @Test
    fun theUnderworldLooksLikeItself() {
        assertFalse(noon(Place.UNDERWORLD).contentEquals(noon(Place.CORINTH)))
        assertFalse(noon(Place.UNDERWORLD).contentEquals(noon(Place.ETNA)))
    }

    @Test
    fun cairoLooksLikeItself() {
        for (other in listOf(Place.CORINTH, Place.ETNA, Place.UNDERWORLD)) assertFalse(noon(Place.CAIRO).contentEquals(noon(other)))
    }

    @Test
    fun mountFujiLooksLikeItself() {
        for (other in listOf(Place.CORINTH, Place.ETNA, Place.UNDERWORLD, Place.CAIRO)) assertFalse(noon(Place.FUJI).contentEquals(noon(other)))
    }

    /** A stone of the tale stands by the path ahead, in every world. */
    @Test
    fun theStoneAheadIsDrawn() {
        fun noon(place: Place, stone: Boolean): IntArray {
            val keys = mutableMapOf("effort" to (0.5 - START_TIME) * DAY_LENGTH, "shop.alone" to 1.0, "stelesTold" to 0.0, "place" to place.id.toDouble())
            if (stone) keys["nextStele"] = 2 * PX_PER_TURN
            val px = Pixels(geo.width, geo.height)
            SceneRenderer(geo).render(px, Game(MemoryStore(keys), seed = 3), World(4).also { it.reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
            return px.data.copyOf()
        }
        for (place in Place.entries) assertFalse(noon(place, stone = true).contentEquals(noon(place, stone = false)), place.title)
    }

    /** Each stone, in the colors of its world, has a slab of 9 by 15, and a relief of 7 by 7 for each tale. */
    @Test
    fun everyStoneHasTheReliefOfEveryTale() {
        for (stone in listOf(LIMESTONE, LAVA_STONE, BLACK_STONE, SANDSTONE, GRANITE)) {
            assertEquals(9 to 15, stone.slab.width to stone.slab.height)
            assertEquals(TALES.size, stone.reliefs.size)
            assertTrue(stone.reliefs.all { it.width == 7 && it.height == 7 })
        }
    }

    /** The stones of a tale all show its relief: the stone of the 1st part looks as the stone of the 10th. */
    @Test
    fun theStonesOfATaleShareItsRelief() {
        fun stoneAhead(part: Int): IntArray {
            val keys = mapOf("effort" to (2.5 - START_TIME) * DAY_LENGTH, "stelesTold" to part.toDouble(), "nextStele" to 40.0)
            val px = Pixels(geo.width, geo.height)
            SceneRenderer(geo).render(px, Game(MemoryStore(keys), seed = 3), World(4).also { it.reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
            return px.data.copyOf()
        }
        assertTrue(stoneAhead(0).contentEquals(stoneAhead(9)))
    }

    @Test
    fun eachWorldHasItsOwnStone() {
        val slabs = Place.entries.map { lookOf(it).stone.slab.data.toList() }
        assertEquals(Place.entries.size, slabs.toSet().size)
    }

    /** When he has walked to a stone, it stands at the front of the rock: the rock reaches it there. */
    @Test
    fun theRockReachesAStoneWhenHeHasWalkedToIt() {
        val left = SceneRenderer(geo).steleLeft(at = 500.0, p = 500)
        assertEquals(geo.heroX + (geo.rockDx + geo.rockR).toInt(), left + LIMESTONE.slab.width / 2)
    }

    /**
     * Over a stone that he is near, a popup tells its part. On the narrow phones, the shortest too, every text of every tale fits
     * under the day and the obols, in the top 13 rows of the scene. A vision hides the popup.
     */
    @Test
    fun thePopupOfAStoneFitsTheNarrowPhones() {
        for (phone in NARROW_PHONES) popupOfAStoneFits(phone.scene.let { SceneGeometry(it.w, it.h) })
    }

    /**
     * On the narrow phones, every text of every tale, with its note such as "9/15", takes at most 4 rows of the popup. The popup
     * stays under the day and the obols, so a longer text would cover the rock.
     */
    @Test
    fun theTextsOfTheTalesFitTheirRows() {
        for (phone in NARROW_PHONES) for (tale in TALES) for ((part, stele) in tale.parts.withIndex()) for (text in stele.texts) {
            val rows = popupRows(phone.scene.w, text, "${part + 1}/${tale.parts.size}").size
            assertTrue(rows <= 4, "tale ${tale.id}, part ${part + 1}: $rows rows on a scene ${phone.scene.w} wide")
        }
    }

    private fun popupOfAStoneFits(small: SceneGeometry) {
        val at = 400.0
        val read = Config.READ_TURNS * PX_PER_TURN
        fun scene(tale: Int, part: Int, page: Int, near: Boolean, vision: Boolean = false): IntArray {
            val keys = mapOf(
                "effort" to (2.5 - START_TIME) * DAY_LENGTH, "tale" to tale.toDouble(), "stelesTold" to part.toDouble(),
                "position" to at - Config.NEAR_TURNS * PX_PER_TURN + page * read + 1, "nextStele" to if (near) at else at + 1000,
            )
            val game = Game(MemoryStore(keys), seed = 3)
            if (vision) game.summonVision()
            val px = Pixels(small.width, small.height)
            SceneRenderer(small).render(px, game, World(4).also { it.reset(small, Weather.CLEAR) }, banner = 0.0, hud = false)
            return px.data.copyOf()
        }
        val hud = 13 * small.width
        val sky = 45 * small.width
        for (tale in TALES) for ((part, texts) in tale.parts.map { it.texts.size }.withIndex()) for (page in 0 until texts) {
            val near = scene(tale.id, part, page, near = true)
            val far = scene(tale.id, part, page, near = false)
            assertTrue(near.copyOfRange(0, hud).contentEquals(far.copyOfRange(0, hud)), "tale ${tale.id}, part ${part + 1} covers the day")
            assertFalse(near.copyOfRange(hud, sky).contentEquals(far.copyOfRange(hud, sky)), "tale ${tale.id}, part ${part + 1} shows no popup")
        }
        assertTrue(scene(0, 0, 0, near = true, vision = true).copyOfRange(0, sky).contentEquals(scene(0, 0, 0, near = false).copyOfRange(0, sky)))
    }

    /** As the vision leaves, he walks again: the popup of a stone and the bubble of a joker show again with him, so that no text passes unseen. */
    @Test
    fun theTextsByThePathShowAgainAsTheVisionLeaves() {
        val at = 400.0
        val near = at - Config.NEAR_TURNS * PX_PER_TURN + 1
        val stone = mapOf("effort" to (2.5 - START_TIME) * DAY_LENGTH, "position" to near, "nextStele" to at, "nextJoker" to at + 1000)
        val joker = mapOf("effort" to (0.5 - START_TIME) * DAY_LENGTH, "position" to near, "nextJoker" to at)
        fun scene(keys: Map<String, Double>, vision: Vision.Phase?): IntArray {
            val game = Game(MemoryStore(keys), seed = 3)
            if (vision != null) game.summonVision()
            if (vision == Vision.Phase.LEAVING) game.vision!!.leave()
            val px = Pixels(geo.width, geo.height)
            SceneRenderer(geo).render(px, game, World(4).also { it.reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
            return px.data.copyOf()
        }
        for (keys in listOf(stone, joker)) {
            val plain = scene(keys, null)
            assertFalse(scene(keys, Vision.Phase.COMING).contentEquals(plain), "a vision that comes hides the text")
            assertTrue(scene(keys, Vision.Phase.LEAVING).contentEquals(plain), "the text shows again as the vision leaves")
        }
    }

    /** Each joker has a sprite of his own, a little shorter than Sisyphus: at most 15 by 22, so that it fits his card with its outline. */
    @Test
    fun everyJokerHasASprite() {
        assertEquals(JOKERS.size, JOKER_SPRITES.size)
        assertTrue(JOKER_SPRITES.all { it.width <= 15 && it.height <= 22 })
        assertEquals(JOKERS.size, JOKER_SPRITES.map { it.data.toList() }.toSet().size)
    }

    /** A joker stands by the path ahead, in every world: 3.5 turns ahead, before his bubble opens. */
    @Test
    fun theJokerAheadIsDrawn() {
        fun noon(place: Place, joker: Boolean): IntArray {
            val keys = mapOf(
                "effort" to (0.5 - START_TIME) * DAY_LENGTH, "place" to place.id.toDouble(),
                "nextJoker" to if (joker) 3.5 * PX_PER_TURN else 5000.0,
            )
            val px = Pixels(geo.width, geo.height)
            SceneRenderer(geo).render(px, Game(MemoryStore(keys), seed = 3), World(4).also { it.reset(geo, Weather.CLEAR) }, banner = 0.0, hud = false)
            return px.data.copyOf()
        }
        for (place in Place.entries) assertFalse(noon(place, joker = true).contentEquals(noon(place, joker = false)), place.title)
    }

    /**
     * Over a joker that he is near, a bubble tells his joke. On the narrow phones, the shortest too, every text of every joker,
     * with his name as its note, fits under the day and the obols. A vision hides the bubble.
     */
    @Test
    fun theBubbleOfAJokerFitsTheNarrowPhones() {
        for (phone in NARROW_PHONES) bubbleOfAJokerFits(phone.scene.let { SceneGeometry(it.w, it.h) })
    }

    private fun bubbleOfAJokerFits(small: SceneGeometry) {
        val at = 400.0
        val read = Config.READ_TURNS * PX_PER_TURN
        fun scene(joker: Joker, line: Int, page: Int, near: Boolean, vision: Boolean = false): IntArray {
            val keys = mapOf(
                "effort" to (0.5 - START_TIME) * DAY_LENGTH, "joker" to joker.id.toDouble(), "met.${joker.id}" to line.toDouble(),
                "position" to at - Config.NEAR_TURNS * PX_PER_TURN + page * read + 1, "nextJoker" to if (near) at else at + 1000,
            )
            val game = Game(MemoryStore(keys), seed = 3)
            if (vision) game.summonVision()
            val px = Pixels(small.width, small.height)
            SceneRenderer(small).render(px, game, World(4).also { it.reset(small, Weather.CLEAR) }, banner = 0.0, hud = false)
            return px.data.copyOf()
        }
        val hud = 13 * small.width
        val rock = floor(small.baseY + small.rockDy - small.rockR).toInt() * small.width
        val paper = rgb(0xF1E4C8)
        /** The pixels of paper between the day and the top of the rock: the bubble. */
        fun bubble(px: IntArray) = (hud until rock).count { px[it] == paper }
        for (joker in JOKERS) for ((line, joke) in joker.jokes.withIndex()) for (page in joke.texts.indices) {
            val near = scene(joker, line, page, near = true)
            val far = scene(joker, line, page, near = false)
            assertTrue(near.copyOfRange(0, hud).contentEquals(far.copyOfRange(0, hud)), "${joker.name}, line ${line + 1} covers the day")
            assertTrue(bubble(near) > bubble(far) + 100, "${joker.name}, line ${line + 1} shows no bubble")
        }
        assertEquals(bubble(scene(JOKERS[0], 0, 0, near = false)), bubble(scene(JOKERS[0], 0, 0, near = true, vision = true)))
    }

    /**
     * On the smallest phone, every text of a joker, with his name as its note, takes at most 4 rows of the bubble, and the first
     * text of a line at most 3. A first text shows while the joker stands ahead, higher on the slope: a shorter bubble stays above his head.
     */
    @Test
    fun theTextsOfTheJokersFitTheirRows() {
        val small = Layout(135, 292, 18, 13, 0, 0).scene.w
        for (joker in JOKERS) for ((line, joke) in joker.jokes.withIndex()) for ((page, text) in joke.texts.withIndex()) {
            val rows = popupRows(small, text, joker.name).size
            assertTrue(rows <= if (page == 0) 3 else 4, "${joker.name}, line ${line + 1}, text ${page + 1}: $rows rows")
        }
    }

    /** On the flat top, a joint between two courses runs straight across, under the sand. */
    @Test
    fun thePyramidCoursesLieLevelOnTheFlatTop() {
        val summit = SceneGeometry(133, 133).also { it.top = 10 }
        val px = noon(Place.CAIRO, summit)
        val columns = 90 until summit.width
        assertEquals(summit.surfaceY(columns.first, 0), summit.surfaceY(columns.last, 0))
        val level = (summit.surfaceY(columns.first, 0) + 4 until summit.height).filter { y ->
            columns.all { px[y * summit.width + it] == px[y * summit.width + columns.first] }
        }
        assertTrue(level.isNotEmpty())
    }

    /** Over a second of rain, how far the drops fall, and how many splashes they make. */
    private fun rain(place: Place): Pair<Double, Int> {
        val world = World(4).also { it.reset(geo, Weather.RAIN) }
        var fall = 0.0
        var splashes = 0
        repeat(10) {
            val before = world.drops.map { it.y }
            world.update(0.1, 0.0, 0.0, 0, Weather.RAIN, 0.5, 0f, 0, place)
            fall += world.drops.zip(before).filter { (d, y) -> d.y > y }.map { (d, y) -> d.y - y }.average()
            splashes += world.particles.size
        }
        return fall to splashes
    }

    @Test
    fun ashFallsSlowlyOnEtnaWhereRainFallsInCorinth() {
        val (rainFall, rainSplashes) = rain(Place.CORINTH)
        val (ashFall, ashSplashes) = rain(Place.ETNA)
        assertTrue(rainFall > 60, "rain falls $rainFall")
        assertTrue(ashFall < 30, "ash falls $ashFall")
        assertTrue(rainSplashes > 0)
        assertEquals(0, ashSplashes)
    }

    @Test
    fun rainFallsOnMountFujiAsInCorinth() {
        assertEquals(rain(Place.CORINTH), rain(Place.FUJI))
    }

    @Test
    fun sandBlowsByInCairoWhereRainFallsInCorinth() {
        val (sandFall, sandSplashes) = rain(Place.CAIRO)
        val world = World(4).also { it.reset(geo, Weather.RAIN) }
        val before = world.drops.map { it.x }
        world.update(0.1, 0.0, 0.0, 0, Weather.RAIN, 0.5, 0f, 0, Place.CAIRO)
        val blown = world.drops.zip(before).map { (d, x) -> abs(d.x - x) }.filter { it < 50 }.average() * 10
        assertTrue(sandFall < 30, "sand falls $sandFall")
        assertTrue(blown > 60, "sand blows $blown")
        assertEquals(0, sandSplashes)
    }
}
