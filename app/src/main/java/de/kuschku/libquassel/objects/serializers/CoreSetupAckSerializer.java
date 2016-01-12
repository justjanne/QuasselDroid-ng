package de.kuschku.libquassel.objects.serializers;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.CoreSetupAck;
import de.kuschku.libquassel.primitives.types.QVariant;

public class CoreSetupAckSerializer implements ObjectSerializer<CoreSetupAck> {
    private static final CoreSetupAckSerializer serializer = new CoreSetupAckSerializer();

    private CoreSetupAckSerializer() {
    }

    public static CoreSetupAckSerializer get() {
        return serializer;
    }

    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(final CoreSetupAck data) {
        return new QVariant<>(new HashMap<>());
    }

    @Override
    public CoreSetupAck fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public CoreSetupAck fromLegacy(Map<String, QVariant> map) {
        return new CoreSetupAck();
    }

    @Override
    public CoreSetupAck from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
