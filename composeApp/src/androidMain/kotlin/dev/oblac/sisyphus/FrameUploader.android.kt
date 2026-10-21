package dev.oblac.sisyphus

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun frameUploader(): FrameUploader {
    var bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    return FrameUploader { px ->
        if (bitmap.width != px.width || bitmap.height != px.height) {
            bitmap = Bitmap.createBitmap(px.width, px.height, Bitmap.Config.ARGB_8888).apply { setHasAlpha(false) }
        }
        bitmap.setPixels(px.data, 0, px.width, 0, 0, px.width, px.height)
        bitmap.asImageBitmap()
    }
}
