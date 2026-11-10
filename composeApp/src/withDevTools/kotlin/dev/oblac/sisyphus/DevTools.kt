package dev.oblac.sisyphus

/**
 * The build holds the dev screen and the key of RevenueCat's Test Store. Debug builds use them.
 * The App Store and TestFlight builds of iOS take [DEV_TOOLS] from `src/withoutDevTools` instead: see `build.gradle.kts`.
 */
internal const val DEV_TOOLS = true
