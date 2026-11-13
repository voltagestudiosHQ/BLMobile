package org.levimc.launcher.ui.dialogs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import org.levimc.launcher.R;

public class PlayStoreValidationDialog {
    private static final String MINECRAFT_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.mojang.minecraftpe";
    public static void showNotFromPlayStoreDialog(Context context) {
        new CustomAlertDialog(context)
                .setTitleText(context.getString(R.string.minecraft_playstore_required_title))
                .setMessage(context.getString(R.string.minecraft_playstore_required_message))
                .setPositiveButton(context.getString(R.string.buy_minecraft), v -> {
                    openPlayStore(context);
                })
                .setNegativeButton(context.getString(R.string.cancel), null)
                .show();
    }

    public static void showNotInstalledDialog(Context context) {
        new CustomAlertDialog(context)
                .setTitleText(context.getString(R.string.minecraft_not_installed_title))
                .setMessage(context.getString(R.string.minecraft_not_installed_message))
                .setPositiveButton(context.getString(R.string.install_minecraft), v -> {
                    openPlayStore(context);
                })
                .setNegativeButton(context.getString(R.string.cancel), null)
                .show();
    }

    private static void openPlayStore(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mojang.minecraftpe"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MINECRAFT_PLAY_STORE_URL));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception ex) {
                Toast.makeText(context, context.getString(R.string.error_no_browser), Toast.LENGTH_SHORT).show();
            }
        }
    }
}