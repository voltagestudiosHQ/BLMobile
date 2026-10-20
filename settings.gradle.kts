pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://repo.opencollab.dev/maven-snapshots")
        maven("https://repo.opencollab.dev/maven-releases")
    }
    versionCatalogs {
        create("leviLibs") {
            from(files("BedrockModule/vendor/LeviLaunchroid/gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "BedroomLauncher"
include(":BedroomLauncherMobile")
include(":LWJGL")
include(":LWJGL:patches")
project(":LWJGL:patches").projectDir = file("LWJGL/patches")
include(":LWJGL:lwjgl-3.3.3")
project(":LWJGL:lwjgl-3.3.3").projectDir = file("LWJGL/3.3.3")
include(":LWJGL:lwjgl-3.4.1")
project(":LWJGL:lwjgl-3.4.1").projectDir = file("LWJGL/3.4.1")
include(":LayerController")
include(":ColorPicker")
include(":Terracotta")
include(":InputMap")
include(":BedrockLauncher")
project(":BedrockLauncher").projectDir = file("BedrockModule/launcher")
include(":BedrockMinecraft")
project(":BedrockMinecraft").projectDir = file("BedrockModule/minecraft")
include(":BedrockControls")
project(":BedrockControls").projectDir = file("BedrockModule/controls")
