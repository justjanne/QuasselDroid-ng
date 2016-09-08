/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.util.observables.lists;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.callbacks.wrappers.MultiUICallbackWrapper;

public class ObservableList<T> extends ArrayList<T> implements IObservableList<UICallback, T> {
    @NonNull
    private final MultiUICallbackWrapper callback = MultiUICallbackWrapper.of();

    public ObservableList() {
        super();
    }

    public ObservableList(int capacity) {
        super(capacity);
    }

    public ObservableList(@NonNull Collection<? extends T> collection) {
        super(collection);
    }

    public void addCallback(@NonNull UICallback callback) {
        this.callback.addCallback(callback);
    }

    public void removeCallback(@NonNull UICallback callback) {
        this.callback.removeCallback(callback);
    }

    @Override
    public void removeCallbacks() {
        callback.removeCallbacks();
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
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        return addAll(getPosition(), collection);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> collection) {
        boolean result = super.addAll(index, collection);
        if (result)
            callback.notifyItemRangeInserted(index, collection.size());
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
            return true;
        }
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        callback.notifyItemRangeRemoved(fromIndex, toIndex - fromIndex);
    }

    @Override
    public int indexOf(Object object) {
        for (int i = 0; i < size(); i++) {
            if (get(i) == object) return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object object) {
        for (int i = size() - 1; i >= 0; i--) {
            if (get(i) == object) return i;
        }
        return -1;
    }

    @Override
    public void clear() {
        int size = size();
        super.clear();
        callback.notifyItemRangeRemoved(0, size);
    }

    @Override
    public T set(int index, T element) {
        T set = super.set(index, element);
        callback.notifyItemChanged(index);
        return set;
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
