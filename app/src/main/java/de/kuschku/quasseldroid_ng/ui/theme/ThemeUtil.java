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
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.util.SparseIntArray;

import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.annotationbind.AutoBinder;
import de.kuschku.util.annotationbind.AutoColor;
import de.kuschku.util.annotationbind.AutoDimen;
import de.kuschku.util.annotationbind.AutoInt;
import de.kuschku.util.annotationbind.AutoString;
import de.kuschku.util.irc.chanmodes.ChanMode;
import de.kuschku.util.ui.DateTimeFormatHelper;
import de.kuschku.util.ui.SpanFormatter;

import static android.support.v4.content.res.ResourcesCompat.getDrawable;

public class ThemeUtil {
    @NonNull
    public final Colors res = new Colors();

    @NonNull
    public final FormatStrings translations = new FormatStrings();

    @NonNull
    public final ChanModeStrings chanModes = new ChanModeStrings();

    @NonNull
    public final DateTimeFormatHelper formatter;

    @NonNull
    public final StatusDrawables statusDrawables;

    public ThemeUtil(@NonNull Context ctx) {
        ContextThemeWrapper themeWrapper = new ContextThemeWrapper(ctx, ctx.getTheme());
        initColors(themeWrapper);
        statusDrawables = new StatusDrawables(ctx, res, themeWrapper.getTheme());
        formatter = new DateTimeFormatHelper(ctx);
    }

    public ThemeUtil(@NonNull Context ctx, @NonNull AppTheme theme) {
        ContextThemeWrapper themeWrapper = new ContextThemeWrapper(ctx, theme.themeId);
        initColors(themeWrapper);
        statusDrawables = new StatusDrawables(ctx, res, themeWrapper.getTheme());
        formatter = new DateTimeFormatHelper(ctx);
    }

