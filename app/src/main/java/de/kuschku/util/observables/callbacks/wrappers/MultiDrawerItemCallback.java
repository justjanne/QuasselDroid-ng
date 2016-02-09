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

import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.kuschku.util.observables.IObservable;
import de.kuschku.util.observables.callbacks.DrawerItemCallback;

public class MultiDrawerItemCallback implements DrawerItemCallback, IObservable<DrawerItemCallback> {
    @NonNull
    final Set<DrawerItemCallback> callbacks;

    private MultiDrawerItemCallback(@NonNull List<DrawerItemCallback> multiGeneralCallbacks) {
        this.callbacks = new HashSet<>(multiGeneralCallbacks);
    }

    @NonNull
    public static MultiDrawerItemCallback of(DrawerItemCallback... callbacks) {
        return new MultiDrawerItemCallback(Arrays.asList(callbacks));
    }

    @Override
    public void notifyChanged(IDrawerItem item) {
        for (DrawerItemCallback callback : callbacks) {
            callback.notifyChanged(item);
        }
    }

    @Override
    public void addCallback(DrawerItemCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeCallback(DrawerItemCallback callback) {
        callbacks.remove(callback);
    }
}
