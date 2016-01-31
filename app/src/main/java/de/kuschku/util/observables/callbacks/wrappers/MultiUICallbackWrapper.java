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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.kuschku.util.observables.callbacks.wrappers;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.kuschku.util.observables.callbacks.UICallback;

@UiThread
public class MultiUICallbackWrapper implements UICallback {
    @NonNull
    private final Set<UICallback> callbacks = new HashSet<>();

    private MultiUICallbackWrapper(@NonNull Collection<UICallback> callbacks) {
        this.callbacks.addAll(callbacks);
    }

    @NonNull
    public static MultiUICallbackWrapper of(@NonNull UICallback... callbacks) {
        return new MultiUICallbackWrapper(Arrays.asList(callbacks));
    }

    public void addCallback(@NonNull UICallback callback) {
        callbacks.add(callback);
    }

    public void removeCallback(@NonNull UICallback callback) {
        callbacks.remove(callback);
    }

    @Override
    public void notifyItemInserted(int position) {
        for (UICallback callback : callbacks) {
            callback.notifyItemInserted(position);
        }
    }

    @Override
    public void notifyItemChanged(int position) {
        for (UICallback callback : callbacks) {
            callback.notifyItemChanged(position);
        }
    }

    @Override
    public void notifyItemRemoved(int position) {
        for (UICallback callback : callbacks) {
            callback.notifyItemRemoved(position);
        }
    }

    @Override
    public void notifyItemMoved(int from, int to) {
        for (UICallback callback : callbacks) {
            callback.notifyItemMoved(from, to);
        }
    }

    @Override
    public void notifyItemRangeInserted(int position, int count) {
        for (UICallback callback : callbacks) {
            callback.notifyItemRangeInserted(position, count);
        }
    }

    @Override
    public void notifyItemRangeChanged(int position, int count) {
        for (UICallback callback : callbacks) {
            callback.notifyItemRangeChanged(position, count);
        }
    }

    @Override
    public void notifyItemRangeRemoved(int position, int count) {
        for (UICallback callback : callbacks) {
            callback.notifyItemRangeRemoved(position, count);
        }
    }
}
