/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import de.kuschku.quasseldroid_ng.QuasselDroidNG;

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

    /**
     * Because Android’s String::split is broken
     * @param str The string to be broken into chars
     * @return A list with all substrings of length 1 of the first string, in order
     */
    public static String[] partStringByChar(String str) {
        String[] chars = new String[str.length()];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = str.substring(i, i+1);
        }
        return chars;
    }
}
