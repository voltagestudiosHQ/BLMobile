import org.gradle.api.tasks.Sync
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
}

val leviRoot = rootProject.file("BedrockModule/vendor/LeviLaunchroid")
val leviApp = leviRoot.resolve("app/src/main")
val syncBedrockJavaSources = tasks.register<Sync>("syncBedrockJavaSources") {
    from(leviApp.resolve("java")) {
        exclude("org/levimc/launcher/core/minecraft/Application.kt")
    }
    into(layout.buildDirectory.dir("generated/bedrock/java"))
    doLast {
        fun replaceOnce(source: String, anchor: String, replacement: String, description: String): String {
            check(source.indexOf(anchor) >= 0 && source.indexOf(anchor) == source.lastIndexOf(anchor)) {
                "LeviLaunchroid $description changed; review the embedded-launcher integration."
            }
            return source.replace(anchor, replacement)
        }

        val updaterFile = layout.buildDirectory
            .file("generated/bedrock/java/org/levimc/launcher/util/GithubReleaseUpdater.java")
            .get()
            .asFile
        var source = updaterFile.readText()
        val manualCheck = "    public void checkUpdate() {"
        val startupCheck = "    public void checkUpdateOnLaunch() {"
        check(
            source.indexOf(manualCheck) >= 0 &&
                source.indexOf(manualCheck) == source.lastIndexOf(manualCheck) &&
                source.indexOf(startupCheck) >= 0 &&
                source.indexOf(startupCheck) == source.lastIndexOf(startupCheck)
        ) {
            "LeviLaunchroid update entry points changed; review the embedded-launcher integration."
        }
        source = source.replace(
            manualCheck,
            """    public void checkUpdate() {
        if (org.levimc.launcher.BuildConfig.IS_EMBEDDED) {
            activity.runOnUiThread(() -> Toast.makeText(activity,
                    activity.getString(R.string.bedrock_embedded_updates_message),
                    Toast.LENGTH_LONG).show());
            return;
        }"""
        )
        source = source.replace(
            startupCheck,
            """    public void checkUpdateOnLaunch() {
        if (org.levimc.launcher.BuildConfig.IS_EMBEDDED) return;"""
        )
        updaterFile.writeText(source)

        val mainActivityFile = layout.buildDirectory
            .file("generated/bedrock/java/org/levimc/launcher/ui/activities/MainActivity.java")
            .get()
            .asFile
        source = mainActivityFile.readText()
        source = replaceOnce(
            source,
            "    private boolean launcherInitialized;",
            """    private boolean launcherInitialized;
    private boolean embeddedDirectLaunch;""",
            "launcher activity state"
        )
        source = replaceOnce(
            source,
            """        if (versionManager == null || binding == null) return;
        fileHandler = new FileHandler(this, viewModel, versionManager);""",
            """        if (versionManager == null || binding == null) return;
        fileHandler = new FileHandler(this, viewModel, versionManager);
        if (getIntent().getBooleanExtra("org.levimc.launcher.extra.EMBEDDED_DIRECT_LAUNCH", false)) {
            getIntent().removeExtra("org.levimc.launcher.extra.EMBEDDED_DIRECT_LAUNCH");
            if (versionManager.getSelectedVersion() != null) {
                embeddedDirectLaunch = true;
                performActualLaunch();
                return;
            }
        }""",
            "version manager launch handoff"
        )
        source = replaceOnce(
            source,
            """                public void onLaunchStarted() {
                    trace.milestone("Loading screen requested");
                }""",
            """                public void onLaunchStarted() {
                    trace.milestone("Loading screen requested");
                    if (embeddedDirectLaunch) finish();
                }""",
            "launch callback"
        )
        mainActivityFile.writeText(source)

        val restarterFile = layout.buildDirectory
            .file("generated/bedrock/java/org/levimc/launcher/core/minecraft/MinecraftProcessRestarter.kt")
            .get()
            .asFile
        source = restarterFile.readText()
        source = replaceOnce(
            source,
            "                startActivity(Intent(this, MainActivity::class.java).apply {",
            """                val returnIntent = if (BuildConfig.IS_EMBEDDED) {
                    Intent().setClassName(
                        packageName,
                        "com.movtery.zalithlauncher.ui.activities.MainActivity"
                    )
                } else {
                    Intent(this, MainActivity::class.java)
                }
                startActivity(returnIntent.apply {""",
            "game-exit activity handoff"
        )
        source = replaceOnce(
            source,
            """                        Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_NO_ANIMATION""",
            """                        Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_NO_ANIMATION or
                            (if (BuildConfig.IS_EMBEDDED) Intent.FLAG_ACTIVITY_CLEAR_TASK else 0)""",
            "game-exit task flags"
        )
        restarterFile.writeText(source)

        val modsActivityFile = layout.buildDirectory
            .file("generated/bedrock/java/org/levimc/launcher/ui/activities/ModsFullscreenActivity.java")
            .get()
            .asFile
        source = modsActivityFile.readText()
        source = replaceOnce(
            source,
            "    private int lastModsCount = -1;",
            """    private int lastModsCount = -1;
    private org.levimc.launcher.core.content.ContentImporter bedrockContentImporter;""",
            "Bedrock mods importer state"
        )
        source = replaceOnce(
            source,
            """        inbuiltModManager = InbuiltModManager.getInstance(this);
        setupViews();""",
            """        inbuiltModManager = InbuiltModManager.getInstance(this);
        bedrockContentImporter = new org.levimc.launcher.core.content.ContentImporter(
                getApplicationContext());
        setupViews();""",
            "Bedrock mods importer initialization"
        )
        source = replaceOnce(
            source,
            """                        for (Uri uri : StorageAccess.selectedUris(result.getData())) {
                            StorageAccess.retainReadPermission(this, result.getData(), uri);
                        }
                        fileHandler.processIncomingFilesWithConfirmation(
                                result.getData(), createImportCallback(), true);""",
            """                        List<Uri> modUris = new ArrayList<>();
                        List<Uri> contentUris = new ArrayList<>();
                        for (Uri uri : StorageAccess.selectedUris(result.getData())) {
                            StorageAccess.retainReadPermission(this, result.getData(), uri);
                            if (isBedrockContentFile(uri)) {
                                contentUris.add(uri);
                            } else {
                                modUris.add(uri);
                            }
                        }
                        if (!modUris.isEmpty()) {
                            fileHandler.processIncomingFilesWithConfirmation(
                                    modUris, createImportCallback(), true);
                        }
                        if (!contentUris.isEmpty()) {
                            importBedrockContent(contentUris);
                        }""",
            "Bedrock mods and add-ons import routing"
        )
        source = replaceOnce(
            source,
            """        Button addModButton = findViewById(R.id.add_mod_fullscreen_button);
        addModButton.setVisibility(View.VISIBLE);""",
            """        Button addModButton = findViewById(R.id.add_mod_fullscreen_button);
        addModButton.setText(R.string.bedrock_import_mods_addons);
        addModButton.setVisibility(View.VISIBLE);""",
            "Bedrock mods import label"
        )
        source = replaceOnce(
            source,
            "    private void startFilePicker() {",
            """    private boolean isBedrockContentFile(Uri uri) {
        String displayName = null;
        try (android.database.Cursor cursor = getContentResolver().query(
                uri,
                new String[]{android.provider.OpenableColumns.DISPLAY_NAME},
                null,
                null,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(0);
            }
        }
        if (displayName == null) displayName = uri.getLastPathSegment();
        if (displayName == null) return false;
        String lowerName = displayName.toLowerCase(java.util.Locale.ROOT);
        return lowerName.endsWith(".mcaddon")
                || lowerName.endsWith(".mcpack")
                || lowerName.endsWith(".mcworld");
    }

    private void importBedrockContent(List<Uri> uris) {
        org.levimc.launcher.core.versions.GameVersion version =
                VersionManager.get(this).getSelectedVersion();
        if (version == null) {
            Toast.makeText(this, R.string.not_found_version, Toast.LENGTH_LONG).show();
            return;
        }

        android.content.SharedPreferences preferences =
                getSharedPreferences("content_management", MODE_PRIVATE);
        String savedStorageType = preferences.getString("storage_type", "INTERNAL");
        org.levimc.launcher.settings.FeatureSettings.StorageType storageType;
        try {
            storageType = org.levimc.launcher.settings.FeatureSettings.StorageType.valueOf(
                    savedStorageType);
        } catch (IllegalArgumentException error) {
            storageType = org.levimc.launcher.settings.FeatureSettings.StorageType.INTERNAL;
        }
        storageType = org.levimc.launcher.util.LauncherStorage.normalizeContentStorageType(
                storageType, version.versionIsolation);
        java.io.File baseDir = org.levimc.launcher.util.LauncherStorage.getContentGameDataDir(
                this, version.getStorageProfileId(), storageType);

        bedrockContentImporter.importContent(
                uris,
                new java.io.File(baseDir, "resource_packs"),
                new java.io.File(baseDir, "behavior_packs"),
                new java.io.File(baseDir, "skin_packs"),
                new java.io.File(baseDir, "minecraftWorlds"),
                new org.levimc.launcher.core.content.ContentImporter.ImportCallback() {
                    @Override
                    public void onSuccess(String message) {
                        runOnUiThread(() -> Toast.makeText(
                                ModsFullscreenActivity.this, message, Toast.LENGTH_LONG).show());
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> Toast.makeText(
                                ModsFullscreenActivity.this, error, Toast.LENGTH_LONG).show());
                    }

                    @Override
                    public void onProgress(int progress) {
                    }
                });
    }

    private void startFilePicker() {""",
            "Bedrock content import handlers"
        )
        modsActivityFile.writeText(source)
    }
}
tasks.named("preBuild").configure {
    dependsOn(syncBedrockJavaSources)
}

