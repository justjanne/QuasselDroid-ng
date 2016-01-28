package de.kuschku.libquassel.syncables.types;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.BufferSyncerSerializer;
import de.kuschku.libquassel.syncables.serializers.BufferViewManagerSerializer;

public class BufferViewManager extends SyncableObject<BufferViewManager> {
    @NonNull
    public Map<Integer, BufferViewConfig> BufferViews = new HashMap<>();
    private Client client;

    public BufferViewManager(@NonNull List<Integer> BufferViewIds) {
        for (int i : BufferViewIds) {
            BufferViews.put(i, null);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "BufferViewManager{" +
                "BufferViews=" + BufferViews +
                '}';
    }

    @Override
    public void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {
        this.client = client;
        setObjectName(function.objectName);
        client.setBufferViewManager(this);
    }

    @Override
    public void update(BufferViewManager from) {
        this.BufferViews = from.BufferViews;
        for (int id : BufferViews.keySet()) {
            client.sendInitRequest("BufferViewConfig", String.valueOf(id));
        }
    }

    @Override
    public void update(Map<String, QVariant> from) {
        update(BufferViewManagerSerializer.get().fromDatastream(from));
    }
}
