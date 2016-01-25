package de.kuschku.util.observables.lists;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.kuschku.util.backports.Stream;
import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.callbacks.wrappers.MultiUICallbackWrapper;

public class ObservableSortedList<T> implements IObservableList<UICallback, T> {
    public final SortedList<T> list;
    Callback internal = new Callback();
    boolean reverse;

    MultiUICallbackWrapper callback = MultiUICallbackWrapper.of();
    ItemComparator<T> comparator;

    @Override
    public void addCallback(UICallback callback) {
        this.callback.addCallback(callback);
    }

    @Override
    public void removeCallback(UICallback callback) {
        this.callback.removeCallback(callback);
    }

    public ObservableSortedList(Class<T> cl, ItemComparator<T> comparator) {
        this(cl, comparator, false);
    }

    public ObservableSortedList(Class<T> cl, ItemComparator<T> comparator, boolean reverse) {
        this.list = new SortedList<>(cl, internal);
        this.comparator = comparator;
        this.reverse = reverse;
    }

    public T last() {
        if (list.size() == 0) return null;

        return list.get(list.size() - 1);
    }

    @Override
    public void add(int location, T object) {
        list.add(object);
    }

    @Override
    public boolean add(T object) {
        list.add(object);
        return true;
    }

    @Override
    public boolean addAll(int location, @NonNull Collection<? extends T> collection) {
        list.addAll((Collection<T>) collection);
        return true;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        list.addAll((Collection<T>) collection);
        return false;
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object object) {
        return indexOf((T) object) != SortedList.INVALID_POSITION;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return new Stream<>(collection).allMatch(this::contains);
    }

    @Override
    public T get(int location) {
        return list.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return list.indexOf((T) object);
    }

    @Override
    public boolean isEmpty() {
        return list.size() == 0;
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new CallbackedSortedListIterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return 0;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new CallbackedSortedListIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int location) {
        return new CallbackedSortedListIterator(location);
    }

    @Override
    public T remove(int location) {
        return null;
    }

    @Override
    public boolean remove(Object object) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    @Override
    public T set(int location, T object) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @NonNull
    @Override
    public List<T> subList(int start, int end) {
        return null;
    }

    @NonNull
    @Override
    public Object[] toArray() {
        throw new MaterialDialog.NotImplementedException("Not implemented");
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(T1[] array) {
        throw new MaterialDialog.NotImplementedException("Not implemented");
    }

    class Callback extends SortedList.Callback<T> {
        @Override
        public int compare(T o1, T o2) {
            return (reverse) ? comparator.compare(o2, o1) : comparator.compare(o1, o2);
        }

        @Override
        public void onInserted(int position, int count) {
            if (callback != null)
                if (count == 1)
                    callback.notifyItemInserted(position);
                else
                    callback.notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            if (callback != null)
                if (count == 1)
                    callback.notifyItemRemoved(position);
                else
                    callback.notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            if (callback != null)
                callback.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            if (callback != null)
                if (count == 1)
                    callback.notifyItemChanged(position);
                else
                    callback.notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(T oldItem, T newItem) {
            return comparator.areContentsTheSame(oldItem, newItem);
        }

        @Override
        public boolean areItemsTheSame(T item1, T item2) {
            return comparator.areItemsTheSame(item1, item2);
        }
    }

    class CallbackedSortedListIterator implements Iterator<T>, ListIterator<T> {
        int position;

        public CallbackedSortedListIterator() {
            this(0);
        }

        public CallbackedSortedListIterator(int position) {
            this.position = position;
        }

        @Override
        public void add(T object) {
            list.add(object);
        }

        @Override
        public boolean hasNext() {
            return list.size() > position + 1;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public T next() {
            return list.get(position++);
        }

        @Override
        public int nextIndex() {
            return position+1;
        }

        @Override
        public T previous() {
            return list.get(position--);
        }

        @Override
        public int previousIndex() {
            return position-1;
        }

        @Override
        public void remove() {
            list.remove(list.get(position));
            callback.notifyItemRemoved(position);
        }

        @Override
        public void set(T object) {
            list.remove(list.get(position));
            list.add(object);
        }
    }

    public interface ItemComparator<T> {
        int compare(T o1, T o2);

        boolean areContentsTheSame(T oldItem, T newItem);

        boolean areItemsTheSame(T item1, T item2);
    }
}
