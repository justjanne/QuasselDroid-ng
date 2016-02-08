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

import java.util.List;

import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.syncables.Synced;
import de.kuschku.util.observables.lists.ObservableSet;

public interface QIrcChannel extends QObservable {
    boolean isKnownUser(QIrcUser ircuser);

    boolean isValidChannelUserMode(final String mode);

    String name();

    String topic();

    String password();

    boolean encrypted();

    QNetwork network();

    @NonNull
    List<QIrcUser> ircUsers();

    String userModes(QIrcUser ircuser);

    String userModes(final String nick);

    boolean hasMode(final char mode);

    String modeValue(final char mode);

    List<String> modeValueList(final char mode);

    String channelModeString();

    String codecForEncoding();

    String codecForDecoding();

    void setCodecForEncoding(final String codecName);

    void setCodecForDecoding(final String codecName);

    @Synced
    void setTopic(final String topic);

    void _setTopic(final String topic);

    @Synced
    void setPassword(final String password);

    void _setPassword(final String password);

    @Synced
    void setEncrypted(boolean encrypted);

    void _setEncrypted(boolean encrypted);

    // Disabled due to type erasure
    //@Synced void joinIrcUsers(final List<IrcUser> users, final List<String> modes);
    //void _joinIrcUsers(final List<IrcUser> users, final List<String> modes);

    @Synced
    void joinIrcUsers(final List<String> nicks, final List<String> modes);

    void _joinIrcUsers(final List<String> nicks, final List<String> modes);

    @Synced
    void joinIrcUser(QIrcUser ircuser);

    void _joinIrcUser(QIrcUser ircuser);

    @Synced
    void part(QIrcUser ircuser);

    void _part(QIrcUser ircuser);

    @Synced
    void part(final String nick);

    void _part(final String nick);

    @Synced
    void setUserModes(QIrcUser ircuser, final String modes);

    void _setUserModes(QIrcUser ircuser, final String modes);

    @Synced
    void setUserModes(final String nick, final String modes);

    void _setUserModes(final String nick, final String modes);

    @Synced
    void addUserMode(QIrcUser ircuser, final String mode);

    void _addUserMode(QIrcUser ircuser, final String mode);

    @Synced
    void addUserMode(final String nick, final String mode);

    void _addUserMode(final String nick, final String mode);

    @Synced
    void removeUserMode(QIrcUser ircuser, final String mode);

    void _removeUserMode(QIrcUser ircuser, final String mode);

    @Synced
    void removeUserMode(final String nick, final String mode);

    void _removeUserMode(final String nick, final String mode);

    @Synced
    void addChannelMode(final char mode, final String value);

    void _addChannelMode(final char mode, final String value);

    @Synced
    void removeChannelMode(final char mode, final String value);

    void _removeChannelMode(final char mode, final String value);

    void init(QNetwork network, Client client);

    String getObjectName();

    ObservableSet<QIrcUser> users();
}
