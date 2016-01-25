package de.kuschku.util.ui;

import android.os.Build;

public class CompatibilityUtils {
    private CompatibilityUtils() {

    }

    /**
     * This method is used to check if the current device supports Sockets with the KeepAlive flag.
     *
     * As that feature is only missing on Chromium devices, we just check for that
     * @return supports KeepAlive
     */
    public static boolean deviceSupportsKeepAlive() {
        return !(Build.MANUFACTURER.toLowerCase().contains("chromium") && Build.BRAND.toLowerCase().contains("chromium"));
    }

    /**
     * This method is used to check if the device supports both @link{DeflaterInputStream}
     * and @link{DeflaterOutputStream}.
     *
     * As that feature was only added in KitKat, we just check for the device version.
     * @return supports DeflaterStream
     */
    public static boolean deviceSupportsCompression() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
