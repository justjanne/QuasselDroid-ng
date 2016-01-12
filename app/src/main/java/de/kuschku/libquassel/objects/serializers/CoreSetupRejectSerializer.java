package de.kuschku.libquassel.objects.serializers;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.CoreSetupReject;
import de.kuschku.libquassel.primitives.types.QVariant;

public class CoreSetupRejectSerializer implements ObjectSerializer<CoreSetupReject> {
    private static final CoreSetupRejectSerializer serializer = new CoreSetupRejectSerializer();

    private CoreSetupRejectSerializer() {
    }

    public static CoreSetupRejectSerializer get() {
        return serializer;
    }

    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(final CoreSetupReject data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        map.data.put("Error", new QVariant<>(data.Error));
        return map;
    }

    @Override
    public CoreSetupReject fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public CoreSetupReject fromLegacy(Map<String, QVariant> map) {
        return new CoreSetupReject(
                (String) map.get("Error").data
        );
    }

    @Override
    public CoreSetupReject from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
