package de.kuschku.quasseldroid_ng.ui.chat.drawer;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.syncables.types.BufferViewConfig;
import de.kuschku.libquassel.syncables.types.Network;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.observables.callbacks.wrappers.ChildUICallbackWrapper;
import de.kuschku.util.observables.callbacks.wrappers.MultiUIChildCallback;
import de.kuschku.util.observables.ContentComparable;
import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.callbacks.UIChildCallback;
import de.kuschku.util.observables.lists.ObservableSortedList;
import de.kuschku.util.ui.Bindable;

@UiThread
public class NetworkWrapper implements ParentListItem, Nameable<NetworkWrapper>,ContentComparable<NetworkWrapper> {
    @NonNull
    private final MultiUIChildCallback callback = MultiUIChildCallback.of();

    @NonNull
    private final Network network;
    @NonNull
    private final Client client;

    @NonNull
    private ObservableSortedList<BufferWrapper> buffers = new ObservableSortedList<>(BufferWrapper.class, new BufferWrapperSorterWrapper(getSorter(SortMode.NONE)));
    @NonNull
    private final ChildUICallbackWrapper wrapper = new ChildUICallbackWrapper(callback);
    private int groupId;

    public NetworkWrapper(@NonNull Network network, @NonNull Client client, @NonNull BufferViewConfig bufferViewConfig, @NonNull SortMode sortMode, int groupId) {
        this.network = network;
        this.client = client;
        setSortMode(sortMode);
        bufferViewConfig.getBufferList().addCallback(new ElementCallback<Integer>() {
            @Override
            public void notifyItemInserted(Integer element) {
                Buffer buffer = client.getBuffer(element);
                if (buffer.getInfo().networkId != network.getNetworkId()) return;

                buffers.add(new BufferWrapper(buffer));
            }

            @Override
            public void notifyItemRemoved(Integer element) {
                Buffer buffer = client.getBuffer(element);
                if (buffer.getInfo().networkId != network.getNetworkId()) return;

                buffers.remove(new BufferWrapper(buffer));
            }

            @Override
            public void notifyItemChanged(Integer element) {
                Buffer buffer = client.getBuffer(element);
                if (buffer.getInfo().networkId != network.getNetworkId()) return;

                callback.notifyChildItemChanged(groupId, buffers.indexOf(new BufferWrapper(buffer)));
            }
        });
        buffers.addCallback(wrapper);
        wrapper.setGroupPosition(groupId);
    }

    public void addCallback(@NonNull UIChildCallback callback) {
        this.callback.addCallback(callback);
    }

    public void removeCallback(@NonNull UIChildCallback callback) {
        this.callback.removeCallback(callback);
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
        this.wrapper.setGroupPosition(groupId);
    }

    public void setSortMode(@NonNull SortMode sortMode) {
        ObservableSortedList.ItemComparator<BufferWrapper> sorter = new BufferWrapperSorterWrapper(getSorter(sortMode));
        ObservableSortedList<BufferWrapper> newBuffers = new ObservableSortedList<>(BufferWrapper.class, sorter);
        if (buffers.size() > newBuffers.size()) {
            for (int i = newBuffers.size(); i < buffers.size(); i++) {
                callback.notifyChildItemRemoved(groupId, i);
            }
        } else if (newBuffers.size() > buffers.size()) {
            for (int i = buffers.size(); i < newBuffers.size(); i++) {
                callback.notifyChildItemInserted(groupId, i);
            }
        }

        int commonElementCount = Math.min(buffers.size(), newBuffers.size());
        for (int i = 0; i < commonElementCount; i++) {
            callback.notifyChildItemChanged(groupId, i);
        }
        buffers = newBuffers;
    }

    @NonNull
    private ObservableSortedList.ItemComparator<Buffer> getSorter(@NonNull SortMode sortMode) {
        switch (sortMode) {
            case ALPHABETICAL:
                return new AlphabeticalBufferSorter();
            case RECENT_ACTIVITY:
                return new RecentActivityBufferSorter();
            case LAST_SEEN:
                return new LastSeenBufferSorter();
            default:
                return new IdBufferSorter();
        }
    }

    @NonNull
    @Override
    public List<?> getChildItemList() {
        return buffers;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return network.isConnected();
    }

