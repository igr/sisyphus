package dev.oblac.sisyphus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import dev.oblac.gamekit.enableFullScreen

class MainActivity : ComponentActivity() {
    private lateinit var sisyphus: SisyphusController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFullScreen()
        sisyphus = SisyphusController(
            this, window.decorView, dev = BuildConfig.DEBUG || BuildConfig.BETA, testStore = BuildConfig.DEBUG,
            build = BuildConfig.VERSION_CODE.toString(),
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
