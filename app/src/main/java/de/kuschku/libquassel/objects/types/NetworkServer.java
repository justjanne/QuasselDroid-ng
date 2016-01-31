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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

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
