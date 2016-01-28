package de.kuschku.libquassel.syncables.serializers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.protocols.DatastreamPeer;
import de.kuschku.libquassel.syncables.types.BufferSyncer;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class BufferSyncerSerializer implements ObjectSerializer<BufferSyncer> {
    @NonNull
    private static final BufferSyncerSerializer serializer = new BufferSyncerSerializer();

    private BufferSyncerSerializer() {
    }

    @NonNull
    public static BufferSyncerSerializer get() {
        return serializer;
    }

    @Nullable
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull BufferSyncer data) {
        // FIXME: IMPLEMENT
        throw new IllegalArgumentException();
    }

    @NonNull
    @Override
    public BufferSyncer fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public BufferSyncer fromLegacy(@NonNull Map<String, QVariant> map) {
        return new BufferSyncer(
                DatastreamPeer.unboxedListToMap((List<Integer>) map.get("LastSeenMsg").data),
                DatastreamPeer.unboxedListToMap((List<Integer>) map.get("LastSeenMsg").data)
        );
    }

    @Override
    public BufferSyncer from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
