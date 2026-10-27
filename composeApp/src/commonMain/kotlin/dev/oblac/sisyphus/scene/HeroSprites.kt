package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.gfx.Pixels
import dev.oblac.sisyphus.gfx.rgb
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

private val SKIN = rgb(0xE0A070)
private val SKIN_FAR = rgb(0xA86840)
private val TUNIC = rgb(0xF4ECDA)
private val TUNIC_SHADOW = rgb(0xC4B496)
private val BELT = rgb(0xB4482A)
private val HAIR = rgb(0x3C2618)
private val SANDAL = rgb(0x6A4024)
private val SANDAL_FAR = rgb(0x4A2C18)
/** His eye: it looks up the hill while he pushes, and down it when he turns around at the top. */
internal val HERO_EYE = rgb(0x2A1A10)
private val OUTLINE = rgb(0x1C120C)

private class P(val x: Double, val y: Double) {
    operator fun plus(o: P) = P(x + o.x, y + o.y)
    operator fun minus(o: P) = P(x - o.x, y - o.y)
    operator fun times(k: Double) = P(x * k, y * k)
    val length get() = sqrt(x * x + y * y)
}

/**
 * Sisyphus, drawn from a tiny skeleton. Each walk frame moves the planted foot back by exactly
 * one pixel, so the feet stick to the sliding ground. Frames are rendered once, then reused.
 */
class HeroSprites(rockDx: Double, rockDy: Double, private val rockR: Double) {
    private val rock = P(rockDx, rockDy)

    val walk: List<Pixels> = List(WALK_FRAMES) { draw(it.toDouble() / WALK_FRAMES, walking = true, breath = 0.0) }
    val idle: List<Pixels> = listOf(draw(0.0, walking = false, breath = 0.0), draw(0.0, walking = false, breath = 1.0))
    /** Standing up straight at the top, as the rock slips back. */
    val stand: Pixels = standing(facing = 1.0)
    /** Turned around at the top, to watch the rock roll down the hill. */
    val turned: Pixels = standing(facing = -1.0)

    /** At the top, as the rock rolls back [rolled] pixels: he stands up when it slips away, and turns around once it is past him. */
    fun atTop(rolled: Double): Pixels? = when {
        rolled > rock.x -> turned
        rolled > 0 -> stand
        else -> null
    }

    private fun draw(phase: Double, walking: Boolean, breath: Double): Pixels {
        val px = Pixels(FRAME_W, FRAME_H)
        val o = P(ORIGIN_X - 1.0, ORIGIN_Y - 1.0)
        fun seg(a: P, b: P, w: Double, c: Int) = px.segment(a.x + o.x, a.y + o.y, b.x + o.x, b.y + o.y, w, c)
        fun disc(a: P, r: Double, c: Int) = px.disc(a.x + o.x, a.y + o.y, r, c)

        val s = sin(phase * 2 * PI)
        val bob = if (walking) -0.7 * s * s else 0.0
        val hip = P(-1.5, -10.2 + bob)
        val dir = P(sin(LEAN), -cos(LEAN))
        val down = P(cos(LEAN), sin(LEAN))
        val shoulder = hip + dir * 7.0 + P(0.0, -0.6 * breath)
        val head = shoulder + P(1.4, -2.9)

        val nearFoot = if (walking) foot(phase) else P(1.2, -0.6)
        val farFoot = if (walking) foot(phase + 0.5) else P(-3.2, 1.6)
        val nearAnkle = nearFoot + P(0.0, -1.1)
        val farAnkle = farFoot + P(0.0, -1.1)
        val nearKnee = joint(hip, nearAnkle, THIGH, SHIN, -1.0)
        val farKnee = joint(hip, farAnkle, THIGH, SHIN, -1.0)

        val nearHand = onRock(NEAR_HAND)
        val farHand = onRock(FAR_HAND)
        val nearElbow = joint(shoulder, nearHand, UPPER_ARM, FOREARM, 1.0)
        val farElbow = joint(shoulder, farHand, UPPER_ARM, FOREARM, 1.0)

        // far side, behind the body
        seg(hip, farKnee, 2.2, SKIN_FAR)
        seg(farKnee, farAnkle, 2.0, SKIN_FAR)
        sandal(::seg, farFoot, SANDAL_FAR)
        seg(shoulder, farElbow, 1.9, SKIN_FAR)
        seg(farElbow, farHand, 1.8, SKIN_FAR)
        seg(hip, hip + (farKnee - hip) * 0.6, 3.8, TUNIC_SHADOW)

        // body
        seg(hip, shoulder, 4.6, TUNIC)
        seg(hip + down * 1.4, shoulder + down * 1.4 - dir * 0.8, 1.4, TUNIC_SHADOW)
        val belt = hip + dir * 1.6
        seg(belt - down * 2.3, belt + down * 2.3, 1.3, BELT)

        // near side
        seg(hip, nearKnee, 2.2, SKIN)
        seg(nearKnee, nearAnkle, 2.0, SKIN)
        sandal(::seg, nearFoot, SANDAL)
        seg(hip, hip + (nearKnee - hip) * 0.6, 3.8, TUNIC)
        seg(shoulder, nearElbow, 1.9, SKIN)
        seg(nearElbow, nearHand, 1.8, SKIN)

        head(px, o, shoulder, head, facing = 1.0)
        return px.outlined(OUTLINE)
    }

