plugins {
    alias(libs.plugins.android.library)
}

val sourceRoot = rootProject.file("BedrockModule/vendor/LeviLaunchroid/pojav_controls/src/main")

android {
    namespace = "org.levimc.pojavcontrols"
    compileSdk = 37

    defaultConfig {
        minSdk = 28
    }

    sourceSets {
        getByName("main") {
            java.srcDir(sourceRoot.resolve("java"))
            res.srcDir(sourceRoot.resolve("res"))
            manifest.srcFile(sourceRoot.resolve("AndroidManifest.xml"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(leviLibs.appcompat)
    implementation(leviLibs.material)
    implementation(leviLibs.gson)
}
