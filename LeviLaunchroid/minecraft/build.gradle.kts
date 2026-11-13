plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp.plugin)
}

android {
    namespace = "com.mojang.minecraftpe"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources {
            excludes += listOf(
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
    implementation(leviLibs.firebase.messaging)
    implementation(leviLibs.firebase.iid)

    implementation(leviLibs.pkix)

    implementation(leviLibs.gson)
    implementation(leviLibs.httpclient)
    implementation(leviLibs.simple.xml) {
        exclude(group = "xpp3", module = "xpp3")
        exclude(group = "stax", module = "stax")
        exclude(group = "stax", module = "stax-api")
    }
    implementation(leviLibs.androidx.activity.ktx)
    implementation(leviLibs.androidx.browser)

    implementation(leviLibs.pkix)
    implementation(leviLibs.core)
    implementation(leviLibs.prov)

    // Architectural Components
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
