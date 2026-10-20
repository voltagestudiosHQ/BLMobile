package org.levimc.launcher.util;

import android.app.Activity;
import org.levimc.launcher.ui.dialogs.LoadingDialog;

public final class DialogUtils {
    private DialogUtils() {}

    public static LoadingDialog ensure(Activity activity, LoadingDialog existing) {
        return existing != null ? existing : new LoadingDialog(activity);
    }

    public static void showWithMessage(LoadingDialog dialog, String message) {
        if (dialog == null) return;
        dialog.setMessage(message);
        if (!dialog.isShowing()) dialog.show();
    }

    public static void dismissQuietly(LoadingDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}