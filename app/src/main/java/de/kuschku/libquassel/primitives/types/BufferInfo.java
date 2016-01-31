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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.primitives.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class BufferInfo {
    public final int id;
    public final int networkId;
    @NonNull
    public final Type type;
    public final int groupId;
    @Nullable
    public final String name;

    public BufferInfo(int id, int networkId, @NonNull Type type, int groupId, @Nullable String name) {
        this.id = id;
        this.networkId = networkId;
        this.type = type;
        this.groupId = groupId;
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "BufferInfo{" +
                "id=" + id +
                ", networkId=" + networkId +
                ", type=" + type +
                ", groupId=" + groupId +
                ", name='" + name + '\'' +
                '}';
    }

    public enum Type {
        INVALID(0x00),
        STATUS(0x01),
        CHANNEL(0x02),
        QUERY(0x04),
        GROUP(0x08);

        public final short id;

        Type(short id) {
            this.id = id;
        }

        Type(int id) {
            this((short) id);
        }

        @NonNull
        public static Type fromId(short id) {
            switch (id) {
                case 0x01:
                    return STATUS;
                case 0x02:
                    return CHANNEL;
                case 0x04:
                    return QUERY;
                case 0x08:
                    return GROUP;
                default:
                    return INVALID;
            }
        }
    }

    public enum BufferStatus {
        OFFLINE,
        AWAY,
        ONLINE
    }

    public enum ContentStatus {
        NONE,
        ACTIVITY,
        MESSAGES,
        HIGHLIGHTS
    }
}
