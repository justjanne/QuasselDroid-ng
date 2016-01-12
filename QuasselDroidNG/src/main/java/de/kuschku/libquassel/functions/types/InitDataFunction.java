package de.kuschku.libquassel.functions.types;

import java.util.Map;

import de.kuschku.libquassel.primitives.types.QVariant;

public abstract class InitDataFunction implements SerializedFunction<Map<String, QVariant>> {
    public String className;
    public String objectName;
}
