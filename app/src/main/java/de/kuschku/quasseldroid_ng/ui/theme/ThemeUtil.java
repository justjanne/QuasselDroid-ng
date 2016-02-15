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

package de.kuschku.quasseldroid_ng.ui.theme;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.view.ContextThemeWrapper;

import de.kuschku.libquassel.events.ConnectionChangeEvent;
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
    @NonNull
    public final FormatStrings translations = new FormatStrings();
    @NonNull
    public final DateTimeFormatHelper formatter;

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

    public String statusName(ConnectionChangeEvent.Status status) {
        switch (status) {
            case HANDSHAKE:
                return translations.statusHandshake;
            case INITIALIZING_DATA:
                return translations.statusInitData;
            case LOADING_BACKLOG:
                return translations.statusBacklog;
            case CONNECTED:
                return translations.statusWelcome;
            case DISCONNECTED:
            default:
                return translations.statusDisconnected;
        }
    }

    public static class FormatStrings {
        @AutoString(R.string.usernameHostmask)
        public String usernameHostmask;

        @AutoString(R.string.messagePlain)
        public String messagePlain;

        @AutoString(R.string.messageJoin)
        public String messageJoin;

        @AutoString(R.string.messagePart)
        public String messagePart;

        @AutoString(R.string.messagePartExtra)
        public String messagePartExtra;

        @AutoString(R.string.messageQuit)
        public String messageQuit;

        @AutoString(R.string.messageQuitExtra)
        public String messageQuitExtra;

        @AutoString(R.string.messageKill)
        public String messageKill;

        @AutoString(R.string.messageKick)
        public String messageKick;

        @AutoString(R.string.messageKickExtra)
        public String messageKickExtra;

        @AutoString(R.string.messageMode)
        public String messageMode;

        @AutoString(R.string.messageNickSelf)
        public String messageNickSelf;

        @AutoString(R.string.messageNickOther)
        public String messageNickOther;

        @AutoString(R.string.messageDayChange)
        public String messageDaychange;

        @AutoString(R.string.messageAction)
        public String messageAction;

        @AutoString(R.string.labelStatusBuffer)
        public String titleStatusBuffer;

        @AutoString(R.string.warningCertificate)
        public String warningCertificate;

        @AutoString(R.string.statusConnecting)
        public String statusConnecting;

        @AutoString(R.string.statusHandshake)
        public String statusHandshake;

        @AutoString(R.string.statusInitData)
        public String statusInitData;

        @AutoString(R.string.statusBacklog)
        public String statusBacklog;

        @AutoString(R.string.statusConnected)
        public String statusConnected;

        @AutoString(R.string.statusDisconnected)
        public String statusDisconnected;

        @AutoString(R.string.statusWelcome)
        public String statusWelcome;

        @NonNull
        public CharSequence formatUsername(@NonNull CharSequence nick, @NonNull CharSequence hostmask) {
            return SpanFormatter.format(usernameHostmask, nick, hostmask);
        }

        @NonNull
        public CharSequence formatJoin(@NonNull CharSequence user, @NonNull CharSequence channel) {
            return SpanFormatter.format(messageJoin, user, channel);
        }

        @NonNull
        public CharSequence formatPart(@NonNull CharSequence user, @NonNull CharSequence channel) {
            return SpanFormatter.format(messagePart, user, channel);
        }

        @NonNull
        public CharSequence formatPart(@NonNull CharSequence user, @NonNull CharSequence channel, @Nullable CharSequence reason) {
            if (reason == null || reason.length() == 0) return formatPart(user, channel);

            return SpanFormatter.format(messagePartExtra, user, channel, reason);
        }

        @NonNull
        public CharSequence formatQuit(@NonNull CharSequence user) {
            return SpanFormatter.format(messageQuit, user);
        }

        @NonNull
        public CharSequence formatQuit(@NonNull CharSequence user, @Nullable CharSequence reason) {
            if (reason == null || reason.length() == 0) return formatQuit(user);

            return SpanFormatter.format(messageQuitExtra, user, reason);
        }

        @NonNull
        public CharSequence formatKill(@NonNull CharSequence user, @NonNull CharSequence channel) {
            return SpanFormatter.format(messageKill, user, channel);
        }

        @NonNull
        public CharSequence formatKick(@NonNull CharSequence user, @NonNull CharSequence kicked) {
            return SpanFormatter.format(messageKick, user, kicked);
        }

        @NonNull
        public CharSequence formatKick(@NonNull CharSequence user, @NonNull CharSequence kicked, @Nullable CharSequence reason) {
            if (reason == null || reason.length() == 0) return formatKick(user, kicked);

            return SpanFormatter.format(messageKickExtra, user, kicked, reason);
        }

        @NonNull
        public CharSequence formatMode(@NonNull CharSequence mode, @NonNull CharSequence user) {
            return SpanFormatter.format(messageMode, mode, user);
        }

        @NonNull
        public CharSequence formatNick(@NonNull CharSequence newNick) {
            return SpanFormatter.format(messageNickSelf, newNick);
        }

        @NonNull
        public CharSequence formatNick(@NonNull CharSequence oldNick, @Nullable CharSequence newNick) {
            if (newNick == null || newNick.length() == 0) return formatNick(oldNick);

            return SpanFormatter.format(messageNickOther, oldNick, newNick);
        }

        @NonNull
        public CharSequence formatDayChange(@NonNull CharSequence day) {
            return SpanFormatter.format(messageDaychange, day);
        }

        @NonNull
        public CharSequence formatAction(@NonNull CharSequence user, @NonNull CharSequence channel) {
            return SpanFormatter.format(messageAction, user, channel);
        }

        @NonNull
        public CharSequence formatPlain(@NonNull CharSequence nick, @NonNull CharSequence message) {
            return SpanFormatter.format(messagePlain, nick, message);
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

        @AutoColor(R.attr.colorControlHighlight)
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
