package de.kuschku.quasseldroid_ng.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;

import de.kuschku.quasseldroid_ng.R;

public class ThemeUtil {
    public final Colors colors = new Colors();

    private static final int[] ATTRS_SENDER = {
            // sender colors
            R.attr.senderColor0,
            R.attr.senderColor1,
            R.attr.senderColor2,
            R.attr.senderColor3,
            R.attr.senderColor4,
            R.attr.senderColor5,
            R.attr.senderColor6,
            R.attr.senderColor7,
            R.attr.senderColor8,
            R.attr.senderColor9,
            R.attr.senderColorA,
            R.attr.senderColorB,
            R.attr.senderColorC,
            R.attr.senderColorD,
            R.attr.senderColorE,
            R.attr.senderColorF,
    };

    private static final int[] ATTRS_MIRC = {
            // mirc colors
            R.attr.mircColor0,
            R.attr.mircColor1,
            R.attr.mircColor2,
            R.attr.mircColor3,
            R.attr.mircColor4,
            R.attr.mircColor5,
            R.attr.mircColor6,
            R.attr.mircColor7,
            R.attr.mircColor8,
            R.attr.mircColor9,
            R.attr.mircColorA,
            R.attr.mircColorB,
            R.attr.mircColorC,
            R.attr.mircColorD,
            R.attr.mircColorE,
            R.attr.mircColorF,
    };

    private static final int[] ATTRS_GENERAL = {
            // General UI colors
            R.attr.colorForeground,
            R.attr.colorForegroundHighlight,
            R.attr.colorForegroundSecondary,

            R.attr.colorBackground,
            R.attr.colorBackgroundHighlight,
            R.attr.colorBackgroundCard,
    };

    private static final int[] ATTRS_TINT = {
            // Tint colors
            R.attr.colorTintActivity,
            R.attr.colorTintMessage,
            R.attr.colorTintHighlight
    };

    public ThemeUtil(Context ctx) {
        initColors(ctx.getTheme());
    }

    public void initColors(Resources.Theme theme) {
        TypedArray arr;

        arr = theme.obtainStyledAttributes(ATTRS_SENDER);
        for (int i = 0; i < colors.senderColors.length;i++) {
            colors.senderColors[i] = arr.getColor(i, colors.transparent);
        }
        arr.recycle();

        arr = theme.obtainStyledAttributes(ATTRS_MIRC);
        for (int i = 0; i < colors.senderColors.length;i++) {
            colors.mircColors[i] = arr.getColor(i, colors.transparent);
        }
        arr.recycle();

        arr = theme.obtainStyledAttributes(ATTRS_GENERAL);
        colors.colorForeground = arr.getColor(0, colors.transparent);
        colors.colorForegroundHighlight = arr.getColor(1, colors.transparent);
        colors.colorForegroundSecondary = arr.getColor(2, colors.transparent);
        colors.colorBackground = arr.getColor(3, colors.transparent);
        colors.colorBackgroundHighlight = arr.getColor(4, colors.transparent);
        colors.colorBackgroundCard = arr.getColor(5, colors.transparent);
        arr.recycle();

        arr = theme.obtainStyledAttributes(ATTRS_TINT);
        colors.colorTintActivity = arr.getColor(0, colors.transparent);
        colors.colorTintMessage = arr.getColor(1, colors.transparent);
        colors.colorTintHighlight = arr.getColor(2, colors.transparent);
        arr.recycle();
    }

    public static class Colors {
        @ColorInt public int transparent = 0x00000000;
        @ColorInt public int[] senderColors = new int[16];
        @ColorInt public int[] mircColors = new int[16];
        @ColorInt public int colorForeground = 0x00000000;
        @ColorInt public int colorForegroundHighlight = 0x00000000;
        @ColorInt public int colorForegroundSecondary = 0x00000000;
        @ColorInt public int colorBackground = 0x00000000;
        @ColorInt public int colorBackgroundHighlight = 0x00000000;
        @ColorInt public int colorBackgroundCard = 0x00000000;
        @ColorInt public int colorTintActivity = 0x00000000;
        @ColorInt public int colorTintMessage = 0x00000000;
        @ColorInt public int colorTintHighlight = 0x00000000;
    }
}
