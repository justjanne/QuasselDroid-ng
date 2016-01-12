package de.kuschku.quasseldroid_ng.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;

import butterknife.BindColor;
import butterknife.ButterKnife;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.annotationbind.Binder;
import de.kuschku.util.annotationbind.Color;

public class ThemeUtil {
    public final Colors colors = new Colors();

    public ThemeUtil(Context ctx) {
        initColors(ctx.getTheme());
    }

    public void initColors(Resources.Theme theme) {
        try {
            Binder.bind(colors, theme);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static class Colors {
        @Color(android.R.color.transparent)
        @ColorInt public int transparent;

        @Color({R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
                R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
                R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
                R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF})
        @ColorInt public int[] senderColors;

        @Color({R.attr.mircColor0, R.attr.mircColor1, R.attr.mircColor2, R.attr.mircColor3,
                R.attr.mircColor4, R.attr.mircColor5, R.attr.mircColor6, R.attr.mircColor7,
                R.attr.mircColor8, R.attr.mircColor9, R.attr.mircColorA, R.attr.mircColorB,
                R.attr.mircColorC, R.attr.mircColorD, R.attr.mircColorE, R.attr.mircColorF})
        @ColorInt public int[] mircColors;

        @Color(R.attr.colorForeground)
        @ColorInt public int colorForeground;

        @Color(R.attr.colorForegroundHighlight)
        @ColorInt public int colorForegroundHighlight;

        @Color(R.attr.colorForegroundSecondary)
        @ColorInt public int colorForegroundSecondary;

        @Color(R.attr.colorBackground)
        @ColorInt public int colorBackground;

        @Color(R.attr.colorBackgroundHighlight)
        @ColorInt public int colorBackgroundHighlight;

        @Color(R.attr.colorBackgroundSecondary)
        @ColorInt public int colorBackgroundSecondary;

        @Color(R.attr.colorBackgroundCard)
        @ColorInt public int colorBackgroundCard;

        @Color(R.attr.colorTintActivity)
        @ColorInt public int colorTintActivity;

        @Color(R.attr.colorTintMessage)
        @ColorInt public int colorTintMessage;

        @Color(R.attr.colorTintHighlight)
        @ColorInt public int colorTintHighlight;
    }
}
