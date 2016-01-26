package de.kuschku.libquassel.functions.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class InitRequestFunction {
    @NonNull
    public final String className;
    @Nullable
    public final String objectName;

    public InitRequestFunction(@NonNull String className, @Nullable String objectName) {
        this.className = className;
        this.objectName = objectName;
    }

    @NonNull
    @Override
    public String toString() {
        return "InitRequestFunction{" +
                "className='" + className + '\'' +
                ", objectName='" + objectName + '\'' +
                '}';
    }
}
