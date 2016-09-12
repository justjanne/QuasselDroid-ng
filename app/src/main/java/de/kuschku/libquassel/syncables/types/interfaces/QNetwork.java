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

package de.kuschku.libquassel.syncables.types.interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.libquassel.syncables.Synced;
import de.kuschku.libquassel.syncables.types.impl.IrcChannel;
import de.kuschku.libquassel.syncables.types.impl.NetworkInfo;
import de.kuschku.util.irc.IrcCaseMappers;
import de.kuschku.util.irc.chanmodes.IrcModeProvider;

public interface QNetwork extends QObservable<QNetwork> {
    int networkId();

    boolean isMyNick(final String nick);

    boolean isMe(QIrcUser ircuser);

    boolean isChannelName(final String channelname);

    boolean isConnected();

    @Synced
    void setConnected(boolean isConnected);

    ConnectionState connectionState();

    String prefixToMode(final char prefix);

    String prefixToMode(final String prefix);

    String modeToPrefix(final char mode);

    String modeToPrefix(final String mode);

    int modeToIndex(String mode);

    int lowestModeIndex(String mode);

    @NonNull
    ChannelModeType channelModeType(final char mode);

    @NonNull
    ChannelModeType channelModeType(final String mode);

    String networkName();

    String currentServer();

    String myNick();

    int latency();

    QIrcUser me();

    int identity();

    @NonNull
    List<String> nicks();

    @NonNull
    List<String> channels();

    List<NetworkServer> serverList();

    boolean useRandomServer();

    List<String> perform();

    boolean useAutoIdentify();

    String autoIdentifyService();

    String autoIdentifyPassword();

    boolean useSasl();

    String saslAccount();

    String saslPassword();

    boolean useAutoReconnect();

    int autoReconnectInterval();

    short autoReconnectRetries();

    boolean unlimitedReconnectRetries();

    boolean rejoinChannels();

    NetworkInfo networkInfo();

    List<String> prefixes();

    List<String> prefixModes();

    IrcModeProvider modeProvider();

    void determinePrefixes();

    boolean supports(final String param);

    String support(final String param);

    QIrcUser ircUser(final String nickname);

    @NonNull
    List<QIrcUser> ircUsers();

    int ircUserCount();

    QIrcChannel newIrcChannel(final String channelname);

    @Nullable
    QIrcChannel ircChannel(final String channelname);

    @NonNull
    List<QIrcChannel> ircChannels();

    int ircChannelCount();

    String codecForServer();

    String codecForEncoding();

    String codecForDecoding();

    String defaultCodecForServer();

    String defaultCodecForEncoding();

    String defaultCodecForDecoding();

    void setDefaultCodecForServer(final String name);

    void setDefaultCodecForEncoding(final String name);

    void setDefaultCodecForDecoding(final String name);

    boolean autoAwayActive();

    @Synced
    void setAutoAwayActive(boolean active);

    void _setAutoAwayActive(boolean active);

    @Synced
    void setNetworkName(final String networkName);

    void _setNetworkName(final String networkName);

    @Synced
    void setCurrentServer(final String currentServer);

    void _setCurrentServer(final String currentServer);

    void _setConnected(boolean isConnected);

    @Synced
    void setConnectionState(int state);

    void _setConnectionState(int state);

    @Synced
    void setMyNick(final String mynick);

    void _setMyNick(final String mynick);

    @Synced
    void setLatency(int latency);

    void _setLatency(int latency);

    @Synced
    void setIdentity(int identityId);

    void _setIdentity(int identityId);

    @Synced
    void setServerList(final List<NetworkServer> serverList);

    void _setServerList(final List<NetworkServer> serverList);

    @Synced
    void setUseRandomServer(boolean useRandomServer);

    void _setUseRandomServer(boolean useRandomServer);

    @Synced
    void setPerform(final List<String> performs);

    void _setPerform(final List<String> performs);

    @Synced
    void setUseAutoIdentify(boolean useAutoIdentify);

    void _setUseAutoIdentify(boolean useAutoIdentify);

    @Synced
    void setAutoIdentifyService(final String autoIdentifyService);

    void _setAutoIdentifyService(final String autoIdentifyService);

