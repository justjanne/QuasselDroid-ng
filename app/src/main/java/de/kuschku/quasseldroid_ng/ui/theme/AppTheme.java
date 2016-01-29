package de.kuschku.quasseldroid_ng.ui.theme;

import android.support.annotation.StyleRes;

import de.kuschku.quasseldroid_ng.R;

public enum AppTheme {
    QUASSEL_LIGHT(R.style.Quassel_Light),
    QUASSEL_DARK(R.style.Quassel_Dark),
    MATERIAL_DARK(R.style.Material_Dark),
    MATERIAL_LIGHT(R.style.Material_Light);

    public final int themeId;

    AppTheme(@StyleRes int themeId) {
        this.themeId = themeId;
    }

    public static int resFromString(String s) {
        return themeFromString(s).themeId;
    }

    public static AppTheme themeFromString(String s) {
        if (s == null) s = "";
        switch (s) {
            case "MATERIAL_DARK": return MATERIAL_DARK;
            case "MATERIAL_LIGHT": return MATERIAL_LIGHT;
            case "QUASSEL_DARK": return QUASSEL_DARK;

            default:
            case "QUASSEL_LIGHT": return QUASSEL_LIGHT;
        }
    }

    @Override
    public String toString() {
        return name() + "{" +
                "themeId=" + themeId +
                '}';
    }
}
