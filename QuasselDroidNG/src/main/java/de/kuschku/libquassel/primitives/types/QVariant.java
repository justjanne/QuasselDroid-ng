package de.kuschku.libquassel.primitives.types;

import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.QMetaTypeRegistry;

public class QVariant<T> {
    public final T data;
    public final QMetaType<T> type;

    public QVariant(T data) {
        this.type = QMetaTypeRegistry.getTypeByObject(data);
        this.data = data;
    }

    public QVariant(QMetaType<T> type, T data) {
        this.type = type;
        this.data = data;
    }

    public QVariant(QMetaType.Type type, T data) {
        this.type = QMetaTypeRegistry.getType(type);
        this.data = data;
    }

    public QVariant(String typeName, T data) {
        this.type = QMetaTypeRegistry.getType(typeName);
        this.data = data;
    }

    public static <T> T orNull(QVariant<T> data) {
        if (data == null) return null;
        else return data.data;
    }

    public String toString() {
        return "QVariant(data=" + String.valueOf(this.data) + ", type=" + (this.type == null ? "null" : this.type.name) + ")";
    }

    public T or(T ifNull) {
        return data == null ? ifNull : data;
    }

    public T get() {
        return data;
    }
}
