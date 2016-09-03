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

package de.kuschku.quasseldroid_ng.ui.chat.chatview;

import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.message.Message;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.irc.IrcUserUtils;
import de.kuschku.util.irc.format.IrcFormatHelper;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@UiThread
public class ChatMessageRenderer {

    @NonNull
    private final AppContext context;
    private IrcFormatHelper helper;

    public ChatMessageRenderer(@NonNull AppContext context) {
        this.context = context;
        setTheme(context);
    }

    public void setTheme(@NonNull AppContext context) {
        this.helper = new IrcFormatHelper(context);
    }

    @NonNull
    private CharSequence formatNick(@NonNull String hostmask, boolean full) {
        CharSequence formattedNick = helper.formatUserNick(IrcUserUtils.getNick(hostmask));
        if (full) {
            return context.themeUtil().translations.formatUsername(formattedNick, IrcUserUtils.getMask(hostmask));
        } else {
            return formattedNick;
        }
    }

    @NonNull
    private CharSequence formatNick(@NonNull String hostmask) {
        return formatNick(hostmask, context.settings().preferenceHostmask.or(false));
    }

    @NonNull
    private CharSequence getBufferName(@NonNull Message message) {
        assertNotNull(context.client());
        Buffer buffer = context.client().bufferManager().buffer(message.bufferInfo.id);
        assertNotNull(buffer);
        String name = buffer.getName();
        assertNotNull(name);
        return name;
    }

    private void onBindPlain(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(
                context.themeUtil().translations.formatPlain(
                        formatNick(message.sender, false),
                        helper.formatIrcMessage(context.client(), message)
                )
        );
    }

    private void onBindNotice(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(context.themeUtil().translations.formatAction(
                formatNick(message.sender, false),
                helper.formatIrcMessage(context.client(), message)
        ));
    }

    private void onBindAction(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(
                context.themeUtil().translations.formatAction(
                        formatNick(message.sender, false),
                        helper.formatIrcMessage(context.client(), message)
                )
        );
    }

    private void onBindNick(@NonNull MessageViewHolder holder, @NonNull Message message) {
        // FIXME: Ugly hack to get around the issue that quasselcore doesn’t set the Self flag
        boolean self = message.flags.Self || message.sender.equals(message.content);
        if (self)
            holder.content.setText(context.themeUtil().translations.formatNick(
                    formatNick(message.sender, false)
            ));
        else
            holder.content.setText(context.themeUtil().translations.formatNick(
                    formatNick(message.sender, false),
                    helper.formatUserNick(message.content)
            ));
    }

    // TODO: Replace this with better display of mode changes
    private void onBindMode(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(context.themeUtil().translations.formatMode(
                message.content,
                formatNick(message.sender, false)
        ));
    }

    private void onBindJoin(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(context.themeUtil().translations.formatJoin(
                formatNick(message.sender),
                getBufferName(message)
        ));
    }

    private void onBindPart(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(context.themeUtil().translations.formatPart(
                formatNick(message.sender),
                getBufferName(message),
                message.content
        ));
    }

    private void onBindQuit(@NonNull MessageViewHolder holder, @NonNull Message message) {
        if (message.content == null || message.content.isEmpty())
            holder.content.setText(context.themeUtil().translations.formatQuit(
                    formatNick(message.sender)
            ));
        else
            holder.content.setText(context.themeUtil().translations.formatQuit(
                    formatNick(message.sender),
                    message.content
            ));
    }

    private void onBindKick(@NonNull MessageViewHolder holder, @NonNull Message message) {
        if (message.content.contains(" "))
            holder.content.setText(context.themeUtil().translations.formatKick(
                    formatNick(message.sender),
                    message.content.substring(0, message.content.indexOf(" ")),
                    getBufferName(message),
                    message.content.substring(message.content.indexOf(" ") + 1)
            ));
        else
            holder.content.setText(context.themeUtil().translations.formatKick(
                    formatNick(message.sender),
                    message.content,
                    getBufferName(message)
            ));
    }

    private void onBindKill(@NonNull MessageViewHolder holder, @NonNull Message message) {
        if (message.content.contains(" "))
            holder.content.setText(context.themeUtil().translations.formatKill(
                    formatNick(message.sender),
                    message.content.substring(0, message.content.indexOf(" ")),
                    message.content.substring(message.content.indexOf(" ") + 1)
            ));
        else
            holder.content.setText(context.themeUtil().translations.formatKill(
                    formatNick(message.sender),
                    message.content
            ));
    }

    private void onBindServer(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(message.content);
    }

    private void onBindInfo(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(message.content);
    }

    private void onBindError(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(message.content);
    }

    private void onBindDayChange(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(context.themeUtil().translations.formatDayChange(
                context.themeUtil().formatter.getLongDateFormatter().print(message.time)
        ));
    }

    private void onBindTopic(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(message.content);
    }

    private void onBindNetsplitJoin(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(message.toString());
    }

    private void onBindNetsplitQuit(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(message.toString());
    }

    private void onBindInvite(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.content.setText(message.toString());
    }

    public void onBind(@NonNull MessageViewHolder holder, @NonNull Message message) {
        holder.time.setText(context.themeUtil().formatter.getTimeFormatter().print(message.time));
        switch (message.type) {
            case Plain:
                onBindPlain(holder, message);
                break;
            case Notice:
                onBindNotice(holder, message);
                break;
            case Action:
                onBindAction(holder, message);
                break;
            case Nick:
                onBindNick(holder, message);
                break;
            case Mode:
                onBindMode(holder, message);
                break;
            case Join:
                onBindJoin(holder, message);
                break;
            case Part:
                onBindPart(holder, message);
                break;
            case Quit:
                onBindQuit(holder, message);
                break;
            case Kick:
                onBindKick(holder, message);
                break;
            case Kill:
                onBindKill(holder, message);
                break;
            case Server:
                onBindServer(holder, message);
                break;
            case Info:
                onBindInfo(holder, message);
                break;
            case Error:
                onBindError(holder, message);
                break;
            case DayChange:
                onBindDayChange(holder, message);
                break;
            case Topic:
                onBindTopic(holder, message);
                break;
            case NetsplitJoin:
                onBindNetsplitJoin(holder, message);
                break;
            case NetsplitQuit:
                onBindNetsplitQuit(holder, message);
                break;
            case Invite:
                onBindInvite(holder, message);
                break;
        }
    }

    public
    @LayoutRes
    int getLayoutRes(Message.Type type) {
        switch (type) {
            default:
            case Plain:
                return R.layout.widget_chatmessage_plain;
            case Action:
                return R.layout.widget_chatmessage_action;
            case Nick:
            case Notice:
            case Mode:
            case Join:
            case Part:
            case Quit:
            case Kick:
            case Kill:
            case Server:
            case Info:
            case DayChange:
            case Topic:
            case NetsplitJoin:
            case NetsplitQuit:
            case Invite:
                return R.layout.widget_chatmessage_server;
            case Error:
                return R.layout.widget_chatmessage_error;
        }
    }

    private static class MessageStyleContainer {
        public final
        @ColorInt
        int textColor;
        public final int fontstyle;
        public final
        @ColorInt
        int timeColor;
        public final
        @ColorInt
        int bgColor;

        public MessageStyleContainer(int textColor, int fontstyle, int timeColor, int bgColor) {
            this.textColor = textColor;
            this.fontstyle = fontstyle;
            this.timeColor = timeColor;
            this.bgColor = bgColor;
        }
    }
}
