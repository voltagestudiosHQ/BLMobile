import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "org.levimc.launcher"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
        ndk {
            abiFilters.add("arm64-v8a")
        }
        buildConfigField("String", "CURSEFORGE_API_KEY", "\"\"")
        buildConfigField("long", "DISCORD_APPLICATION_ID", "0L")
        buildConfigField("String", "DISCORD_LARGE_IMAGE", "\"\"")
        buildConfigField("boolean", "SKIP_PLAY_STORE_CHECK", "false")
        buildConfigField("boolean", "IS_BETA", "false")
        buildConfigField("String", "VERSION_NAME", "\"embedded\"")
        buildConfigField("int", "VERSION_CODE", "1")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        prefab = true
    }

    sourceSets {
        getByName("main") {
            java.srcDir("../LeviLaunchroid/app/src/main/java")
            res.srcDir("../LeviLaunchroid/app/src/main/res")
            assets.srcDir("../LeviLaunchroid/app/src/main/assets")
            assets.srcDir("../LeviLaunchroid/resources")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

    ndkVersion = "28.2.13676358"

    packaging {
        jniLibs {
            useLegacyPackaging = true
            keepDebugSymbols += listOf(
                "**/libpreloader.so",
                "**/libgxcore.so",
                "**/libinbuiltmods.so"
            )
        }
        resources {
            excludes += "DebugProbesKt.bin"
            pickFirsts += listOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES",
                "META-INF/io.netty.versions.properties"
            )
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(project(":minecraft"))
    implementation(project(":pojav_controls"))
    implementation(leviLibs.commonmark)
    implementation(leviLibs.commonmark.ext.gfm.tables)
    implementation(leviLibs.commonmark.ext.gfm.strikethrough)
    implementation(leviLibs.commonmark.ext.autolink)
    implementation(leviLibs.minecraftauth)
    implementation(leviLibs.commons.httpclient)
    implementation(leviLibs.commons.gson)
    implementation(platform(leviLibs.firebase.bom))
    implementation(leviLibs.firebase.analytics)
    implementation(leviLibs.firebase.crashlytics)
    implementation(leviLibs.firebase.crashlytics.ndk)
    implementation(leviLibs.firebase.messaging)
    implementation(leviLibs.core.splashscreen)
    implementation(leviLibs.appcompat)
    implementation(leviLibs.conscrypt.android)
    implementation(leviLibs.constraintlayout)
    implementation(leviLibs.material)
    implementation(leviLibs.gson)
    implementation(leviLibs.androidx.preference.ktx)
    implementation(leviLibs.androidx.games.activity)
    implementation(leviLibs.okhttp)
    implementation(leviLibs.androidx.dynamicanimation)
    implementation(leviLibs.androidx.browser)
    implementation(leviLibs.xcrash)
    implementation(leviLibs.play.services.games.v2)
    implementation(leviLibs.glide)
}
