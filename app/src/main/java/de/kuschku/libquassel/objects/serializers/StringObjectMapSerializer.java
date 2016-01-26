package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class StringObjectMapSerializer<T> implements ObjectSerializer<Map<String, T>> {
    @NonNull
    private static final StringObjectMapSerializer serializer = new StringObjectMapSerializer();

    private StringObjectMapSerializer() {
    }

    @NonNull
    public static <T> StringObjectMapSerializer<T> get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull Map<String, T> data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        assertNotNull(map.data);

        for (Map.Entry<String, T> entry : data.entrySet()) {
            map.data.put(entry.getKey(), new QVariant<>(entry.getValue()));
        }
        return map;
    }

    @NonNull
    @Override
    public Map<String, T> fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public Map<String, T> fromLegacy(@NonNull Map<String, QVariant> map) {
        final HashMap<String, T> result = new HashMap<>();
        for (Map.Entry<String, QVariant> entry : map.entrySet()) {
            result.put(entry.getKey(), (T) entry.getValue().get());
        }
        return result;
    }

    @Override
    public Map<String, T> from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
