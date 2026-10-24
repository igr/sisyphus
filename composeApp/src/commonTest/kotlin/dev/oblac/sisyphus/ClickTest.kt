package dev.oblac.sisyphus

import dev.oblac.sisyphus.audio.readWav
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

/** A WAV file with one PCM data chunk: [values] of all [channels], one frame after the other. */
private fun wav(bits: Int, channels: Int, vararg values: Int, rate: Int = 44100): ByteArray {
    val width = bits / 8
    val size = values.size * width
    val out = ByteArray(44 + size)
    fun put(at: Int, value: Int, bytes: Int) = repeat(bytes) { out[at + it] = (value shr (8 * it)).toByte() }
    "RIFF".encodeToByteArray().copyInto(out, 0)
    put(4, 36 + size, 4)
    "WAVEfmt ".encodeToByteArray().copyInto(out, 8)
    put(16, 16, 4)
    put(20, 1, 2)
    put(22, channels, 2)
    put(24, rate, 4)
    put(28, rate * channels * width, 4)
    put(32, channels * width, 2)
    put(34, bits, 2)
    "data".encodeToByteArray().copyInto(out, 36)
    put(40, size, 4)
    values.forEachIndexed { i, v -> put(44 + i * width, v, width) }
    return out
}

class ClickTest {
    @Test
    fun readsSixteenBits() {
        assertContentEquals(floatArrayOf(0.5f, -1f, 0.25f), readWav(wav(16, 1, 16384, -32768, 8192)))
    }

    @Test
    fun mixesTwentyFourBitStereoToMono() {
        assertContentEquals(floatArrayOf(0.25f, -0.75f), readWav(wav(24, 2, 4194304, 0, -8388608, -4194304)))
    }

    @Test
    fun cutsTheQuietBeforeAndAfterTheSound() {
        assertContentEquals(floatArrayOf(0.5f, 0f, 0.25f), readWav(wav(16, 1, 0, 10, 16384, 0, 8192, -10, 0)))
    }

    @Test
    fun refusesWhatItCannotPlay() {
        assertFailsWith<IllegalArgumentException> { readWav(wav(8, 1, 100)) }
        assertFailsWith<IllegalArgumentException> { readWav(wav(16, 1, 100, rate = 48000)) }
    }
}
