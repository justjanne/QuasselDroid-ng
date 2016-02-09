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

package de.kuschku.quasseldroid_ng.ui.chat.drawer;

import android.support.annotation.NonNull;

import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.util.observables.ContentComparable;
import de.kuschku.util.observables.IObservable;
import de.kuschku.util.observables.callbacks.DrawerItemCallback;
import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.callbacks.wrappers.MultiDrawerItemCallback;

public class NetworkItem extends PrimaryDrawerItem implements IObservable<DrawerItemCallback>, ContentComparable<NetworkItem> {
    @NonNull
    private final QBufferViewConfig config;
    @NonNull
    private final Client client;
    @NonNull
    private final BufferItemManager manager;
    @NonNull
    private final QNetwork network;
    private final MultiDrawerItemCallback callback = MultiDrawerItemCallback.of();

    public NetworkItem(@NonNull QBufferViewConfig config, @NonNull Client client, @NonNull BufferItemManager manager, @NonNull QNetwork network) {
        this.config = config;
        this.client = client;
        this.manager = manager;
        this.network = network;
        ElementCallback<Integer> elemCallback = new ElementCallback<Integer>() {
            @Override
            public void notifyItemInserted(Integer element) {
                callback.notifyChanged(NetworkItem.this);
            }

            @Override
            public void notifyItemRemoved(Integer element) {
                callback.notifyChanged(NetworkItem.this);
            }

            @Override
            public void notifyItemChanged(Integer element) {
                callback.notifyChanged(manager.get(element));
            }
        };
        config.bufferIds().addCallback(elemCallback);
        client.bufferManager().byNetwork(network.networkId()).addCallback(elemCallback);
    }

    @NonNull
    @Override
    public List<IDrawerItem> getSubItems() {
        List<IDrawerItem> bufferItems = new ArrayList<>();
        for (int id : config.bufferList()) {
            if (client.bufferManager().byNetwork(network.networkId()).contains(id)) {
                bufferItems.add(manager.get(id));
            }
        }
        return bufferItems;
    }

    @NonNull
    @Override
    public StringHolder getName() {
        return new StringHolder(network.networkName());
    }

    @Override
    public void addCallback(DrawerItemCallback callback) {
        this.callback.addCallback(callback);
    }

    @Override
    public void removeCallback(DrawerItemCallback callback) {
        this.callback.removeCallback(callback);
    }

    @Override
    public boolean areItemsTheSame(@NonNull NetworkItem other) {
        return network.networkId() == other.network.networkId();
    }

    @Override
    public boolean areContentsTheSame(NetworkItem other) {
        return network.equals(other);
    }

    @Override
    public int compareTo(@NonNull NetworkItem another) {
        return network.networkName().compareToIgnoreCase(another.network.networkName());
    }

    @NonNull
    public QNetwork getNetwork() {
        return network;
    }

    @Override
    public String toString() {
        return String.valueOf(network);
    }

    @Override
    public long getIdentifier() {
        return network.networkId() << 16;
    }
}
