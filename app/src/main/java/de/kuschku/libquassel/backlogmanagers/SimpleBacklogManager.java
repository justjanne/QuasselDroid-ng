package de.kuschku.libquassel.backlogmanagers;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

import com.google.common.collect.Lists;

import java.util.List;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.primitives.types.Message;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.util.ObservableList;

public class SimpleBacklogManager extends BacklogManager {
    SparseArray<ObservableList<Message>> backlogs = new SparseArray<>();
    private BusProvider busProvider;

    public SimpleBacklogManager(BusProvider busProvider) {
        this.busProvider = busProvider;
    }

    public void requestBacklog(int bufferId, int from, int to, int count, int extra) {
        busProvider.dispatch(new SyncFunction("BacklogManager", "", "requestBacklog", Lists.newArrayList(
                new QVariant<>("BufferId", bufferId),
                new QVariant<>("MsgId", from),
                new QVariant<>("MsgId", to),
                new QVariant<>(count),
                new QVariant<>(extra)
        )));
    }

    public void receiveBacklog(int bufferId, int from, int to, int count, int extra, List<Message> messages) {
        get(bufferId).list.addAll(messages);
    }

    @Override
    public void displayMessage(int bufferId, Message message) {
        get(bufferId).list.add(message);
    }

    public void bind(int bufferId, @Nullable RecyclerView.Adapter adapter) {
        if (adapter == null)
            get(bufferId).setCallback(null);
        else
            get(bufferId).setCallback(new ObservableList.RecyclerViewAdapterCallback(adapter));
    }

    public ObservableList<Message> get(int bufferId) {
        if (backlogs.get(bufferId) == null)
            backlogs.put(bufferId, new ObservableList<>(Message.class));

        return backlogs.get(bufferId);
    }

    @Override
    public void init(InitDataFunction function, BusProvider provider, Client client) {

    }
}
