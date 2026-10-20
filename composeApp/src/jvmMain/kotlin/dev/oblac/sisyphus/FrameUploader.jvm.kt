package dev.oblac.sisyphus

import androidx.compose.ui.graphics.asComposeImageBitmap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo

actual fun frameUploader(): FrameUploader {
    val bitmap = Bitmap()
    var bytes = ByteArray(0)
    var info = ImageInfo(1, 1, ColorType.BGRA_8888, ColorAlphaType.OPAQUE)
    return FrameUploader { px ->
        val n = px.width * px.height
        if (bytes.size != n * 4) {
            bytes = ByteArray(n * 4)
            info = ImageInfo(px.width, px.height, ColorType.BGRA_8888, ColorAlphaType.OPAQUE)
        }
        val data = px.data
        var j = 0
        for (i in 0 until n) {
            val c = data[i]
            bytes[j] = c.toByte()
            bytes[j + 1] = (c shr 8).toByte()
            bytes[j + 2] = (c shr 16).toByte()
            bytes[j + 3] = (c ushr 24).toByte()
            j += 4
        }
        bitmap.installPixels(info, bytes, px.width * 4)
        bitmap.asComposeImageBitmap()
    }
}