    /** Upright, with the arms down, facing up the hill when [facing] is 1 and down it when -1. The feet stay on the slope. */
    private fun standing(facing: Double): Pixels {
        val px = Pixels(FRAME_W, FRAME_H)
        val o = P(ORIGIN_X - 1.0, ORIGIN_Y - 1.0)
        fun seg(a: P, b: P, w: Double, c: Int) = px.segment(a.x + o.x, a.y + o.y, b.x + o.x, b.y + o.y, w, c)

        val dir = P(sin(STAND_LEAN) * facing, -cos(STAND_LEAN))
        val down = P(cos(STAND_LEAN) * facing, sin(STAND_LEAN))
        val hip = P(-0.2 * facing, -12.9)
        val shoulder = hip + dir * 7.0
        val head = shoulder + P(0.5 * facing, -3.0)

        val nearFoot = P(0.9, -0.45)
        val farFoot = P(-1.3, 0.65)
        val nearAnkle = nearFoot + P(0.0, -1.1)
        val farAnkle = farFoot + P(0.0, -1.1)
        val nearKnee = joint(hip, nearAnkle, THIGH, SHIN, -facing)
        val farKnee = joint(hip, farAnkle, THIGH, SHIN, -facing)

        val nearHand = shoulder + P(1.0 * facing, 8.4)
        val farHand = shoulder + P(-0.6 * facing, 8.2)
        val nearElbow = joint(shoulder, nearHand, UPPER_ARM, FOREARM, facing)
        val farElbow = joint(shoulder, farHand, UPPER_ARM, FOREARM, facing)

        // far side, behind the body
        seg(hip, farKnee, 2.2, SKIN_FAR)
        seg(farKnee, farAnkle, 2.0, SKIN_FAR)
        sandal(::seg, farFoot, SANDAL_FAR)
        seg(shoulder, farElbow, 1.9, SKIN_FAR)
        seg(farElbow, farHand, 1.8, SKIN_FAR)

        // body
        seg(hip, shoulder, 4.6, TUNIC)
        seg(hip + down * 1.4, shoulder + down * 1.4 - dir * 0.8, 1.4, TUNIC_SHADOW)
        val belt = hip + dir * 1.6
        seg(belt - down * 2.3, belt + down * 2.3, 1.3, BELT)

        // near side
        seg(hip, nearKnee, 2.2, SKIN)
        seg(nearKnee, nearAnkle, 2.0, SKIN)
        sandal(::seg, nearFoot, SANDAL)
        seg(hip, hip + (nearKnee - hip) * 0.6, 3.8, TUNIC)
        seg(shoulder, nearElbow, 1.9, SKIN)
        seg(nearElbow, nearHand, 1.8, SKIN)

        head(px, o, shoulder, head, facing)
        return px.outlined(OUTLINE)
    }

    /** The neck and the head, with the hair and the beard, the nose and the eye, looking the way of [facing]. Drawn last, so the face is never hidden. */
    private fun head(px: Pixels, o: P, shoulder: P, head: P, facing: Double) {
        px.segment(shoulder.x + o.x, shoulder.y + o.y, head.x + o.x, head.y + o.y, 1.8, SKIN)
        px.disc(head.x + o.x, head.y + o.y, 2.7, SKIN)
        px.disc(head.x + 2.5 * facing + o.x, head.y + 0.2 + o.y, 0.6, SKIN)
        for (j in 0 until px.height) for (i in 0 until px.width) {
            if (px[i, j] != SKIN) continue
            val dx = (i + 0.5 - o.x - head.x) * facing
            val dy = j + 0.5 - o.y - head.y
            if (dx * dx + dy * dy > 2.8 * 2.8) continue
            if (dy < -1.0 || (dx < -0.5 && dy < 1.3) || (dy > 0.8 && dx > -1.1)) px[i, j] = HAIR
        }
        px.disc(head.x + 1.2 * facing + o.x, head.y - 0.4 + o.y, 0.5, HERO_EYE)
    }

    private fun onRock(angle: Double) = rock + P(cos(angle), sin(angle)) * (rockR - 0.3)

    private fun sandal(seg: (P, P, Double, Int) -> Unit, foot: P, color: Int) {
        seg(foot + P(-0.7, -0.55), foot + P(1.6, -1.7), 1.3, color)
    }

    /** Foot position for a leg at [phase]: half the cycle planted on the slope, half swinging forward. */
    private fun foot(phase: Double): P {
        val p = phase - floor(phase)
        val x: Double
        var lift = 0.0
        if (p < 0.5) {
            x = FRONT + (BACK - FRONT) * (p / 0.5)
        } else {
            val s = (p - 0.5) / 0.5
            x = BACK + (FRONT - BACK) * s * s * (3 - 2 * s)
            lift = 2.6 * sin(PI * s)
        }
        return P(x, -x / 2 - lift)
    }

    /** Two-bone inverse kinematics. [bend] picks the side the middle joint goes to. */
    private fun joint(from: P, to: P, l1: Double, l2: Double, bend: Double): P {
        val v = to - from
        val d = v.length.coerceIn(0.01, l1 + l2 - 0.01)
        val angle = acos(((l1 * l1 + d * d - l2 * l2) / (2 * l1 * d)).coerceIn(-1.0, 1.0))
        val base = atan2(v.y, v.x) + bend * angle
        return from + P(cos(base), sin(base)) * l1
    }

    companion object {
        const val WALK_FRAMES = 12
        /** Where the hero stands inside a frame. */
        const val ORIGIN_X = 10
        const val ORIGIN_Y = 31
        private const val FRAME_W = 26
        private const val FRAME_H = 34

        private const val LEAN = 48.0 * PI / 180
        private const val STAND_LEAN = 6.0 * PI / 180
        private const val THIGH = 5.9
        private const val SHIN = 6.1
        private const val UPPER_ARM = 4.5
        private const val FOREARM = 4.6
        private const val FRONT = 2.2
        private const val BACK = -3.8
        private const val NEAR_HAND = 136.0 * PI / 180
        private const val FAR_HAND = 127.0 * PI / 180
    }
}
