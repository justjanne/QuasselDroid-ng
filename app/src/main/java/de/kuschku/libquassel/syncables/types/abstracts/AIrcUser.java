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

import org.joda.time.DateTime;

import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;

public abstract class AIrcUser<T extends AIrcUser<T>> extends SyncableObject<T> implements QIrcUser {
    @Override
    public void setUser(String user) {
        _setUser(user);
        syncVar("setUser", user);
    }

    @Override
    public void setHost(String host) {
        _setHost(host);
        syncVar("setHost", host);
    }

    @Override
    public void setNick(String nick) {
        _setNick(nick);
        syncVar("setNick", nick);
    }

    @Override
    public void setRealName(String realName) {
        _setRealName(realName);
        syncVar("setRealName", realName);
    }

    @Override
    public void setAway(boolean away) {
        _setAway(away);
        syncVar("setAway", away);
    }

    @Override
    public void setAwayMessage(String awayMessage) {
        _setAwayMessage(awayMessage);
        syncVar("setAwayMessage", awayMessage);
    }

    @Override
    public void setIdleTime(DateTime idleTime) {
        _setIdleTime(idleTime);
        syncVar("setIdleTime", idleTime);
    }

    @Override
    public void setLoginTime(DateTime loginTime) {
        _setLoginTime(loginTime);
        syncVar("setLoginTime", loginTime);
    }

    @Override
    public void setServer(String server) {
        _setServer(server);
        syncVar("setServer", server);
    }

    @Override
    public void setIrcOperator(String ircOperator) {
        _setIrcOperator(ircOperator);
        syncVar("setIrcOperator", ircOperator);
    }

    @Override
    public void setLastAwayMessage(int lastAwayMessage) {
        _setLastAwayMessage(lastAwayMessage);
        syncVar("setLastAwayMessage", lastAwayMessage);
    }

    @Override
    public void setWhoisServiceReply(String whoisServiceReply) {
        _setWhoisServiceReply(whoisServiceReply);
        syncVar("setWhoisServiceReply", whoisServiceReply);
    }

    @Override
    public void setSuserHost(String suserHost) {
        _setSuserHost(suserHost);
        syncVar("setSuserHost", suserHost);
    }

    @Override
    public void setEncrypted(boolean encrypted) {
        _setEncrypted(encrypted);
        syncVar("setEncrypted", encrypted);
    }

    @Override
    public void updateHostmask(String mask) {
        _updateHostmask(mask);
        syncVar("updateHostmask", mask);
    }

    @Override
    public void setUserModes(String modes) {
        _setUserModes(modes);
        syncVar("setUserModes", modes);
    }

    @Override
    public void joinChannel(QIrcChannel channel) {
        _joinChannel(channel);
        syncVar("joinChannel", channel);
    }

    @Override
    public void joinChannel(QIrcChannel channel, boolean skip_channel_join) {
        _joinChannel(channel, skip_channel_join);
        syncVar("joinChannel", channel, skip_channel_join);
    }

    @Override
    public void joinChannel(String channelname) {
        _joinChannel(channelname);
        syncVar("joinChannel", channelname);
    }

    @Override
    public void partChannel(QIrcChannel channel) {
        _partChannel(channel);
        syncVar("partChannel", channel);
    }

    @Override
    public void partChannel(String channelname) {
        _partChannel(channelname);
        syncVar("partChannel", channelname);
    }

    @Override
    public void quit() {
        _quit();
        syncVar("quit");
    }

    @Override
    public void addUserModes(String modes) {
        _addUserModes(modes);
        syncVar("addUserModes", modes);
    }

    @Override
    public void removeUserModes(String modes) {
        _removeUserModes(modes);
        syncVar("removeUserModes", modes);
    }

    @Override
    public void partChannel(QIrcChannel channel, boolean skip_channel_part) {
        _partChannel(channel, skip_channel_part);
        syncVar("partChannel", channel, skip_channel_part);
    }
}
