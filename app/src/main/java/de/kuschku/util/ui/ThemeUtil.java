package de.kuschku.util.ui;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.view.ContextThemeWrapper;

import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.AppTheme;
import de.kuschku.util.annotationbind.AutoBinder;
import de.kuschku.util.annotationbind.AutoColor;

public class ThemeUtil {
    @NonNull
    public final Colors colors = new Colors();
    public DateTimeFormatHelper formatter;

    public ThemeUtil(@NonNull Context ctx) {
        initColors(new ContextThemeWrapper(ctx, ctx.getTheme()));
        formatter = new DateTimeFormatHelper(ctx);
    }

    public ThemeUtil(@NonNull Context ctx, @NonNull AppTheme theme) {
        initColors(new ContextThemeWrapper(ctx, theme.themeId));
        formatter = new DateTimeFormatHelper(ctx);
    }

    @UiThread
    public void initColors(@NonNull ContextThemeWrapper wrapper) {
        try {
            AutoBinder.bind(colors, wrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static class Colors {
        @AutoColor(android.R.color.transparent)
        @ColorInt
        public int transparent;

        @AutoColor(R.attr.colorPrimary)
        @ColorInt
        public int colorPrimary;

        @AutoColor(R.attr.colorPrimaryDark)
        @ColorInt
        public int colorPrimaryDark;

        @AutoColor(R.attr.colorAccent)
        @ColorInt
        public int colorAccent;

        @AutoColor({R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
                R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
                R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
                R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF})
        @ColorInt
        public int[] senderColors;

        @AutoColor({R.attr.mircColor0, R.attr.mircColor1, R.attr.mircColor2, R.attr.mircColor3,
                R.attr.mircColor4, R.attr.mircColor5, R.attr.mircColor6, R.attr.mircColor7,
                R.attr.mircColor8, R.attr.mircColor9, R.attr.mircColorA, R.attr.mircColorB,
                R.attr.mircColorC, R.attr.mircColorD, R.attr.mircColorE, R.attr.mircColorF})
        @ColorInt
        public int[] mircColors;

        @AutoColor(R.attr.colorForeground)
        @ColorInt
        public int colorForeground;

        @AutoColor(R.attr.colorForegroundHighlight)
        @ColorInt
        public int colorForegroundHighlight;

        @AutoColor(R.attr.colorForegroundSecondary)
        @ColorInt
        public int colorForegroundSecondary;

        @AutoColor(R.attr.colorForegroundAction)
        @ColorInt
        public int colorForegroundAction;

        @AutoColor(R.attr.colorBackground)
        @ColorInt
        public int colorBackground;

        @AutoColor(R.attr.colorBackgroundHighlight)
        @ColorInt
        public int colorBackgroundHighlight;

        @AutoColor(R.attr.colorBackgroundSecondary)
        @ColorInt
        public int colorBackgroundSecondary;

        @AutoColor(R.attr.colorBackgroundCard)
        @ColorInt
        public int colorBackgroundCard;

        @AutoColor(R.attr.colorTintActivity)
        @ColorInt
        public int colorTintActivity;

        @AutoColor(R.attr.colorTintMessage)
        @ColorInt
        public int colorTintMessage;

        @AutoColor(R.attr.colorTintHighlight)
        @ColorInt
        public int colorTintHighlight;
    }
}
