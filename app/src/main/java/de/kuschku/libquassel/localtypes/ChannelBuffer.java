package de.kuschku.libquassel.localtypes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.IrcChannel;

public class ChannelBuffer implements Buffer {
    @NonNull
    private final BufferInfo info;
    @Nullable
    private final IrcChannel channel;

    public ChannelBuffer(@NonNull BufferInfo info, @Nullable IrcChannel channel) {
        this.info = info;
        this.channel = channel;
    }

    @NonNull
    @Override
    public BufferInfo getInfo() {
        return info;
    }

    @Nullable
    @Override
    public String getName() {
        return getInfo().name;
    }

    @NonNull
    @Override
    public BufferInfo.BufferStatus getStatus() {
        return channel == null ? BufferInfo.BufferStatus.OFFLINE : BufferInfo.BufferStatus.ONLINE;
    }

    @Nullable
    public IrcChannel getChannel() {
        return channel;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChannelBuffer{" +
                "info=" + info +
                ", channel=" + channel +
                '}';
    }
}
