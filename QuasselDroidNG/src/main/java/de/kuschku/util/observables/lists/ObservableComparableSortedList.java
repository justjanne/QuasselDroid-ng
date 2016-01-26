package de.kuschku.util.observables.lists;

import android.support.annotation.NonNull;

import de.kuschku.util.observables.ContentComparable;
import de.kuschku.util.observables.callbacks.UICallback;

public class ObservableComparableSortedList<T extends ContentComparable<T>> extends ObservableSortedList<T> implements IObservableList<UICallback, T> {


    public ObservableComparableSortedList(@NonNull Class<T> cl) {
        super(cl, new SimpleItemComparator<>());
    }

    public ObservableComparableSortedList(@NonNull Class<T> cl, boolean reverse) {
        super(cl, new SimpleItemComparator<>(), reverse);
    }

    public static class SimpleItemComparator<T extends ContentComparable<T>> implements ItemComparator<T> {
        @Override
        public int compare(@NonNull T o1, @NonNull T o2) {
            return o1.compareTo(o2);
        }

        @Override
        public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
            return oldItem.equalsContent(newItem);
        }

        @Override
        public boolean areItemsTheSame(@NonNull T item1, @NonNull T item2) {
            return item1.equals(item2);
        }
    }
}
