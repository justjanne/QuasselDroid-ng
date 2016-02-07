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

package de.kuschku.libquassel.syncables.types.impl;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Observable;

import de.kuschku.libquassel.objects.types.NetworkServer;

public class NetworkInfo extends Observable {
    private int networkId;
    private String networkName;
    private int identity;

    private String codecForServer;
    private String codecForEncoding;
    private String codecForDecoding;

    private List<NetworkServer> serverList;
    private boolean useRandomServer;

    private List<String> perform;

    private boolean useAutoIdentify;
    private String autoIdentifyService;
    private String autoIdentifyPassword;

    private boolean useSasl;
    private String saslAccount;
    private String saslPassword;

    private boolean useAutoReconnect;
    private int autoReconnectInterval;
    private short autoReconnectRetries;
    private boolean unlimitedReconnectRetries;
    private boolean rejoinChannels;

    public NetworkInfo(int networkId, String networkName, int identity, String codecForServer, String codecForEncoding, String codecForDecoding, List<NetworkServer> serverList, boolean useRandomServer, List<String> perform, boolean useAutoIdentify, String autoIdentifyService, String autoIdentifyPassword, boolean useSasl, String saslAccount, String saslPassword, boolean useAutoReconnect, int autoReconnectInterval, short autoReconnectRetries, boolean unlimitedReconnectRetries, boolean rejoinChannels) {
        this.networkId = networkId;
        this.networkName = networkName;
        this.identity = identity;
        this.codecForServer = codecForServer;
        this.codecForEncoding = codecForEncoding;
        this.codecForDecoding = codecForDecoding;
        this.serverList = serverList;
        this.useRandomServer = useRandomServer;
        this.perform = perform;
        this.useAutoIdentify = useAutoIdentify;
        this.autoIdentifyService = autoIdentifyService;
        this.autoIdentifyPassword = autoIdentifyPassword;
        this.useSasl = useSasl;
        this.saslAccount = saslAccount;
        this.saslPassword = saslPassword;
        this.useAutoReconnect = useAutoReconnect;
        this.autoReconnectInterval = autoReconnectInterval;
        this.autoReconnectRetries = autoReconnectRetries;
        this.unlimitedReconnectRetries = unlimitedReconnectRetries;
        this.rejoinChannels = rejoinChannels;
    }

    @NonNull
    public static NetworkInfo create(int networkId) {
        return new NetworkInfo(
                networkId,
                "<not initialized>",
                0,
                null,
                null,
                null,
                Collections.emptyList(),
                false,
                Collections.emptyList(),
                false,
                null,
                null,
                false,
                null,
                null,
                false,
                60,
                (short) 10,
                false,
                false
        );
    }

    public boolean rejoinChannels() {
        return rejoinChannels;
    }

    public void _setRejoinChannels(boolean rejoinChannels) {
        this.rejoinChannels = rejoinChannels;
    }

    public int networkId() {
        return networkId;
    }

    public void _setNetworkId(int networkId) {
        this.networkId = networkId;
        _update();
    }

    public String networkName() {
        return networkName;
    }

    public void _setNetworkName(String networkName) {
        this.networkName = networkName;
        _update();
    }

    public int identity() {
        return identity;
    }

    public void _setIdentity(int identity) {
        this.identity = identity;
        _update();
    }

    public String codecForServer() {
        return codecForServer;
    }

    public void _setCodecForServer(String codecForServer) {
        this.codecForServer = codecForServer;
        _update();
    }

    public String codecForEncoding() {
        return codecForEncoding;
    }

    public void _setCodecForEncoding(String codecForEncoding) {
        this.codecForEncoding = codecForEncoding;
        _update();
    }

    public String codecForDecoding() {
        return codecForDecoding;
    }

    public void _setCodecForDecoding(String codecForDecoding) {
        this.codecForDecoding = codecForDecoding;
        _update();
    }

    public List<NetworkServer> serverList() {
        return serverList;
    }

    public void _setServerList(List<NetworkServer> serverList) {
        this.serverList = serverList;
        _update();
    }

    public boolean useRandomServer() {
        return useRandomServer;
    }

    public void _setUseRandomServer(boolean useRandomServer) {
        this.useRandomServer = useRandomServer;
        _update();
    }

    public List<String> perform() {
        return perform;
    }

    public void _setPerform(List<String> perform) {
        this.perform = perform;
        _update();
    }

    public boolean useAutoIdentify() {
        return useAutoIdentify;
    }

    public void _setUseAutoIdentify(boolean useAutoIdentify) {
        this.useAutoIdentify = useAutoIdentify;
        _update();
    }

    public String autoIdentifyService() {
        return autoIdentifyService;
    }

    public void _setAutoIdentifyService(String autoIdentifyService) {
        this.autoIdentifyService = autoIdentifyService;
        _update();
    }

    public String autoIdentifyPassword() {
        return autoIdentifyPassword;
    }

    public void _setAutoIdentifyPassword(String autoIdentifyPassword) {
        this.autoIdentifyPassword = autoIdentifyPassword;
        _update();
    }

