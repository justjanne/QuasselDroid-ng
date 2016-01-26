package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;

public class ClientInit {
    @NonNull
    public final String ClientDate;
    public final boolean UseSsl;
    @NonNull
    public final String ClientVersion;
    public final boolean UseCompression;
    public final int ProtocolVersion;

    public ClientInit(@NonNull String clientDate, boolean useSsl, @NonNull String clientVersion, boolean useCompression,
                      int protocolVersion) {
        ClientDate = clientDate;
        UseSsl = useSsl;
        ClientVersion = clientVersion;
        UseCompression = useCompression;
        ProtocolVersion = protocolVersion;
    }

    @NonNull
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
