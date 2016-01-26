package de.kuschku.libquassel.functions.types;

import android.support.annotation.NonNull;

import java.util.Map;

import de.kuschku.libquassel.primitives.types.QVariant;

public class PackedInitDataFunction extends InitDataFunction implements PackedFunction {
    @NonNull
    private final Map<String, QVariant> data;

    public PackedInitDataFunction(@NonNull String className, @NonNull String objectName, @NonNull Map<String, QVariant> data) {
        this.className = className;
        this.objectName = objectName;
        this.data = data;
    }

    @NonNull
    @Override
    public Map<String, QVariant> getData() {
        return data;
    }

    @NonNull
    @Override
    public String toString() {
        return "PackedInitDataFunction{" +
                "data=" + data +
                '}';
    }
}
