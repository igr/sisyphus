package dev.oblac.sisyphus

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.oblac.gamekit.DesktopStore
import dev.oblac.sisyphus.game.Game

fun main() = application {
    val game = remember { Game(DesktopStore("dev/oblac/sisyphus")) }
    Window(
        onCloseRequest = {
            game.save()
            exitApplication()
        },
        title = "Sisyphus",
        state = rememberWindowState(width = 393.dp, height = 852.dp),
    ) {
        SisyphusApp(game, sound = remember { ClipSound() }, screen = remember { Screen(game, dev = true) })
    }
}
