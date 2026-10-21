package dev.oblac.sisyphus.scene

import kotlin.math.sqrt

/**
 * Where things are in the display. The slope rises one pixel for every two pixels to the right.
 * The camera follows Sisyphus, so he and the rock stay put while the ground slides by.
 */
class SceneGeometry(val width: Int, val height: Int) {
    /** Sisyphus stands on the slope here: the top of ground pixel ([heroX], [baseY]). */
    val heroX = width / 2 - 8
    /** The slope meets the bottom edge an eighth of the way in. */
    val baseY = height - 1 - (heroX - width / 8) / 2
    /** The sea line runs just below his feet, so he stands against the sky. */
    val horizon = baseY + 6

    val rockR = 10.0
    /** Rock center relative to the hero, so that the rock rests on the slope. */
    val rockDx = 19.0
    val rockDy = -rockDx / 2 - rockR * sqrt(1.25)

    /** First ground row of column [x] when the hero has walked [p] pixels. */
    fun surfaceY(x: Int, p: Int): Int = baseY - ((x - heroX + p).floorDiv(2) - p.floorDiv(2))

    /** Ground coordinate of column [x], fixed to the hill rather than the screen. */
    fun worldX(x: Int, p: Int): Int = x - heroX + p
}
