package com.wyrnlab.jotdownthatmovie.Utils;

import android.app.Activity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class StatusBarUtils {

    /**
     * Android 15+ forces edge-to-edge, so the status bar can no longer be tinted via
     * Window.setStatusBarColor. Paint a scrim behind it instead, matching the app's
     * dark action bar, and force light (white) status bar icons so they stay legible.
     */
    public static void applyDarkStatusBarScrim(Activity activity) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();

        View scrim = new View(activity);
        scrim.setBackgroundColor(resolveColorPrimaryDark(activity));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        decorView.addView(scrim, params);
        scrim.bringToFront();

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(activity.getWindow(), decorView);
        controller.setAppearanceLightStatusBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(decorView, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            ViewGroup.LayoutParams lp = scrim.getLayoutParams();
            if (lp.height != statusBarHeight) {
                lp.height = statusBarHeight;
                scrim.setLayoutParams(lp);
            }
            return insets;
        });
        ViewCompat.requestApplyInsets(decorView);
    }

    private static int resolveColorPrimaryDark(Activity activity) {
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }
}
