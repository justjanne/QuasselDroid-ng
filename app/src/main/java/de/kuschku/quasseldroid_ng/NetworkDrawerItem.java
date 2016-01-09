package de.kuschku.quasseldroid_ng;

import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.List;
import java.util.Set;

import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.libquassel.syncables.types.Network;
import de.kuschku.util.Stream;

public class NetworkDrawerItem extends PrimaryDrawerItem {
    final Network network;
    final Set<Buffer> buffers;

    public NetworkDrawerItem(Network network, Set<Buffer> buffers) {
        this.network = network;
        this.buffers = buffers;
    }

    @Override
    public List<IDrawerItem> getSubItems() {
        return new Stream<>(buffers).map(Buffer::getDrawerElement).list();
    }

    @Override
    public StringHolder getName() {
        return new StringHolder(network.getNetworkName());
    }

    @Override
    public int getIdentifier() {
        return network.getNetworkId() * Short.MAX_VALUE;
    }
}
