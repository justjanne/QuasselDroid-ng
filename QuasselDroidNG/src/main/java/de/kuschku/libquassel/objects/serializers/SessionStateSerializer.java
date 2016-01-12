package de.kuschku.libquassel.objects.serializers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.SessionState;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.primitives.types.QVariant;

public class SessionStateSerializer implements ObjectSerializer<SessionState> {
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(final SessionState data) {
        return null;
    }

    @Override
    public SessionState fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public SessionState fromLegacy(Map<String, QVariant> map) {
        return new SessionState(
                (List<Map<String, QVariant>>) map.get("Identities").or(new ArrayList<>()),
                (List<BufferInfo>) map.get("BufferInfos").or(new ArrayList<>()),
                (List<Integer>) map.get("NetworkIds").or(new ArrayList<>())
        );
    }

    @Override
    public SessionState from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
