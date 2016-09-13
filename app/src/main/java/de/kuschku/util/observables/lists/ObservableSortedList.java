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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.callbacks.wrappers.MultiUICallbackWrapper;

public class ObservableSortedList<T> extends ArrayList<T> implements IObservableList<UICallback, T> {
    @NonNull
    private final MultiUICallbackWrapper callback = MultiUICallbackWrapper.of();

    private final Comparator<T> comparator;

    public ObservableSortedList(Comparator<T> comparator) {
        super();
        this.comparator = comparator;
    }

    public ObservableSortedList(Comparator<T> comparator, int capacity) {
        super(capacity);
        this.comparator = comparator;
    }

    public ObservableSortedList(AndroidObservableSortedList.ItemComparator<T> itemComparator) {
        this.comparator = new ObservableComparableSortedList.ItemComparatorWrapper<>(itemComparator);
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

    private int getPosition(T key) {
        int low = 0;
        int high = size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = get(mid);
            int cmp = comparator.compare(midVal, key);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return -1; // key found
        }
        return low;  // key not found
    }

    @Override
    public boolean add(T object) {
        int position = addInternal(object);
        callback.notifyItemInserted(position);
        return position != -1;
    }

    public int addInternal(T object) {
        int position = getPosition(object);
        if (position != -1) super.add(position, object);
        return position;
    }

    @Override
    public void add(int index, T object) {
        add(object);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        boolean addedAny = false;
        for (T t : collection) {
            addedAny |= add(t);
        }
        return addedAny;
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> collection) {
        return addAll(collection);
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
        return Collections.binarySearch(this, (T) object, comparator);
    }

    @Override
    public int lastIndexOf(Object object) {
        return Collections.binarySearch(this, (T) object, comparator);
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
        notifyItemChanged(element);
        callback.notifyItemChanged(index);
        return set;
    }

    public void notifyItemChanged(T element) {
        int index = super.indexOf(element);
        super.remove(index);
        int position = getPosition(element);
        if (position != index) {
            callback.notifyItemRemoved(index);
            add(position, element);
        } else {
            super.add(index, element);
            callback.notifyItemChanged(index);
        }
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
