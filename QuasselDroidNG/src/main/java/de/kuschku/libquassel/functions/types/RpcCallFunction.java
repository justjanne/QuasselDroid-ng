package de.kuschku.libquassel.functions.types;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public class RpcCallFunction {
    @NonNull
    public final String functionName;
    @NonNull
    public final List<Object> params;

    public RpcCallFunction(@NonNull String functionName, @NonNull List<Object> params) {
        this.functionName = functionName;
        this.params = params;
    }

    public RpcCallFunction(@NonNull String functionName, @NonNull Object... params) {
        this.functionName = functionName;
        this.params = Arrays.asList(params);
    }

    @NonNull
    @Override
    public String toString() {
        return "RpcCallFunction{" +
                "functionName='" + functionName + '\'' +
                ", params=" + params +
                '}';
    }
}
