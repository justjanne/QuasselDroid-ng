package de.kuschku.libquassel.objects.types;

public class ClientInit {
    public final String ClientDate;
    public final boolean UseSsl;
    public final String ClientVersion;
    public final boolean UseCompression;
    public final int ProtocolVersion;

    public ClientInit(String clientDate, boolean useSsl, String clientVersion, boolean useCompression,
                      int protocolVersion) {
        ClientDate = clientDate;
        UseSsl = useSsl;
        ClientVersion = clientVersion;
        UseCompression = useCompression;
        ProtocolVersion = protocolVersion;
    }

    @Override
    public String toString() {
        return "ClientInit{" +
                "ClientDate='" + ClientDate + '\'' +
                ", UseSsl=" + UseSsl +
                ", ClientVersion='" + ClientVersion + '\'' +
                ", UseCompression=" + UseCompression +
                ", ProtocolVersion=" + ProtocolVersion +
                '}';
    }
}
