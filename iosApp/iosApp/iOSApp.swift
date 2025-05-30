import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        CommonModuleKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}