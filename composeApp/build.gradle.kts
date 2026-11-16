import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(17)
    jvm()
    android {
        namespace = "dev.oblac.sisyphus.shared"
        compileSdk = 37
        minSdk = 26
        compilerOptions { jvmTarget = JvmTarget.JVM_17 }
        androidResources { enable = true }
        withHostTest { }
    }
    listOf(iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    sourceSets {
        // Xcode makes the App Store and TestFlight builds in its Release configuration. They leave out the dev screen,
        // because Apple rejects an app with hidden features: see stores/appstore/check.py.
        val devTools = providers.environmentVariable("CONFIGURATION").orNull != "Release"
        commonMain { kotlin.srcDir(if (devTools) "src/withDevTools/kotlin" else "src/withoutDevTools/kotlin") }
        commonMain.dependencies {
            api(libs.gamekit)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.resources)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.coroutines.swing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "dev.oblac.sisyphus.MainKt"
    }
}
