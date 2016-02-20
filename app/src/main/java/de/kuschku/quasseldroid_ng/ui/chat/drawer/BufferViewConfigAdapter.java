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

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.lists.ObservableSortedList;

public class BufferViewConfigAdapter extends ExpandableRecyclerAdapter<NetworkViewHolder, BufferViewHolder> implements OnBufferClickListener {
    private final AppContext context;
    private final ObservableSortedList<NetworkItem> items;
    private final Map<QNetwork, NetworkItem> itemMap = new WeakHashMap<>();
    private final Map<Integer, BufferViewHolder> bufferViewHolderMap = new WeakHashMap<>();
    private QBufferViewConfig config;
    private WeakReference<RecyclerView> recyclerView = new WeakReference<>(null);
    private int selectedFrom;
    private int selectedTo;
    private int open;
    private OnBufferClickListener bufferClickListener;

    private ElementCallback<QNetwork> callback = new ElementCallback<QNetwork>() {
        @Override
        public void notifyItemInserted(QNetwork network) {
            NetworkItem networkItem = new NetworkItem(context, config, network);
            itemMap.put(network, networkItem);
            items.add(networkItem);
        }

        @Override
        public void notifyItemRemoved(QNetwork network) {
            items.remove(itemMap.remove(network));
        }

        @Override
        public void notifyItemChanged(QNetwork network) {
            items.notifyItemChanged(items.indexOf(itemMap.get(network)));
        }
    };

    private BufferViewConfigAdapter(AppContext context, ObservableSortedList<NetworkItem> items) {
        super(items);
        this.context = context;
        this.items = items;
        items.addCallback(new UICallback() {
            @Override
            public void notifyItemInserted(int position) {
                notifyParentItemInserted(position);
            }

            @Override
            public void notifyItemChanged(int position) {
                notifyParentItemChanged(position);
            }

            @Override
            public void notifyItemRemoved(int position) {
                notifyParentItemRemoved(position);
            }

            @Override
            public void notifyItemMoved(int from, int to) {
                notifyParentItemRemoved(from);
                notifyParentItemInserted(to);
            }

            @Override
            public void notifyItemRangeInserted(int position, int count) {
                notifyParentItemRangeInserted(position, count);
            }

            @Override
            public void notifyItemRangeChanged(int position, int count) {
                for (int i = position; i < position + count; i++) {
                    notifyParentItemChanged(i);
                }
            }

            @Override
            public void notifyItemRangeRemoved(int position, int count) {
                for (int i = position; i < position + count; i++) {
                    notifyParentItemRemoved(position);
                }
            }
        });
    }

    public static BufferViewConfigAdapter of(AppContext context) {
        final ObservableSortedList<NetworkItem> networkItems = new ObservableSortedList<>(NetworkItem.class, new ObservableSortedList.ItemComparator<NetworkItem>() {
            @Override
            public int compare(NetworkItem o1, NetworkItem o2) {
                return o1.getNetwork().networkName().compareTo(o2.getNetwork().networkName());
            }

            @Override
            public boolean areContentsTheSame(NetworkItem oldItem, NetworkItem newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areItemsTheSame(NetworkItem item1, NetworkItem item2) {
                return item1.getNetwork().networkId() == item2.getNetwork().networkId();
            }
        });
        return new BufferViewConfigAdapter(context, networkItems);
    }

    @Override
    public NetworkViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        LayoutInflater inflater = LayoutInflater.from(parentViewGroup.getContext());
        return new NetworkViewHolder(inflater.inflate(NetworkViewHolder.layout(), parentViewGroup, false));
    }

    @Override
    public BufferViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        LayoutInflater inflater = LayoutInflater.from(childViewGroup.getContext());
        return new BufferViewHolder(context, inflater.inflate(BufferViewHolder.layout(), childViewGroup, false));
    }

    @Override
    public void onBindParentViewHolder(NetworkViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        parentViewHolder.bind(context, (NetworkItem) parentListItem);
    }

    @Override
    public void onBindChildViewHolder(BufferViewHolder childViewHolder, int position, Object childListItem) {
        bufferViewHolderMap.remove(childViewHolder.id);
        childViewHolder.bind(this, (Buffer) childListItem);
        bufferViewHolderMap.put(childViewHolder.id, childViewHolder);
    }

    @Override
    public void onClick(Buffer buffer) {
        if (bufferClickListener != null) {
            bufferClickListener.onClick(buffer);
        }
    }

    public void setBufferClickListener(OnBufferClickListener bufferClickListener) {
        this.bufferClickListener = bufferClickListener;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = new WeakReference<>(recyclerView);
    }

    public void selectConfig(int id) {
        QBufferViewConfig newconfig = context.client().bufferViewManager().bufferViewConfig(id);
        Parcelable state = (newconfig == config) ? saveState() : null;

        if (config != null)
            config.networkList().removeCallback(callback);
        config = newconfig;
        config.updateNetworks();
        items.clear();
        itemMap.clear();
        for (QNetwork network : config.networkList()) {
            NetworkItem networkItem = new NetworkItem(context, config, network);
            itemMap.put(network, networkItem);
            items.add(networkItem);
        }
        config.networkList().addCallback(callback);

        loadState(state);
    }

    private void loadState(@Nullable Parcelable state) {
        if (state != null) {
            RecyclerView list = recyclerView.get();
            if (list != null) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) list.getLayoutManager();
                layoutManager.onRestoreInstanceState(state);
            }
        }
    }

    @Nullable
    private Parcelable saveState() {
        RecyclerView list = recyclerView.get();
        if (list != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) list.getLayoutManager();
            return layoutManager.onSaveInstanceState();
        } else {
            return null;
        }
    }

    public void setSelection(int from, int to) {

    }

    public void setOpen(int id) {
        BufferViewHolder old = bufferViewHolderMap.get(open);
        if (old != null) old.setSelected(false);
        else bufferViewHolderMap.remove(open);
        BufferViewHolder now = bufferViewHolderMap.get(id);
        if (now != null) now.setSelected(true);
        else bufferViewHolderMap.remove(id);
        open = id;
    }
}
