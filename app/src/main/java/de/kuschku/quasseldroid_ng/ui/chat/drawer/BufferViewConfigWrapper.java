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

import java.util.ArrayList;

import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.observables.lists.ObservableSortedList;

public class BufferViewConfigWrapper {
    @NonNull
    private final ObservableSortedList<NetworkItem> networks = new ObservableSortedList<>(NetworkItem.class, new ObservableSortedList.ItemComparator<NetworkItem>() {
        @Override
        public int compare(@NonNull NetworkItem o1, @NonNull NetworkItem o2) {
            return o1.getName().getText().compareTo(o2.getName().getText());
        }

        @Override
        public boolean areContentsTheSame(NetworkItem item1, NetworkItem item2) {
            return item1 == item2;
        }

        @Override
        public boolean areItemsTheSame(@NonNull NetworkItem item1, @NonNull NetworkItem item2) {
            return item1.getNetwork().networkId() == item2.getNetwork().networkId();
        }
    });
    @NonNull
    private final AppContext context;
    @NonNull
    private final QBufferViewConfig config;
    private Drawer drawer;

    public BufferViewConfigWrapper(@NonNull AppContext context, @NonNull QBufferViewConfig config, Drawer drawer) {
        this.context = context;
        this.config = config;
        this.drawer = drawer;
        updateNetworks();
    }

    public void updateNetworks() {
        networks.clear();
        if (config.networkId() != 0)
            networks.add(new NetworkItem(context, context.client().networkManager().network(config.networkId()), config));
        else
            for (QNetwork network : context.client().networkManager().networks())
                networks.add(new NetworkItem(context, network, config));
    }

    public void updateDrawerItems() {
        drawer.removeAllItems();
        for (IDrawerItem item : getItems()) {
            drawer.addItem(item);
        }
        for (int i = 0; i < drawer.getAdapter().getItemCount(); i++) {
            IDrawerItem item = drawer.getAdapter().getItem(i);
            if (item instanceof NetworkItem) {
                NetworkItem networkItem = (NetworkItem) item;
                if (networkItem.getNetwork().isConnected())
                    drawer.getAdapter().expand(i);
            }
        }
    }

    public void setDrawer(Drawer drawer) {
        this.drawer = drawer;
    }

    @NonNull
    public ArrayList<IDrawerItem> getItems() {
        ArrayList<IDrawerItem> items = new ArrayList<>();
        for (IDrawerItem item : networks) {
            items.add(item);
        }
        return items;
    }
}
