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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.callbacks.wrappers.MultiElementCallbackWrapper;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@SuppressWarnings("unchecked")
public class ObservableSet<T> extends HashSet<T> implements IObservableSet<ElementCallback<T>, T>, ElementCallback<T> {
    @NonNull
    private final MultiElementCallbackWrapper<T> callback = MultiElementCallbackWrapper.<T>of();

    public ObservableSet(int capacity) {
        super(capacity);
        assertNotNull(this.callback);
    }

    public ObservableSet() {
        super();
        assertNotNull(this.callback);
    }

    public ObservableSet(@NonNull Collection<? extends T> collection) {
        assertNotNull(this.callback);

        assertNotNull(callback);
    }

    public void addCallback(@NonNull ElementCallback<T> callback) {
        assertNotNull(this.callback);

        this.callback.addCallback(callback);
    }

    public void removeCallback(@NonNull ElementCallback<T> callback) {
        assertNotNull(this.callback);

        this.callback.removeCallback(callback);
    }

    @Override
    public boolean add(T object) {
        assertNotNull(this.callback);

        boolean result = super.add(object);
        callback.notifyItemInserted(object);
        return result;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        assertNotNull(this.callback);

        boolean result = super.addAll(collection);
        if (result)
            for (T element : collection)
                callback.notifyItemInserted(element);
        return result;
    }

    @Override
    public boolean remove(Object object) {
        assertNotNull(this.callback);

        boolean contains = contains(object);
        if (contains) {
            super.remove(object);
            callback.notifyItemRemoved((T) object);
            return true;
        } else {
            return false;
        }
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new CallbackedArrayListIterator<>(super.iterator());
    }

    @Override
    public void notifyItemInserted(T element) {
        callback.notifyItemInserted(element);
    }

    @Override
    public void notifyItemRemoved(T element) {
        callback.notifyItemRemoved(element);
    }

    @Override
    public void notifyItemChanged(T element) {
        callback.notifyItemChanged(element);
    }

    class CallbackedArrayListIterator<E> implements Iterator<E> {
        final Iterator<E> iterator;
        E current;

        public CallbackedArrayListIterator(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public E next() {
            current = iterator.next();
            return current;
        }

        @Override
        public void remove() {
            assertNotNull(callback);

            iterator.remove();
            callback.notifyItemRemoved((T) current);
        }
    }
}
