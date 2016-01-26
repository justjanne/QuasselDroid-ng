package de.kuschku.libquassel.functions.types;

import android.support.annotation.NonNull;

import java.util.Map;

import de.kuschku.libquassel.primitives.types.QVariant;

public class UnpackedInitDataFunction extends InitDataFunction implements UnpackedFunction {
    @NonNull
    private final Map<String, QVariant> data;

    public UnpackedInitDataFunction(@NonNull String className, @NonNull String objectName, @NonNull Map<String, QVariant> data) {
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
        return "UnpackedInitDataFunction{" +
                "data=" + data +
                '}';
    }
}
