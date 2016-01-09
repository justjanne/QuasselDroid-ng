package de.kuschku.libquassel.functions.types;

import java.util.List;

public class SyncFunction<T> {
    public final String className;
    public final String objectName;
    public final String methodName;
    public final List<T> params;

    public SyncFunction(String className, String objectName, String methodName, List<T> params) {
        this.className = className;
        this.objectName = objectName;
        this.methodName = methodName;
        this.params = params;
    }

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
