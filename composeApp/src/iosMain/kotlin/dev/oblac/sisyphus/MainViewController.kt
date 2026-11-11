package dev.oblac.sisyphus

import androidx.compose.ui.window.ComposeUIViewController
import dev.oblac.gamekit.IosStore
import dev.oblac.sisyphus.game.Game
import platform.Foundation.NSBundle
import platform.UIKit.UIPasteboard
import platform.UIKit.UISelectionFeedbackGenerator
import platform.UIKit.UIViewController

private class IosHaptics : Haptics {
    private val generator = UISelectionFeedbackGenerator()
    override fun prepare() = generator.prepare()
    override fun tick() = generator.selectionChanged()
}

/** The build of the app, such as "41", from its Info.plist. */
private fun bundleBuild(): String? = NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion") as? String

/**
 * The Swift app holds one of these; it pauses the game when the app leaves the foreground, and resumes it on return.
 * Debug builds add the dev screen to the Agora, and buy worlds in the Test Store, for free.
 * The App Store and TestFlight builds do not hold either: see [DEV_TOOLS].
 */
class SisyphusController {
    private val game = Game(IosStore("sisyphus."))
    private val haptics = IosHaptics()
    private val sound = EngineSound()
    private val screen = Screen(game, dev = DEV_TOOLS, billing = RevenueCatBilling(testStore = DEV_TOOLS), build = bundleBuild())

    fun viewController(): UIViewController = ComposeUIViewController {
        SisyphusApp(game, haptics, sound, screen, copy = { UIPasteboard.generalPasteboard.string = it })
    }

    /** The game pauses, and every sound stops. A vision ends only when the app goes [away], not under Control Center or a banner. */
    fun pause(away: Boolean) {
        game.pause(away)
        sound.pause()
    }

    fun resume() = game.resume()
}
