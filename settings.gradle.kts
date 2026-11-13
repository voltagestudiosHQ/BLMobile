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
    versionCatalogs {
        create("leviLibs") {
            from(files("LeviLaunchroid/gradle/libs.versions.toml"))
        }
    }
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://repo.opencollab.dev/maven-snapshots") }
        maven { url = uri("https://repo.opencollab.dev/maven-releases") }
    }
}

rootProject.name = "BedroomLauncherMobile"
include(":BLMobileApplication")
include(":LWJGL")
include(":LWJGL:patches")
project(":LWJGL:patches").projectDir = file("LWJGL/patches")
include(":LWJGL:lwjgl-3.3.3")
project(":LWJGL:lwjgl-3.3.3").projectDir = file("LWJGL/3.3.3")
include(":LWJGL:lwjgl-3.4.1")
project(":LWJGL:lwjgl-3.4.1").projectDir = file("LWJGL/3.4.1")
include(":LayerController")
include(":ColorPicker")
include(":CardGrid")
include(":Terracotta")
include(":InputMap")
include(":Guide")
include(":PetLib")
include(":BedrockRuntime")
include(":minecraft")
project(":minecraft").projectDir = file("LeviLaunchroid/minecraft")
include(":pojav_controls")
project(":pojav_controls").projectDir = file("LeviLaunchroid/pojav_controls")
