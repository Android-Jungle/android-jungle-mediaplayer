package com.jungle.mediaplayer.base;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.Formatter;

public class MediaPlayerUtils {

    public static float getSystemBrightnessPercent(Context context) {
        ContentResolver resolver = context.getContentResolver();

        float brightness = 0;
        try {
            brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
            brightness /= 255.0f;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return brightness;
    }

    public static float getBrightnessPercent(Context context) {
        Activity activity = (Activity) context;
        WindowManager.LayoutParams layout = activity.getWindow().getAttributes();
        return layout.screenBrightness;
    }

    public static void setBrightness(Context context, float percent) {
        if (!(context instanceof Activity)) {
            return;
        }

        if (percent < 0.01f) {
            percent = 0.01f;
        } else if (percent > 1.0f) {
            percent = 1.0f;
        }

        Activity activity = (Activity) context;
        WindowManager.LayoutParams layout = activity.getWindow().getAttributes();

        layout.screenBrightness = percent;
        activity.getWindow().setAttributes(layout);
    }

    public static String formatTime(long timeMs) {
        if (timeMs <= 0) {
            return "00:00";
        }

        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = totalSeconds / 60 % 60;
        long hours = totalSeconds / 3600;

        Formatter formatter = new Formatter();
        return hours > 0
                ? formatter.format("%d:%02d:%02d", new Object[]{hours, minutes, seconds}).toString()
                : formatter.format("%02d:%02d", new Object[]{minutes, seconds}).toString();
    }

    public static MediaSize getScreenSize(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return new MediaSize(metrics.widthPixels, metrics.heightPixels);
    }

    public static Bitmap takeViewScreenshot(View view) {
        if (view == null) {
            return null;
        }

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return bitmap;
    }
}
