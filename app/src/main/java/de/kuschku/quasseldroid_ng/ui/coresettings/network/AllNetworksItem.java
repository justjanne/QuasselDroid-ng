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

package de.kuschku.quasseldroid_ng.ui.coresettings.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.libquassel.syncables.types.impl.IrcChannel;
import de.kuschku.libquassel.syncables.types.impl.NetworkInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.irc.chanmodes.IrcModeProvider;
import de.kuschku.util.observables.callbacks.GeneralCallback;

class AllNetworksItem implements QNetwork {
    private final Context context;

    public AllNetworksItem(Context context) {
        this.context = context;
    }

    @Override
    public int networkId() {
        return 0;
    }

    @Override
    public boolean isMyNick(String nick) {
        return false;
    }

    @Override
    public boolean isMe(QIrcUser ircuser) {
        return false;
    }

    @Override
    public boolean isChannelName(String channelname) {
        return false;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void setConnected(boolean isConnected) {

    }

    @Override
    public ConnectionState connectionState() {
        return null;
    }

    @Override
    public String prefixToMode(char prefix) {
        return null;
    }

    @Override
    public String prefixToMode(String prefix) {
        return null;
    }

    @Override
    public String modeToPrefix(char mode) {
        return null;
    }

    @Override
    public String modeToPrefix(String mode) {
        return null;
    }

    @Override
    public int modeToIndex(String mode) {
        return 0;
    }

    @NonNull
    @Override
    public ChannelModeType channelModeType(char mode) {
        return null;
    }

    @NonNull
    @Override
    public ChannelModeType channelModeType(String mode) {
        return null;
    }

    @Override
    public String networkName() {
        return context.getString(R.string.labelAllNetworks);
    }

    @Override
    public String currentServer() {
        return null;
    }

    @Override
    public String myNick() {
        return null;
    }

    @Override
    public int latency() {
        return 0;
    }

    @Override
    public QIrcUser me() {
        return null;
    }

    @Override
    public int identity() {
        return 0;
    }

    @NonNull
    @Override
    public List<String> nicks() {
        return null;
    }

    @NonNull
    @Override
    public List<String> channels() {
        return null;
    }

    @Override
    public List<NetworkServer> serverList() {
        return null;
    }

    @Override
    public boolean useRandomServer() {
        return false;
    }

    @Override
    public List<String> perform() {
        return null;
    }

    @Override
    public boolean useAutoIdentify() {
        return false;
    }

    @Override
    public String autoIdentifyService() {
        return null;
    }

    @Override
    public String autoIdentifyPassword() {
        return null;
    }

    @Override
    public boolean useSasl() {
        return false;
    }

    @Override
    public String saslAccount() {
        return null;
    }

    @Override
    public String saslPassword() {
        return null;
    }

    @Override
    public boolean useAutoReconnect() {
        return false;
    }

    @Override
    public int autoReconnectInterval() {
        return 0;
    }

    @Override
    public short autoReconnectRetries() {
        return 0;
    }

    @Override
    public boolean unlimitedReconnectRetries() {
        return false;
    }

    @Override
    public boolean rejoinChannels() {
        return false;
    }

    @Override
    public NetworkInfo networkInfo() {
        return null;
    }

    @Override
    public List<String> prefixes() {
        return null;
    }

    @Override
    public List<String> prefixModes() {
        return null;
    }

    @Override
    public IrcModeProvider modeProvider() {
        return null;
    }

    @Override
    public void determinePrefixes() {

    }

    @Override
    public boolean supports(String param) {
        return false;
    }

    @Override
    public String support(String param) {
        return null;
    }

    @Override
    public QIrcUser ircUser(String nickname) {
        return null;
    }

    @NonNull
    @Override
    public List<QIrcUser> ircUsers() {
        return null;
    }

    @Override
    public int ircUserCount() {
        return 0;
    }

    @Override
    public QIrcChannel newIrcChannel(String channelname) {
        return null;
    }

    @Nullable
    @Override
    public QIrcChannel ircChannel(String channelname) {
        return null;
    }

    @NonNull
    @Override
    public List<QIrcChannel> ircChannels() {
        return null;
    }

    @Override
    public int ircChannelCount() {
        return 0;
    }

    @Override
    public String codecForServer() {
        return null;
    }

    @Override
    public String codecForEncoding() {
        return null;
    }

    @Override
    public String codecForDecoding() {
        return null;
    }

    @Override
    public String defaultCodecForServer() {
        return null;
    }

    @Override
    public String defaultCodecForEncoding() {
        return null;
    }

    @Override
    public String defaultCodecForDecoding() {
        return null;
    }

    @Override
    public void setDefaultCodecForServer(String name) {

    }

    @Override
    public void setDefaultCodecForEncoding(String name) {

    }

    @Override
    public void setDefaultCodecForDecoding(String name) {

    }

    @Override
    public boolean autoAwayActive() {
        return false;
    }

    @Override
    public void setAutoAwayActive(boolean active) {

    }

    @Override
    public void _setAutoAwayActive(boolean active) {

    }

    @Override
    public void setNetworkName(String networkName) {

    }

    @Override
    public void _setNetworkName(String networkName) {

    }

    @Override
    public void setCurrentServer(String currentServer) {

    }

    @Override
    public void _setCurrentServer(String currentServer) {

    }

    @Override
    public void _setConnected(boolean isConnected) {

    }

    @Override
    public void setConnectionState(int state) {

    }

    @Override
    public void _setConnectionState(int state) {

    }

    @Override
    public void setMyNick(String mynick) {

    }

    @Override
    public void _setMyNick(String mynick) {

    }

    @Override
    public void setLatency(int latency) {

    }

    @Override
    public void _setLatency(int latency) {

    }

    @Override
    public void setIdentity(int identityId) {

    }

    @Override
    public void _setIdentity(int identityId) {

    }

    @Override
    public void setServerList(List<NetworkServer> serverList) {

    }

    @Override
    public void _setServerList(List<NetworkServer> serverList) {

    }

    @Override
    public void setUseRandomServer(boolean useRandomServer) {

    }

    @Override
    public void _setUseRandomServer(boolean useRandomServer) {

    }

    @Override
    public void setPerform(List<String> performs) {

    }

    @Override
    public void _setPerform(List<String> performs) {

    }

    @Override
    public void setUseAutoIdentify(boolean useAutoIdentify) {

    }

    @Override
    public void _setUseAutoIdentify(boolean useAutoIdentify) {

    }

    @Override
    public void setAutoIdentifyService(String autoIdentifyService) {

    }

    @Override
    public void _setAutoIdentifyService(String autoIdentifyService) {

    }

    @Override
    public void setAutoIdentifyPassword(String autoIdentifyPassword) {

    }

    @Override
    public void _setAutoIdentifyPassword(String autoIdentifyPassword) {

    }

    @Override
    public void setUseSasl(boolean useSasl) {

    }

    @Override
    public void _setUseSasl(boolean useSasl) {

    }

    @Override
    public void setSaslAccount(String saslAccount) {

    }

    @Override
    public void _setSaslAccount(String saslAccount) {

    }

    @Override
    public void setSaslPassword(String saslPassword) {

    }

    @Override
    public void _setSaslPassword(String saslPassword) {

    }

    @Override
    public void setUseAutoReconnect(boolean useAutoReconnect) {

    }

    @Override
    public void _setUseAutoReconnect(boolean useAutoReconnect) {

    }

    @Override
    public void setAutoReconnectInterval(int autoReconnectInterval) {

    }

    @Override
    public void _setAutoReconnectInterval(int autoReconnectInterval) {

    }

    @Override
    public void setAutoReconnectRetries(short autoReconnectRetries) {

    }

    @Override
    public void _setAutoReconnectRetries(short autoReconnectRetries) {

    }

    @Override
    public void setUnlimitedReconnectRetries(boolean unlimitedReconnectRetries) {

    }

    @Override
    public void _setUnlimitedReconnectRetries(boolean unlimitedReconnectRetries) {

    }

    @Override
    public void setRejoinChannels(boolean rejoinChannels) {

    }

    @Override
    public void _setRejoinChannels(boolean rejoinChannels) {

    }

    @Override
    public void setCodecForServer(String codecName) {

    }

    @Override
    public void _setCodecForServer(String codecName) {

    }

    @Override
    public void setCodecForEncoding(String codecName) {

    }

    @Override
    public void _setCodecForEncoding(String codecName) {

    }

    @Override
    public void setCodecForDecoding(String codecName) {

    }

    @Override
    public void _setCodecForDecoding(String codecName) {

    }

    @Override
    public void addSupport(String param) {

    }

    @Override
    public void _addSupport(String param) {

    }

    @Override
    public void addSupport(String param, String value) {

    }

    @Override
    public void _addSupport(String param, String value) {

    }

    @Override
    public void removeSupport(String param) {

    }

    @Override
    public void _removeSupport(String param) {

    }

    @Override
    public void addIrcUser(String hostmask) {

    }

    @Override
    public void _addIrcUser(String hostmask) {

    }

    @Override
    public void addIrcChannel(String channel) {

    }

    @Override
    public void _addIrcChannel(String channel) {

    }

    @Override
    public QIrcUser updateNickFromMask(String mask) {
        return null;
    }

    @Override
    public QIrcUser _updateNickFromMask(String mask) {
        return null;
    }

    @Override
    public void ircUserNickChanged(String oldNick, String newnick) {

    }

    @Override
    public void _ircUserNickChanged(String oldNick, String newnick) {

    }

    @Override
    public void connect() {

    }

    @Override
    public void _connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void _disconnect() {

    }

    @Override
    public void setNetworkInfo(NetworkInfo info) {

    }

    @Override
    public void _setNetworkInfo(NetworkInfo info) {

    }

    @Override
    public void removeIrcUser(QIrcUser ircuser) {

    }

    @Override
    public void _removeIrcUser(QIrcUser ircuser) {

    }

    @Override
    public void removeIrcChannel(QIrcChannel ircChannel) {

    }

    @Override
    public void _removeIrcChannel(QIrcChannel ircChannel) {

    }

    @Override
    public void removeChansAndUsers() {

    }

    @Override
    public void _removeChansAndUsers() {

    }

    @Override
    public void _addIrcChannel(IrcChannel ircChannel) {

    }

    @Override
    public void addObserver(GeneralCallback<QNetwork> o) {

    }

    @Override
    public void deleteObserver(GeneralCallback<QNetwork> o) {

    }
}
