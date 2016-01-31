package de.kuschku.quasseldroid_ng.ui.chat.drawer;

import android.support.annotation.NonNull;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;

import de.kuschku.libquassel.syncables.types.BufferViewConfig;
import de.kuschku.libquassel.syncables.types.Network;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.lists.ObservableSortedList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class BufferViewConfigWrapper {
    private Drawer drawer;
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
            return item1.getNetwork().getNetworkId() == item2.getNetwork().getNetworkId();
        }
    });

    public BufferViewConfigWrapper(@NonNull AppContext context, @NonNull BufferViewConfig config, Drawer drawer) {
        this.drawer = drawer;
        config.doLateInit();
        networks.clear();
        for (Integer networkId : config.getNetworkList()) {
            Network network = context.getClient().getNetwork(networkId);
            assertNotNull(network);
            networks.add(new NetworkItem(context, network, config));
        }
        config.getNetworkList().addCallback(new ElementCallback<Integer>() {
            @Override
            public void notifyItemInserted(Integer element) {
                networks.add(new NetworkItem(context, context.getClient().getNetwork(element), config));
            }

            @Override
            public void notifyItemRemoved(Integer element) {
                for (NetworkItem network : networks) {
                    if (network.getNetwork().getNetworkId() == element) {
                        networks.remove(network);
                        break;
                    }
                }
            }

            @Override
            public void notifyItemChanged(Integer element) {
                for (NetworkItem network : networks) {
                    if (network.getNetwork().getNetworkId() == element) {
                        networks.notifyItemChanged(networks.indexOf(network));
                        break;
                    }
                }
            }
        });
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