    public boolean useSasl() {
        return useSasl;
    }

    public void _setUseSasl(boolean useSasl) {
        this.useSasl = useSasl;
        _update();
    }

    public String saslAccount() {
        return saslAccount;
    }

    public void _setSaslAccount(String saslAccount) {
        this.saslAccount = saslAccount;
        _update();
    }

    public String saslPassword() {
        return saslPassword;
    }

    public void _setSaslPassword(String saslPassword) {
        this.saslPassword = saslPassword;
        _update();
    }

    public boolean useAutoReconnect() {
        return useAutoReconnect;
    }

    public void _setUseAutoReconnect(boolean useAutoReconnect) {
        this.useAutoReconnect = useAutoReconnect;
        _update();
    }

    public int autoReconnectInterval() {
        return autoReconnectInterval;
    }

    public void _setAutoReconnectInterval(int autoReconnectInterval) {
        this.autoReconnectInterval = autoReconnectInterval;
        _update();
    }

    public short autoReconnectRetries() {
        return autoReconnectRetries;
    }

    public void _setAutoReconnectRetries(short autoReconnectRetries) {
        this.autoReconnectRetries = autoReconnectRetries;
        _update();
    }

    public boolean unlimitedReconnectRetries() {
        return unlimitedReconnectRetries;
    }

    public void _setUnlimitedReconnectRetries(boolean unlimitedReconnectRetries) {
        this.unlimitedReconnectRetries = unlimitedReconnectRetries;
        _update();
    }

    private void _update() {
        setChanged();
        notifyObservers();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkInfo that = (NetworkInfo) o;

        if (networkId != that.networkId) return false;
        if (identity != that.identity) return false;
        if (useRandomServer != that.useRandomServer) return false;
        if (useAutoIdentify != that.useAutoIdentify) return false;
        if (useSasl != that.useSasl) return false;
        if (useAutoReconnect != that.useAutoReconnect) return false;
        if (autoReconnectInterval != that.autoReconnectInterval) return false;
        if (autoReconnectRetries != that.autoReconnectRetries) return false;
        if (unlimitedReconnectRetries != that.unlimitedReconnectRetries) return false;
        if (rejoinChannels != that.rejoinChannels) return false;
        if (networkName != null ? !networkName.equals(that.networkName) : that.networkName != null)
            return false;
        if (codecForServer != null ? !codecForServer.equals(that.codecForServer) : that.codecForServer != null)
            return false;
        if (codecForEncoding != null ? !codecForEncoding.equals(that.codecForEncoding) : that.codecForEncoding != null)
            return false;
        if (codecForDecoding != null ? !codecForDecoding.equals(that.codecForDecoding) : that.codecForDecoding != null)
            return false;
        if (serverList != null ? !serverList.equals(that.serverList) : that.serverList != null)
            return false;
        if (perform != null ? !perform.equals(that.perform) : that.perform != null) return false;
        if (autoIdentifyService != null ? !autoIdentifyService.equals(that.autoIdentifyService) : that.autoIdentifyService != null)
            return false;
        if (autoIdentifyPassword != null ? !autoIdentifyPassword.equals(that.autoIdentifyPassword) : that.autoIdentifyPassword != null)
            return false;
        if (saslAccount != null ? !saslAccount.equals(that.saslAccount) : that.saslAccount != null)
            return false;
        return saslPassword != null ? saslPassword.equals(that.saslPassword) : that.saslPassword == null;

    }

    @Override
    public int hashCode() {
        int result = networkId;
        result = 31 * result + (networkName != null ? networkName.hashCode() : 0);
        result = 31 * result + identity;
        result = 31 * result + (codecForServer != null ? codecForServer.hashCode() : 0);
        result = 31 * result + (codecForEncoding != null ? codecForEncoding.hashCode() : 0);
        result = 31 * result + (codecForDecoding != null ? codecForDecoding.hashCode() : 0);
        result = 31 * result + (serverList != null ? serverList.hashCode() : 0);
        result = 31 * result + (useRandomServer ? 1 : 0);
        result = 31 * result + (perform != null ? perform.hashCode() : 0);
        result = 31 * result + (useAutoIdentify ? 1 : 0);
        result = 31 * result + (autoIdentifyService != null ? autoIdentifyService.hashCode() : 0);
        result = 31 * result + (autoIdentifyPassword != null ? autoIdentifyPassword.hashCode() : 0);
        result = 31 * result + (useSasl ? 1 : 0);
        result = 31 * result + (saslAccount != null ? saslAccount.hashCode() : 0);
        result = 31 * result + (saslPassword != null ? saslPassword.hashCode() : 0);
        result = 31 * result + (useAutoReconnect ? 1 : 0);
        result = 31 * result + autoReconnectInterval;
        result = 31 * result + (int) autoReconnectRetries;
        result = 31 * result + (unlimitedReconnectRetries ? 1 : 0);
        result = 31 * result + (rejoinChannels ? 1 : 0);
        return result;
    }
}
