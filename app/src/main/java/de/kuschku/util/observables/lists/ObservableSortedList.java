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
import android.support.annotation.Nullable;
import android.support.v7.util.SortedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import de.kuschku.util.backports.Stream;
import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.callbacks.wrappers.MultiUICallbackWrapper;

import static de.kuschku.util.AndroidAssert.assertTrue;

@SuppressWarnings("unchecked")
public class ObservableSortedList<T> implements IObservableList<UICallback, T> {
    @NonNull
    private final SortedList<T> list;
    private final boolean reverse;

    @NonNull
    private final MultiUICallbackWrapper callback = MultiUICallbackWrapper.of();
    @NonNull
    private ItemComparator<T> comparator;

    public ObservableSortedList(@NonNull Class<T> cl, @NonNull ItemComparator<T> comparator) {
        this(cl, comparator, false);
    }

    public ObservableSortedList(@NonNull Class<T> cl, @NonNull ItemComparator<T> comparator, boolean reverse) {
        this.list = new SortedList<>(cl, new Callback());
        this.comparator = comparator;
        this.reverse = reverse;
    }

    @Override
    public void addCallback(@NonNull UICallback callback) {
        this.callback.addCallback(callback);
    }

    @Override
    public void removeCallback(@NonNull UICallback callback) {
        this.callback.removeCallback(callback);
    }

    @Override
    public void removeCallbacks() {
        callback.removeCallbacks();
    }

    public void setComparator(@NonNull ItemComparator<T> comparator) {
        this.comparator = comparator;
    }

    @Nullable
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
        return indexOf(object) != SortedList.INVALID_POSITION;
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
        return indexOf(object);
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator() {
        return new CallbackedSortedListIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int location) {
        return new CallbackedSortedListIterator(location);
    }

    @Nullable
    @Override
    public T remove(int location) {
        T item = list.get(location);
        list.remove(item);
        return item;
    }

    @Override
    public boolean remove(Object object) {
        try {
            list.remove((T) object);
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        boolean result = true;
        for (Object o : collection) {
            result &= remove(o);
        }
        return result;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return false;
    }

    @Nullable
    @Override
    public T set(int location, T object) {
        return null;
    }

    @Override
    public int size() {
        return list.size();
    }

    @NonNull
    @Override
    public List<T> subList(int start, int end) {
        assertTrue(start <= end);
        assertTrue(start >= 0);
        assertTrue(end <= list.size());

        List<T> subList = new ArrayList<>(end - start);
        for (int i = start; i < end; i++) {
            subList.add(list.get(i));
        }
        return subList;
    }

    @NonNull
    @Override
    public Object[] toArray() {
        Object[] array = new Object[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(@NonNull T1[] a) {
        try {
            Object[] elements = toArray();
            if (a.length < elements.length)
                // Make a new array of a's runtime type, but my contents:
                return (T1[]) Arrays.copyOf(elements, elements.length, a.getClass());
            System.arraycopy(elements, 0, a, 0, elements.length);
            if (a.length > elements.length)
                a[elements.length] = null;
            return a;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyItemChanged(int position) {
        callback.notifyItemChanged(position);
    }

    @NonNull
    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }

    public interface ItemComparator<T> {
        int compare(T o1, T o2);

        boolean areContentsTheSame(T oldItem, T newItem);

        boolean areItemsTheSame(T item1, T item2);
    }

    class Callback extends SortedList.Callback<T> {
        @Override
        public int compare(T o1, T o2) {
            return (reverse) ? comparator.compare(o2, o1) : comparator.compare(o1, o2);
        }

        @Override
        public void onInserted(int position, int count) {
            if (count == 1)
                callback.notifyItemInserted(position);
            else
                callback.notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            if (count == 1)
                callback.notifyItemRemoved(position);
            else
                callback.notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            callback.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
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
            return position < list.size();
        }

        @Override
        public boolean hasPrevious() {
            return position >= 0;
        }

        @Override
        public T next() {
            return list.get(position++);
        }

        @Override
        public int nextIndex() {
            return position + 1;
        }

        @Override
        public T previous() {
            return list.get(position--);
        }

        @Override
        public int previousIndex() {
            return position - 1;
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
}
