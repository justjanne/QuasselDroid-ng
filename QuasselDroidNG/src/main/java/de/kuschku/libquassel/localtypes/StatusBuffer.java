package de.kuschku.libquassel.localtypes;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.Network;

public class StatusBuffer implements Buffer {
    private final BufferInfo info;
    private final Network network;

    public StatusBuffer(BufferInfo info, Network network) {
        this.info = info;
        this.network = network;
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
