rootProject.name = "Sisyphus"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        // gamekit, the library of the games, comes from GitHub Packages of igr/gamekit. The login is in ~/.gradle/gradle.properties: see the README of gamekit.
        maven("https://maven.pkg.github.com/igr/gamekit") {
            name = "gamekit"
            credentials(PasswordCredentials::class)
            content { includeGroup("dev.oblac.gamekit") }
        }
        google()
        mavenCentral()
    }
}

include(":composeApp", ":androidApp")
