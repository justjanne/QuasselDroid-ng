package de.kuschku.libquassel.backlogmanagers;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
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
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;
import de.kuschku.util.observables.lists.ObservableSortedList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class SimpleBacklogManager extends BacklogManager {
    @NonNull
    private final SparseArray<ObservableSortedList<Message>> backlogs = new SparseArray<>();
    @NonNull
    private final BusProvider busProvider;

    public SimpleBacklogManager(@NonNull BusProvider busProvider) {
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

    public void receiveBacklog(@IntRange(from=0) int bufferId, int from, int to, int count, int extra, @NonNull List<Message> messages) {
        get(bufferId).list.addAll(messages);

        busProvider.sendEvent(new BacklogReceivedEvent(bufferId));
    }

    @Override
    public void displayMessage(@IntRange(from=0) int bufferId, @NonNull Message message) {
        ObservableSortedList<Message> messages = get(bufferId);
        assertNotNull(messages);

        messages.list.add(message);
    }

    public void bind(@IntRange(from=0) int bufferId, @NonNull RecyclerView.Adapter adapter, @Nullable AutoScroller scroller) {
        ObservableSortedList<Message> messages = get(bufferId);
        assertNotNull(messages);

        messages.addCallback(new AdapterUICallbackWrapper(adapter, scroller));
    }

    @Override
    public void requestMoreBacklog(@IntRange(from=0) int bufferId, int count) {
        ObservableSortedList<Message> backlog = backlogs.get(bufferId);
        int messageId =
                (backlog == null) ? -1 :
                (backlog.last() == null) ? -1 :
                backlog.last().messageId;

        requestBacklog(bufferId, -1, messageId, count, 0);
    }

    public ObservableSortedList<Message> get(@IntRange(from=0) int bufferId) {
        if (backlogs.get(bufferId) == null)
            backlogs.put(bufferId, new ObservableComparableSortedList<>(Message.class, true));

        ObservableSortedList<Message> messages = backlogs.get(bufferId);
        assertNotNull(messages);

        return messages;
    }

    @Override
    public void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {

    }
}
