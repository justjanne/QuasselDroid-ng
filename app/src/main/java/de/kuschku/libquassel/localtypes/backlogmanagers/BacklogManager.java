package de.kuschku.libquassel.localtypes.backlogmanagers;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.List;

import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.util.observables.lists.ObservableSortedList;

public abstract class BacklogManager<T extends BacklogManager<T>> extends SyncableObject<T> {
    public abstract void requestBacklog(int bufferId, int from, int to, int count, int extra);

    public abstract void receiveBacklog(int bufferId, int from, int to, int count, int extra, @NonNull List<Message> messages);

    public abstract void displayMessage(int bufferId, @NonNull Message message);

    public abstract ObservableSortedList<Message> get(@IntRange(from = -1) int bufferId);

    public abstract ObservableSortedList<Message> getFiltered(@IntRange(from = -1) int bufferId);

    public abstract BacklogFilter getFilter(@IntRange(from = -1) int bufferId);

    public abstract void requestMoreBacklog(int bufferId, int count);

    public abstract void setClient(Client client);
}
