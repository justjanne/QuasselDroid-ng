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
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.kuschku.libquassel.client.NetworkManager;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.quasseldroid_ng.R;

public class NetworkSpinnerAdapter implements ThemedSpinnerAdapter {
    @Nullable
    private Resources.Theme theme;
    private FakeNetworksWrapper wrapper;

    public NetworkSpinnerAdapter(Context context) {
        wrapper = new FakeNetworksWrapper(context);
    }


    public void setNetworkManager(NetworkManager networkManager) {
        wrapper.setBase(networkManager.networks());
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
        QNetwork networks = getItem(position);
        view.setText(networks == null ? "" : networks.networkName());
        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        wrapper.addObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        wrapper.removeObserver(observer);
    }

    @Override
    public int getCount() {
        return wrapper.getCount();
    }

    @Override
    public QNetwork getItem(int position) {
        return wrapper.get(position);
    }

    @Override
    public long getItemId(int position) {
        QNetwork network = getItem(position);
        if (network != null)
            return network.networkId();
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
        QNetwork networks = getItem(position);
        view.setText(networks == null ? "" : networks.networkName());
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
}
