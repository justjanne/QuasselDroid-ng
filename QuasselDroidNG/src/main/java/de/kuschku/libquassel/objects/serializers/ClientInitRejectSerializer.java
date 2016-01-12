package de.kuschku.libquassel.objects.serializers;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.ClientInitReject;
import de.kuschku.libquassel.primitives.types.QVariant;

public class ClientInitRejectSerializer implements ObjectSerializer<ClientInitReject> {
    private static final ClientInitRejectSerializer serializer = new ClientInitRejectSerializer();

    private ClientInitRejectSerializer() {
    }

    public static ClientInitRejectSerializer get() {
        return serializer;
    }

    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(final ClientInitReject data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        map.data.put("Error", new QVariant<>(data.Error));
        return map;
    }

    @Override
    public ClientInitReject fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public ClientInitReject fromLegacy(Map<String, QVariant> map) {
        return new ClientInitReject(
                (String) map.get("Error").data
        );
    }

    @Override
    public ClientInitReject from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
