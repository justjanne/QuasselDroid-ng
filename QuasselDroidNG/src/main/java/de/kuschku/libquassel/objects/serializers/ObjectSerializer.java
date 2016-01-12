package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.Nullable;

import java.util.Map;

import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.primitives.types.QVariant;

public interface ObjectSerializer<T> {
    QVariant<Map<String, QVariant>> toVariantMap(T data);

    T fromDatastream(Map<String, QVariant> map);

    T fromLegacy(Map<String, QVariant> map);

    @Nullable
    T from(SerializedFunction function);
}
