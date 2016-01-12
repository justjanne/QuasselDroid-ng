package de.kuschku.libquassel.objects.serializers;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.SessionInit;
import de.kuschku.libquassel.primitives.types.QVariant;

public class SessionInitSerializer implements ObjectSerializer<SessionInit> {
    private static final SessionInitSerializer serializer = new SessionInitSerializer();

    private SessionInitSerializer() {
    }

    public static SessionInitSerializer get() {
        return serializer;
    }

    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(final SessionInit data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        map.data.put("SessionState", new SessionStateSerializer().toVariantMap(data.SessionState));
        return map;
    }

    @Override
    public SessionInit fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public SessionInit fromLegacy(Map<String, QVariant> map) {
        return new SessionInit(
                new SessionStateSerializer().fromLegacy((Map<String, QVariant>) map.get("SessionState").data)
        );
    }

    @Override
    public SessionInit from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
