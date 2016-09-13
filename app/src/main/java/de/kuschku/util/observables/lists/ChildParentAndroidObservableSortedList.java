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

import de.kuschku.util.observables.IObservable;
import de.kuschku.util.observables.callbacks.UIChildCallback;
import de.kuschku.util.observables.callbacks.UIChildParentCallback;
import de.kuschku.util.observables.callbacks.UIParentCallback;
import de.kuschku.util.observables.callbacks.wrappers.MultiUIChildParentCallback;
import de.kuschku.util.observables.callbacks.wrappers.ParentUICallbackWrapper;

public class ChildParentAndroidObservableSortedList<T extends IObservable<UIChildCallback>> extends AndroidObservableSortedList<T> {
    @NonNull
    private final MultiUIChildParentCallback callback = MultiUIChildParentCallback.of();

    public ChildParentAndroidObservableSortedList(@NonNull Class<T> cl, @NonNull ItemComparator<T> comparator) {
        super(cl, comparator);
        registerCallbacks();
    }

    public ChildParentAndroidObservableSortedList(@NonNull Class<T> cl, @NonNull ItemComparator<T> comparator, boolean reverse) {
        super(cl, comparator, reverse);
        registerCallbacks();
    }

    private void registerCallbacks() {
        super.addCallback(new MyWrapper(callback));
    }

    public void addChildParentCallback(@NonNull UIChildParentCallback callback) {
        this.callback.addCallback(callback);
    }

    public void removeChildParentCallback(@NonNull UIChildParentCallback callback) {
        this.callback.removeCallback(callback);
    }

    private class MyWrapper extends ParentUICallbackWrapper {
        public MyWrapper(@NonNull UIParentCallback wrapped) {
            super(wrapped);
        }

        @Override
        public void notifyItemInserted(int position) {
            super.notifyItemInserted(position);
            get(position).addCallback(callback);
        }

        @Override
        public void notifyItemRangeInserted(int position, int count) {
            super.notifyItemRangeInserted(position, count);
            for (int i = position; i < position + count; i++) {
                get(position).addCallback(callback);
            }
        }

    }
}
