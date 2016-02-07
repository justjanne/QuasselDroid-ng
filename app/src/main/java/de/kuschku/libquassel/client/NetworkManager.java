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

package de.kuschku.libquassel.client;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import de.kuschku.libquassel.syncables.types.impl.Network;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.util.observables.lists.ObservableSet;

public class NetworkManager extends Observable {
    @NonNull
    private final Map<Integer, QNetwork> networks = new HashMap<>();
    private final ObservableSet<QNetwork> list = new ObservableSet<>();
    private final Client client;

    public NetworkManager(Client client) {
        this.client = client;
    }

    public void createNetwork(@IntRange(from = 0) int networkId) {
        createNetwork(Network.create(networkId));
    }

    public void createNetwork(@NonNull QNetwork network) {
        list.remove(networks.get(network.networkId()));
        networks.put(network.networkId(), network);
        list.add(network);
    }

    public QNetwork network(@IntRange(from = 0) int networkId) {
        return networks.get(networkId);
    }

    public void removeNetwork(@IntRange(from = 0) int network) {
        list.remove(networks.get(network));
        networks.remove(network);
    }


    public void init(@NonNull List<Integer> networkIds) {
        for (int networkId : networkIds) {
            createNetwork(networkId);
            client.requestInitObject("Network", String.valueOf(networkId));
        }
    }

    public ObservableSet<QNetwork> list() {
        return list;
    }

    public List<QNetwork> networks() {
        return new ArrayList<>(networks.values());
    }
}
