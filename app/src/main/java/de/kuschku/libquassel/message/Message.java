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

package de.kuschku.libquassel.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.Comparator;

import de.kuschku.libquassel.localtypes.orm.ConnectedDatabase;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.util.backports.Objects;
import de.kuschku.util.observables.ContentComparable;

@Table(database = ConnectedDatabase.class)
public class Message extends BaseModel implements ContentComparable<Message> {
    @PrimaryKey
    public int id;

    @Column
    public DateTime time;

    @Column
    public Type type;

    @Column
    public Flags flags;

    @ForeignKey
    public BufferInfo bufferInfo;

    @Column
    public String sender;

    @Column
    public String content;

    public LocalDate date;

    public static Message create(int id, DateTime time, Type type, Flags flags, BufferInfo bufferInfo, String sender, String content) {
        Message message = new Message();
        message.id = id;
        message.time = time;
        message.type = type;
        message.flags = flags;
        message.bufferInfo = bufferInfo;
        message.sender = sender;
        message.content = content;
        return message;
    }

    public LocalDate getLocalDate() {
        if (date == null)
            date = time.toLocalDate();
        return date;
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", time=" + time +
                ", type=" + type +
                ", flags=" + flags +
                ", bufferInfo=" + bufferInfo +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public boolean areContentsTheSame(@Nullable Message message) {
        return message != null && this.id == message.id && Objects.equals(this.sender, message.sender) && this.type == message.type && Objects.equals(this.time, message.time) && Objects.equals(this.content, message.content) && this.flags == message.flags;
    }

    @Override
    public boolean areItemsTheSame(@NonNull Message other) {
        if (this.type == Type.DayChange && other.type == Type.DayChange)
            return this.bufferInfo.id == other.bufferInfo.id && this.time.equals(other.time);
        else if (this.type == Type.DayChange)
            return false;
        else if (other.type == Type.DayChange)
            return false;
        else
            return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public int compareTo(@NonNull Message another) {
        if (this.type == Type.DayChange || another.type == Type.DayChange) {
            return this.time.compareTo(another.time);
        } else if (this.id == another.id) {
            if (this.type == Type.Markerline)
                return 1;
            else if (another.type == Type.Markerline)
                return -1;
            else
                return 0;
        } else {
            return this.id - another.id;
        }
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
        Invite(0x20000),
        Markerline(0x40000);

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
            output.append(flags);
            return output.toString();
        }

    }

    public static class MessageComparator implements Comparator<Message>, Serializable {
        @Override
        public int compare(@NonNull Message o1, @NonNull Message o2) {
            return o1.id - o2.id;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return obj == this;
        }
    }

}