    @Synced
    void setAutoIdentifyPassword(final String autoIdentifyPassword);

    void _setAutoIdentifyPassword(final String autoIdentifyPassword);

    @Synced
    void setUseSasl(boolean useSasl);

    void setSaslPassword(final String saslPassword);

    void _setUseSasl(boolean useSasl);

    @Synced
    void setSaslAccount(final String saslAccount);

    void _setSaslAccount(final String saslAccount);

    @Synced
    void _setSaslPassword(final String saslPassword);

    @Synced
    void setUseAutoReconnect(boolean useAutoReconnect);

    void _setUseAutoReconnect(boolean useAutoReconnect);

    @Synced
    void setAutoReconnectInterval(int autoReconnectInterval);

    void _setAutoReconnectInterval(int autoReconnectInterval);

    @Synced
    void setAutoReconnectRetries(short autoReconnectRetries);

    void _setAutoReconnectRetries(short autoReconnectRetries);

    @Synced
    void setUnlimitedReconnectRetries(boolean unlimitedReconnectRetries);

    void _setUnlimitedReconnectRetries(boolean unlimitedReconnectRetries);

    @Synced
    void setRejoinChannels(boolean rejoinChannels);

    void _setRejoinChannels(boolean rejoinChannels);

    @Synced
    void setCodecForServer(final String codecName);

    void _setCodecForServer(final String codecName);

    @Synced
    void setCodecForEncoding(final String codecName);

    void _setCodecForEncoding(final String codecName);

    @Synced
    void setCodecForDecoding(final String codecName);

    void _setCodecForDecoding(final String codecName);

    @Synced
    void addSupport(final String param);

    void _addSupport(final String param);

    @Synced
    void addSupport(final String param, final String value);

    void _addSupport(final String param, final String value);

    @Synced
    void removeSupport(final String param);

    void _removeSupport(final String param);

    @Synced
    void addIrcUser(final String hostmask);

    void _addIrcUser(final String hostmask);

    @Synced
    void addIrcChannel(final String channel);

    void _addIrcChannel(final String channel);

    @Synced
    QIrcUser updateNickFromMask(final String mask);

    QIrcUser _updateNickFromMask(final String mask);

    void ircUserNickChanged(String oldNick, String newnick);

    @Synced
    void connect();

    void _connect();

    @Synced
    void disconnect();

    void _disconnect();

    @Synced
    void setNetworkInfo(final NetworkInfo info);

    void _setNetworkInfo(final NetworkInfo info);

    @Synced
    void removeIrcUser(QIrcUser ircuser);

    void _removeIrcUser(QIrcUser ircuser);

    @Synced
    void removeIrcChannel(QIrcChannel ircChannel);

    void _removeIrcChannel(QIrcChannel ircChannel);

    @Synced
    void removeChansAndUsers();

    void _removeChansAndUsers();

    void _addIrcChannel(IrcChannel ircChannel);

    IrcCaseMappers.IrcCaseMapper caseMapper();

    enum ConnectionState {
        Disconnected(0),
        Connecting(1),
        Initializing(2),
        Initialized(3),
        Reconnecting(4),
        Disconnecting(5);

        public final int value;

        ConnectionState(int value) {
            this.value = value;
        }

        @NonNull
        public static ConnectionState of(int id) {
            switch (id) {
                case 1:
                    return Connecting;
                case 2:
                    return Initializing;
                case 3:
                    return Initialized;
                case 4:
                    return Reconnecting;
                case 5:
                    return Disconnecting;
                default:
                case 0:
                    return Disconnected;
            }
        }
    }

    enum ChannelModeType {
        NOT_A_CHANMODE(-1),
        A_CHANMODE(0),
        B_CHANMODE(1),
        C_CHANMODE(2),
        D_CHANMODE(3);

        public final int id;

        ChannelModeType(int id) {
            this.id = id;
        }

        @NonNull
        public static ChannelModeType of(int id) {
            switch (id) {
                case 0:
                    return A_CHANMODE;
                case 1:
                    return B_CHANMODE;
                case 2:
                    return C_CHANMODE;
                case 3:
                    return D_CHANMODE;
                default:
                case -1:
                    return NOT_A_CHANMODE;
            }
        }
    }
}
