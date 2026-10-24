import SwiftUI
import ComposeApp

/// Debug builds (Run in Xcode) get the dev screen; Release builds (Archive) never do.
#if DEBUG
private let isDebug = true
#else
private let isDebug = false
#endif

@main
struct SisyphusApp: App {
    @Environment(\.scenePhase) private var scenePhase
    private let controller = SisyphusController(dev: isDebug)

    var body: some Scene {
        WindowGroup {
            GameView(controller: controller)
                .ignoresSafeArea()
                .statusBarHidden(true)
                .persistentSystemOverlays(.hidden)
                .preferredColorScheme(.dark)
                .onChange(of: scenePhase) { phase in
                    if phase == .active { controller.resume() } else { controller.pause() }
                }
        }
    }
}

private struct GameView: UIViewControllerRepresentable {
    let controller: SisyphusController

    func makeUIViewController(context: Context) -> UIViewController { controller.viewController() }

    func updateUIViewController(_ controller: UIViewController, context: Context) {}
}
