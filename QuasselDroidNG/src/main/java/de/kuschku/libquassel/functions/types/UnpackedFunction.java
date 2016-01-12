package de.kuschku.libquassel.functions.types;

import java.util.Map;

import de.kuschku.libquassel.primitives.types.QVariant;

public interface UnpackedFunction extends SerializedFunction<Map<String, QVariant>> {
    Map<String, QVariant> getData();
}
