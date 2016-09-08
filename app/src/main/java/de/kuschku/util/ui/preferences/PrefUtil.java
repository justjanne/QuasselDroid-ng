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

package de.kuschku.util.ui.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.commons.R;

import java.lang.reflect.Method;

/**
 * @author Aidan Follestad (afollestad)
 */
class PrefUtil {

    private PrefUtil() {
    }

    public static void setLayoutResource(@NonNull Context context, @NonNull Preference preference, @Nullable AttributeSet attrs) {
        boolean foundLayout = false;
        if (attrs != null) {
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                final String namespace = ((XmlResourceParser) attrs).getAttributeNamespace(0);
                if (namespace.equals("http://schemas.android.com/apk/res/android") &&
                        attrs.getAttributeName(i).equals("layout")) {
                    foundLayout = true;
                    break;
                }
            }
        }

        boolean useStockLayout = false;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Preference, 0, 0);
            try {
                useStockLayout = a.getBoolean(R.styleable.Preference_useStockLayout, false);
            } finally {
                a.recycle();
            }
        }

        if (!foundLayout && !useStockLayout)
            preference.setLayoutResource(R.layout.md_preference_custom);
    }

    public static void registerOnActivityDestroyListener(@NonNull Preference preference, @NonNull PreferenceManager.OnActivityDestroyListener listener) {
        try {
            PreferenceManager pm = preference.getPreferenceManager();
            Method method = pm.getClass().getDeclaredMethod(
                    "registerOnActivityDestroyListener",
                    PreferenceManager.OnActivityDestroyListener.class);
            method.setAccessible(true);
            method.invoke(pm, listener);
        } catch (Exception ignored) {
        }
    }

    public static void unregisterOnActivityDestroyListener(@NonNull Preference preference, @NonNull PreferenceManager.OnActivityDestroyListener listener) {
        try {
            PreferenceManager pm = preference.getPreferenceManager();
            Method method = pm.getClass().getDeclaredMethod(
                    "unregisterOnActivityDestroyListener",
                    PreferenceManager.OnActivityDestroyListener.class);
            method.setAccessible(true);
            method.invoke(pm, listener);
        } catch (Exception ignored) {
        }
    }
}
