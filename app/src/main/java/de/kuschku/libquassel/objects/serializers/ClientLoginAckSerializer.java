package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.ClientLoginAck;
import de.kuschku.libquassel.primitives.types.QVariant;

public class ClientLoginAckSerializer implements ObjectSerializer<ClientLoginAck> {
    @NonNull
    private static final ClientLoginAckSerializer serializer = new ClientLoginAckSerializer();

    private ClientLoginAckSerializer() {
    }

    @NonNull
    public static ClientLoginAckSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull final ClientLoginAck data) {
        return new QVariant<>(new HashMap<>());
    }

    @NonNull
    @Override
    public ClientLoginAck fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public ClientLoginAck fromLegacy(@NonNull final Map<String, QVariant> map) {
        return new ClientLoginAck();
    }

    @Override
    public ClientLoginAck from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
