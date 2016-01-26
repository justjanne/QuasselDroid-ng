package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.ClientInitAck;
import de.kuschku.libquassel.objects.types.StorageBackend;
import de.kuschku.libquassel.primitives.types.QVariant;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class ClientInitAckSerializer implements ObjectSerializer<ClientInitAck> {
    @NonNull
    private static final ClientInitAckSerializer serializer = new ClientInitAckSerializer();

    private ClientInitAckSerializer() {
    }

    @NonNull
    public static ClientInitAckSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull final ClientInitAck data) {
        final List<Map<String, QVariant>> storageBackends = new ArrayList<>();
        final StorageBackendSerializer storageBackendSerializer = StorageBackendSerializer.get();
        if (data.StorageBackends != null)
        for (StorageBackend backend : data.StorageBackends) {
            storageBackends.add((Map<String, QVariant>) storageBackendSerializer.toVariantMap(backend));
        }

        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        map.data.put("Configured", new QVariant<>(data.Configured));
        map.data.put("LoginEnabled", new QVariant<>(data.LoginEnabled));
        map.data.put("StorageBackends", new QVariant<>(storageBackends));
        map.data.put("CoreFeatures", new QVariant<>(data.CoreFeatures));
        return map;
    }

    @NonNull
    @Override
    public ClientInitAck fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public ClientInitAck fromLegacy(@NonNull Map<String, QVariant> map) {
        final List<StorageBackend> storageBackends = new ArrayList<>();
        if (map.containsKey("StorageBackends")) {
            final StorageBackendSerializer storageBackendSerializer = StorageBackendSerializer.get();
            for (Map<String, QVariant> backend : (List<Map<String, QVariant>>) map.get("StorageBackends").data) {
                storageBackends.add(storageBackendSerializer.fromLegacy(backend));
            }
        }
        final int coreFeatures = map.containsKey("CoreFeatures") ? ((QVariant<Integer>) map.get("CoreFeatures")).data : 0x00;
        return new ClientInitAck(
                ((QVariant<Boolean>) map.get("Configured")).data,
                ((QVariant<Boolean>) map.get("LoginEnabled")).data,
                coreFeatures,
                storageBackends
        );
    }

    @Nullable
    @Override
    public ClientInitAck from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
