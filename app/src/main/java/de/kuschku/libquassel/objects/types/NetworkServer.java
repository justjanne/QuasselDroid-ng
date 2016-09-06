/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class NetworkServer {
    public final boolean useSSL;
    public final int sslVersion;

    @NonNull
    public final String host;
    public final int port;
    @Nullable
    public final String password;

    public final boolean useProxy;
    public final ProxyType proxyType;
    @Nullable
    public final String proxyHost;
    public final int proxyPort;
    @Nullable
    public final String proxyUser;
    @Nullable
    public final String proxyPass;

    public NetworkServer(boolean useSSL, int sslVersion, @NonNull String host, int port, @Nullable String password, boolean useProxy,
                         ProxyType proxyType, @Nullable String proxyHost, int proxyPort, @Nullable String proxyUser, @Nullable String proxyPass) {
        this.useSSL = useSSL;
        this.sslVersion = sslVersion;
        this.host = host;
        this.port = port;
        this.password = password;
        this.useProxy = useProxy;
        this.proxyType = proxyType;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxyUser = proxyUser;
        this.proxyPass = proxyPass;
    }

    @NonNull
    @Override
    public String toString() {
        return "NetworkServer{" +
                "UseSSL=" + useSSL +
                ", sslVersion=" + sslVersion +
                ", Host='" + host + '\'' +
                ", Port=" + port +
                ", Password='" + password + '\'' +
                ", UseProxy=" + useProxy +
                ", ProxyType=" + proxyType +
                ", ProxyHost='" + proxyHost + '\'' +
                ", ProxyPort=" + proxyPort +
                ", ProxyUser='" + proxyUser + '\'' +
                ", ProxyPass='" + proxyPass + '\'' +
                '}';
    }

    public enum ProxyType {
        DefaultProxy(0),
        Socks5Proxy(1),
        HttpProxy(3);

        public final int id;

        ProxyType(int id) {
            this.id = id;
        }

        public static ProxyType fromId(int id) {
            switch (id) {
                default:
                case 0:
                    return DefaultProxy;
                case 1:
                    return Socks5Proxy;
                case 3:
                    return HttpProxy;
            }
        }
    }
}
