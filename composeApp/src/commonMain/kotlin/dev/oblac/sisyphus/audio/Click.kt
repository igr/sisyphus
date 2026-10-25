package dev.oblac.sisyphus.audio

import sisyphus.composeapp.generated.resources.Res
import kotlin.math.abs

const val SAMPLE_RATE = 44100

/** The knob click, from composeResources/files/click.wav. */
suspend fun loadClick(): FloatArray = readWav(Res.readBytes("files/click.wav"))

/** The sound of buying in the Agora, from composeResources/files/instant-win.wav. */
suspend fun loadWin(): FloatArray = readWav(Res.readBytes("files/instant-win.wav"))

/**
 * Reads a WAV file of 44.1 kHz PCM, 16 or 24 bits, as mono samples in [-1, 1].
 * The quiet before and after the sound is cut off, so the sound plays at once.
 */
fun readWav(bytes: ByteArray): FloatArray {
    fun int(at: Int, size: Int) = (0 until size).fold(0) { v, i -> v or ((bytes[at + i].toInt() and 0xFF) shl (8 * i)) }
    fun id(at: Int) = bytes.decodeToString(at, at + 4)

    require(id(0) == "RIFF" && id(8) == "WAVE") { "Not a WAV file" }
    var channels = 1
    var bits = 16
    var at = 12
    while (id(at) != "data") {
        if (id(at) == "fmt ") {
            val tag = int(at + 8, 2)
            channels = int(at + 10, 2)
            bits = int(at + 22, 2)
            require((tag == 1 || tag == 0xFFFE) && (bits == 16 || bits == 24)) { "The WAV file must be 16 or 24 bit PCM" }
            require(int(at + 12, 4) == SAMPLE_RATE) { "The WAV file must be 44.1 kHz" }
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
    return mono.copyOfRange(mono.indexOfFirst { abs(it) >= quiet }, mono.indexOfLast { abs(it) >= quiet } + 1)
}
