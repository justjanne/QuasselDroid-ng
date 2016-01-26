package de.kuschku.libquassel.primitives.types;

import android.support.annotation.NonNull;

import de.kuschku.libquassel.ClientData;

public class Protocol {
    @NonNull
    public final ClientData.FeatureFlags protocolFlags;
    public final short protocolData;
    public final byte protocolVersion;

    public Protocol(@NonNull ClientData.FeatureFlags protocolFlags, short protocolData, byte protocolVersion) {
        this.protocolFlags = protocolFlags;
        this.protocolData = protocolData;
        this.protocolVersion = protocolVersion;
    }

    @NonNull
    @Override
    public String toString() {
        return "Protocol{" +
                "protocolFlags=" + protocolFlags +
                ", protocolData=" + protocolData +
                ", protocolVersion=" + protocolVersion +
                '}';
    }
}
