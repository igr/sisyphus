package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.gfx.lerpColor
import dev.oblac.sisyphus.gfx.luminance
import dev.oblac.sisyphus.gfx.rgb
import dev.oblac.sisyphus.gfx.shade

/** Colors of the sky, the sea and the light for one moment of the day. */
class Atmosphere(
    val skyTop: Int,
    val skyMid: Int,
    val skyHorizon: Int,
    val sea: Int,
    /** Multiplies everything that is not sky: ground, hero, rock. */
    val light: Int,
    val cloudLight: Int,
    val cloudBody: Int,
    val cloudShadow: Int,
    val stars: Float,
) {
    fun sky(g: Float): Int =
        if (g < 0.55f) lerpColor(skyTop, skyMid, g / 0.55f) else lerpColor(skyMid, skyHorizon, (g - 0.55f) / 0.45f)
}

private class Key(
    val t: Double,
    val top: Int, val mid: Int, val horizon: Int, val sea: Int, val light: Int,
    val cloudLight: Int, val cloudBody: Int, val cloudShadow: Int,
    val stars: Float,
)

private fun key(t: Double, top: Int, mid: Int, horizon: Int, sea: Int, light: Int, cl: Int, cb: Int, cs: Int, stars: Float) =
    Key(t, rgb(top), rgb(mid), rgb(horizon), rgb(sea), rgb(light), rgb(cl), rgb(cb), rgb(cs), stars)

private val KEYS = listOf(
    key(0.00, 0x070B22, 0x0E1638, 0x1A2550, 0x0B1530, 0x4A5590, 0x3A4468, 0x2A3352, 0x1C2340, 1.0f),
    key(0.21, 0x0E1236, 0x262A5C, 0x4A3A6A, 0x161E40, 0x5A5C90, 0x5A5280, 0x43406A, 0x2E2C50, 0.7f),
    key(0.255, 0x28337A, 0x7A5C96, 0xF49A6A, 0x4A4C7C, 0xC89890, 0xFFD2B8, 0xE4A0A0, 0x9A6A8A, 0.15f),
    key(0.30, 0x3F78CC, 0x86B0E4, 0xF6D2A2, 0x4474AE, 0xF2DCCB, 0xFFF0E0, 0xF4D4C8, 0xB4A4B8, 0f),
    key(0.38, 0x3A80DA, 0x78B4EC, 0xC6E6F6, 0x3576B8, 0xFFFFFF, 0xFFFFFF, 0xEEF3F8, 0xBCC8DA, 0f),
    key(0.62, 0x3878D2, 0x74ACE6, 0xC2E0F2, 0x3270B0, 0xFFFFFF, 0xFFFFFF, 0xEEF3F8, 0xBCC8DA, 0f),
    key(0.70, 0x4470C0, 0x9AAAD4, 0xF4CC92, 0x4A6CA2, 0xFBE6C8, 0xFFF2DA, 0xF6DCC0, 0xBCA4A8, 0f),
    key(0.75, 0x34367A, 0xB05C7A, 0xF6844C, 0x5A4676, 0xEAA48C, 0xFFC496, 0xF09474, 0x8A5476, 0.1f),
    key(0.80, 0x171A46, 0x3A2C5C, 0x74466A, 0x221C42, 0x8070A0, 0x6A5A86, 0x4E4470, 0x342E54, 0.55f),
    key(0.86, 0x0B0F2C, 0x141C44, 0x22305C, 0x101838, 0x505C98, 0x3E4870, 0x2E3758, 0x1E2644, 0.95f),
    key(1.00, 0x070B22, 0x0E1638, 0x1A2550, 0x0B1530, 0x4A5590, 0x3A4468, 0x2A3352, 0x1C2340, 1.0f),
)

private fun gray(c: Int, amount: Float, darken: Float): Int {
    val l = luminance(c)
    val g = lerpColor(c, rgb(l, l, l + 8), amount)
    val k = (255 * (1 - darken)).toInt()
    return shade(g, rgb(k, k, k))
}

/** The atmosphere at [t] (0 is midnight, 0.5 is noon), dimmed by [overcast] clouds. */
fun atmosphere(t: Double, overcast: Float): Atmosphere {
    val i = KEYS.indexOfLast { it.t <= t }.coerceIn(0, KEYS.size - 2)
    val a = KEYS[i]
    val b = KEYS[i + 1]
    val k = ((t - a.t) / (b.t - a.t)).toFloat()
    val o = overcast
    fun sky(x: Int, y: Int) = gray(lerpColor(x, y, k), o * 0.85f, o * 0.25f)
    fun cloud(x: Int, y: Int) = gray(lerpColor(x, y, k), o * 0.9f, o * 0.42f)
    return Atmosphere(
        skyTop = sky(a.top, b.top),
        skyMid = sky(a.mid, b.mid),
        skyHorizon = sky(a.horizon, b.horizon),
        sea = sky(a.sea, b.sea),
        light = gray(lerpColor(a.light, b.light, k), o * 0.2f, o * 0.2f),
        cloudLight = cloud(a.cloudLight, b.cloudLight),
        cloudBody = cloud(a.cloudBody, b.cloudBody),
        cloudShadow = cloud(a.cloudShadow, b.cloudShadow),
        stars = (a.stars + (b.stars - a.stars) * k) * (1 - o),
    )
}
