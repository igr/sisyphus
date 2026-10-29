package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.rgb

/** What falls on the days it rains in Corinth: rain, flakes of ash, or sand that the wind blows by. */
internal enum class Fall { RAIN, ASH, SAND }

/**
 * How a world looks: its [sky] over the day, the colors of its [sun] (its core low and high in the sky, its edge
 * low and high, and the glow around it low and high), its [ground] with [blades] of grass ([grass] says
 * how many) and [flowers], the [props] by the path, its four [rocks], its birds, the [dust] at his feet, and what [fall]s
 * on the days it rains in Corinth. [layers] gives the ground color of a column of the hill at a height, and at a depth
 * under the surface.
 * The far view and the beauty sights are drawn by each world's own code.
 */
internal class Look(
    val sky: List<Key>,
    val sun: IntArray,
    val ground: IntArray,
    val blades: IntArray,
    val grass: Float,
    val flowers: IntArray,
    val props: List<Prop>,
    val rocks: List<Rock>,
    val birdUp: Pixels,
    val birdFlat: Pixels,
    val dust: Int,
    val fall: Fall,
    val stone: Stone,
    val layers: (wx: Int, wy: Int, d: Int) -> Int = { wx, _, d -> soil(wx, d) },
)

internal val CORINTH_LOOK = Look(
    sky = KEYS,
    sun = intArrayOf(rgb(0xFF9A58), rgb(0xFFF6C8), rgb(0xE85A30), rgb(0xFFCF52), rgb(0xFFD08A), rgb(0xFFF6DA)),
    ground = GROUND,
    blades = intArrayOf(GROUND[0], GROUND[1]),
    grass = 1f,
    flowers = FLOWERS,
    props = PROPS,
    rocks = ROCKS,
    birdUp = BIRD_UP,
    birdFlat = BIRD_FLAT,
    dust = DUST,
    fall = Fall.RAIN,
    stone = LIMESTONE,
)

internal fun lookOf(place: Place): Look = when (place) {
    Place.CORINTH -> CORINTH_LOOK
    Place.ETNA -> ETNA_LOOK
    Place.UNDERWORLD -> UNDERWORLD_LOOK
    Place.CAIRO -> CAIRO_LOOK
}
