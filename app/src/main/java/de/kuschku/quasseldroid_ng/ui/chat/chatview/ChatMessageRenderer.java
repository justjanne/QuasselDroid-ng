package de.kuschku.quasseldroid_ng.ui.chat.chatview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;

import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.libquassel.message.Message;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.annotationbind.AutoBinder;
import de.kuschku.util.annotationbind.AutoString;
import de.kuschku.util.irc.IrcFormatHelper;
import de.kuschku.util.irc.IrcUserUtils;
import de.kuschku.util.ui.MessageUtil;
import de.kuschku.util.ui.SpanFormatter;
import de.kuschku.quasseldroid_ng.ui.theme.ThemeUtil;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@UiThread
public class ChatMessageRenderer {

    private IrcFormatHelper helper;
    private MessageStyleContainer highlightStyle;
    private MessageStyleContainer serverStyle;
    private MessageStyleContainer actionStyle;
    private MessageStyleContainer plainStyle;

    @NonNull
    private AppContext context;

    public ChatMessageRenderer(@NonNull Context ctx, @NonNull AppContext context) {
        this.context = context;
        setTheme(context);
    }

    public void setTheme(AppContext context) {
        this.helper = new IrcFormatHelper(context);

        this.highlightStyle = new MessageStyleContainer(
                context.getThemeUtil().res.colorForegroundHighlight,
                Typeface.NORMAL,
                context.getThemeUtil().res.colorForegroundHighlight,
                context.getThemeUtil().res.colorBackgroundHighlight
        );
        this.serverStyle = new MessageStyleContainer(
                context.getThemeUtil().res.colorForegroundSecondary,
                Typeface.ITALIC,
                context.getThemeUtil().res.colorForegroundSecondary,
                context.getThemeUtil().res.colorBackgroundSecondary
        );
        this.plainStyle = new MessageStyleContainer(
                context.getThemeUtil().res.colorForeground,
                Typeface.NORMAL,
                context.getThemeUtil().res.colorForegroundSecondary,
                context.getThemeUtil().res.transparent
        );
        this.actionStyle = new MessageStyleContainer(
                context.getThemeUtil().res.colorForegroundAction,
                Typeface.ITALIC,
                context.getThemeUtil().res.colorForegroundSecondary,
                context.getThemeUtil().res.transparent
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
            return context.getThemeUtil().translations.formatUsername(formattedNick, IrcUserUtils.getMask(hostmask));
        } else {
            return formattedNick;
        }
    }

    @NonNull
    private CharSequence formatNick(@NonNull String hostmask) {
        return formatNick(hostmask, context.getSettings().fullHostmask.or(false));
    }

    @NonNull
    private CharSequence getBufferName(Message message) {
        assertNotNull(context.getClient());
        Buffer buffer = context.getClient().getBuffer(message.bufferInfo.id);
        assertNotNull(buffer);
        String name = buffer.getName();
        assertNotNull(name);
        return name;
    }

    private void onBindPlain(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, plainStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(
                context.getThemeUtil().translations.formatPlain(
                        formatNick(message.sender, false),
                        helper.formatIrcMessage(message.content)
                )
        );
    }

    private void onBindNotice(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, plainStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(context.getThemeUtil().translations.formatAction(
                formatNick(message.sender, false),
                helper.formatIrcMessage(message.content)
        ));
    }

    private void onBindAction(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, actionStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(
                context.getThemeUtil().translations.formatAction(
                        formatNick(message.sender, false),
                        helper.formatIrcMessage(message.content)
                )
        );
    }

    private void onBindNick(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        if (message.flags.Self)
            holder.content.setText(context.getThemeUtil().translations.formatNick(
                    formatNick(message.sender, false)
            ));
        else
            holder.content.setText(context.getThemeUtil().translations.formatNick(
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
        holder.content.setText(context.getThemeUtil().translations.formatJoin(
                formatNick(message.sender),
                getBufferName(message)
        ));
    }

    private void onBindPart(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(context.getThemeUtil().translations.formatPart(
                formatNick(message.sender),
                getBufferName(message),
                message.content
        ));
    }

    private void onBindQuit(@NonNull MessageViewHolder holder, @NonNull Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(context.getThemeUtil().translations.formatQuit(
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
        holder.content.setText(context.getThemeUtil().translations.formatDayChange(
                context.getThemeUtil().formatter.getLongDateFormatter().print(message.time)
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
        holder.time.setText(context.getThemeUtil().formatter.getTimeFormatter().print(message.time));
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
