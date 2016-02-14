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

import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.message.Message;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.irc.IrcUserUtils;
import de.kuschku.util.irc.format.IrcFormatHelper;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@UiThread
public class ChatMessageRenderer {

    @NonNull
    private final AppContext context;
    private IrcFormatHelper helper;
    private MessageStyleContainer highlightStyle;
    private MessageStyleContainer serverStyle;
    private MessageStyleContainer actionStyle;
    private MessageStyleContainer plainStyle;

    public ChatMessageRenderer(@NonNull AppContext context) {
        this.context = context;
        setTheme(context);
    }

    public void setTheme(@NonNull AppContext context) {
        this.helper = new IrcFormatHelper(context);

        this.highlightStyle = new MessageStyleContainer(
                context.themeUtil().res.colorForegroundHighlight,
                Typeface.NORMAL,
                context.themeUtil().res.colorForegroundHighlight,
                context.themeUtil().res.colorBackgroundHighlight
        );
        this.serverStyle = new MessageStyleContainer(
                context.themeUtil().res.colorForegroundSecondary,
                Typeface.ITALIC,
                context.themeUtil().res.colorForegroundSecondary,
                context.themeUtil().res.colorBackgroundSecondary
        );
        this.plainStyle = new MessageStyleContainer(
                context.themeUtil().res.colorForeground,
                Typeface.NORMAL,
                context.themeUtil().res.colorForegroundSecondary,
                context.themeUtil().res.transparent
        );
        this.actionStyle = new MessageStyleContainer(
                context.themeUtil().res.colorForegroundAction,
                Typeface.ITALIC,
                context.themeUtil().res.colorForegroundSecondary,
                context.themeUtil().res.transparent
        );
    }

    private void applyStyle(@NonNull MessageViewHolder holder, @NonNull MessageStyleContainer style, @NonNull MessageStyleContainer highlightStyle, boolean highlight) {
        MessageStyleContainer container = highlight ? highlightStyle : style;
        holder.content.setTextColor(container.textColor);
        holder.content.setTypeface(null, container.fontstyle);
        holder.time.setTextColor(container.timeColor);
        holder.itemView.setBackgroundColor(container.bgColor);
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
        return formatNick(hostmask, context.settings().fullHostmask.or(false));
    }

    @NonNull
    private CharSequence getBufferName(@NonNull Message message) {
        assertNotNull(context.client());
        Buffer buffer = context.client().bufferManager().buffer(message.bufferInfo.id());
        assertNotNull(buffer);
        String name = buffer.getName();
        assertNotNull(name);
        return name;
    }

    private void onBindPlain(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, plainStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(
                context.themeUtil().translations.formatPlain(
                        formatNick(message.sender, false),
                        helper.formatIrcMessage(message.content)
                )
        );
    }

    private void onBindNotice(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, plainStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(context.themeUtil().translations.formatAction(
                formatNick(message.sender, false),
                helper.formatIrcMessage(message.content)
        ));
    }

    private void onBindAction(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, actionStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(
                context.themeUtil().translations.formatAction(
                        formatNick(message.sender, false),
                        helper.formatIrcMessage(message.content)
                )
        );
    }

    private void onBindNick(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        if (message.flags.Self)
            holder.content.setText(context.themeUtil().translations.formatNick(
                    formatNick(message.sender, false)
            ));
        else
            holder.content.setText(context.themeUtil().translations.formatNick(
                    formatNick(message.sender, false),
                    helper.formatUserNick(message.content)
            ));
    }

    private void onBindMode(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindJoin(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(context.themeUtil().translations.formatJoin(
                formatNick(message.sender),
                getBufferName(message)
        ));
    }

    private void onBindPart(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(context.themeUtil().translations.formatPart(
                formatNick(message.sender),
                getBufferName(message),
                message.content
        ));
    }

    private void onBindQuit(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(context.themeUtil().translations.formatQuit(
                formatNick(message.sender),
                message.content
        ));
    }

    private void onBindKick(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindKill(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindServer(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindInfo(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindError(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindDayChange(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(context.themeUtil().translations.formatDayChange(
                context.themeUtil().formatter.getLongDateFormatter().print(message.time)
        ));
    }

    private void onBindTopic(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindNetsplitJoin(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindNetsplitQuit(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    private void onBindInvite(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
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
