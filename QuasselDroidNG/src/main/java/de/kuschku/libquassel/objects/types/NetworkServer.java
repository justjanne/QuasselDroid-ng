package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class NetworkServer {
    public final boolean UseSSL;
    public final int sslVersion;

    @NonNull
    public final String Host;
    public final int Port;
    @Nullable
    public final String Password;

    public final boolean UseProxy;
    public final int ProxyType;
    @Nullable
    public final String ProxyHost;
    public final int ProxyPort;
    @Nullable
    public final String ProxyUser;
    @Nullable
    public final String ProxyPass;

    public NetworkServer(boolean useSSL, int sslVersion, @NonNull String host, int port, @Nullable String password, boolean useProxy,
                         int proxyType, @Nullable String proxyHost, int proxyPort, @Nullable String proxyUser, @Nullable String proxyPass) {
        this.UseSSL = useSSL;
        this.sslVersion = sslVersion;
        this.Host = host;
        this.Port = port;
        this.Password = password;
        this.UseProxy = useProxy;
        this.ProxyType = proxyType;
        this.ProxyHost = proxyHost;
        this.ProxyPort = proxyPort;
        this.ProxyUser = proxyUser;
        this.ProxyPass = proxyPass;
    }

    @NonNull
    @Override
    public String toString() {
        return "NetworkServer{" +
                "UseSSL=" + UseSSL +
                ", sslVersion=" + sslVersion +
                ", Host='" + Host + '\'' +
                ", Port=" + Port +
                ", Password='" + Password + '\'' +
                ", UseProxy=" + UseProxy +
                ", ProxyType=" + ProxyType +
                ", ProxyHost='" + ProxyHost + '\'' +
                ", ProxyPort=" + ProxyPort +
                ", ProxyUser='" + ProxyUser + '\'' +
                ", ProxyPass='" + ProxyPass + '\'' +
                '}';
    }
}
