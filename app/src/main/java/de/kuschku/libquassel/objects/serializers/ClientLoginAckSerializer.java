package de.kuschku.libquassel.objects.serializers;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.ClientLoginAck;
import de.kuschku.libquassel.primitives.types.QVariant;

public class ClientLoginAckSerializer implements ObjectSerializer<ClientLoginAck> {
    private static final ClientLoginAckSerializer serializer = new ClientLoginAckSerializer();

    private ClientLoginAckSerializer() {
    }

    public static ClientLoginAckSerializer get() {
        return serializer;
    }

    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(final ClientLoginAck data) {
        return new QVariant<>(new HashMap<>());
    }

    @Override
    public ClientLoginAck fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public ClientLoginAck fromLegacy(final Map<String, QVariant> map) {
        return new ClientLoginAck();
    }

    @Override
    public ClientLoginAck from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
