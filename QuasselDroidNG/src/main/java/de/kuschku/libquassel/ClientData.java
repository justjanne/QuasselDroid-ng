package de.kuschku.libquassel;

import java.util.Arrays;

public class ClientData {
    /**
     * The flags the client supports.
     */
    public final FeatureFlags flags;

    /**
     * The list of protocols supported, 0x01 is Legacy and 0x02 is Datastream.
     */
    public final int[] supportedProtocols;

    /**
     * A string identifying the client.
     */
    public final String identifier;

    /**
     * The protocol version of Quassel internally, for example 10.
     */
    public final int protocolVersion;

    public ClientData(FeatureFlags flags, int[] supportedProtocols, String identifier, int protocolVersion) {
        this.flags = flags;
        this.supportedProtocols = supportedProtocols.clone();
        this.identifier = identifier;
        this.protocolVersion = protocolVersion;
    }

    public int[] getSupportedProtocols() {
        return supportedProtocols;
    }

    @Override
    public String toString() {
        return "ClientData{" +
                "flags=" + flags +
                ", supportedProtocols=" + Arrays.toString(supportedProtocols) +
                ", identifier='" + identifier + '\'' +
                ", protocolVersion=" + protocolVersion +
                '}';
    }

    public static class FeatureFlags {
        public final boolean supportsSSL;
        public final boolean supportsCompression;
        public final byte flags;

        public FeatureFlags(final byte flags) {
            this.flags = flags;
            this.supportsSSL = (flags & 0x01) > 0;
            this.supportsCompression = (flags & 0x02) > 0;
        }

        public FeatureFlags(final boolean supportsSSL, final boolean supportsCompression) {
            this.supportsSSL = supportsSSL;
            this.supportsCompression = supportsCompression;
            this.flags = (byte) ((this.supportsSSL ? 0x01 : 0x00) |
                    (this.supportsCompression ? 0x02 : 0x00));
        }

        @Override
        public String toString() {
            return "FeatureFlags{" +
                    "supportsSSL=" + supportsSSL +
                    ", supportsCompression=" + supportsCompression +
                    '}';
        }
    }
}
