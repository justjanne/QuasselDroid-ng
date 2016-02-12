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

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.HashSet;
import java.util.Set;

import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.client.NetworkManager;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.observables.callbacks.DrawerItemCallback;
import de.kuschku.util.observables.callbacks.GeneralCallback;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class BufferViewConfigItem implements DrawerItemCallback {
    @NonNull
    private final BufferItemManager manager;
    @NonNull
    private final ObservableComparableSortedList<NetworkItem> networks = new ObservableComparableSortedList<>(NetworkItem.class);
    @NonNull
    private final Drawer drawer;
    @NonNull
    private final QBufferViewConfig config;
    @NonNull
    private final AppContext context;

    GeneralCallback rebuildNetworkList = this::rebuildNetworkList;
    AdapterUICallbackWrapper callbackWrapper;

    public BufferViewConfigItem(@NonNull Drawer drawer, @NonNull QBufferViewConfig config, @NonNull AppContext context) {
        this.drawer = drawer;
        this.config = config;
        this.context = context;
        manager = new BufferItemManager(context);
        config.addObserver(rebuildNetworkList);
        assertNotNull(drawer.getItemAdapter());
        callbackWrapper = new AdapterUICallbackWrapper(drawer.getItemAdapter());
        networks.addCallback(callbackWrapper);
        rebuildNetworkList();
    }

    private void rebuildNetworkList() {
        Client client = context.client();
        assertNotNull(client);
        NetworkManager networkManager = client.networkManager();
        assertNotNull(networkManager);

        // First we build a list of all network ids we want to display
        Set<Integer> ids = new HashSet<>();
        for (QNetwork network : networkManager.networks()) {
            if (config.networkId() <= 0 || network.networkId() == config.networkId()) {
                ids.add(network.networkId());
            }
        }
        // Now we build a list of all items to remove
        Set<NetworkItem> removed = new HashSet<>();
        for (NetworkItem item : networks) {
            if (item.getNetwork() != null && ids.contains(item.getNetwork().networkId())) {
                // And make sure that ids only contains those networks added
                ids.remove(item.getNetwork().networkId());
            } else {
                removed.add(item);
                item.removeCallback(this);
            }
        }

        // By now we know that removed contains all removed networks, and ids all added ones
        // We do this to avoid concurrent modification and to reduce the amount of UI changes
        networks.removeAll(removed);
        for (IDrawerItem item : removed) {
            drawer.removeItem(item.getIdentifier());
        }
        for (int id : ids) {
            QNetwork network = networkManager.network(id);
            if (network != null) {
                NetworkItem item = new NetworkItem(config, client, manager, network);
                networks.add(item);
                item.addCallback(this);
            }
        }
        for (NetworkItem item : networks) {
            if (ids.contains(item.getNetwork().networkId())) {
                int position = networks.indexOf(item);
                drawer.addItemAtPosition(item, position);
            }
        }
        for (int i = 0; i < drawer.getAdapter().getItemCount(); i++) {
            IDrawerItem adapterItem = drawer.getAdapter().getItem(i);
            if (adapterItem instanceof NetworkItem &&
                    ((NetworkItem) adapterItem).getNetwork().isConnected() &&
                    ids.contains(((NetworkItem) adapterItem).getNetwork().networkId()))
                drawer.getAdapter().expand(i);
        }
    }

    @Override
    public void notifyChanged(@NonNull IDrawerItem item) {
        int position = drawer.getAdapter().getPosition(item);
        if (position != -1) {
            drawer.getAdapter().notifyAdapterItemChanged(position);
            if (item instanceof NetworkItem)
                drawer.getAdapter().notifyAdapterSubItemsChanged(position);
        }
    }

    public void remove() {
        config.deleteObserver(rebuildNetworkList);
        networks.removeCallback(callbackWrapper);
    }
}