    @NonNull
    @Override
    public NetworkWrapper withName(String name) {
        return this;
    }

    @NonNull
    @Override
    public NetworkWrapper withName(int nameRes) {
        return this;
    }

    @NonNull
    @Override
    public NetworkWrapper withName(StringHolder name) {
        return this;
    }

    @NonNull
    @Override
    public StringHolder getName() {
        return new StringHolder(network.getNetworkName());
    }

    @Override
    public boolean equalsContent(@NonNull NetworkWrapper other) {
        return network.equalsContent(other.network);
    }

    @Override
    public int compareTo(@NonNull NetworkWrapper another) {
        return network.compareTo(another.network);
    }

    public static class ViewHolder extends ParentViewHolder implements Bindable<NetworkWrapper> {
        @Bind(R.id.material_drawer_icon)
        ImageView materialDrawerIcon;
        @Bind(R.id.material_drawer_name)
        TextView materialDrawerName;
        @Bind(R.id.material_drawer_description)
        TextView materialDrawerDescription;
        @Bind(R.id.material_drawer_badge_container)
        View materialDrawerBadgeContainer;
        @Bind(R.id.material_drawer_badge)
        TextView materialDrawerBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(@NonNull NetworkWrapper wrapper) {
            materialDrawerName.setText(wrapper.getName().getText());
        }
    }

    public enum SortMode {
        NONE,
        ALPHABETICAL,
        RECENT_ACTIVITY,
        LAST_SEEN
    }

    private static class BufferWrapperSorterWrapper implements ObservableSortedList.ItemComparator<BufferWrapper> {
        @NonNull
        private final ObservableSortedList.ItemComparator<Buffer> wrapped;

        public BufferWrapperSorterWrapper(@NonNull ObservableSortedList.ItemComparator<Buffer> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public int compare(@NonNull BufferWrapper lhs, @NonNull BufferWrapper rhs) {
            return wrapped.compare(lhs.getBuffer(), rhs.getBuffer());
        }

        @Override
        public boolean areContentsTheSame(@NonNull BufferWrapper oldItem, @NonNull BufferWrapper newItem) {
            return wrapped.areContentsTheSame(oldItem.getBuffer(), newItem.getBuffer());
        }

        @Override
        public boolean areItemsTheSame(@NonNull BufferWrapper item1, @NonNull BufferWrapper item2) {
            return areContentsTheSame(item1, item2);
        }
    }

    private class IdBufferSorter extends BasicBufferSorter {
        @Override
        public int compare(@NonNull Buffer lhs, @NonNull Buffer rhs) {
            return lhs.getInfo().id - rhs.getInfo().id;
        }
    }

    private class AlphabeticalBufferSorter extends BasicBufferSorter {
        @Override
        public int compare(@NonNull Buffer lhs, @NonNull Buffer rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    private class RecentActivityBufferSorter extends BasicBufferSorter {
        @Override
        public int compare(@NonNull Buffer lhs, @NonNull Buffer rhs) {
            return getLastMessageId(lhs) - getLastMessageId(rhs);
        }

        private int getLastMessageId(@NonNull Buffer buffer) {
            int bufferId = buffer.getInfo().id;
            Message message = client.getBacklogManager().get(bufferId).last();
            return message == null ? -1 : message.messageId;
        }
    }

    private class LastSeenBufferSorter extends BasicBufferSorter {
        @Override
        public int compare(@NonNull Buffer lhs, @NonNull Buffer rhs) {
            return getLastSeenMessageId(lhs) - getLastSeenMessageId(rhs);
        }

        private int getLastSeenMessageId(@NonNull Buffer buffer) {
            int bufferId = buffer.getInfo().id;
            return client.getBufferSyncer().getLastSeenMsg(bufferId);
        }
    }

    private abstract class BasicBufferSorter implements ObservableSortedList.ItemComparator<Buffer> {
        @Override
        public boolean areContentsTheSame(@NonNull Buffer oldItem, @NonNull Buffer newItem) {
            return oldItem.getInfo().id == newItem.getInfo().id;
        }

        @Override
        public boolean areItemsTheSame(@NonNull Buffer item1, @NonNull Buffer item2) {
            return areContentsTheSame(item1, item2);
        }
    }
}
