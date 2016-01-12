package de.kuschku.libquassel.syncables.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.events.BuffersChangedEvent;
import de.kuschku.libquassel.functions.types.InitDataFunction;

public class BufferViewManager extends SyncableObject {
    public final Map<Integer, BufferViewConfig> BufferViews = new HashMap<>();

    public BufferViewManager(List<Integer> BufferViewIds) {
        for (int i : BufferViewIds) {
            BufferViews.put(i, null);
        }
    }

    @Override
    public String toString() {
        return "BufferViewManager{" +
                "BufferViews=" + BufferViews +
                '}';
    }

    @Override
    public void init(InitDataFunction function, BusProvider provider, Client client) {
        setObjectName(function.objectName);
        client.setBufferViewManager(this);
        provider.sendEvent(new BuffersChangedEvent());
    }
}
