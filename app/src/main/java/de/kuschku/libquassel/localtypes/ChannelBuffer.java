package de.kuschku.libquassel.localtypes;

import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.IrcChannel;
import de.kuschku.quasseldroid_ng.ui.BufferDrawerItem;

public class ChannelBuffer implements Buffer {
    private final BufferInfo info;
    private IrcChannel channel;
    private IDrawerItem drawerElement = new BufferDrawerItem(this);

    public ChannelBuffer(BufferInfo info, IrcChannel channel) {
        this.info = info;
        this.channel = channel;
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
