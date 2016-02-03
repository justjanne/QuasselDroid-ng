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

package de.kuschku.util.observables.callbacks.wrappers;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.kuschku.util.observables.callbacks.ElementCallback;

@UiThread
public class MultiElementCallbackWrapper<T> implements ElementCallback<T> {
    @NonNull
    private final Set<ElementCallback<T>> callbacks = new HashSet<>();

    private MultiElementCallbackWrapper(@NonNull Collection<ElementCallback<T>> callbacks) {
        this.callbacks.addAll(callbacks);
    }

    @SafeVarargs
    @NonNull
    public static <T> MultiElementCallbackWrapper of(@NonNull ElementCallback<T>... callbacks) {
        return new MultiElementCallbackWrapper<>(Arrays.asList(callbacks));
    }

    public void addCallback(@NonNull ElementCallback<T> callback) {
        callbacks.add(callback);
    }

    public void removeCallback(@NonNull ElementCallback<T> callback) {
        callbacks.remove(callback);
    }

    @Override
    public void notifyItemInserted(T element) {
        for (ElementCallback<T> callback : callbacks) {
            callback.notifyItemInserted(element);
        }
    }

    @Override
    public void notifyItemRemoved(T element) {
        for (ElementCallback<T> callback : callbacks) {
            callback.notifyItemInserted(element);
        }
    }

    @Override
    public void notifyItemChanged(T element) {
        for (ElementCallback<T> callback : callbacks) {
            callback.notifyItemInserted(element);
        }
    }
}
