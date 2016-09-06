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

package de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.objects.types.NetworkServer;

public class NetworkServerSerializeHelper {
    public static Bundle[] serialize(List<NetworkServer> servers) {
        Bundle[] list = new Bundle[servers.size()];
        for (int i = 0; i < servers.size(); i++) {
            NetworkServer server = servers.get(i);
            Bundle bundle = serialize(server);
            list[i] = bundle;
        }
        return list;
    }

    @NonNull
    public static Bundle serialize(NetworkServer server) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("useSSL", server.useSSL);
        bundle.putInt("sslVersion", server.sslVersion);
        bundle.putString("host", server.host);
        bundle.putInt("port", server.port);
        bundle.putString("password", server.password);
        bundle.putBoolean("useProxy", server.useProxy);
        bundle.putInt("proxyType", server.proxyType.id);
        bundle.putString("proxyHost", server.proxyHost);
        bundle.putInt("proxyPort", server.proxyPort);
        bundle.putString("proxyUser", server.proxyUser);
        bundle.putString("proxyPass", server.proxyPass);
        return bundle;
    }

    @NonNull
    public static List<NetworkServer> deserialize(Parcelable[] serverList) {
        List<NetworkServer> servers = new ArrayList<>(serverList.length);
        for (Parcelable parcelable : serverList) {
            Bundle bundle = (Bundle) parcelable;
            servers.add(deserialize(bundle));
        }
        return servers;
    }

    @NonNull
    public static NetworkServer deserialize(Bundle bundle) {
        return new NetworkServer(
                bundle.getBoolean("useSSL"),
                bundle.getInt("sslVersion"),
                bundle.getString("host"),
                bundle.getInt("port"),
                bundle.getString("password"),
                bundle.getBoolean("useProxy"),
                NetworkServer.ProxyType.fromId(bundle.getInt("proxyType")),
                bundle.getString("proxyHost"),
                bundle.getInt("proxyPort"),
                bundle.getString("proxyUser"),
                bundle.getString("proxyPass")
        );
    }
}
