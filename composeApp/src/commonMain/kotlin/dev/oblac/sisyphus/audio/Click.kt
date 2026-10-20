package dev.oblac.sisyphus.audio

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

const val SAMPLE_RATE = 44100

/** Number of click variants. They differ a little in pitch, so a fast turn does not sound like a machine. */
const val CLICKS = 3

private val PITCH = doubleArrayOf(1.0, 1.05, 0.96)
private const val LENGTH = 0.028
private const val SNAP = 0.0035
private const val LOUDNESS = 0.6

/**
 * One detent of the knob, as mono samples in [-1, 1]: a short noise burst, two quick tones,
 * and a second, softer snap when the detent falls into place.
 */
fun click(variant: Int, sampleRate: Int = SAMPLE_RATE): FloatArray {
    val pitch = PITCH[variant.mod(CLICKS)]
    val random = Random(variant + 1)
    val n = (sampleRate * LENGTH).toInt()
    val attack = 8.0
    val release = sampleRate * 0.002
    val raw = DoubleArray(n)
    var low = 0.0
    for (i in 0 until n) {
        val t = i.toDouble() / sampleRate
        val noise = random.nextDouble(-1.0, 1.0)
        low += (noise - low) * 0.25
        val hiss = noise - low
        var s = hiss * exp(-t / 0.0006) * 0.8 +
            sin(2 * PI * 2900 * pitch * t) * exp(-t / 0.0018) * 0.5 +
            sin(2 * PI * 1250 * pitch * t) * exp(-t / 0.004) * 0.28 +
            sin(2 * PI * 210 * pitch * t) * exp(-t / 0.006) * 0.12
        val u = t - SNAP
        if (u >= 0) s += hiss * exp(-u / 0.0004) * 0.35 + sin(2 * PI * 3800 * pitch * u) * exp(-u / 0.001) * 0.2
        raw[i] = s * minOf(1.0, (i + 1) / attack, (n - i) / release)
    }
    val gain = LOUDNESS / raw.maxOf { abs(it) }
    return FloatArray(n) { (raw[it] * gain).toFloat() }
}
