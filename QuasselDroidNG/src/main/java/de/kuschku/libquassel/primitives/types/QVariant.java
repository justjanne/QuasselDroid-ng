package de.kuschku.libquassel.primitives.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.QMetaTypeRegistry;

public class QVariant<T> {
    @Nullable
    public final T data;
    @NonNull
    public final QMetaType<T> type;

    public QVariant(@NonNull T data) {
        this.type = QMetaTypeRegistry.getTypeByObject(data);
        this.data = data;
    }

    public QVariant(@NonNull QMetaType<T> type, @Nullable T data) {
        this.type = type;
        this.data = data;
    }

    public QVariant(@NonNull QMetaType.Type type, @Nullable T data) {
        this.type = QMetaTypeRegistry.getType(type);
        this.data = data;
    }

    public QVariant(@NonNull String typeName, @Nullable T data) {
        this.type = QMetaTypeRegistry.getType(typeName);
        this.data = data;
    }

    @Nullable
    public static <T> T orNull(@Nullable QVariant<T> data) {
        if (data == null) return null;
        else return data.data;
    }

    @NonNull
    public String toString() {
        return "QVariant(data=" + String.valueOf(this.data) + ", type=" + this.type.name + ")";
    }

    @NonNull
    public T or(@NonNull T ifNull) {
        return data == null ? ifNull : data;
    }

    @Nullable
    public T get() {
        return data;
    }
}
