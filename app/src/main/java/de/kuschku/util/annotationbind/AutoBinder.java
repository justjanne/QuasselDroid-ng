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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.kuschku.util.annotationbind;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import java.lang.reflect.Field;

public class AutoBinder {
    private AutoBinder() {

    }

    public static void bind(@NonNull Object o, @NonNull Resources.Theme t) throws IllegalAccessException {
        for (Field f : o.getClass().getFields()) {
            if (f.isAnnotationPresent(AutoColor.class)) {
                int[] colors = obtainColors(f.getAnnotation(AutoColor.class).value(), t);
                if (f.getType().isArray())
                    f.set(o, colors);
                else if (colors.length == 1)
                    f.set(o, colors[0]);
                else
                    throw new IllegalAccessException("Field length does not correspond to argument length");
            }
        }
    }

    public static void bind(@NonNull Object o, @NonNull Context t) throws IllegalAccessException {
        Resources.Theme theme = t.getTheme();
        for (Field f : o.getClass().getFields()) {
            if (f.isAnnotationPresent(AutoColor.class)) {
                int[] colors = obtainColors(f.getAnnotation(AutoColor.class).value(), theme);
                if (f.getType().isArray())
                    f.set(o, colors);
                else if (colors.length == 1)
                    f.set(o, colors[0]);
                else
                    throw new IllegalAccessException("Field length does not correspond to argument length");
            } else if (f.isAnnotationPresent(AutoString.class)) {
                String[] strings = obtainStrings(f.getAnnotation(AutoString.class).value(), t);
                if (f.getType().isArray())
                    f.set(o, strings);
                else if (strings.length == 1)
                    f.set(o, strings[0]);
                else
                    throw new IllegalAccessException("Field length does not correspond to argument length");
            } else if (f.isAnnotationPresent(AutoDimen.class)) {
                int[] dimens = obtainDimen(f.getAnnotation(AutoDimen.class).value(), theme);
                if (f.getType().isArray())
                    f.set(o, dimens);
                else if (dimens.length == 1)
                    f.set(o, dimens[0]);
                else
                    throw new IllegalAccessException("Field length does not correspond to argument length");
            }
        }
    }

    @NonNull
    @ColorInt
    private static int[] obtainColors(@NonNull int[] res, @NonNull Resources.Theme theme) {
        int[] result = new int[res.length];
        TypedArray t = theme.obtainStyledAttributes(res);
        for (int i = 0; i < res.length; i++) {
            result[i] = t.getColor(i, 0x00000000);
        }
        t.recycle();
        return result;
    }

    @NonNull
    private static String[] obtainStrings(@NonNull int[] res, @NonNull Context ctx) {
        String[] result = new String[res.length];
        for (int i = 0; i < res.length; i++) {
            result[i] = ctx.getString(res[i]);
        }
        return result;
    }

    @NonNull
    private static int[] obtainDimen(@NonNull int[] res, @NonNull Resources.Theme theme) {
        int[] result = new int[res.length];
        TypedArray t = theme.obtainStyledAttributes(res);
        for (int i = 0; i < res.length; i++) {
            result[i] = (int) t.getDimension(i, 0x00000000);
        }
        t.recycle();
        return result;
    }
}
