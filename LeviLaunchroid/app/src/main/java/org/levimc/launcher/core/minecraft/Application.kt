package org.levimc.launcher.core.minecraft

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.levimc.launcher.core.settings.LauncherPreferences
import org.levimc.launcher.core.crash.CrashReporter
import org.levimc.launcher.core.discord.DiscordPresence
import org.levimc.launcher.core.packages.PackageService
import org.levimc.launcher.core.news.NewsNotificationHelper
import org.levimc.launcher.settings.FeatureSettings
import org.levimc.launcher.ui.dialogs.LogcatOverlayManager

class LauncherApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        FeatureSettings.init(applicationContext)
        CrashReporter.init(this)
        val processName = Application.getProcessName()
        if (processName.endsWith(":crash")) return

        PackageService.init(this)
        NewsNotificationHelper.initialize(this)
        LogcatOverlayManager.init(this)

        preferences = LauncherPreferences.getDefaultSharedPreferences(this)
        DiscordPresence.initialize(this)
    }

    companion object {
        private var embeddedInitialized = false

        @JvmStatic
        lateinit var context: Context
            private set

        @JvmStatic
        lateinit var preferences: SharedPreferences
            private set

        /** Initializes shared state for embedded activities without owning the host application lifecycle. */
        @JvmStatic
        fun initializeEmbedded(application: Application) {
            if (embeddedInitialized) return

            val appContext = application.applicationContext
            context = appContext
            preferences = LauncherPreferences.getDefaultSharedPreferences(appContext)
            FeatureSettings.init(appContext)
            PackageService.init(appContext)
            LogcatOverlayManager.init(application)
            embeddedInitialized = true
        }
    }
}
