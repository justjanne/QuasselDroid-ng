package de.kuschku.libquassel.functions.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public class SyncFunction<T> {
    @NonNull
    public final String className;
    @Nullable
    public final String objectName;
    @NonNull
    public final String methodName;
    @NonNull
    public final List<T> params;

    public SyncFunction(@NonNull String className, @Nullable String objectName, @NonNull String methodName, @NonNull List<T> params) {
        this.className = className;
        this.objectName = objectName;
        this.methodName = methodName;
        this.params = params;
    }

    @NonNull
    @Override
    public String toString() {
        return "SyncFunction{" +
                "className='" + className + '\'' +
                ", objectName='" + objectName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", params=" + params +
                '}';
    }
}
