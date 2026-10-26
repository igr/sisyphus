package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.game.Place
import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.rgb

/**
 * How a world looks: its [sky] over the day, the colors of its [sun] (its core low and high in the sky, its edge
 * low and high, and the glow around it low and high), its [ground] with [blades] of grass ([grass] says
 * how many) and [flowers], the [props] by the path, its four [rocks], its birds, and the [dust] at his feet.
 * Where [ash] is true, ash falls instead of rain. The far view and the beauty sights are drawn by each world's own code.
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
    val ash: Boolean,
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
    ash = false,
)

internal fun lookOf(place: Place): Look = when (place) {
    Place.CORINTH -> CORINTH_LOOK
    Place.ETNA -> ETNA_LOOK
    Place.UNDERWORLD -> UNDERWORLD_LOOK
}
