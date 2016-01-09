package de.kuschku.libquassel.primitives.types;

import de.kuschku.libquassel.ClientData;

public class Protocol {
    public final ClientData.FeatureFlags protocolFlags;
    public final short protocolData;
    public final byte protocolVersion;

    public Protocol(ClientData.FeatureFlags protocolFlags, short protocolData, byte protocolVersion) {
        this.protocolFlags = protocolFlags;
        this.protocolData = protocolData;
        this.protocolVersion = protocolVersion;
    }

    @Override
    public String toString() {
        return "Protocol{" +
                "protocolFlags=" + protocolFlags +
                ", protocolData=" + protocolData +
                ", protocolVersion=" + protocolVersion +
                '}';
    }
}
