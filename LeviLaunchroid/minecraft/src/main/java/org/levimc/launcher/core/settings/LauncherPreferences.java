package org.levimc.launcher.core.settings;

import android.content.Context;
import android.content.SharedPreferences;

public final class LauncherPreferences {
    private static final String PREFERENCES_NAME = "org.levimc.launcher_preferences";

    private LauncherPreferences() {}

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
