package de.kuschku.libquassel.localtypes;

import android.util.SparseArray;

import de.kuschku.libquassel.message.Message;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;

public class NotificationManager {
    private SparseArray<ObservableComparableSortedList<Message>> notifications = new SparseArray<>();

    public ObservableComparableSortedList<Message> getNotifications(int bufferid) {
        return notifications.get(bufferid);
    }

    public void init(int id) {
        notifications.put(id, new ObservableComparableSortedList<>(Message.class));
    }
}
