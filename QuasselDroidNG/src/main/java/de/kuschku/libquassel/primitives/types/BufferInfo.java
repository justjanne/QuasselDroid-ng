package de.kuschku.libquassel.primitives.types;

public class BufferInfo {
    public final int id;
    public final int networkId;
    public final Type type;
    public final int groupId;
    public final String name;

    public BufferInfo(int id, int networkId, Type type, int groupId, String name) {
        this.id = id;
        this.networkId = networkId;
        this.type = type;
        this.groupId = groupId;
        this.name = name;
    }

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
}
