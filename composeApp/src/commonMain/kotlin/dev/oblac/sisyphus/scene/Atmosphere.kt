package dev.oblac.sisyphus.scene

import dev.oblac.sisyphus.game.Place
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
    /** How dark it is: about 0 at noon and 0.7 at midnight. Fire and lights glow brighter in the dark. */
    val darkness: Float get() = 1f - luminance(light) / 255f

    fun sky(g: Float): Int =
        if (g < 0.55f) lerpColor(skyTop, skyMid, g / 0.55f) else lerpColor(skyMid, skyHorizon, (g - 0.55f) / 0.45f)
}

/** The colors of one moment of the day, at time [t]. */
internal class Key(
    val t: Double,
    val top: Int, val mid: Int, val horizon: Int, val sea: Int, val light: Int,
    val cloudLight: Int, val cloudBody: Int, val cloudShadow: Int,
    val stars: Float,
)

private fun key(t: Double, top: Int, mid: Int, horizon: Int, sea: Int, light: Int, cl: Int, cb: Int, cs: Int, stars: Float) =
    Key(t, rgb(top), rgb(mid), rgb(horizon), rgb(sea), rgb(light), rgb(cl), rgb(cb), rgb(cs), stars)

/** The day of Corinth. */
internal val KEYS = listOf(
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

/** The day of Etna: hazy, with smoke in the air, a red dusk, and the lava glowing under a dark night with few stars. */
internal val ETNA_KEYS = listOf(
    key(0.00, 0x0A0614, 0x170C20, 0x3A1626, 0x140A10, 0x584668, 0x3C2E3E, 0x2C202E, 0x1C121E, 0.6f),
    key(0.21, 0x120A22, 0x2C1838, 0x5A2A3A, 0x1C1018, 0x685272, 0x5C4254, 0x433042, 0x2C1E2C, 0.45f),
    key(0.255, 0x3A2A5A, 0x8A5070, 0xF0906A, 0x3E2A2C, 0xC8968A, 0xF6C2AA, 0xD69292, 0x8C5C6A, 0.1f),
    key(0.30, 0x6A84B4, 0xAAB4C8, 0xF0D0A8, 0x4E403A, 0xF0DCC8, 0xF6EEE2, 0xE2D6CA, 0xAA9E98, 0f),
    key(0.38, 0x6A90C8, 0xA6C0DA, 0xE4DCCA, 0x4C4440, 0xFAF4EA, 0xF6F4F0, 0xE0DCD6, 0xACA8A4, 0f),
    key(0.62, 0x6890C4, 0xA2BCD6, 0xE2D8C6, 0x4C4440, 0xFAF4EA, 0xF6F4F0, 0xE0DCD6, 0xACA8A4, 0f),
    key(0.70, 0x7086B4, 0xB4B2C0, 0xF0C890, 0x4E3E38, 0xF6E0C0, 0xFAEAD2, 0xEAD2BA, 0xAA9690, 0f),
    key(0.75, 0x4A2E5A, 0xA84A52, 0xF06A3A, 0x3E2626, 0xE8987C, 0xFFB08A, 0xE87A5C, 0x7A3A46, 0.05f),
    key(0.80, 0x1E1230, 0x4A2238, 0x8A3A3A, 0x221416, 0x7E5C72, 0x5C3C4C, 0x42283A, 0x2C1A2A, 0.35f),
    key(0.86, 0x0C0818, 0x1A0E22, 0x42182A, 0x160A10, 0x584668, 0x402E3C, 0x2E1E2C, 0x1E1220, 0.55f),
    key(1.00, 0x0A0614, 0x170C20, 0x3A1626, 0x140A10, 0x584668, 0x3C2E3E, 0x2C202E, 0x1C121E, 0.6f),
)

/** The day of The Underworld: a misty cave, dim even at noon, and a black night with a few faint stars. */
internal val UNDERWORLD_KEYS = listOf(
    key(0.00, 0x05040A, 0x0A0814, 0x141420, 0x06080C, 0x3A3E54, 0x1E2230, 0x161A26, 0x0E1018, 0.35f),
    key(0.21, 0x080A12, 0x12141E, 0x22283A, 0x0A0E14, 0x4A4E66, 0x2A3040, 0x1E2432, 0x141822, 0.25f),
    key(0.255, 0x10141E, 0x22303A, 0x4A6060, 0x141C22, 0x7A8890, 0x5A6E70, 0x42545A, 0x2A363C, 0.08f),
    key(0.30, 0x1A2430, 0x3A5058, 0x7A9894, 0x1E2A30, 0xA0B0B0, 0x8AA0A0, 0x6A8084, 0x4A5A60, 0f),
    key(0.38, 0x1E2A36, 0x46606A, 0x8AACA8, 0x223038, 0xB4C4C0, 0x9AB4B0, 0x7A9494, 0x56686C, 0f),
    key(0.62, 0x1E2A36, 0x445E68, 0x88AAA6, 0x223038, 0xB4C4C0, 0x9AB4B0, 0x7A9494, 0x56686C, 0f),
    key(0.70, 0x1C2430, 0x404E5E, 0x8A8CA0, 0x202834, 0xA8AAB8, 0x9A9CAC, 0x7A7C8E, 0x585A6A, 0f),
    key(0.75, 0x141420, 0x2E2A40, 0x5A4A6A, 0x161620, 0x7A7090, 0x5A5070, 0x42385A, 0x2A2440, 0.05f),
    key(0.80, 0x0A0A14, 0x181828, 0x2E2C44, 0x0C0C16, 0x504E6A, 0x2E2C42, 0x22203A, 0x16142A, 0.2f),
    key(0.86, 0x06060C, 0x0E0C18, 0x1A1828, 0x08080E, 0x3E3E56, 0x22222E, 0x1A1A26, 0x101018, 0.3f),
    key(1.00, 0x05040A, 0x0A0814, 0x141420, 0x06080C, 0x3A3E54, 0x1E2230, 0x161A26, 0x0E1018, 0.35f),
)

private fun gray(c: Int, amount: Float, darken: Float): Int {
    val l = luminance(c)
    val g = lerpColor(c, rgb(l, l, l + 8), amount)
    val k = (255 * (1 - darken)).toInt()
    return shade(g, rgb(k, k, k))
}

/** The atmosphere of [place] at [t] (0 is midnight, 0.5 is noon), dimmed by [overcast] clouds. */
fun atmosphere(t: Double, overcast: Float, place: Place = Place.CORINTH): Atmosphere {
    val keys = lookOf(place).sky
    val i = keys.indexOfLast { it.t <= t }.coerceIn(0, keys.size - 2)
    val a = keys[i]
    val b = keys[i + 1]
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
