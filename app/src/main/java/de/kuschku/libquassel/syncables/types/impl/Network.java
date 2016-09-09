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
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.localtypes.buffers.StatusBuffer;
import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.abstracts.ANetwork;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.util.CompatibilityUtils;
import de.kuschku.util.irc.IrcCaseMappers;
import de.kuschku.util.irc.IrcUserUtils;
import de.kuschku.util.irc.ModeUtils;
import de.kuschku.util.irc.chanmodes.IrcModeProvider;
import de.kuschku.util.irc.chanmodes.IrcModeProviderFactory;

public class Network extends ANetwork implements Observer {
    private final Map<String, QIrcChannel> channels;
    @NonNull
    private final Map<String, QIrcUser> nicks;
    private final Map<String, String> supports;
    private ConnectionState connectionState;
    private boolean autoAwayActive;
    private String currentServer;
    private boolean isConnected;
    private int latency;
    private String myNick;

    private NetworkInfo networkInfo;

    private String defaultCodecForServer = "UTF-8";
    private String defaultCodecForEncoding = "UTF-8";
    private String defaultCodecForDecoding = "UTF-8";

    private List<String> prefixes;
    private List<String> prefixModes;
    private IrcModeProvider modeProvider;
    private IrcCaseMappers.IrcCaseMapper caseMapper;

    public Network(Map<String, QIrcChannel> channels,
                   Map<String, QIrcUser> nicks,
                   List<NetworkServer> serverList, Map<String, String> supports,
                   int connectionState, String currentServer, boolean isConnected, int latency,
                   String myNick, NetworkInfo networkInfo) {
        this.channels = new HashMap<>(channels);
        this.nicks = new HashMap<>(nicks);
        this.supports = new HashMap<>(supports);
        this.connectionState = ConnectionState.of(connectionState);
        this.currentServer = currentServer;
        this.isConnected = isConnected;
        this.latency = latency;
        this.myNick = myNick;
        _setNetworkInfo(networkInfo);
        this.networkInfo._setServerList(serverList);

        updateCaseMapper();
    }

    public Network(Map<String, QIrcChannel> channels, Map<String, QIrcUser> users, Map<String, String> supports, int connectionState, String currentServer, boolean isConnected, int latency, String myNick, NetworkInfo networkInfo) {
        this(channels, users, Collections.emptyList(), supports, connectionState, currentServer, isConnected, latency, myNick, networkInfo);
    }

    public Network(Map<String, QIrcChannel> channels, Map<String, QIrcUser> nicks, List<NetworkServer> serverList, Map<String, String> supports, ConnectionState connectionState, String currentServer, boolean isConnected, int latency, String myNick, NetworkInfo networkInfo) {
        this.channels = new HashMap<>(channels);
        this.nicks = new HashMap<>(nicks);
        this.supports = new HashMap<>(supports);
        this.connectionState = connectionState;
        this.currentServer = currentServer;
        this.isConnected = isConnected;
        this.latency = latency;
        this.myNick = myNick;
        _setNetworkInfo(networkInfo);
        this.networkInfo._setServerList(serverList);

        updateCaseMapper();
    }

    @NonNull
    public static QNetwork create(int network) {
        return new Network(
                Collections.emptyMap(),
                Collections.emptyMap(),
                Collections.emptyList(),
                Collections.emptyMap(),
                ConnectionState.Disconnected,
                "",
                false,
                0,
                "",
                NetworkInfo.create(network)
        );
    }

    @Override
    public int networkId() {
        return networkInfo.networkId();
    }

    @Override
    public boolean isMyNick(String nick) {
        return caseMapper.equalsIgnoreCase(myNick, nick);
    }

    @Override
    public boolean isMe(@NonNull QIrcUser ircuser) {
        return caseMapper.equalsIgnoreCase(ircuser.nick(), myNick());
    }

