package de.kuschku.libquassel.objects.types;

public class NetworkServer {
    public final boolean UseSSL;
    public final int sslVersion;

    public final String Host;
    public final int Port;
    public final String Password;

    public final boolean UseProxy;
    public final int ProxyType;
    public final String ProxyHost;
    public final int ProxyPort;
    public final String ProxyUser;
    public final String ProxyPass;

    public NetworkServer(boolean useSSL, int sslVersion, String host, int port, String password, boolean useProxy,
                         int proxyType, String proxyHost, int proxyPort, String proxyUser, String proxyPass) {
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
