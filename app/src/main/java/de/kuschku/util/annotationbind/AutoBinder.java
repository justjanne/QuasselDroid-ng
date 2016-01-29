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
