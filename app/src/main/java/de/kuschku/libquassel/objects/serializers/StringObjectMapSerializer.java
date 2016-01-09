package de.kuschku.libquassel.objects.serializers;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.primitives.types.QVariant;

public class StringObjectMapSerializer<T> implements ObjectSerializer<Map<String, T>> {
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(Map<String, T> data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<Map<String, QVariant>>(new HashMap<String, QVariant>());
        for (Map.Entry<String, T> entry : data.entrySet()) {
            map.data.put(entry.getKey(), new QVariant<>(entry.getValue()));
        }
        return map;
    }

    @Override
    public Map<String, T> fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public Map<String, T> fromLegacy(Map<String, QVariant> map) {
        final HashMap<String, T> result = new HashMap<>();
        for (Map.Entry<String, QVariant> entry : map.entrySet()) {
            result.put(entry.getKey(), (T) entry.getValue().get());
        }
        return result;
    }

    @Override
    public Map<String, T> from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