    @Override
    public boolean isChannelName(@NonNull String channelname) {
        if (channelname.isEmpty())
            return false;

        if (supports("CHANTYPES"))
            return support("CHANTYPES").contains(channelname.substring(0, 1));
        else
            return "#&!+".contains(channelname.substring(0, 1));
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public ConnectionState connectionState() {
        return connectionState;
    }

    @Override
    public String prefixToMode(char prefix) {
        return prefixToMode(ModeUtils.fromMode(prefix));
    }

    @Override
    public String prefixToMode(String prefix) {
        if (prefixes().contains(prefix))
            return prefixModes().get(prefixes().indexOf(prefix));
        else
            return "";
    }

    @Override
    public String modeToPrefix(char ch) {
        return modeToPrefix(ModeUtils.fromMode(ch));
    }

    @Override
    public String modeToPrefix(String mode) {
        if (prefixModes().contains(mode))
            return prefixes().get(prefixModes().indexOf(mode));
        else
            return "";
    }

    @Override
    public int modeToIndex(String mode) {
        int index = prefixModes().indexOf(mode);
        return index == -1 ? Integer.MAX_VALUE : index;
    }

    @NonNull
    @Override
    public ChannelModeType channelModeType(char mode) {
        return channelModeType(ModeUtils.fromMode(mode));
    }

    @NonNull
    @Override
    public ChannelModeType channelModeType(@NonNull String mode) {
        String chanmodes = support("CHANMODES");
        if (chanmodes.isEmpty())
            return ChannelModeType.NOT_A_CHANMODE;

        String[] split = chanmodes.split(",");
        for (int i = 0; i < split.length; i++) {
            if (split[i].contains(mode)) {
                return ChannelModeType.of(i);
            }
        }
        return ChannelModeType.NOT_A_CHANMODE;
    }

    @Override
    public String networkName() {
        return networkInfo.networkName();
    }

    @Override
    public String currentServer() {
        return currentServer;
    }

    @Override
    public String myNick() {
        return myNick;
    }

    @Override
    public int latency() {
        return latency;
    }

    @Override
    public QIrcUser me() {
        return ircUser(myNick());
    }

    @Override
    public int identity() {
        return networkInfo.identity();
    }

    @NonNull
    @Override
    public List<String> nicks() {
        List<String> nicks = new ArrayList<>();
        for (QIrcUser user : this.nicks.values()) {
            nicks.add(user.nick());
        }
        return nicks;
    }

    @NonNull
    @Override
    public List<String> channels() {
        return new ArrayList<>(channels.keySet());
    }

    @Override
    public boolean useRandomServer() {
        return networkInfo.useRandomServer();
    }

    @Override
    public List<String> perform() {
        return networkInfo.perform();
    }

    @Override
    public boolean useAutoIdentify() {
        return networkInfo.useAutoIdentify();
    }

    @Override
    public String autoIdentifyService() {
        return networkInfo.autoIdentifyService();
    }

    @Override
    public String autoIdentifyPassword() {
        return networkInfo.autoIdentifyPassword();
    }

    @Override
    public boolean useSasl() {
        return networkInfo.useSasl();
    }

    @Override
    public String saslAccount() {
        return networkInfo.saslAccount();
    }

    @Override
    public String saslPassword() {
        return networkInfo.saslPassword();
    }

    @Override
    public boolean useAutoReconnect() {
        return networkInfo.useAutoReconnect();
    }

    @Override
    public int autoReconnectInterval() {
        return networkInfo.autoReconnectInterval();
    }

    @Override
    public short autoReconnectRetries() {
        return networkInfo.autoReconnectRetries();
    }

    @Override
    public boolean unlimitedReconnectRetries() {
        return networkInfo.unlimitedReconnectRetries();
    }

    @Override
    public boolean rejoinChannels() {
        return networkInfo.rejoinChannels();
    }

    @Override
    public NetworkInfo networkInfo() {
        return networkInfo;
    }

    @Override
    public List<String> prefixes() {
        if (prefixes == null)
            determinePrefixes();
        return prefixes;
    }

    @Override
    public List<String> prefixModes() {
        if (prefixModes == null)
            determinePrefixes();
        return prefixModes;
    }

    @Override
    public IrcModeProvider modeProvider() {
        if (modeProvider == null)
            modeProvider = IrcModeProviderFactory.identifyServer(supports.get("CHANMODES"));
        return modeProvider;
    }

    @Override
    public void determinePrefixes() {
        // seems like we have to construct them first
        String prefix = support("PREFIX");

        if (prefix.startsWith("(") && prefix.contains(")")) {
            String[] data = prefix.substring(1).split("\\)");
            prefixes = Arrays.asList(CompatibilityUtils.partStringByChar(data[1]));
            prefixModes = Arrays.asList(CompatibilityUtils.partStringByChar(data[0]));
        } else {
            List<String> defaultPrefixes = Arrays.asList(CompatibilityUtils.partStringByChar("~&@%+"));
            List<String> defaultPrefixModes = Arrays.asList(CompatibilityUtils.partStringByChar("qaohv"));

            if (prefix.isEmpty()) {
                prefixes = defaultPrefixes;
                prefixModes = defaultPrefixModes;
                return;
            }

            // clear the existing modes, just in case we're run multiple times
            prefixes.clear();
            prefixModes.clear();

            // we just assume that in PREFIX are only prefix chars stored
            for (int i = 0; i < defaultPrefixes.size(); i++) {
                if (prefix.contains(defaultPrefixes.get(i))) {
                    prefixes.add(defaultPrefixes.get(i));
                    prefixModes.add(defaultPrefixModes.get(i));
                }
            }
            // check for success
            if (!prefixes.isEmpty())
                return;

            // well... our assumption was obviously wrong...
            // check if it's only prefix modes
            for (int i = 0; i < defaultPrefixes.size(); i++) {
                if (prefix.contains(defaultPrefixModes.get(i))) {
                    prefixes.add(defaultPrefixes.get(i));
                    prefixModes.add(defaultPrefixModes.get(i));
                }
            }
            // now we've done all we've could...
        }
    }

    @Override
    public boolean supports(String param) {
        return supports.containsKey(param);
    }

    @Override
    public String support(@NonNull String param) {
        String key = param.toUpperCase(Locale.US);
        if (supports.containsKey(key))
            return supports.get(key);
        else
            return "";
    }

    @Override
    public QIrcUser ircUser(String nickname) {
        return nicks.get(nickname);
    }

    @NonNull
    @Override
    public List<QIrcUser> ircUsers() {
        return new ArrayList<>(nicks.values());
    }

    @Override
    public int ircUserCount() {
        return nicks.size();
    }

    @Override
    public QIrcChannel newIrcChannel(@NonNull String channelname) {
        if (!channels.containsKey(caseMapper.toLowerCase(channelname))) {
            QIrcChannel channel = IrcChannel.create(channelname);
            channels.put(caseMapper.toLowerCase(channelname), channel);
            channel.init(this, client);
        }
        return channels.get(caseMapper.toLowerCase(channelname));
    }

    @Nullable
    @Override
    public QIrcChannel ircChannel(String channelname) {
        channelname = caseMapper.toLowerCase(channelname);
        if (channels.containsKey(channelname)) {
            return channels.get(channelname);
        } else {
            return null;
        }
    }

    @NonNull
    @Override
    public List<QIrcChannel> ircChannels() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public int ircChannelCount() {
        return channels.size();
    }

    @Override
    public String codecForServer() {
        return networkInfo.codecForServer();
    }

    @Override
    public String codecForEncoding() {
        return networkInfo.codecForEncoding();
    }

    @Override
    public String codecForDecoding() {
        return networkInfo.codecForDecoding();
    }

    @Override
    public String defaultCodecForServer() {
        return defaultCodecForServer;
    }

    @Override
    public String defaultCodecForEncoding() {
        return defaultCodecForEncoding;
    }

    @Override
    public String defaultCodecForDecoding() {
        return defaultCodecForDecoding;
    }

    @Override
    public void setDefaultCodecForServer(String name) {
        this.defaultCodecForServer = name;
    }

    @Override
    public void setDefaultCodecForEncoding(String name) {
        this.defaultCodecForEncoding = name;
    }

    @Override
    public void setDefaultCodecForDecoding(String name) {
        this.defaultCodecForDecoding = name;
    }

    @Override
    public boolean autoAwayActive() {
        return autoAwayActive;
    }

    @Override
    public void _setAutoAwayActive(boolean active) {
        this.autoAwayActive = active;
        _update();
    }

    @Override
    public void _setNetworkName(String networkName) {
        this.networkInfo._setNetworkName(networkName);
    }

    @Override
    public void _setCurrentServer(String currentServer) {
        this.currentServer = currentServer;
        _update();
    }

    @Override
    public void _setConnected(boolean isConnected) {
        this.isConnected = isConnected;
        _update();
        updateDisplay();
    }

    @Override
    public void _setConnectionState(int state) {
        this.connectionState = ConnectionState.of(state);
        _update();
    }

    @Override
    public void _setMyNick(String mynick) {
        this.myNick = mynick;
        _update();
    }

    @Override
    public void _setLatency(int latency) {
        this.latency = latency;
        _update();
    }

    @Override
    public void _setIdentity(int identityId) {
        this.networkInfo._setIdentity(identityId);
    }

    @Override
    public void _setServerList(List<NetworkServer> serverList) {
        this.networkInfo._setServerList(serverList);
    }

    @Override
    public void _setUseRandomServer(boolean useRandomServer) {
        this.networkInfo._setUseRandomServer(useRandomServer);
    }

    @Override
    public void _setPerform(List<String> performs) {
        this.networkInfo._setPerform(performs);
    }

    @Override
    public void _setUseAutoIdentify(boolean useAutoIdentify) {
        this.networkInfo._setUseAutoIdentify(useAutoIdentify);
    }

    @Override
    public void _setAutoIdentifyService(String autoIdentifyService) {
        this.networkInfo._setAutoIdentifyService(autoIdentifyService);
    }

    @Override
    public void _setAutoIdentifyPassword(String autoIdentifyPassword) {
        this.networkInfo._setAutoIdentifyPassword(autoIdentifyPassword);
    }

    @Override
    public void _setUseSasl(boolean useSasl) {
        this.networkInfo._setUseSasl(useSasl);
    }

    @Override
    public void _setSaslAccount(String saslAccount) {
        this.networkInfo._setSaslAccount(saslAccount);
    }

    @Override
    public void _setSaslPassword(String saslPassword) {
        this.networkInfo._setSaslPassword(saslPassword);
    }

    @Override
    public void _setUseAutoReconnect(boolean useAutoReconnect) {
        this.networkInfo._setUseAutoReconnect(useAutoReconnect);
    }

    @Override
    public void _setAutoReconnectInterval(int autoReconnectInterval) {
        this.networkInfo._setAutoReconnectInterval(autoReconnectInterval);
    }

    @Override
    public void _setAutoReconnectRetries(short autoReconnectRetries) {
        this.networkInfo._setAutoReconnectRetries(autoReconnectRetries);
    }

    @Override
    public void _setUnlimitedReconnectRetries(boolean unlimitedReconnectRetries) {
        this.networkInfo._setUnlimitedReconnectRetries(unlimitedReconnectRetries);
    }

    @Override
    public void _setRejoinChannels(boolean rejoinChannels) {
        this.networkInfo._setRejoinChannels(rejoinChannels);
    }

    @Override
    public void _setCodecForServer(String codecName) {
        this.networkInfo._setCodecForServer(codecName);
    }

    @Override
    public void _setCodecForEncoding(String codecName) {
        this.networkInfo._setCodecForEncoding(codecName);
    }

    @Override
    public void _setCodecForDecoding(String codecName) {
        this.networkInfo._setCodecForDecoding(codecName);
    }

    @Override
    public void _addSupport(String param) {
        _addSupport(param, "");
    }

    @Override
    public void _addSupport(String param, String value) {
        supports.put(param, value);
        _update();
        updateCaseMapper();
    }

    @Override
    public IrcCaseMappers.IrcCaseMapper caseMapper() {
        return caseMapper;
    }

    private void updateCaseMapper() {
        String mapping = support("CASEMAPPING");
        if (mapping == null) {
            caseMapper = IrcCaseMappers.unicode;
        } else {
            switch (mapping.toLowerCase(Locale.US)) {
                case "rfc1459":
                case "strict-rfc1459":
                    caseMapper = IrcCaseMappers.irc;
                case "ascii":
                default:
                    caseMapper = IrcCaseMappers.unicode;
            }
        }
    }

    @Override
    public void _removeSupport(String param) {
        supports.remove(param);
        _update();
        updateCaseMapper();
    }

    @Override
    public void _addIrcUser(@NonNull String hostmask) {
        newIrcUser(hostmask);
    }

    @Override
    public void _addIrcChannel(@NonNull String channel) {
        newIrcChannel(channel);
    }

    @Override
    public QIrcUser _updateNickFromMask(@NonNull String mask) {
        String nick = caseMapper.toLowerCase(IrcUserUtils.getNick(mask));
        QIrcUser user;

        if (nicks.containsKey(nick)) {
            user = nicks.get(nick);
            user.updateHostmask(mask);
        } else {
            user = newIrcUser(mask);
        }
        _update();
        return user;
    }

    @NonNull
    private QIrcUser newIrcUser(@NonNull String mask) {
        IrcUser user = IrcUser.create(mask);
        user.init(this, client);
        client.requestInitObject("IrcUser", user.getObjectName());
        nicks.put(user.nick(), user);
        _update();
        return user;
    }

    @Override
    public void _ircUserNickChanged(@NonNull String oldNick, @NonNull String newNick) {
        if (!caseMapper.equalsIgnoreCase(oldNick, newNick)) {
            nicks.put(newNick, nicks.remove(oldNick));
            for (QIrcChannel channel : channels.values()) {
                channel._ircUserNickChanged(oldNick, newNick);
            }
            _update();
        }

        if (caseMapper.equalsIgnoreCase(myNick(), oldNick))
            _setMyNick(newNick);
    }

    @Override
    public void _connect() {
        // Do nothing
    }

    @Override
    public void _disconnect() {
        // Do nothing
    }

    @Override
    public void _setNetworkInfo(NetworkInfo info) {
        if (this.networkInfo != null)
            this.networkInfo.deleteObserver(this);
        this.networkInfo = info;
        if (this.networkInfo != null)
            this.networkInfo.addObserver(this);
        _update();
        updateDisplay();
    }

    @Override
    public void _removeIrcUser(QIrcUser ircuser) {
        if (!nicks.containsValue(ircuser))
            return;

        for (Map.Entry<String, QIrcUser> entry : nicks.entrySet()) {
            if (entry.getValue() == ircuser) {
                nicks.remove(entry.getKey());
                break;
            }
        }
        _update();
    }

    @Override
    public void _removeIrcChannel(QIrcChannel ircChannel) {
        if (!channels.containsValue(ircChannel))
            return;

        for (Map.Entry<String, QIrcChannel> entry : channels.entrySet()) {
            if (entry.getValue() == ircChannel) {
                channels.remove(entry.getKey());
                break;
            }
        }
        _update();
    }

    @Override
    public void _removeChansAndUsers() {
        nicks.clear();
        channels.clear();
        _update();
    }

    @Override
    public void _addIrcChannel(@NonNull IrcChannel ircChannel) {
        channels.put(ircChannel.name(), ircChannel);
    }

    @Override
    public void _update(Map<String, QVariant> from) {

    }

    @Override
    public void _update(QNetwork from) {

    }


    @Override
    public void _update() {
        super._update();
        if (client != null)
            client.networkManager().networks().notifyItemChanged(client.networkManager().networks().indexOf(this));
    }

    private void updateDisplay() {
        if (client != null && client.connectionStatus() != ConnectionChangeEvent.Status.INITIALIZING_DATA && client.bufferViewManager() != null) {
            StatusBuffer buffer = client.bufferManager().network(networkInfo.networkId());
            if (buffer != null) {
                buffer.updateStatus();
                for (QBufferViewConfig qBufferViewConfig : client.bufferViewManager().bufferViewConfigs()) {
                    qBufferViewConfig.bufferIds().notifyItemChanged(buffer.getInfo().id);
                    qBufferViewConfig.networkList().notifyItemChanged(this);
                }
            } else {
                for (QBufferViewConfig qBufferViewConfig : client.bufferViewManager().bufferViewConfigs()) {
                    qBufferViewConfig.networkList().notifyItemChanged(this);
                }
            }
        }
    }

    @Override
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull Client client) {
        super.init(objectName, provider, client);
        networkInfo._setNetworkId(Integer.parseInt(objectName));
        client.networkManager().createNetwork(this);
        for (QIrcChannel channel : channels.values()) {
            ((IrcChannel) channel).init(networkId() + "/" + channel.name(), provider, client);
        }
        for (QIrcUser user : nicks.values()) {
            ((IrcUser) user).init(networkId() + "/" + user.nick(), provider, client);
        }
    }

    public void update(Observable observable, Object data) {
        _update();
    }

    @Override
    public List<NetworkServer> serverList() {
        return networkInfo.serverList();
    }

    @NonNull
    @Override
    public String toString() {
        return "Network{" +
                "name='" + networkInfo.networkName() + '\'' +
                ", id='" + networkInfo.networkId() + '\'' +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Network network = (Network) o;

        return networkInfo != null ? networkInfo.equals(network.networkInfo) : network.networkInfo == null;

    }

    @Override
    public int hashCode() {
        return networkInfo != null ? networkInfo.hashCode() : 0;
    }
}
