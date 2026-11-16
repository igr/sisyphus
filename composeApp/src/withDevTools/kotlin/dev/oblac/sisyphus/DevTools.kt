package dev.oblac.sisyphus

/**
 * The build holds the dev screen. Debug builds show it.
 * The App Store and TestFlight builds of iOS take [DEV_TOOLS] from `src/withoutDevTools` instead: see `build.gradle.kts`.
 */
internal const val DEV_TOOLS = true
