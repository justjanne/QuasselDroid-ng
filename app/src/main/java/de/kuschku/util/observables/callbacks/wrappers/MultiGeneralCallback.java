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

public class MultiGeneralCallback implements IObservable<GeneralCallback>, GeneralCallback {
    @NonNull
    final Set<GeneralCallback> callbacks;

    private MultiGeneralCallback(@NonNull List<MultiGeneralCallback> multiGeneralCallbacks) {
        this.callbacks = new HashSet<>(multiGeneralCallbacks);
    }

    @NonNull
    public static MultiGeneralCallback of(MultiGeneralCallback... callbacks) {
        return new MultiGeneralCallback(Arrays.asList(callbacks));
    }

    @Override
    public void notifyChanged() {
        for (GeneralCallback callback : callbacks) {
            callback.notifyChanged();
        }
    }

    @Override
    public void addCallback(GeneralCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeCallback(GeneralCallback callback) {
        callbacks.remove(callback);
    }
}
