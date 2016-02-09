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

import java.util.List;

import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;

public abstract class AIrcChannel<T extends AIrcChannel<T>> extends SyncableObject<T> implements QIrcChannel {
    @Override
    public void setTopic(String topic) {
        _setTopic(topic);
        syncVar("setTopic", topic);
    }

    @Override
    public void setPassword(String password) {
        _setPassword(password);
        syncVar("setPassword", password);
    }

    @Override
    public void setEncrypted(boolean encrypted) {
        _setEncrypted(encrypted);
        syncVar("setEncrypted", encrypted);
    }

    @Override
    public void joinIrcUsers(List<String> nicks, List<String> modes) {
        _joinIrcUsers(nicks, modes);
        syncVar("joinIrcUsers", nicks, modes);
    }

    @Override
    public void joinIrcUser(@NonNull QIrcUser ircuser) {
        _joinIrcUser(ircuser);
        syncVar("joinIrcUser", ircuser.nick());
    }

    @Override
    public void part(@NonNull QIrcUser ircuser) {
        _part(ircuser);
        syncVar("part", ircuser.nick());
    }

    @Override
    public void part(String nick) {
        _part(nick);
        syncVar("part", nick);
    }

    @Override
    public void setUserModes(@NonNull QIrcUser ircuser, String modes) {
        _setUserModes(ircuser, modes);
        syncVar("setUserModes", ircuser.nick(), modes);
    }

    @Override
    public void setUserModes(String nick, String modes) {
        _setUserModes(nick, modes);
        syncVar("setUserModes", nick, modes);
    }

    @Override
    public void addUserMode(@NonNull QIrcUser ircuser, String mode) {
        _addUserMode(ircuser, mode);
        syncVar("addUserMode", ircuser.nick(), mode);
    }

    @Override
    public void addUserMode(String nick, String mode) {
        _addUserMode(nick, mode);
        syncVar("addUserMode", nick, mode);
    }

    @Override
    public void removeUserMode(@NonNull QIrcUser ircuser, String mode) {
        _removeUserMode(ircuser, mode);
        syncVar("removeUserMode", ircuser.nick(), mode);
    }

    @Override
    public void removeUserMode(String nick, String mode) {
        _removeUserMode(nick, mode);
        syncVar("removeUserMode", nick, mode);

    }

    @Override
    public void addChannelMode(char mode, String value) {
        _addChannelMode(mode, value);
        syncVar("addChannelMode", mode, value);
    }

    @Override
    public void removeChannelMode(char mode, String value) {
        _removeChannelMode(mode, value);
        syncVar("removeChannelMode", mode, value);
    }
}
