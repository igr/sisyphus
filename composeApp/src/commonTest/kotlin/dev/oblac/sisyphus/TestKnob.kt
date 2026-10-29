package dev.oblac.sisyphus

import dev.oblac.sisyphus.game.Game
import kotlin.math.abs

internal const val DT = 1 / 60.0

/** Drags the knob by [radians] in small steps, one step per frame. */
internal fun Game.turn(radians: Double, step: Double = 0.1) {
    var touch = 0.0
    knob.grab(touch)
    val steps = (abs(radians) / step).toInt()
    repeat(steps) {
        touch += if (radians > 0) step else -step
        knob.drag(touch)
        update(DT)
    }
}
