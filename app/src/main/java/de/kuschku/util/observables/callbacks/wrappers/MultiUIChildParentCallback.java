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

import de.kuschku.util.observables.callbacks.UIChildParentCallback;

@UiThread
public class MultiUIChildParentCallback implements UIChildParentCallback {
    @NonNull
    private final Set<UIChildParentCallback> callbacks = new HashSet<>();

    private MultiUIChildParentCallback(@NonNull Collection<UIChildParentCallback> callbacks) {
        this.callbacks.addAll(callbacks);
    }

    @NonNull
    public static MultiUIChildParentCallback of(@NonNull UIChildParentCallback... callbacks) {
        return new MultiUIChildParentCallback(Arrays.asList(callbacks));
    }


    public void addCallback(@NonNull UIChildParentCallback callback) {
        this.callbacks.add(callback);
    }

    public void removeCallback(@NonNull UIChildParentCallback callback) {
        this.callbacks.remove(callback);
    }

    @Override
    public void notifyChildItemInserted(int group, int position) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyChildItemInserted(group, position);
    }

    @Override
    public void notifyChildItemChanged(int group, int position) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyChildItemChanged(group, position);
    }

    @Override
    public void notifyChildItemRemoved(int group, int position) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyChildItemRemoved(group, position);
    }

    @Override
    public void notifyParentItemInserted(int position) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyParentItemInserted(position);
    }

    @Override
    public void notifyParentItemRemoved(int position) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyParentItemRemoved(position);
    }

    @Override
    public void notifyParentItemChanged(int position) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyParentItemChanged(position);
    }

    @Override
    public void notifyParentItemRangeInserted(int from, int to) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyParentItemRangeInserted(from, to);
    }
}
