package dev.oblac.sisyphus.audio

import sisyphus.composeapp.generated.resources.Res
import kotlin.math.abs

const val SAMPLE_RATE = 44100

/** The knob click, from composeResources/files/click.wav. */
suspend fun loadClick(): FloatArray = readWav(Res.readBytes("files/click.wav"))

/** The click of every other tap: menus, buttons, the help. From composeResources/files/ui-click.wav. */
suspend fun loadUi(): FloatArray = readWav(Res.readBytes("files/ui-click.wav"))

/** The sound of buying in the Agora, from composeResources/files/instant-win.wav. */
suspend fun loadWin(): FloatArray = readWav(Res.readBytes("files/instant-win.wav"))

/** The fanfare on arriving in another world, from composeResources/files/success-fanfare-trumpets.wav. */
suspend fun loadFanfare(): FloatArray = readWav(Res.readBytes("files/success-fanfare-trumpets.wav"))

/**
 * Reads a WAV file of PCM, 16 or 24 bits, as mono samples in [-1, 1] at [SAMPLE_RATE].
 * The quiet before and after the sound is cut off, so the sound plays at once.
 */
fun readWav(bytes: ByteArray): FloatArray {
    fun int(at: Int, size: Int) = (0 until size).fold(0) { v, i -> v or ((bytes[at + i].toInt() and 0xFF) shl (8 * i)) }
    fun id(at: Int) = bytes.decodeToString(at, at + 4)

    require(id(0) == "RIFF" && id(8) == "WAVE") { "Not a WAV file" }
    var channels = 1
    var rate = SAMPLE_RATE
    var bits = 16
    var at = 12
    while (id(at) != "data") {
        if (id(at) == "fmt ") {
            val tag = int(at + 8, 2)
            channels = int(at + 10, 2)
            rate = int(at + 12, 4)
            bits = int(at + 22, 2)
            require((tag == 1 || tag == 0xFFFE) && (bits == 16 || bits == 24)) { "The WAV file must be 16 or 24 bit PCM" }
        }
        val size = int(at + 4, 4)
        at += 8 + size + size % 2
    }
    val width = bits / 8
    val data = at + 8
    fun sample(i: Int) = (int(data + i * width, width) shl (32 - bits) shr (32 - bits)) / (1 shl (bits - 1)).toFloat()
    val mono = FloatArray(int(at + 4, 4) / (width * channels)) { frame ->
        var sum = 0f
        repeat(channels) { sum += sample(frame * channels + it) }
        sum / channels
    }
    val quiet = mono.maxOf { abs(it) } / 100
    val sound = mono.copyOfRange(mono.indexOfFirst { abs(it) >= quiet }, mono.indexOfLast { abs(it) >= quiet } + 1)
    return if (rate == SAMPLE_RATE) sound else resample(sound, rate)
}

/** From [rate] to [SAMPLE_RATE], each new sample on the line between its two old neighbours. */
private fun resample(samples: FloatArray, rate: Int) = FloatArray((samples.size.toLong() * SAMPLE_RATE / rate).toInt()) {
    val t = it.toDouble() * rate / SAMPLE_RATE
    val k = t.toInt()
    val f = (t - k).toFloat()
    samples[k] * (1 - f) + samples[minOf(k + 1, samples.size - 1)] * f
}
