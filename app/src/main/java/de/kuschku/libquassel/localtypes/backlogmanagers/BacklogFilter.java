package de.kuschku.libquassel.localtypes.backlogmanagers;

import android.support.annotation.NonNull;

import com.android.internal.util.Predicate;

import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.message.Message;
import de.kuschku.quasseldroid_ng.ui.AppContext;
import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.lists.ObservableSortedList;

public class BacklogFilter implements UICallback {
    @NonNull
    private final Client client;
    @NonNull
    private final ObservableSortedList<Message> unfiltered;
    @NonNull
    private final ObservableSortedList<Message> filtered;

    public BacklogFilter(@NonNull Client client, @NonNull ObservableSortedList<Message> unfiltered, @NonNull ObservableSortedList<Message> filtered) {
        this.client = client;
        this.unfiltered = unfiltered;
        this.filtered = filtered;
    }

    @Override
    public void notifyItemInserted(int position) {
        Message message = unfiltered.get(position);
        if (filterItem(message)) filtered.add(message);
    }

    private boolean filterItem(Message message) {
        return !client.getIgnoreListManager().matches(message);
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
