package de.kuschku.libquassel.syncables.types;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;

public class BufferViewManager extends SyncableObject {
    @NonNull
    public final Map<Integer, BufferViewConfig> BufferViews = new HashMap<>();

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
        setObjectName(function.objectName);
        client.setBufferViewManager(this);
    }
}
