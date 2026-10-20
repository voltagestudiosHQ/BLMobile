import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    id("com.google.devtools.ksp")
}

val sourceRoot = rootProject.file("BedrockModule/vendor/LeviLaunchroid/minecraft/src/main")

android {
    namespace = "com.mojang.minecraftpe"
    compileSdk = 37

    defaultConfig {
        minSdk = 28
        consumerProguardFiles("consumer-rules.pro")
    }

    sourceSets {
        getByName("main") {
            java.srcDir(sourceRoot.resolve("java"))
            res.srcDir(sourceRoot.resolve("res"))
            assets.srcDir(sourceRoot.resolve("assets"))
            jniLibs.srcDir(sourceRoot.resolve("jniLibs"))
            manifest.srcFile(sourceRoot.resolve("AndroidManifest.xml"))
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources.excludes += setOf(
            "META-INF/AL2.0",
            "META-INF/LGPL2.1",
            "META-INF/DEPENDENCIES",
            "META-INF/LICENSE",
            "META-INF/LICENSE.txt",
            "META-INF/license.txt",
            "META-INF/NOTICE",
            "META-INF/NOTICE.txt",
            "META-INF/notice.txt",
            "META-INF/ASL2.0",
            "META-INF/INDEX.LIST",
            "META-INF/*.kotlin_module"
        )
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(leviLibs.androidx.room.runtime)
    implementation(leviLibs.androidx.room.ktx)
    ksp(leviLibs.androidx.room.compiler)
    implementation(leviLibs.androidx.sqlite.framework)
    implementation(leviLibs.androidx.fragment.ktx)
    implementation(leviLibs.annotations)
    implementation(leviLibs.androidx.annotation)
    implementation(leviLibs.okhttp)
    implementation(leviLibs.httpclient)
    implementation(platform(leviLibs.firebase.bom))
    implementation(leviLibs.firebase.messaging)
    implementation(leviLibs.firebase.iid)
    implementation(leviLibs.gson)
    implementation(leviLibs.simple.xml)
    implementation(leviLibs.androidx.activity.ktx)
    implementation(leviLibs.androidx.browser)
    implementation(leviLibs.pkix)
    implementation(leviLibs.core)
    implementation(leviLibs.prov)
    implementation(leviLibs.androidx.lifecycle.viewmodel.ktx)
    implementation(leviLibs.androidx.lifecycle.runtime.ktx)
    implementation(leviLibs.androidx.lifecycle.livedata.ktx)
    implementation(leviLibs.androidx.lifecycle.extensions)
    implementation(leviLibs.billing)
    implementation(leviLibs.conscrypt.android)
    implementation(leviLibs.androidx.games.activity)
    implementation(leviLibs.core.splashscreen)
    implementation(leviLibs.androidx.preference.ktx)
    implementation(leviLibs.appcompat)
    implementation(leviLibs.material)
}
