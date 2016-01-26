package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.ClientInitReject;
import de.kuschku.libquassel.primitives.types.QVariant;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class ClientInitRejectSerializer implements ObjectSerializer<ClientInitReject> {
    @NonNull
    private static final ClientInitRejectSerializer serializer = new ClientInitRejectSerializer();

    private ClientInitRejectSerializer() {
    }

    @NonNull
    public static ClientInitRejectSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull final ClientInitReject data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        map.data.put("Error", new QVariant<>(data.Error));
        return map;
    }

    @NonNull
    @Override
    public ClientInitReject fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public ClientInitReject fromLegacy(@NonNull Map<String, QVariant> map) {
        return new ClientInitReject(
                (String) map.get("Error").data
        );
    }

    @Override
    public ClientInitReject from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
