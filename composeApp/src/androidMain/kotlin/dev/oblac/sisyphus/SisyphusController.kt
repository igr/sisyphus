package dev.oblac.sisyphus

import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import dev.oblac.gamekit.AndroidStore
import dev.oblac.sisyphus.game.Game

private class ViewHaptics(private val view: View) : Haptics {
    override fun tick() {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }
}

/**
 * The activity holds one of these; it pauses the game when the app leaves the foreground, and resumes it on return.
 * Debug and beta builds pass [dev], which adds the dev screen to the Agora. The settings show [build].
 */
class SisyphusController(context: Context, view: View, dev: Boolean, build: String) {
    private val game = Game(AndroidStore(context.getSharedPreferences("sisyphus", Context.MODE_PRIVATE)))
    private val haptics = ViewHaptics(view)
    private val sound = TrackSound()
    private val screen = Screen(game, dev = dev, build = build)

    @Composable
    fun Content() = SisyphusApp(game, haptics, sound, screen)

    /** Back closes the Agora; tells whether it did. */
    fun back() = screen.back()

    /** The game pauses, and every sound stops: the frames that still come until the app is gone keep them silent. */
    fun pause() {
        game.pause()
        sound.pause()
    }

    fun resume() = game.resume()

    fun release() = sound.release()
}
