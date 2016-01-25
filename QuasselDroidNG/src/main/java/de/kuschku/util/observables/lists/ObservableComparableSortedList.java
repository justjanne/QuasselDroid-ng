package de.kuschku.util.observables.lists;

import de.kuschku.util.observables.ContentComparable;
import de.kuschku.util.observables.callbacks.UICallback;

public class ObservableComparableSortedList<T extends ContentComparable<T>> extends ObservableSortedList<T> implements IObservableList<UICallback, T> {


    public ObservableComparableSortedList(Class<T> cl) {
        super(cl, new SimpleItemComparator<>());
    }

    public ObservableComparableSortedList(Class<T> cl, boolean reverse) {
        super(cl, new SimpleItemComparator<>(), reverse);
    }

    public static class SimpleItemComparator<T extends ContentComparable<T>> implements ItemComparator<T> {
        @Override
        public int compare(T o1, T o2) {
            return o1.compareTo(o2);
        }

        @Override
        public boolean areContentsTheSame(T oldItem, T newItem) {
            return oldItem.equalsContent(newItem);
        }

        @Override
        public boolean areItemsTheSame(T item1, T item2) {
            return item1.equals(item2);
        }
    }
}
