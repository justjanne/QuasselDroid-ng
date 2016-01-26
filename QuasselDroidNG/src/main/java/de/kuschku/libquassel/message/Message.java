package de.kuschku.libquassel.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.util.observables.ContentComparable;

public class Message implements ContentComparable<Message> {
    public final int messageId;
    @NonNull
    public final DateTime time;
    @NonNull
    public final Type type;
    @NonNull
    public final Flags flags;
    @NonNull
    public final BufferInfo bufferInfo;
    @NonNull
    public final String sender;
    @NonNull
    public final String content;

    public Message(int messageId, @NonNull DateTime time, @NonNull Type type, @NonNull Flags flags, @NonNull BufferInfo bufferInfo, @NonNull String sender,
                   @NonNull String content) {
        this.messageId = messageId;
        this.time = time;
        this.type = type;
        this.flags = flags;
        this.bufferInfo = bufferInfo;
        this.sender = sender;
        this.content = content;
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", time=" + time +
                ", type=" + type +
                ", flags=" + flags +
                ", bufferInfo=" + bufferInfo +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean equalsContent(@NonNull Message message) {
        return messageId == message.messageId &&
                time.equals(message.time) &&
                type == message.type &&
                flags.equals(message.flags) &&
                bufferInfo.equals(message.bufferInfo) &&
                sender.equals(message.sender) &&
                content.equals(message.content);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && messageId == ((Message) o).messageId;

    }

    @Override
    public int hashCode() {
        return messageId;
    }

    @Override
    public int compareTo(@NonNull Message another) {
        return this.messageId - another.messageId;
    }

    public enum Type {
        Plain(0x00001),
        Notice(0x00002),
        Action(0x00004),
        Nick(0x00008),
        Mode(0x00010),
        Join(0x00020),
        Part(0x00040),
        Quit(0x00080),
        Kick(0x00100),
        Kill(0x00200),
        Server(0x00400),
        Info(0x00800),
        Error(0x01000),
        DayChange(0x02000),
        Topic(0x04000),
        NetsplitJoin(0x08000),
        NetsplitQuit(0x10000),
        Invite(0x20000);

        public final int value;

        Type(int value) {
            this.value = value;
        }

        @NonNull
        public static Type fromId(int id) {
            switch (id) {
                case 0x00001:
                    return Plain;
                case 0x00002:
                    return Notice;
                case 0x00004:
                    return Action;
                case 0x00008:
                    return Nick;
                case 0x00010:
                    return Mode;
                case 0x00020:
                    return Join;
                case 0x00040:
                    return Part;
                case 0x00080:
                    return Quit;
                case 0x00100:
                    return Kick;
                case 0x00200:
                    return Kill;
                case 0x00400:
                    return Server;
                case 0x00800:
                    return Info;
                case 0x01000:
                    return Error;
                case 0x02000:
                    return DayChange;
                case 0x04000:
                    return Topic;
                case 0x08000:
                    return NetsplitJoin;
                case 0x10000:
                    return NetsplitQuit;
                case 0x20000:
                    return Invite;
                default:
                    return Error;
            }
        }
    }

    public static class Flags {
        public final boolean Self;
        public final boolean Highlight;
        public final boolean Redirected;
        public final boolean ServerMsg;
        public final boolean Backlog;

        public final byte flags;

        public Flags(boolean self, boolean highlight, boolean redirected, boolean serverMsg, boolean backlog) {
            Self = self;
            Highlight = highlight;
            Redirected = redirected;
            ServerMsg = serverMsg;
            Backlog = backlog;

            flags = (byte) ((Self ? 0x01 : 0x00) |
                    (Highlight ? 0x02 : 0x00) |
                    (Redirected ? 0x04 : 0x00) |
                    (ServerMsg ? 0x08 : 0x00) |
                    (Backlog ? 0x80 : 0x00));
        }

        public Flags(byte flags) {
            this.flags = flags;
            Self = (0 != (flags & 0x01));
            Highlight = (0 != (flags & 0x02));
            Redirected = (0 != (flags & 0x04));
            ServerMsg = (0 != (flags & 0x08));
            Backlog = (0 != (flags & 0x80));
        }

        @NonNull
        @Override
        public String toString() {
            final StringBuilder output = new StringBuilder("Flags[, ");
            if (Self) output.append("Self, ");
            if (Highlight) output.append("Highlight, ");
            if (Redirected) output.append("Redirected, ");
            if (ServerMsg) output.append("ServerMsg, ");
            if (Backlog) output.append("Backlog, ");
            output.deleteCharAt(output.length() - 1);
            output.deleteCharAt(output.length() - 1);
            output.append("]");
            return output.toString();
        }
    }

    public static class MessageComparator implements Comparator<Message>, Serializable {
        @Override
        public int compare(@NonNull Message o1, @NonNull Message o2) {
            return o1.messageId - o2.messageId;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return obj == this;
        }
    }
}
