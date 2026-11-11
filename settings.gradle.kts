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
        // gamekit, the library of the games in ~/prj/igr/gamekit, comes from the local Maven repository: see its README.
        mavenLocal { content { includeGroup("dev.oblac.gamekit") } }
        google()
        mavenCentral()
    }
}

include(":composeApp", ":androidApp")
