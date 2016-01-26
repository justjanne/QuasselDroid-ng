package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.StorageBackend;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class StorageBackendSerializer implements ObjectSerializer<StorageBackend> {
    @NonNull
    private static final StorageBackendSerializer serializer = new StorageBackendSerializer();

    private StorageBackendSerializer() {
    }

    @NonNull
    public static StorageBackendSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull final StorageBackend data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        assertNotNull(map.data);

        map.data.put("DisplayName", new QVariant<>(data.DisplayName));
        map.data.put("SetupDefaults", new QVariant<>(data.SetupDefaults));
        map.data.put("Description", new QVariant<>(data.Description));
        map.data.put("SetupKeys", new QVariant<>(data.SetupKeys));
        return map;
    }

    @NonNull
    @Override
    public StorageBackend fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public StorageBackend fromLegacy(@NonNull Map<String, QVariant> map) {
        return new StorageBackend(
                (String) map.get("DisplayName").data,
                (Map<String, QVariant>) map.get("SetupDefaults").data,
                (String) map.get("Description").data,
                (List<String>) map.get("SetupKeys").data
        );
    }

    @Override
    public StorageBackend from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
