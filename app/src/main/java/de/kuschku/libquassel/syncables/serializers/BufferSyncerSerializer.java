package de.kuschku.libquassel.syncables.serializers;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.protocols.DatastreamPeer;
import de.kuschku.libquassel.syncables.types.BufferSyncer;

public class BufferSyncerSerializer implements ObjectSerializer<BufferSyncer> {
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(BufferSyncer data) {
        // TODO: Implement this
        return null;
    }

    @Override
    public BufferSyncer fromDatastream(Map<String, QVariant> map) {
        return new BufferSyncer(
                DatastreamPeer.unboxedListToMap((List<Integer>) map.get("LastSeenMsg").data),
                DatastreamPeer.unboxedListToMap((List<Integer>) map.get("MarkerLines").data)
        );
    }

    @Override
    public BufferSyncer fromLegacy(Map<String, QVariant> map) {
        return new BufferSyncer(
                (Map<Integer, Integer>) map.get("LastSeenMsg").data,
                (Map<Integer, Integer>) map.get("MarkerLines").data
        );
    }

    @Override
    public BufferSyncer from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