android {
    namespace = "org.levimc.launcher"
    compileSdk = 37
    ndkVersion = "28.2.13676358"

    defaultConfig {
        minSdk = 28
        ndk {
            abiFilters += "arm64-v8a"
        }
        buildConfigField("String", "VERSION_NAME", "\"1.0.0\"")
        buildConfigField("int", "VERSION_CODE", "1")
        buildConfigField("boolean", "IS_BETA", "false")
        buildConfigField("boolean", "IS_EMBEDDED", "true")
        buildConfigField("String", "CURSEFORGE_API_KEY", "\"\"")
    }

    buildFeatures {
        buildConfig = true
        prefab = true
        viewBinding = true
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile(leviApp.resolve("AndroidManifest.xml"))
            java.srcDir(layout.buildDirectory.dir("generated/bedrock/java").get().asFile)
            res.srcDir(leviApp.resolve("res"))
            assets.srcDir(leviRoot.resolve("resources"))
            jniLibs.srcDir(leviApp.resolve("jniLibs"))
        }
    }

    externalNativeBuild {
        cmake {
            path = leviApp.resolve("cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        jniLibs.useLegacyPackaging = true
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
            "META-INF/*.kotlin_module",
            "DebugProbesKt.bin"
        )
        resources.pickFirsts += setOf(
            "META-INF/INDEX.LIST",
            "META-INF/DEPENDENCIES",
            "META-INF/io.netty.versions.properties"
        )
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(project(":BedrockMinecraft"))
    implementation(project(":BedrockControls"))
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
    implementation(leviLibs.bedrock.connection)
    implementation(leviLibs.xcrash)
    implementation(leviLibs.play.services.games.v2)
    implementation(leviLibs.glide)
}
