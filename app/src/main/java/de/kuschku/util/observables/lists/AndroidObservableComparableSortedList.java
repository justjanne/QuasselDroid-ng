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

import java.util.List;

import de.kuschku.util.observables.ContentComparable;
import de.kuschku.util.observables.callbacks.UICallback;

public class AndroidObservableComparableSortedList<T extends ContentComparable<T>> extends AndroidObservableSortedList<T> implements IObservableList<UICallback, T>, List<T> {


    public AndroidObservableComparableSortedList(@NonNull Class<T> cl) {
        super(cl, new SimpleItemComparator<>());
    }

    public AndroidObservableComparableSortedList(@NonNull Class<T> cl, boolean reverse) {
        super(cl, new SimpleItemComparator<>(), reverse);
    }

    public static class SimpleItemComparator<T extends ContentComparable<T>> implements ItemComparator<T> {
        @Override
        public int compare(@NonNull T o1, @NonNull T o2) {
            return o1.compareTo(o2);
        }

        @Override
        public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
            return oldItem.areContentsTheSame(newItem);
        }

        @Override
        public boolean areItemsTheSame(@NonNull T item1, @NonNull T item2) {
            return item1.areItemsTheSame(item2);
        }
    }
}
