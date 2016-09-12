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
import de.kuschku.util.observables.lists.ObservableElement;
import de.kuschku.util.observables.lists.ObservableSortedList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class BufferViewConfigAdapter extends ExpandableRecyclerAdapter<NetworkViewHolder, BufferViewHolder> implements OnBufferClickListener, OnBufferLongClickListener {
    private final AppContext context;
    private final ObservableSortedList<NetworkItem> items;
    private final Map<QNetwork, NetworkItem> itemMap = new WeakHashMap<>();
    private final Map<Integer, BufferViewHolder> bufferViewHolderMap = new WeakHashMap<>();
    private final ObservableElement<Boolean> showAll = new ObservableElement<>(false);
    private QBufferViewConfig config;
    private final ElementCallback<QNetwork> callback = new ElementCallback<QNetwork>() {
        @Override
        public void notifyItemInserted(QNetwork network) {
            NetworkItem networkItem = new NetworkItem(context, config, network, BufferViewConfigAdapter.this);
            itemMap.put(network, networkItem);
            items.add(networkItem);
        }

        @Override
        public void notifyItemRemoved(QNetwork network) {
            items.remove(itemMap.remove(network));
        }

        @Override
        public void notifyItemChanged(QNetwork network) {
            if (items.contains(itemMap.get(network)))
                items.notifyItemChanged(items.indexOf(itemMap.get(network)));
        }
    };
    private WeakReference<RecyclerView> recyclerView = new WeakReference<>(null);
    private int open;
    private OnBufferClickListener bufferClickListener;
    private ActionModeHandler actionModeHandler;

    private BufferViewConfigAdapter(AppContext context, ObservableSortedList<NetworkItem> items) {
        super(items);
        this.context = context;
        this.items = items;
        items.addCallback(new UICallback() {
            @Override
            public void notifyItemInserted(int position) {
                notifyParentItemInserted(position);
                if (items.get(position).isInitiallyExpanded())
                    expandParent(position);
                else
                    collapseParent(position);
            }

            @Override
            public void notifyItemChanged(int position) {
                notifyParentItemChanged(position);
                if (items.get(position).isInitiallyExpanded())
                    expandParent(position);
                else
                    collapseParent(position);
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
                for (int i = position; i < position + count; i++) {
                    this.notifyItemInserted(i);
                }
            }

            @Override
            public void notifyItemRangeChanged(int position, int count) {
                for (int i = position; i < position + count; i++) {
                    this.notifyItemChanged(i);
                }
            }

            @Override
            public void notifyItemRangeRemoved(int position, int count) {
                for (int i = position; i < position + count; i++) {
                    this.notifyItemRemoved(position);
                }
            }
        });
    }

    public static BufferViewConfigAdapter of(AppContext context) {
        final ObservableSortedList<NetworkItem> networkItems = new ObservableSortedList<>(NetworkItem.class, new ObservableSortedList.ItemComparator<NetworkItem>() {
            @Override
            public int compare(NetworkItem o1, NetworkItem o2) {
                assertNotNull(o1);
                assertNotNull(o2);

                QNetwork network1 = o1.getNetwork();
                QNetwork network2 = o2.getNetwork();

                if (network1 == null && network2 == null) {
                    return 0;
                } else if (network1 == null) {
                    return 1;
                } else if (network2 == null) {
                    return -1;
                } else {
                    String name1 = network1.networkName();
                    String name2 = network2.networkName();

                    if (name1 == null && name2 == null) {
                        return 0;
                    } else if (name1 == null) {
                        return 1;
                    } else if (name2 == null) {
                        return -1;
                    } else {
                        return name1.compareToIgnoreCase(name2);
                    }
                }
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

    public void notifyChildItemInserted(NetworkItem parentItem, int childPosition) {
        super.notifyChildItemInserted(items.indexOf(parentItem), childPosition);
    }

    public void notifyChildItemRemoved(NetworkItem parentItem, int childPosition) {
        super.notifyChildItemRemoved(items.indexOf(parentItem), childPosition);
    }

    public void notifyChildItemChanged(NetworkItem parentItem, int childPosition) {
        super.notifyChildItemChanged(items.indexOf(parentItem), childPosition);
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
        Buffer buffer = (Buffer) childListItem;
        childViewHolder.bind(this, this, buffer);
        bufferViewHolderMap.put(childViewHolder.id, childViewHolder);
        childViewHolder.setSelected(context.client().backlogManager().open() == childViewHolder.id);
        childViewHolder.setChecked(actionModeHandler.isChecked(buffer));
    }

    @Override
    public void onClick(Buffer buffer) {
        if (actionModeHandler.isActive()) {
            actionModeHandler.toggle(buffer);
            bufferViewHolderMap.get(buffer.getInfo().id).setChecked(actionModeHandler.isChecked(buffer));
        } else {
            if (bufferClickListener != null) {
                bufferClickListener.onClick(buffer);
            }
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
        items.clear();
        itemMap.clear();
        if (config != null) {
            for (QNetwork network : config.networkList()) {
                NetworkItem networkItem = new NetworkItem(context, config, network, this);
                itemMap.put(network, networkItem);
                items.add(networkItem);
            }
            config.networkList().addCallback(callback);
        }

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

    public void setOpen(int id) {
        BufferViewHolder old = bufferViewHolderMap.get(open);
        if (old != null) old.setSelected(false);
        else bufferViewHolderMap.remove(open);
        BufferViewHolder now = bufferViewHolderMap.get(id);
        if (now != null) now.setSelected(true);
        else bufferViewHolderMap.remove(id);
        open = id;
    }

    public int indexOf(int bufferViewConfigId) {
        for (int i = 0; i < context.client().bufferViewManager().bufferViewConfigs().size(); i++) {
            if (context.client().bufferViewManager().bufferViewConfigs().get(i).bufferViewId() == bufferViewConfigId)
                return i;
        }
        return -1;
    }

    public ObservableElement<Boolean> showAll() {
        return showAll;
    }

    public boolean toggleShowAll() {
        boolean before = showAll.get();
        showAll.set(!before);
        return !before;
    }

    @Override
    public boolean onLongClick(Buffer buffer) {
        if (!actionModeHandler.isActive())
            actionModeHandler.start();

        actionModeHandler.toggle(buffer);
        BufferViewHolder bufferViewHolder = bufferViewHolderMap.get(buffer.getInfo().id);
        if (bufferViewHolder != null)
            bufferViewHolder.setChecked(actionModeHandler.isChecked(buffer));
        return false;
    }

    public void setActionModeHandler(ActionModeHandler actionModeHandler) {
        this.actionModeHandler = actionModeHandler;
    }
}
