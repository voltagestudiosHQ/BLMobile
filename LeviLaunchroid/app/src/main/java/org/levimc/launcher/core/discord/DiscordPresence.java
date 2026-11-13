package org.levimc.launcher.core.discord;

import android.app.Application;

/** Discord presence is unavailable because its optional SDK is not included in this build. */
public final class DiscordPresence {
    private DiscordPresence() {}

    public static void initialize(Application application) {}

    public static boolean isConfigured() {
        return false;
    }

    public static void refresh() {}

    public static void shutdown() {}
}
