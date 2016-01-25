package de.kuschku.libquassel.backlogmanagers;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

import com.google.common.collect.Lists;

import java.util.List;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.events.BacklogReceivedEvent;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.util.observables.AutoScroller;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;
import de.kuschku.util.observables.lists.ObservableSortedList;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;

public class SimpleBacklogManager extends BacklogManager {
    SparseArray<ObservableSortedList<Message>> backlogs = new SparseArray<>();
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

        busProvider.sendEvent(new BacklogReceivedEvent(bufferId));
    }

    @Override
    public void displayMessage(int bufferId, Message message) {
        get(bufferId).list.add(message);
    }

    public void bind(int bufferId, @Nullable RecyclerView.Adapter adapter, AutoScroller scroller) {
        get(bufferId).addCallback(new AdapterUICallbackWrapper(adapter, scroller));
    }

    @Override
    public void requestMoreBacklog(int bufferId, int count) {
        ObservableSortedList<Message> backlog = backlogs.get(bufferId);
        int messageId =
                (backlog == null) ? -1 :
                (backlog.last() == null) ? -1 :
                backlog.last().messageId;

        requestBacklog(bufferId, -1, messageId, count, 0);
    }

    public ObservableSortedList<Message> get(int bufferId) {
        if (backlogs.get(bufferId) == null)
            backlogs.put(bufferId, new ObservableComparableSortedList<>(Message.class, true));

        return backlogs.get(bufferId);
    }

    @Override
    public void init(InitDataFunction function, BusProvider provider, Client client) {

    }
}
