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

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.HashSet;
import java.util.Set;

import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.observables.callbacks.DrawerItemCallback;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;

public class BufferViewConfigItem implements DrawerItemCallback {
    private final BufferItemManager manager;
    private final ObservableComparableSortedList<NetworkItem> networks = new ObservableComparableSortedList<>(NetworkItem.class);
    private final Drawer drawer;
    private final QBufferViewConfig config;
    private final AppContext context;

    public BufferViewConfigItem(Drawer drawer, QBufferViewConfig config, AppContext context) {
        this.drawer = drawer;
        this.config = config;
        this.context = context;
        manager = new BufferItemManager(context);
        config.addObserver(this::rebuildNetworkList);
        networks.addCallback(new AdapterUICallbackWrapper(drawer.getItemAdapter()));
        rebuildNetworkList();
    }

    private void rebuildNetworkList() {
        // First we build a list of all network ids we want to display
        Set<Integer> ids = new HashSet<>();
        for (QNetwork network : context.client().networkManager().networks()) {
            if (config.networkId() <= 0 || network.networkId() == config.networkId()) {
                ids.add(network.networkId());
            }
        }
        // Now we build a list of all items to remove
        Set<NetworkItem> removed = new HashSet<>();
        for (NetworkItem item : networks) {
            if (ids.contains(item.getNetwork().networkId())) {
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
            NetworkItem item = new NetworkItem(config, context.client(), manager, context.client().networkManager().network(id));
            networks.add(item);
            item.addCallback(this);
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
    public void notifyChanged(IDrawerItem item) {
        int position = drawer.getAdapter().getPosition(item);
        if (position != -1) {
            drawer.getAdapter().notifyAdapterItemChanged(position);
            if (item instanceof NetworkItem)
                drawer.getAdapter().notifyAdapterSubItemsChanged(position);
        }
    }
}
