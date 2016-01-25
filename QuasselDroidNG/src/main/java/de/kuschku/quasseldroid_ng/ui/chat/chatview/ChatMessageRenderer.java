package de.kuschku.quasseldroid_ng.ui.chat.chatview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.UiThread;
import android.util.Log;

import org.joda.time.format.DateTimeFormatter;

import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.message.Message;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.ui.DateFormatHelper;
import de.kuschku.util.irc.IrcFormatHelper;
import de.kuschku.util.irc.IrcUserUtils;
import de.kuschku.util.ui.SpanFormatter;
import de.kuschku.util.ui.ThemeUtil;
import de.kuschku.util.annotationbind.AutoString;
import de.kuschku.util.annotationbind.AutoBinder;

@UiThread
public class ChatMessageRenderer {
    private final DateTimeFormatter format;
    private final FormatStrings strings;
    private final IrcFormatHelper helper;

    private Client client;
    private boolean fullHostmask = false;

    public ChatMessageRenderer(Context ctx) {
        ThemeUtil themeUtil = new ThemeUtil(ctx);
        this.format = DateFormatHelper.getTimeFormatter(ctx);
        this.strings = new FormatStrings(ctx);
        this.helper = new IrcFormatHelper(themeUtil.colors);

        this.highlightStyle = new MessageStyleContainer(
                themeUtil.colors.colorForegroundHighlight,
                Typeface.NORMAL,
                themeUtil.colors.colorForegroundHighlight,
                themeUtil.colors.colorBackgroundHighlight
        );
        this.serverStyle = new MessageStyleContainer(
                themeUtil.colors.colorForegroundSecondary,
                Typeface.ITALIC,
                themeUtil.colors.colorForegroundSecondary,
                themeUtil.colors.colorBackgroundSecondary
        );
        this.plainStyle = new MessageStyleContainer(
                themeUtil.colors.colorForeground,
                Typeface.NORMAL,
                themeUtil.colors.colorForegroundSecondary,
                themeUtil.colors.transparent
        );
        this.actionStyle = new MessageStyleContainer(
                themeUtil.colors.colorForegroundAction,
                Typeface.ITALIC,
                themeUtil.colors.colorForegroundSecondary,
                themeUtil.colors.transparent
        );
    }

    private static class MessageStyleContainer{
        public final @ColorInt int textColor;
        public final int fontstyle;
        public final @ColorInt int timeColor;
        public final @ColorInt int bgColor;

        public MessageStyleContainer(int textColor, int fontstyle, int timeColor, int bgColor) {
            this.textColor = textColor;
            this.fontstyle = fontstyle;
            this.timeColor = timeColor;
            this.bgColor = bgColor;
        }
    }

    public MessageStyleContainer highlightStyle;
    public MessageStyleContainer serverStyle;
    public MessageStyleContainer actionStyle;
    public MessageStyleContainer plainStyle;

    public void setClient(Client client) {
        this.client = client;
    }

    private void applyStyle(MessageViewHolder holder, MessageStyleContainer style, MessageStyleContainer highlightStyle, boolean highlight) {
        MessageStyleContainer container = highlight ? highlightStyle : style;
        holder.content.setTextColor(container.textColor);
        holder.content.setTypeface(null, container.fontstyle);
        holder.time.setTextColor(container.timeColor);
        holder.itemView.setBackgroundColor(container.bgColor);
    }

    private CharSequence formatNick(String hostmask, boolean full) {
        CharSequence formattedNick = helper.formatUserNick(IrcUserUtils.getNick(hostmask));
        if (full) {
            return strings.formatUsername(formattedNick, IrcUserUtils.getMask(hostmask));
        } else {
            return formattedNick;
        }
    }

    private CharSequence formatNick(String hostmask) {
        return formatNick(hostmask, fullHostmask);
    }

