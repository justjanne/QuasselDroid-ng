package de.kuschku.libquassel.functions.types;

import java.util.Map;

import de.kuschku.libquassel.primitives.types.QVariant;

public class PackedInitDataFunction extends InitDataFunction implements PackedFunction {
    private final Map<String, QVariant> data;

    public PackedInitDataFunction(String className, String objectName, Map<String, QVariant> data) {
        this.className = className;
        this.objectName = objectName;
        this.data = data;
    }

    @Override
    public Map<String, QVariant> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "PackedInitDataFunction{" +
                "data=" + data +
                '}';
    }
}
