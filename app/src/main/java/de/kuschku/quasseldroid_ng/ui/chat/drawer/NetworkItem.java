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

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.irc.IrcCaseMapper;
import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.lists.ObservableSortedList;

public class NetworkItem implements ParentListItem {
    private final QNetwork network;
    private final ObservableSortedList<Buffer> buffers = new ObservableSortedList<>(Buffer.class, new ObservableSortedList.ItemComparator<Buffer>() {
        @Override
        public int compare(Buffer o1, Buffer o2) {
            if (o1.getInfo().type() == o2.getInfo().type()) {
                return IrcCaseMapper.toLowerCase(o1.getName()).compareTo(IrcCaseMapper.toLowerCase(o2.getName()));
            } else {
                if (o1.getInfo().type() == BufferInfo.Type.STATUS)
                    return -1;
                else if (o2.getInfo().type() == BufferInfo.Type.STATUS)
                    return 1;
                else if (o1.getInfo().type() == BufferInfo.Type.CHANNEL)
                    return -1;
                else if (o2.getInfo().type() == BufferInfo.Type.CHANNEL)
                    return 1;
                else if (o1.getInfo().type() == BufferInfo.Type.GROUP)
                    return -1;
                else if (o2.getInfo().type() == BufferInfo.Type.GROUP)
                    return 1;
                else
                    return -1;
            }
        }

        @Override
        public boolean areContentsTheSame(Buffer oldItem, Buffer newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areItemsTheSame(Buffer item1, Buffer item2) {
            return item1.getInfo().id() == item2.getInfo().id();
        }
    });

    public NetworkItem(AppContext context, QBufferViewConfig config, QNetwork network) {
        this.network = network;
        for (int id : config.bufferList()) {
            Buffer buffer = context.client().bufferManager().buffer(id);
            if (buffer != null && buffer.getInfo().networkId() == network.networkId())
                buffers.add(buffer);
        }
        config.bufferIds().addCallback(new ElementCallback<Integer>() {
            @Override
            public void notifyItemInserted(Integer id) {
                Buffer buffer = context.client().bufferManager().buffer(id);
                if (buffer != null && buffer.getInfo().networkId() == network.networkId())
                    buffers.add(buffer);
            }

            @Override
            public void notifyItemRemoved(Integer id) {
                buffers.remove(context.client().bufferManager().buffer(id));
            }

            @Override
            public void notifyItemChanged(Integer id) {
                buffers.notifyItemChanged(buffers.indexOf(context.client().bufferManager().buffer(id)));
            }
        });
    }

    @Override
    public List<?> getChildItemList() {
        return buffers;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return network.isConnected();
    }

    public QNetwork getNetwork() {
        return network;
    }

    @Override
    public String toString() {
        return String.valueOf(network);
    }
}
