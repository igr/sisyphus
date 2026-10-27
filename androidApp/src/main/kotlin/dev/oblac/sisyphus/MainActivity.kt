package dev.oblac.sisyphus

import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {
    private lateinit var sisyphus: SisyphusController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
        volumeControlStream = AudioManager.STREAM_MUSIC
        sisyphus = SisyphusController(
            this, window.decorView, dev = BuildConfig.DEBUG || BuildConfig.BETA, testStore = BuildConfig.DEBUG,
            version = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
        )
        setContent { sisyphus.Content() }
        onBackPressedDispatcher.addCallback(this) {
            if (sisyphus.back()) return@addCallback
            isEnabled = false
            onBackPressedDispatcher.onBackPressed()
            isEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        sisyphus.resume()
    }

    override fun onPause() {
        super.onPause()
        sisyphus.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        sisyphus.release()
    }
}
