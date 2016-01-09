package de.kuschku.libquassel.localtypes;

import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.Network;
import de.kuschku.quasseldroid_ng.BufferDrawerItem;

public class StatusBuffer implements Buffer {
    private final BufferInfo info;
    private final Network network;
    private IDrawerItem drawerElement = new BufferDrawerItem(this);

    public StatusBuffer(BufferInfo info, Network network) {
        this.info = info;
        this.network = network;
    }

    public IDrawerItem getDrawerElement() {
        return drawerElement;
    }

    @Override
    public BufferInfo getInfo() {
        return info;
    }

    @Override
    public String getName() {
        return network.getNetworkName();
    }

    @Override
    public String toString() {
        return "StatusBuffer{" +
                "info=" + info +
                ", network=" + network +
                '}';
    }
}
