package de.kuschku.libquassel.functions.types;

import android.support.annotation.NonNull;

import java.util.Map;

import de.kuschku.libquassel.primitives.types.QVariant;

public interface PackedFunction extends SerializedFunction<Map<String, QVariant>> {
    @NonNull
    Map<String, QVariant> getData();
}
