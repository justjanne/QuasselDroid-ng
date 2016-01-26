package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.SessionInit;
import de.kuschku.libquassel.primitives.types.QVariant;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class SessionInitSerializer implements ObjectSerializer<SessionInit> {
    @NonNull
    private static final SessionInitSerializer serializer = new SessionInitSerializer();

    private SessionInitSerializer() {
    }

    @NonNull
    public static SessionInitSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull final SessionInit data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        map.data.put("SessionState", SessionStateSerializer.get().toVariantMap(data.SessionState));
        return map;
    }

    @NonNull
    @Override
    public SessionInit fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public SessionInit fromLegacy(@NonNull Map<String, QVariant> map) {
        return new SessionInit(
                SessionStateSerializer.get().fromLegacy((Map<String, QVariant>) map.get("SessionState").data)
        );
    }

    @Override
    public SessionInit from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
