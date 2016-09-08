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

package de.kuschku.quasseldroid_ng.ui.coresettings.network;

import android.content.Context;
import android.database.DataSetObserver;

import java.util.HashSet;
import java.util.Set;

import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.lists.ObservableSortedList;

public class FakeNetworksWrapper {
    private final QNetwork fakeNetwork;
    private final Set<DataSetObserver> observers = new HashSet<>();
    private final UICallback callback = new UICallback() {
        @Override
        public void notifyItemInserted(int position) {
            notifyChanged();
        }

        @Override
        public void notifyItemChanged(int position) {
            notifyChanged();
        }

        @Override
        public void notifyItemRemoved(int position) {
            notifyChanged();
        }

        @Override
        public void notifyItemMoved(int from, int to) {
            notifyChanged();
        }

        @Override
        public void notifyItemRangeInserted(int position, int count) {
            notifyChanged();
        }

        @Override
        public void notifyItemRangeChanged(int position, int count) {
            notifyChanged();
        }

        @Override
        public void notifyItemRangeRemoved(int position, int count) {
            notifyChanged();
        }
    };
    private ObservableSortedList<QNetwork> base;

    public FakeNetworksWrapper(Context context) {
        this.fakeNetwork = new AllNetworksItem(context);
    }

    public void setBase(ObservableSortedList<QNetwork> base) {
        if (this.base != null)
            this.base.removeCallback(callback);
        this.base = base;
        if (this.base != null)
            this.base.addCallback(callback);
        notifyChanged();
    }

    public QNetwork get(int position) {
        if (position == 0)
            return fakeNetwork;
        else if (position > 0 && position <= base.size() && base != null)
            return base.get(position - 1);
        else
            return null;
    }

    public int getCount() {
        return base == null ? 1 : base.size() + 1;
    }

    public void addObserver(DataSetObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(DataSetObserver observer) {
        observers.remove(observer);
    }

    public void notifyChanged() {
        for (DataSetObserver observer : observers) {
            observer.onChanged();
        }
    }
}
