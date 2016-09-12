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

import de.kuschku.util.observables.IObservable;
import de.kuschku.util.observables.callbacks.GeneralCallback;
import de.kuschku.util.observables.callbacks.wrappers.MultiGeneralCallback;

public class ObservableElement<T> implements IObservable<GeneralCallback<T>> {
    private MultiGeneralCallback<T> callbacks = MultiGeneralCallback.of();

    private T value;

    public ObservableElement(T value) {
        this.value = value;
    }

    public ObservableElement() {
        this.value = null;
    }

    @Override
    public void addCallback(GeneralCallback<T> callback) {
        callbacks.addCallback(callback);
    }

    @Override
    public void removeCallback(GeneralCallback<T> callback) {
        callbacks.removeCallback(callback);
    }

    @Override
    public void removeCallbacks() {
        callbacks.removeCallbacks();
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }
}