    @UiThread
    public void initColors(@NonNull ContextThemeWrapper wrapper) {
        try {
            res.colors = null;
            AutoBinder.bind(res, wrapper);
            AutoBinder.bind(translations, wrapper);
            AutoBinder.bind(chanModes, wrapper);
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

    public static class StatusDrawables {
        public final Drawable online;
        public final Drawable away;
        public final Drawable offline;

        public final Drawable channelOnline;
        public final Drawable channelOffline;

        public StatusDrawables(Context ctx, Colors colors, Resources.Theme theme) {
            Resources resources = ctx.getResources();
            online = getDrawable(resources, R.drawable.ic_status, theme);
            DrawableCompat.setTint(online, colors.colorAccent);
            away = getDrawable(resources, R.drawable.ic_status, theme);
            DrawableCompat.setTint(away, colors.colorAway);
            offline = getDrawable(resources, R.drawable.ic_status_offline, theme);
            DrawableCompat.setTint(offline, colors.colorOffline);

            channelOnline = getDrawable(resources, R.drawable.ic_status_channel, theme);
            DrawableCompat.setTint(channelOnline, colors.colorAccent);
            channelOffline = getDrawable(resources, R.drawable.ic_status_channel_offline, theme);
            DrawableCompat.setTint(channelOffline, colors.colorOffline);
        }

        public Drawable of(BufferInfo.Type type, BufferInfo.BufferStatus status) {
            if (type == BufferInfo.Type.CHANNEL) {
                if (status == BufferInfo.BufferStatus.ONLINE)
                    return channelOnline;
                else
                    return channelOffline;
            } else {
                if (status == BufferInfo.BufferStatus.ONLINE)
                    return online;
                else if (status == BufferInfo.BufferStatus.AWAY)
                    return away;
                else
                    return offline;
            }
        }
    }

    public static class ChanModeStrings {

        @AutoString(R.string.chanMode_RESTRICT_TOPIC_NAME)
        public String chanMode_RESTRICT_TOPIC_NAME;

        @AutoString(R.string.chanMode_RESTRICT_TOPIC_DESCRIPTION)
        public String chanMode_RESTRICT_TOPIC_DESCRIPTION;

        @AutoString(R.string.chanMode_BLOCK_COLORS_NAME)
        public String chanMode_BLOCK_COLOR_NAME;

        @AutoString(R.string.chanMode_BLOCK_COLORS_DESCRIPTION)
        public String chanMode_BLOCK_COLOR_DESCRIPTION;

        @AutoString(R.string.chanMode_STRIP_COLORS_NAME)
        public String chanMode_STRIP_COLOR_NAME;

        @AutoString(R.string.chanMode_STRIP_COLORS_DESCRIPTION)
        public String chanMode_STRIP_COLOR_DESCRIPTION;

        @AutoString(R.string.chanMode_ONLY_INVITE_NAME)
        public String chanMode_ONLY_INVITE_NAME;

        @AutoString(R.string.chanMode_ONLY_INVITE_DESCRIPTION)
        public String chanMode_ONLY_INVITE_DESCRIPTION;

        @AutoString(R.string.chanMode_ONLY_SSL_NAME)
        public String chanMode_ONLY_SSL_NAME;

        @AutoString(R.string.chanMode_ONLY_SSL_DESCRIPTION)
        public String chanMode_ONLY_SSL_DESCRIPTION;

        @AutoString(R.string.chanMode_UNLISTED_NAME)
        public String chanMode_UNLISTED_NAME;

        @AutoString(R.string.chanMode_UNLISTED_DESCRIPTION)
        public String chanMode_UNLISTED_DESCRIPTION;

        @AutoString(R.string.chanMode_BLOCK_UNIDENTIFIED_NAME)
        public String chanMode_BLOCK_UNIDENTIFIED_NAME;

        @AutoString(R.string.chanMode_BLOCK_UNIDENTIFIED_DESCRIPTION)
        public String chanMode_BLOCK_UNIDENTIFIED_DESCRIPTION;

        @AutoString(R.string.chanMode_PARANOID_NAME)
        public String chanMode_PARANOID_NAME;

        @AutoString(R.string.chanMode_PARANOID_DESCRIPTION)
        public String chanMode_PARANOID_DESCRIPTION;

        @AutoString(R.string.chanMode_REGISTERED_NAME)
        public String chanMode_REGISTERED_NAME;

        @AutoString(R.string.chanMode_REGISTERED_DESCRIPTION)
        public String chanMode_REGISTERED_DESCRIPTION;

        @AutoString(R.string.chanMode_MODERATED_NAME)
        public String chanMode_MODERATED_NAME;

        @AutoString(R.string.chanMode_MODERATED_DESCRIPTION)
        public String chanMode_MODERATED_DESCRIPTION;

        @AutoString(R.string.chanMode_BLOCK_EXTERNAL_NAME)
        public String chanMode_BLOCK_EXTERNAL_NAME;

        @AutoString(R.string.chanMode_BLOCK_EXTERNAL_DESCRIPTION)
        public String chanMode_BLOCK_EXTERNAL_DESCRIPTION;

        @AutoString(R.string.chanMode_ANTIFLOOD_NAME)
        public String chanMode_ANTIFLOOD_NAME;

        @AutoString(R.string.chanMode_ANTIFLOOD_DESCRIPTION)
        public String chanMode_ANTIFLOOD_DESCRIPTION;

        @AutoString(R.string.chanMode_PASSWORD_NAME)
        public String chanMode_PASSWORD_NAME;

        @AutoString(R.string.chanMode_PASSWORD_DESCRIPTION)
        public String chanMode_PASSWORD_DESCRIPTION;

        @AutoString(R.string.chanMode_LIMIT_NAME)
        public String chanMode_LIMIT_NAME;

        @AutoString(R.string.chanMode_LIMIT_DESCRIPTION)
        public String chanMode_LIMIT_DESCRIPTION;

        @AutoString(R.string.chanMode_REDUCED_MODERATION_NAME)
        public String chanMode_REDUCED_MODERATION_NAME;

        @AutoString(R.string.chanMode_REDUCED_MODERATION_DESCRIPTION)
        public String chanMode_REDUCED_MODERATION_DESCRIPTION;

        @AutoString(R.string.chanMode_BLOCK_NOTICE_NAME)
        public String chanMode_BLOCK_NOTICE_NAME;

        @AutoString(R.string.chanMode_BLOCK_NOTICE_DESCRIPTION)
        public String chanMode_BLOCK_NOTICE_DESCRIPTION;

        @AutoString(R.string.chanMode_DISABLE_INVITE_NAME)
        public String chanMode_DISABLE_INVITE_NAME;

        @AutoString(R.string.chanMode_DISABLE_INVITE_DESCRIPTION)
        public String chanMode_DISABLE_INVITE_DESCRIPTION;

        @AutoString(R.string.chanMode_AUDITORIUM_NAME)
        public String chanMode_AUDITORIUM_NAME;

        @AutoString(R.string.chanMode_AUDITORIUM_DESCRIPTION)
        public String chanMode_AUDITORIUM_DESCRIPTION;

        @AutoString(R.string.chanMode_QUIET_UNIDENTIFIED_NAME)
        public String chanMode_QUIET_UNIDENTIFIED_NAME;

        @AutoString(R.string.chanMode_QUIET_UNIDENTIFIED_DESCRIPTION)
        public String chanMode_QUIET_UNIDENTIFIED_DESCRIPTION;

        @AutoString(R.string.chanMode_BLOCK_KICK_NAME)
        public String chanMode_BLOCK_KICK_NAME;

        @AutoString(R.string.chanMode_BLOCK_KICK_DESCRIPTION)
        public String chanMode_BLOCK_KICK_DESCRIPTION;

        @AutoString(R.string.chanMode_PERMANENT_NAME)
        public String chanMode_PERMANENT_NAME;

        @AutoString(R.string.chanMode_PERMANENT_DESCRIPTION)
        public String chanMode_PERMANENT_DESCRIPTION;

        @AutoString(R.string.chanMode_ONLY_OPER_NAME)
        public String chanMode_ONLY_OPER_NAME;

        @AutoString(R.string.chanMode_ONLY_OPER_DESCRIPTION)
        public String chanMode_ONLY_OPER_DESCRIPTION;

        @AutoString(R.string.chanMode_ONLY_HELPOPER_NAME)
        public String chanMode_ONLY_HELPOPER_NAME;

        @AutoString(R.string.chanMode_ONLY_HELPOPER_DESCRIPTION)
        public String chanMode_ONLY_HELPOPER_DESCRIPTION;

        @AutoString(R.string.chanMode_BLOCK_NICKCHANGE_NAME)
        public String chanMode_BLOCK_NICKCHANGE_NAME;

        @AutoString(R.string.chanMode_BLOCK_NICKCHANGE_DESCRIPTION)
        public String chanMode_BLOCK_NICKCHANGE_DESCRIPTION;

        @AutoString(R.string.chanMode_JOIN_THROTTLE_NAME)
        public String chanMode_JOIN_THROTTLE_NAME;

        @AutoString(R.string.chanMode_JOIN_THROTTLE_DESCRIPTION)
        public String chanMode_JOIN_THROTTLE_DESCRIPTION;

        @AutoString(R.string.chanMode_ALLOW_INVITE_NAME)
        public String chanMode_ALLOW_INVITE_NAME;

        @AutoString(R.string.chanMode_ALLOW_INVITE_DESCRIPTION)
        public String chanMode_ALLOW_INVITE_DESCRIPTION;

        @AutoString(R.string.chanMode_BLOCK_FORWARDING_NAME)
        public String chanMode_BLOCK_FORWARDING_NAME;

        @AutoString(R.string.chanMode_BLOCK_FORWARDING_DESCRIPTION)
        public String chanMode_BLOCK_FORWARDING_DESCRIPTION;

        @AutoString(R.string.chanMode_ALLOW_FORWARD_NAME)
        public String chanMode_ALLOW_FORWARD_NAME;

        @AutoString(R.string.chanMode_ALLOW_FORWARD_DESCRIPTION)
        public String chanMode_ALLOW_FORWARD_DESCRIPTION;

        @AutoString(R.string.chanMode_BLOCK_ACTION_NAME)
        public String chanMode_BLOCK_ACTION_NAME;

        @AutoString(R.string.chanMode_BLOCK_ACTION_DESCRIPTION)
        public String chanMode_BLOCK_ACTION_DESCRIPTION;

        @AutoString(R.string.chanMode_BLOCK_CAPS_NAME)
        public String chanMode_BLOCK_CAPS_NAME;

        @AutoString(R.string.chanMode_BLOCK_CAPS_DESCRIPTION)
        public String chanMode_BLOCK_CAPS_DESCRIPTION;

        @AutoString(R.string.chanMode_BLOCK_KNOCK_NAME)
        public String chanMode_BLOCK_KNOCK_NAME;

        @AutoString(R.string.chanMode_BLOCK_KNOCK_DESCRIPTION)
        public String chanMode_BLOCK_KNOCK_DESCRIPTION;

        @AutoString(R.string.chanMode_CENSOR_NAME)
        public String chanMode_CENSOR_NAME;

        @AutoString(R.string.chanMode_CENSOR_DESCRIPTION)
        public String chanMode_CENSOR_DESCRIPTION;

        @AutoString(R.string.chanMode_HIDE_JOINS_NAME)
        public String chanMode_HIDE_JOINS_NAME;

        @AutoString(R.string.chanMode_HIDE_JOINS_DESCRIPTION)
        public String chanMode_HIDE_JOINS_DESCRIPTION;

        @AutoString(R.string.chanMode_BLOCK_REPEAT_NAME)
        public String chanMode_BLOCK_REPEAT_NAME;

        @AutoString(R.string.chanMode_BLOCK_REPEAT_DESCRIPTION)
        public String chanMode_BLOCK_REPEAT_DESCRIPTION;

        @AutoString(R.string.chanMode_BLOCK_AUTOREJOIN_NAME)
        public String chanMode_BLOCK_AUTOREJOIN_NAME;

        @AutoString(R.string.chanMode_BLOCK_AUTOREJOIN_DESCRIPTION)
        public String chanMode_BLOCK_AUTOREJOIN_DESCRIPTION;

        @AutoString(R.string.chanMode_IS_SECURE_NAME)
        public String chanMode_IS_SECURE_NAME;

        @AutoString(R.string.chanMode_IS_SECURE_DESCRIPTION)
        public String chanMode_IS_SECURE_DESCRIPTION;

        @AutoString(R.string.chanMode_BLOCK_CTCP_NAME)
        public String chanMode_BLOCK_CTCP_NAME;

        @AutoString(R.string.chanMode_BLOCK_CTCP_DESCRIPTION)
        public String chanMode_BLOCK_CTCP_DESCRIPTION;

        @AutoString(R.string.chanMode_ONLY_ADMIN_NAME)
        public String chanMode_ONLY_ADMIN_NAME;

        @AutoString(R.string.chanMode_ONLY_ADMIN_DESCRIPTION)
        public String chanMode_ONLY_ADMIN_DESCRIPTION;

        @AutoString(R.string.chanMode_FORWARD_NAME)
        public String chanMode_FORWARD_NAME;

        @AutoString(R.string.chanMode_FORWARD_DESCRIPTION)
        public String chanMode_FORWARD_DESCRIPTION;


        public String chanModeToDescription(ChanMode mode) {
            switch (mode) {
                case RESTRICT_TOPIC:
                    return chanMode_RESTRICT_TOPIC_DESCRIPTION;
                case BLOCK_CTCP:
                    return chanMode_BLOCK_CTCP_DESCRIPTION;
                case BLOCK_COLOR:
                    return chanMode_BLOCK_COLOR_DESCRIPTION;
                case STRIP_COLOR:
                    return chanMode_STRIP_COLOR_DESCRIPTION;
                case ONLY_INVITE:
                    return chanMode_ONLY_INVITE_DESCRIPTION;
                case ONLY_ADMIN:
                    return chanMode_ONLY_ADMIN_DESCRIPTION;
                case ONLY_SSL:
                    return chanMode_ONLY_SSL_DESCRIPTION;
                case UNLISTED:
                    return chanMode_UNLISTED_DESCRIPTION;
                case BLOCK_UNIDENTIFIED:
                    return chanMode_BLOCK_UNIDENTIFIED_DESCRIPTION;
                case PARANOID:
                    return chanMode_PARANOID_DESCRIPTION;
                case REGISTERED:
                    return chanMode_REGISTERED_DESCRIPTION;
                case MODERATED:
                    return chanMode_MODERATED_DESCRIPTION;
                case BLOCK_EXTERNAL:
                    return chanMode_BLOCK_EXTERNAL_DESCRIPTION;
                case ANTIFLOOD:
                    return chanMode_ANTIFLOOD_DESCRIPTION;
                case PASSWORD:
                    return chanMode_PASSWORD_DESCRIPTION;
                case LIMIT:
                    return chanMode_LIMIT_DESCRIPTION;
                case REDUCED_MODERATION:
                    return chanMode_REDUCED_MODERATION_DESCRIPTION;
                case BLOCK_NOTICE:
                    return chanMode_BLOCK_NOTICE_DESCRIPTION;
                case DISABLE_INVITE:
                    return chanMode_DISABLE_INVITE_DESCRIPTION;
                case AUDITORIUM:
                    return chanMode_AUDITORIUM_DESCRIPTION;
                case QUIET_UNIDENTIFIED:
                    return chanMode_QUIET_UNIDENTIFIED_DESCRIPTION;
                case BLOCK_KICK:
                    return chanMode_BLOCK_KICK_DESCRIPTION;
                case PERMANENT:
                    return chanMode_PERMANENT_DESCRIPTION;
                case ONLY_OPER:
                    return chanMode_ONLY_OPER_DESCRIPTION;
                case ONLY_HELPOPER:
                    return chanMode_ONLY_HELPOPER_DESCRIPTION;
                case BLOCK_NICKCHANGE:
                    return chanMode_BLOCK_NICKCHANGE_DESCRIPTION;
                case JOIN_THROTTLE:
                    return chanMode_JOIN_THROTTLE_DESCRIPTION;
                case ALLOW_INVITE:
                    return chanMode_ALLOW_INVITE_DESCRIPTION;
                case BLOCK_FORWARDING:
                    return chanMode_BLOCK_FORWARDING_DESCRIPTION;
                case ALLOW_FORWARD:
                    return chanMode_ALLOW_FORWARD_DESCRIPTION;
                case BLOCK_ACTION:
                    return chanMode_BLOCK_ACTION_DESCRIPTION;
                case BLOCK_CAPS:
                    return chanMode_BLOCK_CAPS_DESCRIPTION;
                case BLOCK_KNOCK:
                    return chanMode_BLOCK_KNOCK_DESCRIPTION;
                case CENSOR:
                    return chanMode_CENSOR_DESCRIPTION;
                case HIDE_JOINS:
                    return chanMode_HIDE_JOINS_DESCRIPTION;
                case BLOCK_REPEAT:
                    return chanMode_BLOCK_REPEAT_DESCRIPTION;
                case BLOCK_AUTOREJOIN:
                    return chanMode_BLOCK_AUTOREJOIN_DESCRIPTION;
                case IS_SECURE:
                    return chanMode_IS_SECURE_DESCRIPTION;
                case FORWARD:
                    return chanMode_FORWARD_DESCRIPTION;
            }
            return null;
        }

        public String chanModeToName(ChanMode mode) {
            switch (mode) {
                case RESTRICT_TOPIC:
                    return chanMode_RESTRICT_TOPIC_NAME;
                case BLOCK_CTCP:
                    return chanMode_BLOCK_CTCP_NAME;
                case BLOCK_COLOR:
                    return chanMode_BLOCK_COLOR_NAME;
                case STRIP_COLOR:
                    return chanMode_STRIP_COLOR_NAME;
                case ONLY_INVITE:
                    return chanMode_ONLY_INVITE_NAME;
                case ONLY_ADMIN:
                    return chanMode_ONLY_ADMIN_NAME;
                case ONLY_SSL:
                    return chanMode_ONLY_SSL_NAME;
                case UNLISTED:
                    return chanMode_UNLISTED_NAME;
                case BLOCK_UNIDENTIFIED:
                    return chanMode_BLOCK_UNIDENTIFIED_NAME;
                case PARANOID:
                    return chanMode_PARANOID_NAME;
                case REGISTERED:
                    return chanMode_REGISTERED_NAME;
                case MODERATED:
                    return chanMode_MODERATED_NAME;
                case BLOCK_EXTERNAL:
                    return chanMode_BLOCK_EXTERNAL_NAME;
                case ANTIFLOOD:
                    return chanMode_ANTIFLOOD_NAME;
                case PASSWORD:
                    return chanMode_PASSWORD_NAME;
                case LIMIT:
                    return chanMode_LIMIT_NAME;
                case REDUCED_MODERATION:
                    return chanMode_REDUCED_MODERATION_NAME;
                case BLOCK_NOTICE:
                    return chanMode_BLOCK_NOTICE_NAME;
                case DISABLE_INVITE:
                    return chanMode_DISABLE_INVITE_NAME;
                case AUDITORIUM:
                    return chanMode_AUDITORIUM_NAME;
                case QUIET_UNIDENTIFIED:
                    return chanMode_QUIET_UNIDENTIFIED_NAME;
                case BLOCK_KICK:
                    return chanMode_BLOCK_KICK_NAME;
                case PERMANENT:
                    return chanMode_PERMANENT_NAME;
                case ONLY_OPER:
                    return chanMode_ONLY_OPER_NAME;
                case ONLY_HELPOPER:
                    return chanMode_ONLY_HELPOPER_NAME;
                case BLOCK_NICKCHANGE:
                    return chanMode_BLOCK_NICKCHANGE_NAME;
                case JOIN_THROTTLE:
                    return chanMode_JOIN_THROTTLE_NAME;
                case ALLOW_INVITE:
                    return chanMode_ALLOW_INVITE_NAME;
                case BLOCK_FORWARDING:
                    return chanMode_BLOCK_FORWARDING_NAME;
                case ALLOW_FORWARD:
                    return chanMode_ALLOW_FORWARD_NAME;
                case BLOCK_ACTION:
                    return chanMode_BLOCK_ACTION_NAME;
                case BLOCK_CAPS:
                    return chanMode_BLOCK_CAPS_NAME;
                case BLOCK_KNOCK:
                    return chanMode_BLOCK_KNOCK_NAME;
                case CENSOR:
                    return chanMode_CENSOR_NAME;
                case HIDE_JOINS:
                    return chanMode_HIDE_JOINS_NAME;
                case BLOCK_REPEAT:
                    return chanMode_BLOCK_REPEAT_NAME;
                case BLOCK_AUTOREJOIN:
                    return chanMode_BLOCK_AUTOREJOIN_NAME;
                case IS_SECURE:
                    return chanMode_IS_SECURE_NAME;
                case FORWARD:
                    return chanMode_FORWARD_NAME;
            }
            return null;
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

        @AutoString(R.string.messageKillExtra)
        public String messageKillExtra;

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

        @AutoString(R.string.messageTopic)
        public String messageTopic;

        @AutoString(R.string.confirmationCertficate)
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
        public CharSequence formatKill(@NonNull CharSequence user, @NonNull CharSequence subject) {
            return SpanFormatter.format(messageKill, user, subject);
        }

        @NonNull
        public CharSequence formatKill(@NonNull CharSequence user, @NonNull CharSequence subject, @Nullable CharSequence reason) {
            return SpanFormatter.format(messageKillExtra, user, subject, reason);
        }

        @NonNull
        public CharSequence formatKick(@NonNull CharSequence user, @NonNull CharSequence kicked, @NonNull CharSequence channel) {
            return SpanFormatter.format(messageKick, user, kicked, channel);
        }

        @NonNull
        public CharSequence formatKick(@NonNull CharSequence user, @NonNull CharSequence kicked, @Nullable CharSequence reason, @NonNull CharSequence channel) {
            if (reason == null || reason.length() == 0) return formatKick(user, kicked, channel);

            return SpanFormatter.format(messageKickExtra, user, kicked, reason, channel);
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

        public String proxyType(NetworkServer.ProxyType type) {
            switch (type) {
                default:
                case DefaultProxy:
                    return "No Proxy";
                case Socks5Proxy:
                    return "Socks5";
                case HttpProxy:
                    return "Http";
            }
        }

        public String minimumActivity(QBufferViewConfig.MinimumActivity minimumActivity) {
            switch (minimumActivity) {
                default:
                case NONE:
                    return "No Activity";
                case OTHER:
                    return "Other Activity";
                case MESSAGE:
                    return "Message";
                case HIGHLIGHT:
                    return "Highlight";
            }
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

        @AutoColor(R.attr.colorFill)
        @ColorInt
        public int colorFill;

        @AutoColor(R.attr.colorOffline)
        @ColorInt
        public int colorOffline;

        @AutoColor(R.attr.colorAway)
        @ColorInt
        public int colorAway;

        @AutoInt(R.attr.colorForegroundMirc)
        public int colorForegroundMirc;

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

        @AutoColor(R.attr.colorForegroundError)
        @ColorInt
        public int colorForegroundError;

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

        @AutoColor(R.attr.colorControlHighlight)
        @ColorInt
        public int colorSelected;

        @AutoDimen(R.attr.actionBarSize)
        @ColorInt
        public int actionBarSize;

        private SparseIntArray colors;

        public int colorToId(int foregroundColor) {
            if (colors == null) {
                colors = new SparseIntArray(16);
                for (int i = 0; i < mircColors.length; i++) {
                    colors.put(mircColors[i], i);
                }
            }

            return colors.get(foregroundColor, -1);
        }
    }
}
