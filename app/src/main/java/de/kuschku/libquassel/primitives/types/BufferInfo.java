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

package de.kuschku.libquassel.primitives.types;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import de.kuschku.libquassel.localtypes.orm.ConnectedDatabase;

@Table(database = ConnectedDatabase.class)
public class BufferInfo extends BaseModel {
    @PrimaryKey
    public int id;

    @Column
    public int networkId;

    @Column
    public Type type;

    @Column
    public int groupId;

    @Column
    public String name;

    public static BufferInfo create(int id, int networkId, Type type, int groupId, String name) {
        BufferInfo info = new BufferInfo();
        info.id = id;
        info.networkId = networkId;
        info.type = type;
        info.groupId = groupId;
        info.name = name;
        return info;
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
