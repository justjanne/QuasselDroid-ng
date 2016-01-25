package de.kuschku.libquassel.syncables.types;

import android.util.SparseArray;
import android.util.SparseIntArray;

import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.util.observables.ContentComparable;
import de.kuschku.util.observables.lists.ObservableSortedList;

public class BufferSyncer extends SyncableObject {
    private final SparseIntArray LastSeenMsg = new SparseIntArray();
    private final SparseIntArray MarkerLines = new SparseIntArray();

    Client client;

    public BufferSyncer(Map<Integer, Integer> LastSeenMsg, Map<Integer, Integer> MarkerLines) {
        for (Map.Entry<Integer, Integer> entry : LastSeenMsg.entrySet()) {
            this.LastSeenMsg.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Integer> entry : MarkerLines.entrySet()) {
            this.MarkerLines.put(entry.getKey(), entry.getValue());
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public void init(InitDataFunction function, BusProvider provider, Client client) {
        client.setBufferSyncer(this);
        setClient(client);
    }

    public void markBufferAsRead(int bufferId) {
        int messageId = client.getBacklogManager().get(bufferId).last().messageId;

        setLastSeenMsg(bufferId, messageId);
        setMarkerLine(bufferId, messageId);
    }

    public void setLastSeenMsg(int bufferId, int msgId) {
        LastSeenMsg.put(bufferId, msgId);
    }

    public void setMarkerLine(int bufferId, int msgId) {
        MarkerLines.put(bufferId, msgId);
    }

    public int getLastSeenMsg(int bufferId) {
        return LastSeenMsg.get(bufferId, -1);
    }

    public int getMarkerLine(int bufferId) {
        return MarkerLines.get(bufferId, -1);
    }
}
