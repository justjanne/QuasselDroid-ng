package de.kuschku.util;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class CompatibilityUtils {
    private CompatibilityUtils() {

    }

    /**
     * This method is used to check if the current device supports Sockets with the KeepAlive flag.
     * <p>
     * As that feature is only missing on Chromium devices, we just check for that
     *
     * @return Does the current device support KeepAlive sockets?
     */
    public static boolean deviceSupportsKeepAlive() {
        return !(Build.MANUFACTURER.toLowerCase().contains("chromium") && Build.BRAND.toLowerCase().contains("chromium"));
    }

    /**
     * This method is used to check if the device supports SyncFlush
     * <p>
     * As that feature was only added in KitKat, we just check for the device version.
     *
     * @return Does the current device support SyncFlush natively?
     */
    public static boolean deviceSupportsCompression() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * Creates a SyncFlush output stream, even if the current device does not support doing so
     * natively.
     *
     * @param rawOut the raw output stream to be wrapped
     * @return The wrapping output stream
     */
    @NonNull
    public static DeflaterOutputStream createDeflaterOutputStream(@Nullable OutputStream rawOut) {
        if (deviceSupportsCompression()) return new DeflaterOutputStream(rawOut, true);
        else return new DeflaterOutputStream(rawOut, createSyncFlushDeflater());
    }

    /**
     * Creates a SyncFlush Deflater for use on pre-KitKat Android
     *
     * @return The modified Deflater, or null if the creation failed
     */
    @Nullable
    private static Deflater createSyncFlushDeflater() {
        Deflater def = new Deflater();
        try {
            Field f = def.getClass().getDeclaredField("flushParm");
            f.setAccessible(true);
            f.setInt(def, 2); // Z_SYNC_FLUSH
        } catch (Exception e) {
            return null;
        }
        return def;
    }
}
