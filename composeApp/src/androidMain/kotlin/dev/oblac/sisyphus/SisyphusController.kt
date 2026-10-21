package dev.oblac.sisyphus

import android.content.Context
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import dev.oblac.sisyphus.game.Game

private class PrefsStore(context: Context) : Store {
    private val prefs = context.getSharedPreferences("sisyphus", Context.MODE_PRIVATE)
    override fun load(key: String): Double? = prefs.getString(key, null)?.toDoubleOrNull()
    override fun save(key: String, value: Double) = prefs.edit().putString(key, value.toString()).apply()
}

private class ViewHaptics(private val view: View) : Haptics {
    override fun tick() {
        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
    }
}

/** The activity holds one of these; it saves the game when the app leaves the foreground. */
class SisyphusController(context: Context, view: View) {
    private val game = Game(PrefsStore(context))
    private val haptics = ViewHaptics(view)
    private val sound = TrackSound()

    @Composable
    fun Content() = SisyphusApp(game, haptics, sound)

    fun pause() = game.save()

    fun release() = sound.release()
}
