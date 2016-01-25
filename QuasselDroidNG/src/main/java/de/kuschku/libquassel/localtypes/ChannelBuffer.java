package de.kuschku.libquassel.localtypes;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.IrcChannel;

public class ChannelBuffer implements Buffer {
    private final BufferInfo info;
    private IrcChannel channel;

    public ChannelBuffer(BufferInfo info, IrcChannel channel) {
        this.info = info;
        this.channel = channel;
    }

    @Override
    public BufferInfo getInfo() {
        return info;
    }

    @Override
    public String getName() {
        return getInfo().name;
    }

    public IrcChannel getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "ChannelBuffer{" +
                "info=" + info +
                ", channel=" + channel +
                '}';
    }
}
