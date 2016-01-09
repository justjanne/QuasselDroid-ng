package de.kuschku.libquassel.functions.types;

import java.util.Arrays;
import java.util.List;

public class RpcCallFunction {
    public final String functionName;
    public final List<Object> params;

    public RpcCallFunction(String functionName, List<Object> params) {
        this.functionName = functionName;
        this.params = params;
    }

    public RpcCallFunction(String functionName, Object... params) {
        this.functionName = functionName;
        this.params = Arrays.asList(params);
    }

    @Override
    public String toString() {
        return "RpcCallFunction{" +
                "functionName='" + functionName + '\'' +
                ", params=" + params +
                '}';
    }
}
