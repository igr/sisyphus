import SwiftUI
import ComposeApp

/// Debug builds (Run in Xcode) and `just beta` builds get the dev screen; `just release` builds never do.
#if DEBUG || BETA
private let showDev = true
#else
private let showDev = false
#endif

/// Debug builds buy worlds in RevenueCat's Test Store, for free. The SDK does not stop a release build that uses it, so this check must.
#if DEBUG
private let testStore = true
#else
private let testStore = false
#endif

@main
struct SisyphusApp: App {
    @Environment(\.scenePhase) private var scenePhase
    private let controller = SisyphusController(dev: showDev, testStore: testStore)

    var body: some Scene {
        WindowGroup {
            GameView(controller: controller)
                .ignoresSafeArea()
                .statusBarHidden(true)
                .persistentSystemOverlays(.hidden)
                .preferredColorScheme(.dark)
                .onChange(of: scenePhase) { phase in
                    if phase == .active { controller.resume() } else { controller.pause(away: phase == .background) }
                }
        }
    }
}

private struct GameView: UIViewControllerRepresentable {
    let controller: SisyphusController

    func makeUIViewController(context: Context) -> UIViewController { controller.viewController() }

    func updateUIViewController(_ controller: UIViewController, context: Context) {}
}
