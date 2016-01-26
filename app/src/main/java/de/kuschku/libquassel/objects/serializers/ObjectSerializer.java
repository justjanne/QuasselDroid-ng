package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.primitives.types.QVariant;

public interface ObjectSerializer<T> {
    @Nullable
    QVariant<Map<String, QVariant>> toVariantMap(@NonNull T data);

    @NonNull
    T fromDatastream(@NonNull Map<String, QVariant> map);

    @NonNull
    T fromLegacy(@NonNull Map<String, QVariant> map);

    @Nullable
    T from(@NonNull SerializedFunction function);
}
