package dev.oblac.sisyphus

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.oblac.sisyphus.game.Game
import java.util.prefs.Preferences

private class PreferencesStore : Store {
    private val prefs = Preferences.userRoot().node("dev/oblac/sisyphus")
    override fun load(key: String): Double? = prefs.get(key, null)?.toDoubleOrNull()
    override fun save(key: String, value: Double) = prefs.put(key, value.toString())
}

fun main() = application {
    val game = remember { Game(PreferencesStore()) }
    Window(
        onCloseRequest = {
            game.save()
            exitApplication()
        },
        title = "Sisyphus",
        state = rememberWindowState(width = 393.dp, height = 852.dp),
    ) {
        SisyphusApp(game, sound = remember { ClipSound() })
    }
}
