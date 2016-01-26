package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.CoreSetupAck;
import de.kuschku.libquassel.primitives.types.QVariant;

public class CoreSetupAckSerializer implements ObjectSerializer<CoreSetupAck> {
    @NonNull
    private static final CoreSetupAckSerializer serializer = new CoreSetupAckSerializer();

    private CoreSetupAckSerializer() {
    }

    @NonNull
    public static CoreSetupAckSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull final CoreSetupAck data) {
        return new QVariant<>(new HashMap<>());
    }

    @NonNull
    @Override
    public CoreSetupAck fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public CoreSetupAck fromLegacy(@NonNull Map<String, QVariant> map) {
        return new CoreSetupAck();
    }

    @Override
    public CoreSetupAck from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
