package dev.oblac.sisyphus

import androidx.compose.ui.window.ComposeUIViewController
import dev.oblac.sisyphus.game.Game
import platform.Foundation.NSUserDefaults
import platform.UIKit.UISelectionFeedbackGenerator
import platform.UIKit.UIViewController

private class DefaultsStore : Store {
    private val defaults = NSUserDefaults.standardUserDefaults
    override fun load(key: String): Double? =
        if (defaults.objectForKey("sisyphus.$key") != null) defaults.doubleForKey("sisyphus.$key") else null

    override fun save(key: String, value: Double) = defaults.setDouble(value, "sisyphus.$key")
}

private class IosHaptics : Haptics {
    private val generator = UISelectionFeedbackGenerator()
    override fun prepare() = generator.prepare()
    override fun tick() = generator.selectionChanged()
}

/** The Swift app holds one of these; it pauses the game when the app leaves the foreground. */
class SisyphusController {
    private val game = Game(DefaultsStore())
    private val haptics = IosHaptics()

    fun viewController(): UIViewController = ComposeUIViewController { SisyphusApp(game, haptics) }

    fun pause() = game.save()
}
