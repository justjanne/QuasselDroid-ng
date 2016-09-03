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

package de.kuschku.libquassel.syncables.types.abstracts;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.libquassel.syncables.types.impl.NetworkInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;

public abstract class ANetwork extends SyncableObject<QNetwork> implements QNetwork {
    @Override
    public void setNetworkName(String networkName) {
        _setNetworkName(networkName);
        syncVar("setNetworkName", networkName);
    }

    @Override
    public void setCurrentServer(String currentServer) {
        _setCurrentServer(currentServer);
        syncVar("setCurrentServer", currentServer);
    }

    @Override
    public void setConnected(boolean isConnected) {
        _setConnected(isConnected);
        syncVar("setConnected", isConnected);
    }

    @Override
    public void setConnectionState(int state) {
        _setConnectionState(state);
        syncVar("setConnectionState", state);
    }

    @Override
    public void setMyNick(String mynick) {
        _setMyNick(mynick);
        syncVar("setMyNick", mynick);
    }

    @Override
    public void setLatency(int latency) {
        _setLatency(latency);
        syncVar("setLatency", latency);
    }

    @Override
    public void setIdentity(int identityId) {
        _setIdentity(identityId);
        syncVar("setIdentity", identityId);
    }

    @Override
    public void setServerList(List<NetworkServer> serverList) {
        _setServerList(serverList);
        syncVar("setServerList", serverList);
    }

    @Override
    public void setUseRandomServer(boolean useRandomServer) {
        _setUseRandomServer(useRandomServer);
        syncVar("setUseRandomServer", useRandomServer);
    }

    @Override
    public void setPerform(List<String> performs) {
        _setPerform(performs);
        syncVar("setPerform", performs);
    }

    @Override
    public void setUseAutoIdentify(boolean useAutoIdentify) {
        _setUseAutoIdentify(useAutoIdentify);
        syncVar("setUseAutoIdentify", useAutoIdentify);
    }

    @Override
    public void setAutoIdentifyService(String autoIdentifyService) {
        _setAutoIdentifyService(autoIdentifyService);
        syncVar("setAutoIdentifyService", autoIdentifyService);
    }

    @Override
    public void setAutoIdentifyPassword(String autoIdentifyPassword) {
        _setAutoIdentifyPassword(autoIdentifyPassword);
        syncVar("setAutoIdentifyPassword", autoIdentifyPassword);
    }

    @Override
    public void setUseSasl(boolean useSasl) {
        _setUseSasl(useSasl);
        syncVar("setUseSasl", useSasl);
    }

    @Override
    public void setSaslAccount(String saslAccount) {
        _setSaslAccount(saslAccount);
        syncVar("setSaslAccount", saslAccount);
    }

    @Override
    public void setSaslPassword(String saslPassword) {
        _setSaslPassword(saslPassword);
        syncVar("setSaslPassword", saslPassword);
    }

    @Override
    public void setUseAutoReconnect(boolean useAutoReconnect) {
        _setUseAutoReconnect(useAutoReconnect);
        syncVar("setUseAutoReconnect", useAutoReconnect);
    }

    @Override
    public void setAutoReconnectInterval(int autoReconnectInterval) {
        _setAutoReconnectInterval(autoReconnectInterval);
        syncVar("setAutoReconnectInterval", autoReconnectInterval);
    }

    @Override
    public void setAutoReconnectRetries(short autoReconnectRetries) {
        _setAutoReconnectRetries(autoReconnectRetries);
        syncVar("setAutoReconnectRetries", autoReconnectRetries);
    }

    @Override
    public void setUnlimitedReconnectRetries(boolean unlimitedReconnectRetries) {
        _setUnlimitedReconnectRetries(unlimitedReconnectRetries);
        syncVar("setUnlimitedReconnectRetries", unlimitedReconnectRetries);
    }

    @Override
    public void setRejoinChannels(boolean rejoinChannels) {
        _setRejoinChannels(rejoinChannels);
        syncVar("setRejoinChannels", rejoinChannels);
    }

    @Override
    public void setCodecForServer(String codecName) {
        _setCodecForServer(codecName);
        syncVar("setCodecForServer", codecName);
    }

    @Override
    public void setCodecForEncoding(String codecName) {
        _setCodecForEncoding(codecName);
        syncVar("setCodecForEncoding", codecName);
    }

    @Override
    public void setCodecForDecoding(String codecName) {
        _setCodecForDecoding(codecName);
        syncVar("setCodecForDecoding", codecName);
    }

    @Override
    public void addSupport(String param) {
        _addSupport(param);
        syncVar("addSupport", param);
    }

    @Override
    public void addSupport(String param, String value) {
        _addSupport(value);
        syncVar("addSupport", value);
    }

    @Override
    public void removeSupport(String param) {
        _removeSupport(param);
        syncVar("removeSupport", param);
    }

    @Override
    public void addIrcUser(String hostmask) {
        _addIrcUser(hostmask);
        syncVar("addIrcUser", hostmask);
    }

    @Override
    public void addIrcChannel(String channel) {
        _addIrcChannel(channel);
        syncVar("addIrcChannel", channel);
    }

    @Override
    public void ircUserNickChanged(String oldnick, String newnick) {
        _ircUserNickChanged(oldnick, newnick);
        syncVar("ircUserNickChanged", newnick);
    }

    @Override
    public void connect() {
        _connect();
        syncVar("connect");
    }

    @Override
    public void disconnect() {
        _disconnect();
        syncVar("disconnect");
    }

    @Override
    public void setNetworkInfo(NetworkInfo info) {
        syncVar("requestSetNetworkInfo", info);
    }

    @Override
    public QIrcUser updateNickFromMask(String mask) {
        QIrcUser result = _updateNickFromMask(mask);
        syncVar("updateNickFromMask", mask);
        return result;
    }

    @Override
    public void setAutoAwayActive(boolean active) {
        _setAutoAwayActive(active);
        syncVar("setAutoAwayActive", active);
    }

    @Override
    public void removeChansAndUsers() {
        _removeChansAndUsers();
        syncVar("removeChansAndUsers");
    }

    @Override
    public void removeIrcChannel(@NonNull QIrcChannel ircChannel) {
        _removeIrcChannel(ircChannel);
        syncVar("removeIrcChannel", ircChannel.name());
    }

    @Override
    public void removeIrcUser(@NonNull QIrcUser ircuser) {
        _removeIrcUser(ircuser);
        syncVar("removeIrcUser", ircuser.nick());
    }
}
