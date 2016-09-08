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

package de.kuschku.quasseldroid_ng.ui.coresettings.identity;

import android.content.res.Resources;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import de.kuschku.libquassel.client.IdentityManager;
import de.kuschku.libquassel.syncables.types.interfaces.QIdentity;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.observables.callbacks.GeneralCallback;
import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.lists.ObservableSortedList;

public class IdentitySpinnerAdapter implements ThemedSpinnerAdapter, GeneralCallback {
    private final Set<DataSetObserver> observers = new HashSet<>();
    private ObservableSortedList<QIdentity> identities;
    @Nullable
    private Resources.Theme theme;

    public void setIdentityManager(IdentityManager identityManager) {
        if (identityManager != null) {
            this.identities = identityManager.identities();
            this.identities.addCallback(new UICallback() {
                @Override
                public void notifyItemInserted(int position) {
                    notifyChanged(null);
                }

                @Override
                public void notifyItemChanged(int position) {
                    notifyChanged(null);
                }

                @Override
                public void notifyItemRemoved(int position) {
                    notifyChanged(null);
                }

                @Override
                public void notifyItemMoved(int from, int to) {
                    notifyChanged(null);
                }

                @Override
                public void notifyItemRangeInserted(int position, int count) {
                    notifyChanged(null);
                }

                @Override
                public void notifyItemRangeChanged(int position, int count) {
                    notifyChanged(null);
                }

                @Override
                public void notifyItemRangeRemoved(int position, int count) {
                    notifyChanged(null);
                }
            });
        } else {
            this.identities = null;
        }
        notifyChanged(null);
    }

    @Nullable
    @Override
    public Resources.Theme getDropDownViewTheme() {
        return theme;
    }

    @Override
    public void setDropDownViewTheme(@Nullable Resources.Theme theme) {
        this.theme = theme;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(new ContextThemeWrapper(parent.getContext(), theme));
        TextView view = (TextView) inflater.inflate(R.layout.widget_spinner_item_toolbar, parent, false);
        QIdentity config = getItem(position);
        view.setText(config == null ? "" : config.identityName());
        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        observers.remove(observer);
    }

    @Override
    public int getCount() {
        return identities == null ? 0 : identities.size();
    }

    @Override
    public QIdentity getItem(int position) {
        if (position >= 0 && identities != null && position < identities.size())
            return identities.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        QIdentity identity = getItem(position);
        if (identity != null)
            return identity.id();
        else
            return -1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TextView view = (TextView) inflater.inflate(R.layout.widget_spinner_item_inline, parent, false);
        QIdentity identity = getItem(position);
        view.setText(identity == null ? "" : identity.identityName());
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }

    @Override
    public void notifyChanged(Object o) {
        for (DataSetObserver observer : observers) {
            observer.onChanged();
        }
    }
}
