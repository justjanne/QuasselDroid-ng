package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.SessionState;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.Identity;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class SessionStateSerializer implements ObjectSerializer<SessionState> {
    @NonNull
    private static final SessionStateSerializer serializer = new SessionStateSerializer();

    private SessionStateSerializer() {
    }

    @NonNull
    public static SessionStateSerializer get() {
        return serializer;
    }

    @Nullable
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull final SessionState data) {
        return null;
    }

    @NonNull
    @Override
    public SessionState fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public SessionState fromLegacy(@NonNull Map<String, QVariant> map) {
        return new SessionState(
                (List<Identity>) map.get("Identities").or(new ArrayList<>()),
                (List<BufferInfo>) map.get("BufferInfos").or(new ArrayList<>()),
                (List<Integer>) map.get("NetworkIds").or(new ArrayList<>())
        );
    }

    @Override
    public SessionState from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
