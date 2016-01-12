package de.kuschku.util.annotationbind;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;

import java.lang.reflect.Field;

public class Binder {
    private Binder() {

    }

    public static void bind(Object o, Resources.Theme t) throws IllegalAccessException {
        for (Field f : o.getClass().getFields()) {
            if (f.isAnnotationPresent(Color.class)) {
                int[] colors = obtainColors(f.getAnnotation(Color.class).value(), t);
                if (f.getType().isArray())
                    f.set(o, colors);
                else if (colors.length == 1)
                    f.set(o, colors[0]);
                else
                    throw new IllegalAccessException("Field length does not correspond to argument length");
            }
        }
    }

    public static void bind(Object o, Context t) throws IllegalAccessException {
        bind(o, t.getTheme());
    }

    @ColorInt
    private static int[] obtainColors(int[] res, Resources.Theme theme) {
        int[] result = new int[res.length];
        TypedArray t = theme.obtainStyledAttributes(res);
        for (int i = 0; i < res.length; i++) {
            result[i] = t.getColor(i, 0x00000000);
        }
        t.recycle();
        return result;
    }
}
