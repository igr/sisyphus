import SwiftUI
import ComposeApp

@main
struct SisyphusApp: App {
    @Environment(\.scenePhase) private var scenePhase
    private let controller = SisyphusController()

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
