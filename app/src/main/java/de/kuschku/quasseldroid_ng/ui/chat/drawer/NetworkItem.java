package de.kuschku.quasseldroid_ng.ui.chat.drawer;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.materialdrawer.holder.ColorHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.BaseViewHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.libquassel.syncables.types.BufferViewConfig;
import de.kuschku.libquassel.syncables.types.Network;
import de.kuschku.quasseldroid_ng.ui.AppContext;
import de.kuschku.util.AndroidAssert;
import de.kuschku.util.observables.IObservable;
import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.callbacks.GeneralCallback;
import de.kuschku.util.observables.callbacks.wrappers.GeneralCallbackWrapper;
import de.kuschku.util.observables.lists.ObservableSortedList;

import static de.kuschku.util.AndroidAssert.*;

public class NetworkItem extends PrimaryDrawerItem implements IObservable<GeneralCallback>, GeneralCallback {
    private final AppContext context;
    private final Network network;
    private final BufferViewConfig config;
    private final ObservableSortedList<BufferItem> buffers = new ObservableSortedList<>(BufferItem.class, new AlphabeticalComparator());
    private final SparseArray<BufferItem> bufferIds = new SparseArray<>();

    private final GeneralCallbackWrapper callback = new GeneralCallbackWrapper();

    public NetworkItem(AppContext context, Network network, BufferViewConfig config) {
        this.context = context;
        this.network = network;
        this.config = config;

        for (Integer bufferId : this.config.getBufferList()) {
            Buffer buffer = context.getClient().getBuffer(bufferId);
            if (buffer != null && buffer.getInfo().networkId == network.getNetworkId()) {
                this.buffers.add(new BufferItem(buffer, context));
                Log.e("Drawer", "Buffer can not be null! BufferId: "+ bufferId);
            }
        }
        this.config.getBufferList().addCallback(new ElementCallback<Integer>() {
            @Override
            public void notifyItemInserted(Integer element) {
                if (network.getBuffers().contains(element)) {
                    if (bufferIds.get(element) == null) {
                        Buffer buffer = context.getClient().getBuffer(element);

                        BufferItem bufferItem = new BufferItem(buffer, context);
                        buffers.add(bufferItem);
                        bufferItem.addCallback(NetworkItem.this);
                        bufferIds.put(element, bufferItem);
                        notifyChanged();
                    }
                }
            }

            @Override
            public void notifyItemRemoved(Integer element) {
                if (bufferIds.get(element) != null) {
                    bufferIds.remove(element);
                    notifyChanged();
                }
            }

            @Override
            public void notifyItemChanged(Integer element) {
                if (bufferIds.get(element) != null) {
                    notifyChanged();
                }
            }
        });
    }

    @Override
    public boolean isIconTinted() {
        return super.isIconTinted();
    }

    @Override
    public ColorHolder getIconColor() {
        return super.getIconColor();
    }

    @Override
    public StringHolder getDescription() {
        return new StringHolder(String.valueOf(network.getLatency()));
    }

    @Override
    public StringHolder getName() {
        return new StringHolder(network.getNetworkName());
    }

    @Override
    public List<IDrawerItem> getSubItems() {
        ArrayList<IDrawerItem> items = new ArrayList<>();
        for (IDrawerItem item : buffers) {
            items.add(item);
        }
        return items;
    }

    @Override
    public void notifyChanged() {
        this.callback.notifyChanged();
    }

    @Override
    public void addCallback(GeneralCallback callback) {
        this.callback.addCallback(callback);
    }

    @Override
    public void removeCallback(GeneralCallback callback) {
        this.callback.removeCallback(callback);
    }

    public Network getNetwork() {
        return network;
    }

    @Override
    public long getIdentifier() {
        return network.getNetworkId();
    }

    class AlphabeticalComparator implements ObservableSortedList.ItemComparator<BufferItem> {
        @Override
        public int compare(BufferItem o1, BufferItem o2) {
            return o1.getName().getText().compareTo(o2.getName().getText());
        }

        @Override
        public boolean areContentsTheSame(BufferItem oldItem, BufferItem newItem) {
            return oldItem.getBuffer().getInfo().id == newItem.getBuffer().getInfo().id;
        }

        @Override
        public boolean areItemsTheSame(BufferItem item1, BufferItem item2) {
            return item1 == item2;
        }
    }
    
    class NoneComparator implements ObservableSortedList.ItemComparator<BufferItem> {
        @Override
        public int compare(BufferItem o1, BufferItem o2) {
            return o1.getBuffer().getInfo().id - o2.getBuffer().getInfo().id;
        }

        @Override
        public boolean areContentsTheSame(BufferItem oldItem, BufferItem newItem) {
            return oldItem.getBuffer().getInfo().id == newItem.getBuffer().getInfo().id;
        }

        @Override
        public boolean areItemsTheSame(BufferItem item1, BufferItem item2) {
            return item1 == item2;
        }
    }
}
