package de.kuschku.util.observablelists;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ObservableList<T> extends ArrayList<T> {
    UICallback callback;

    public void setCallback(UICallback callback) {
        this.callback = callback;
    }

    private int getPosition() {
        return isEmpty() ? 0 : size() - 1;
    }

    @Override
    public boolean add(T object) {
        add(getPosition(), object);
        return true;
    }

    @Override
    public void add(int index, T object) {
        super.add(index, object);
        callback.notifyItemInserted(index);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return addAll(getPosition(), collection);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> collection) {
        boolean result = super.addAll(index, collection);
        if (result) callback.notifyItemRangeInserted(index, collection.size());
        return result;
    }

    @Override
    public T remove(int index) {
        T result = super.remove(index);
        callback.notifyItemRemoved(index);
        return result;
    }

    @Override
    public boolean remove(Object object) {
        int position = indexOf(object);
        if (position == -1) {
            return false;
        } else {
            remove(position);
            callback.notifyItemRemoved(position);
            return true;
        }
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        callback.notifyItemRangeRemoved(fromIndex, toIndex - fromIndex);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        return super.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return super.retainAll(collection);
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new CallbackedArrayListIterator<>(super.iterator());
    }

    class CallbackedArrayListIterator<E> implements Iterator<E> {
        final Iterator<E> iterator;
        int position = 0;

        public CallbackedArrayListIterator(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public E next() {
            position++;
            return iterator.next();
        }

        @Override
        public void remove() {
            iterator.remove();
            callback.notifyItemRemoved(position);
        }
    }
}
