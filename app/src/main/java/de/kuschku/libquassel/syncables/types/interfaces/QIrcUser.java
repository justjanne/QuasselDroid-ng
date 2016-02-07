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

import org.joda.time.DateTime;

import java.util.List;

import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.syncables.Synced;

public interface QIrcUser extends QObservable {
    String user();

    String host();

    String nick();

    String realName();

    String hostmask();

    boolean isAway();

    @Synced
    void setAway(final boolean away);

    String awayMessage();

    DateTime idleTime();

    DateTime loginTime();

    String server();

    String ircOperator();

    int lastAwayMessage();

    String whoisServiceReply();

    String suserHost();

    boolean encrypted();

    QNetwork network();

    @NonNull
    String userModes();

    @NonNull
    List<String> channels();

    DateTime lastChannelActivity(int id);

    void setLastChannelActivity(int id, final DateTime time);

    DateTime lastSpokenTo(int id);

    void setLastSpokenTo(int id, final DateTime time);

    @Synced
    void setUser(final String user);

    void _setUser(final String user);

    @Synced
    void setHost(final String host);

    void _setHost(final String host);

    @Synced
    void setNick(final String nick);

    void _setNick(final String nick);

    @Synced
    void setRealName(final String realName);

    void _setRealName(final String realName);

    void _setAway(final boolean away);

    @Synced
    void setAwayMessage(final String awayMessage);

    void _setAwayMessage(final String awayMessage);

    @Synced
    void setIdleTime(final DateTime idleTime);

    void _setIdleTime(final DateTime idleTime);

    @Synced
    void setLoginTime(final DateTime loginTime);

    void _setLoginTime(final DateTime loginTime);

    @Synced
    void setServer(final String server);

    void _setServer(final String server);

    @Synced
    void setIrcOperator(final String ircOperator);

    void _setIrcOperator(final String ircOperator);

    @Synced
    void setLastAwayMessage(final int lastAwayMessage);

    void _setLastAwayMessage(final int lastAwayMessage);

    @Synced
    void setWhoisServiceReply(final String whoisServiceReply);

    void _setWhoisServiceReply(final String whoisServiceReply);

    @Synced
    void setSuserHost(final String suserHost);

    void _setSuserHost(final String suserHost);

    @Synced
    void setEncrypted(boolean encrypted);

    void _setEncrypted(boolean encrypted);

    @Synced
    void updateHostmask(final String mask);

    void _updateHostmask(final String mask);

    @Synced
    void setUserModes(final String modes);

    void _setUserModes(final String modes);


    @Synced
    void joinChannel(QIrcChannel channel);

    void _joinChannel(QIrcChannel channel);

    @Synced
    void joinChannel(QIrcChannel channel, boolean skip_channel_join);

    void _joinChannel(QIrcChannel channel, boolean skip_channel_join);

    @Synced
    void joinChannel(final String channelname);

    void _joinChannel(final String channelname);

    @Synced
    void partChannel(QIrcChannel channel);

    void _partChannel(QIrcChannel channel);

    @Synced
    void partChannel(QIrcChannel channel, boolean skip_channel_part);

    void _partChannel(QIrcChannel channel, boolean skip_channel_part);

    @Synced
    void partChannel(final String channelname);

    void _partChannel(final String channelname);

    @Synced
    void quit();

    void _quit();

    @Synced
    void addUserModes(final String modes);

    void _addUserModes(final String modes);

    @Synced
    void removeUserModes(final String modes);

    void _removeUserModes(final String modes);

    void init(QNetwork network, Client client);

    String getObjectName();
}
