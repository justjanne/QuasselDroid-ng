package de.kuschku.quasseldroid_ng.ui.theme;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;

import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.annotationbind.AutoBinder;
import de.kuschku.util.annotationbind.AutoColor;
import de.kuschku.util.annotationbind.AutoDimen;
import de.kuschku.util.annotationbind.AutoString;
import de.kuschku.util.ui.DateTimeFormatHelper;
import de.kuschku.util.ui.SpanFormatter;

public class ThemeUtil {
    @NonNull
    public final Colors res = new Colors();
    public final FormatStrings translations = new FormatStrings();
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
            AutoBinder.bind(res, wrapper);
            AutoBinder.bind(translations, wrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static class FormatStrings {
        @AutoString(R.string.username_hostmask)
        public String username_hostmask;

        @AutoString(R.string.message_plain)
        public String message_plain;

        @AutoString(R.string.message_join)
        public String message_join;

        @AutoString(R.string.message_part)
        public String message_part;

        @AutoString(R.string.message_part_extra)
        public String message_part_extra;

        @AutoString(R.string.message_quit)
        public String message_quit;

        @AutoString(R.string.message_quit_extra)
        public String message_quit_extra;

        @AutoString(R.string.message_kill)
        public String message_kill;

        @AutoString(R.string.message_kick)
        public String message_kick;

        @AutoString(R.string.message_kick_extra)
        public String message_kick_extra;

        @AutoString(R.string.message_mode)
        public String message_mode;

        @AutoString(R.string.message_nick_self)
        public String message_nick_self;

        @AutoString(R.string.message_nick_other)
        public String message_nick_other;

        @AutoString(R.string.message_daychange)
        public String message_daychange;

        @AutoString(R.string.message_action)
        public String message_action;

        @AutoString(R.string.title_status_buffer)
        public String title_status_buffer;

        @NonNull
        public CharSequence formatUsername(@NonNull CharSequence nick, @NonNull CharSequence hostmask) {
            return SpanFormatter.format(username_hostmask, nick, hostmask);
        }

        @NonNull
        public CharSequence formatJoin(@NonNull CharSequence user, @NonNull CharSequence channel) {
            return SpanFormatter.format(message_join, user, channel);
        }

        @NonNull
        public CharSequence formatPart(@NonNull CharSequence user, @NonNull CharSequence channel) {
            return SpanFormatter.format(message_part, user, channel);
        }

        @NonNull
        public CharSequence formatPart(@NonNull CharSequence user, @NonNull CharSequence channel, @Nullable CharSequence reason) {
            if (reason == null || reason.length() == 0) return formatPart(user, channel);

            return SpanFormatter.format(message_part_extra, user, channel, reason);
        }

        @NonNull
        public CharSequence formatQuit(@NonNull CharSequence user) {
            return SpanFormatter.format(message_quit, user);
        }

        @NonNull
        public CharSequence formatQuit(@NonNull CharSequence user, @Nullable CharSequence reason) {
            if (reason == null || reason.length() == 0) return formatQuit(user);

            return SpanFormatter.format(message_quit_extra, user, reason);
        }

        @NonNull
        public CharSequence formatKill(@NonNull CharSequence user, @NonNull CharSequence channel) {
            return SpanFormatter.format(message_kill, user, channel);
        }

        @NonNull
        public CharSequence formatKick(@NonNull CharSequence user, @NonNull CharSequence kicked) {
            return SpanFormatter.format(message_kick, user, kicked);
        }

        @NonNull
        public CharSequence formatKick(@NonNull CharSequence user, @NonNull CharSequence kicked, @Nullable CharSequence reason) {
            if (reason == null || reason.length() == 0) return formatKick(user, kicked);

            return SpanFormatter.format(message_kick_extra, user, kicked, reason);
        }

        @NonNull
        public CharSequence formatMode(@NonNull CharSequence mode, @NonNull CharSequence user) {
            return SpanFormatter.format(message_mode, mode, user);
        }

        @NonNull
        public CharSequence formatNick(@NonNull CharSequence newNick) {
            return SpanFormatter.format(message_nick_self, newNick);
        }

        @NonNull
        public CharSequence formatNick(@NonNull CharSequence oldNick, @Nullable CharSequence newNick) {
            if (newNick == null || newNick.length() == 0) return formatNick(oldNick);

            return SpanFormatter.format(message_nick_other, oldNick, newNick);
        }

        @NonNull
        public CharSequence formatDayChange(@NonNull CharSequence day) {
            return SpanFormatter.format(message_daychange, day);
        }

        @NonNull
        public CharSequence formatAction(@NonNull CharSequence user, @NonNull CharSequence channel) {
            return SpanFormatter.format(message_action, user, channel);
        }

        @NonNull
        public CharSequence formatPlain(@NonNull CharSequence nick, @NonNull CharSequence message) {
            return SpanFormatter.format(message_plain, nick, message);
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

        @AutoColor(R.attr.colorAccentFocus)
        @ColorInt
        public int colorAccentFocus;

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

        @AutoDimen(R.attr.actionBarSize)
        @ColorInt
        public int actionBarSize;
    }
}
