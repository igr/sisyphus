package dev.oblac.sisyphus

/**
 * The App Store and TestFlight builds of iOS do not hold the dev screen or the key of RevenueCat's Test Store:
 * Apple rejects an app with hidden features. With this constant false, the compiler leaves their code out.
 */
internal const val DEV_TOOLS = false
