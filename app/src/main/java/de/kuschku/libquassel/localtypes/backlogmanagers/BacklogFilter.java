package de.kuschku.libquassel.localtypes.backlogmanagers;

import android.support.annotation.NonNull;
import android.util.Log;

import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.Set;

import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.lists.ObservableSortedList;

public class BacklogFilter implements UICallback {
    @NonNull
    private final Client client;
    private final int bufferId;
    @NonNull
    private final ObservableSortedList<Message> unfiltered;
    @NonNull
    private final ObservableSortedList<Message> filtered;

    private final Set<Message.Type> filteredTypes = new HashSet<>();

    private DateTime earliestMessage;

    public BacklogFilter(@NonNull Client client, int bufferId, @NonNull ObservableSortedList<Message> unfiltered, @NonNull ObservableSortedList<Message> filtered) {
        this.client = client;
        this.bufferId = bufferId;
        this.unfiltered = unfiltered;
        this.filtered = filtered;
    }

    @Override
    public void notifyItemInserted(int position) {
        Message message = unfiltered.get(position);
        if (!filterItem(message)) filtered.add(message);
        if (message.time.isBefore(earliestMessage)) earliestMessage = message.time;
        updateDayChangeMessages();
    }

    private void updateDayChangeMessages() {
        DateTime now = DateTime.now().withMillisOfDay(0);
        int id = -1;
        while (now.isAfter(earliestMessage)) {
            filtered.add(new Message(
                    id,
                    now,
                    Message.Type.DayChange,
                    new Message.Flags(false, false, false, false, false),
                    new BufferInfo(
                            bufferId,
                            -1,
                            BufferInfo.Type.INVALID,
                            -1,
                            null
                    ),
                    "",
                    ""
            ));
            now = now.minusDays(1);
        }
    }

    private boolean filterItem(Message message) {
        return client.getIgnoreListManager().matches(message) || filteredTypes.contains(message.type);
    }

    public void addFilter(Message.Type type) {
        filteredTypes.add(type);
        updateRemove();
    }

    public void removeFilter(Message.Type type) {
        filteredTypes.remove(type);
        updateAdd();
    }

    private void updateRemove() {
        for (Message message : unfiltered) {
            if (filterItem(message)) {
                String simpleName = getClass().getSimpleName();
                Log.e(simpleName, "Filtered: "+message);
                filtered.remove(message);
            }
        }
    }

    private void updateAdd() {
        for (Message message : unfiltered) {
            if (!filterItem(message)) {
                String simpleName = getClass().getSimpleName();
                Log.e(simpleName, "Unfiltered: "+message);
                filtered.add(message);
            }
        }
    }

    @Override
    public void notifyItemChanged(int position) {
        filtered.notifyItemChanged(position);
    }

    @Override
    public void notifyItemRemoved(int position) {
        filtered.remove(position);
    }

    @Override
    public void notifyItemMoved(int from, int to) {
        // Can’t occur: Sorted List
    }

    @Override
    public void notifyItemRangeInserted(int position, int count) {
        for (int i = position; i < position + count; i++) {
            notifyItemInserted(i);
        }
    }

    @Override
    public void notifyItemRangeChanged(int position, int count) {
        for (int i = position; i < position + count; i++) {
            notifyItemChanged(i);
        }
    }

    @Override
    public void notifyItemRangeRemoved(int position, int count) {
        for (int i = position; i < position + count; i++) {
            notifyItemRemoved(i);
        }
    }
}
