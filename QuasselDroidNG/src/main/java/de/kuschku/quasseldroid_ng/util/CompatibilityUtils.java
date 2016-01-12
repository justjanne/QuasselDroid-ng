package de.kuschku.quasseldroid_ng.util;

import android.os.Build;

public class CompatibilityUtils {
    private CompatibilityUtils() {

    }

    public static boolean isChromiumDevice() {
        return (Build.MANUFACTURER.toLowerCase().contains("chromium") && Build.BRAND.toLowerCase().contains("chromium"));
    }
}