    public void onBindPlain(MessageViewHolder holder, Message message) {
        applyStyle(holder, plainStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(
                strings.formatPlain(
                        formatNick(message.sender, false),
                        helper.formatIrcMessage(message.content)
                )
        );
    }

    public void onBindNotice(MessageViewHolder holder, Message message) {
        applyStyle(holder, plainStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(strings.formatAction(
                formatNick(message.sender, false),
                helper.formatIrcMessage(message.content)
        ));
    }

    public void onBindAction(MessageViewHolder holder, Message message) {
        applyStyle(holder, actionStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(
                strings.formatAction(
                        formatNick(message.sender, false),
                        helper.formatIrcMessage(message.content)
                )
        );
    }

    public void onBindNick(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        if (message.flags.Self)
            holder.content.setText(strings.formatNick(
                    formatNick(message.sender, false)
            ));
        else
            holder.content.setText(strings.formatNick(
                    formatNick(message.sender, false),
                    helper.formatUserNick(message.content)
            ));
    }

    public void onBindMode(MessageViewHolder holder, Message message) {
        
        holder.content.setText(message.toString());
    }

    public void onBindJoin(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(strings.formatJoin(
                formatNick(message.sender),
                client.getBuffer(message.bufferInfo.id).getName()
        ));
    }

    public void onBindPart(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(strings.formatPart(
                formatNick(message.sender),
                client.getBuffer(message.bufferInfo.id).getName(),
                message.content
        ));
    }

    public void onBindQuit(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(strings.formatQuit(
                formatNick(message.sender),
                message.content
        ));
    }

    public void onBindKick(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    public void onBindKill(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    public void onBindServer(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    public void onBindInfo(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    public void onBindError(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    public void onBindDayChange(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    public void onBindTopic(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    public void onBindNetsplitJoin(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    public void onBindNetsplitQuit(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }

    public void onBindInvite(MessageViewHolder holder, Message message) {
        applyStyle(holder, serverStyle, highlightStyle, message.flags.Highlight);
        holder.content.setText(message.toString());
    }
    
    public void onBind(MessageViewHolder holder, Message message) {
        holder.time.setText(format.print(message.time));
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

        public FormatStrings(Context ctx) {
            try {
                AutoBinder.bind(this, ctx);
            } catch (IllegalAccessException e) {
                Log.e("ERROR", e.toString());
                e.printStackTrace();
            }
        }

        public CharSequence formatUsername(CharSequence nick, CharSequence hostmask) {
            return SpanFormatter.format(username_hostmask, nick, hostmask);
        }

        public CharSequence formatJoin(CharSequence user, CharSequence channel) {
            return SpanFormatter.format(message_join, user, channel);
        }

        public CharSequence formatPart(CharSequence user, CharSequence channel) {
            return SpanFormatter.format(message_part, user, channel);
        }
        public CharSequence formatPart(CharSequence user, CharSequence channel, CharSequence reason) {
            if (reason == null || reason.length() == 0) return formatPart(user, channel);

            return SpanFormatter.format(message_part_extra, user, channel, reason);
        }

        public CharSequence formatQuit(CharSequence user) {
            return SpanFormatter.format(message_quit, user);
        }
        public CharSequence formatQuit(CharSequence user, CharSequence reason) {
            if (reason == null || reason.length() == 0) return formatQuit(user);

            return SpanFormatter.format(message_quit_extra, user, reason);
        }

        public CharSequence formatKill(CharSequence user, CharSequence channel) {
            return SpanFormatter.format(message_kill, user, channel);
        }

        public CharSequence formatKick(CharSequence user, CharSequence kicked) {
            return SpanFormatter.format(message_kick, user, kicked);
        }
        public CharSequence formatKick(CharSequence user, CharSequence kicked, CharSequence reason) {
            if (reason == null || reason.length() == 0) return formatKick(user, kicked);

            return SpanFormatter.format(message_kick_extra, user, kicked, reason);
        }

        public CharSequence formatMode(CharSequence mode, CharSequence user) {
            return SpanFormatter.format(message_mode, mode, user);
        }

        public CharSequence formatNick(CharSequence newNick) {
            return SpanFormatter.format(message_nick_self, newNick);
        }
        public CharSequence formatNick(CharSequence oldNick, CharSequence newNick) {
            if (newNick == null || newNick.length() == 0) return formatNick(oldNick);

            return SpanFormatter.format(message_nick_other, oldNick, newNick);
        }

        public CharSequence formatDayChange(CharSequence day) {
            return SpanFormatter.format(message_daychange, day);
        }

        public CharSequence formatAction(CharSequence user, CharSequence channel) {
            return SpanFormatter.format(message_action, user, channel);
        }

        public CharSequence formatPlain(CharSequence nick, CharSequence message) {
            return SpanFormatter.format(message_plain, nick, message);
        }
    }
}
