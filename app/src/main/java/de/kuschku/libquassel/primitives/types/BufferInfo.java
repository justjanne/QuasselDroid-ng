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
