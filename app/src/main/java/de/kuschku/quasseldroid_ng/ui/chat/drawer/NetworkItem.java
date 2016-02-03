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
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.observables.IObservable;
import de.kuschku.util.observables.callbacks.GeneralCallback;
import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.callbacks.wrappers.GeneralCallbackWrapper;
import de.kuschku.util.observables.lists.ObservableSortedList;

import static de.kuschku.libquassel.primitives.types.BufferInfo.Type.CHANNEL;
import static de.kuschku.libquassel.primitives.types.BufferInfo.Type.GROUP;
import static de.kuschku.libquassel.primitives.types.BufferInfo.Type.QUERY;
import static de.kuschku.libquassel.primitives.types.BufferInfo.Type.STATUS;

public class NetworkItem extends PrimaryDrawerItem implements IObservable<GeneralCallback>, GeneralCallback {
    @NonNull
    private final QNetwork network;
    @NonNull
    private final ObservableSortedList<BufferItem> buffers = new ObservableSortedList<>(BufferItem.class, new AlphabeticalComparator());
    @NonNull
    private final SparseArray<BufferItem> bufferIds = new SparseArray<>();
    @NonNull
    private final GeneralCallbackWrapper callback = new GeneralCallbackWrapper();

    public NetworkItem(@NonNull AppContext context, @NonNull QNetwork network, @NonNull QBufferViewConfig config) {
        this.network = network;

        for (Integer bufferId : config.bufferList()) {
            Buffer buffer = context.client().bufferManager().buffer(bufferId);
            if (buffer != null && buffer.getInfo().networkId() == network.networkId()) {
                this.buffers.add(new BufferItem(buffer, context));
            }
        }
        config.bufferList().addCallback(new UICallback() {
            @Override
            public void notifyItemInserted(int position) {
                int element = config.bufferList().get(position);
                Buffer buffer = context.client().bufferManager().buffer(element);
                if (buffer.getInfo().networkId() == network.networkId()) {
                    if (bufferIds.get(element) == null) {
                        BufferItem bufferItem = new BufferItem(buffer, context);
                        buffers.add(bufferItem);
                        bufferItem.addCallback(NetworkItem.this);
                        bufferIds.put(element, bufferItem);
                        notifyChanged();
                    }
                }
            }

            @Override
            public void notifyItemChanged(int position) {
                int element = config.bufferList().get(position);
                if (bufferIds.get(element) != null) {
                    notifyChanged();
                }
            }

            @Override
            public void notifyItemRemoved(int position) {
                int element = config.bufferList().get(position);
                if (bufferIds.get(element) != null) {
                    bufferIds.remove(element);
                    notifyChanged();
                }
            }

            @Override
            public void notifyItemMoved(int from, int to) {
                notifyItemChanged(from);
                notifyItemChanged(to);
            }

            @Override
            public void notifyItemRangeInserted(int position, int count) {
                for (int i = position; i < position + count; i++)
                    notifyItemInserted(i);
            }

            @Override
            public void notifyItemRangeChanged(int position, int count) {
                for (int i = position; i < position + count; i++)
                    notifyItemChanged(i);
            }

            @Override
            public void notifyItemRangeRemoved(int position, int count) {
                for (int i = position; i < position + count; i++)
                    notifyItemRemoved(i);
            }
        });
    }

    @NonNull
    @Override
    public StringHolder getDescription() {
        return new StringHolder(String.valueOf(network.latency()));
    }

    @Nullable
    @Override
    public StringHolder getName() {
        return new StringHolder(network.networkName());
    }

    @NonNull
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

    @NonNull
    public QNetwork getNetwork() {
        return network;
    }

    @Override
    public long getIdentifier() {
        return network.networkId();
    }

    class AlphabeticalComparator implements ObservableSortedList.ItemComparator<BufferItem> {
        @Override
        public int compare(@NonNull BufferItem o1, @NonNull BufferItem o2) {
            BufferInfo.Type type1 = o1.getBuffer().getInfo().type();
            BufferInfo.Type type2 = o2.getBuffer().getInfo().type();
            if (type1 == type2) {
                if (o1.getBuffer().getName() == null)
                    return -1;
                else if (o2.getBuffer().getName() == null)
                    return 1;
                else
                    return o1.getBuffer().getName().compareTo(o2.getBuffer().getName());
            } else {
                // Type1 is status, Type2 isn’t
                if (type1 == STATUS) return -1;
                // Type2 is status, Type1 isn’t
                if (type2 == STATUS) return 1;
                // Type1 is channel, Type2 isn’t
                if (type1 == CHANNEL) return -1;
                // Type2 is channel, Type1 isn’t
                if (type2 == CHANNEL) return 1;
                // Type1 is group, Type2 isn’t
                if (type1 == GROUP) return -1;
                // Type2 is group, Type1 isn’t
                if (type2 == GROUP) return 1;
                // Type1 is query, Type2 isn’t
                if (type1 == QUERY) return -1;
                // Type2 is query, Type1 isn’t
                if (type2 == QUERY) return 1;
                // Per default, keep order
                return -1;
            }
        }

        @Override
        public boolean areContentsTheSame(BufferItem item1, BufferItem item2) {
            return item1 == item2;
        }

        @Override
        public boolean areItemsTheSame(@NonNull BufferItem item1, @NonNull BufferItem item2) {
            return item1.getBuffer().getInfo().id() == item2.getBuffer().getInfo().id();
        }
    }

    class NoneComparator implements ObservableSortedList.ItemComparator<BufferItem> {
        @Override
        public int compare(@NonNull BufferItem o1, @NonNull BufferItem o2) {
            return o1.getBuffer().getInfo().id() - o2.getBuffer().getInfo().id();
        }

        @Override
        public boolean areContentsTheSame(BufferItem item1, BufferItem item2) {
            return item1 == item2;
        }

        @Override
        public boolean areItemsTheSame(@NonNull BufferItem item1, @NonNull BufferItem item2) {
            return item1.getBuffer().getInfo().id() == item2.getBuffer().getInfo().id();
        }
    }
}
