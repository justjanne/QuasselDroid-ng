package de.kuschku.libquassel.localtypes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.Network;

public class StatusBuffer implements Buffer {
    @NonNull
    private final BufferInfo info;
    @NonNull
    private final Network network;

    public StatusBuffer(@NonNull BufferInfo info, @NonNull Network network) {
        this.info = info;
        this.network = network;
    }

    @NonNull
    @Override
    public BufferInfo getInfo() {
        return info;
    }

    @Nullable
    @Override
    public String getName() {
        return network.getNetworkName();
    }

    @NonNull
    @Override
    public String toString() {
        return "StatusBuffer{" +
                "info=" + info +
                ", network=" + network +
                '}';
    }
}
