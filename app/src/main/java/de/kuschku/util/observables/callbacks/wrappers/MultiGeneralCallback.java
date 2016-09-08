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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.kuschku.util.observables.IObservable;
import de.kuschku.util.observables.callbacks.GeneralCallback;

public class MultiGeneralCallback<T> implements IObservable<GeneralCallback<T>>, GeneralCallback<T> {
    @NonNull
    final Set<GeneralCallback<T>> callbacks;

    private MultiGeneralCallback(@NonNull List<MultiGeneralCallback<T>> multiGeneralCallbacks) {
        this.callbacks = new HashSet<>(multiGeneralCallbacks);
    }

    @SafeVarargs
    @NonNull
    public static <U> MultiGeneralCallback<U> of(MultiGeneralCallback<U>... callbacks) {
        return new MultiGeneralCallback<>(Arrays.asList(callbacks));
    }

    @Override
    public void notifyChanged(T obj) {
        for (GeneralCallback<T> callback : callbacks) {
            callback.notifyChanged(obj);
        }
    }

    @Override
    public void addCallback(GeneralCallback<T> callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeCallback(GeneralCallback<T> callback) {
        callbacks.remove(callback);
    }

    @Override
    public void removeCallbacks() {
        callbacks.clear();
    }
}
