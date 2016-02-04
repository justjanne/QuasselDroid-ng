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

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.QClient;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.IrcChannelSerializer;
import de.kuschku.libquassel.syncables.types.abstracts.AIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.util.irc.ModeUtils;

import static de.kuschku.util.AndroidAssert.assertEquals;

public class IrcChannel extends AIrcChannel<IrcChannel> {
    private final String name;
    @NonNull
    private final Map<QIrcUser, Set<Character>> userModes = new HashMap<>();
    private String topic;
    private String password;
    private boolean encrypted;
    private QNetwork network;
    private String codecForEncoding;
    private String codecForDecoding;
    @NonNull
    private Map<Character, List<String>> A_channelModes = new HashMap<>();
    @NonNull
    private Map<Character, String> B_channelModes = new HashMap<>();
    @NonNull
    private Map<Character, String> C_channelModes = new HashMap<>();
    @NonNull
    private Set<Character> D_channelModes = new HashSet<>();
    // Because we don’t have networks at the beginning yet
    @Nullable
    private Map<String, String> cachedUserModes;
    @Nullable
    private Map<String, Object> cachedChanModes;

    public IrcChannel(String name, String topic, String password, @Nullable Map<String, String> userModes,
                      @NonNull Map<String, Object> chanModes, boolean encrypted) {
        this.name = name;
        this.topic = topic;
        this.password = password;
        this.encrypted = encrypted;
        this.cachedUserModes = userModes;
        this.cachedChanModes = chanModes;
    }

    @NonNull
    public static IrcChannel create(String channelName) {
        return new IrcChannel(
                channelName,
                "",
                "",
                new HashMap<>(),
                new HashMap<>(),
                false
        );
    }

    @Override
    public boolean isKnownUser(QIrcUser ircuser) {
        return userModes.containsKey(ircuser);
    }

    @Override
    public boolean isValidChannelUserMode(@Nullable String mode) {
        return mode != null && mode.length() == 1;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String topic() {
        return topic;
    }

    @Override
    public String password() {
        return password;
    }

    @Override
    public boolean encrypted() {
        return encrypted;
    }

    @Override
    public QNetwork network() {
        return network;
    }

    @NonNull
    @Override
    public List<QIrcUser> ircUsers() {
        return new ArrayList<>(userModes.keySet());
    }

    @Override
    public String userModes(QIrcUser ircuser) {
        if (userModes.containsKey(ircuser))
            return Joiner.on("").join(userModes.get(ircuser));
        else
            return "";
    }

    @Override
    public String userModes(String nick) {
        return userModes(network().ircUser(nick));
    }

    @Override
    public boolean hasMode(char mode) {
        Network.ChannelModeType modeType = network().channelModeType(mode);

        switch (modeType) {
            case NOT_A_CHANMODE:
                return false;
            case A_CHANMODE:
                return A_channelModes.containsKey(mode);
            case B_CHANMODE:
                return B_channelModes.containsKey(mode);
            case C_CHANMODE:
                return C_channelModes.containsKey(mode);
            case D_CHANMODE:
                return D_channelModes.contains(mode);
            default:
                return false;
        }
    }

    @Override
    public String modeValue(char mode) {
        QNetwork.ChannelModeType modeType = network().channelModeType(mode);

        switch (modeType) {
            case B_CHANMODE:
                if (B_channelModes.containsKey(mode))
                    return B_channelModes.get(mode);
                else
                    return "";
            case C_CHANMODE:
                if (C_channelModes.containsKey(mode))
                    return C_channelModes.get(mode);
                else
                    return "";
            default:
                return "";
        }
    }

    @Override
    public List<String> modeValueList(char mode) {
        QNetwork.ChannelModeType modeType = network().channelModeType(mode);
        switch (modeType) {
            case A_CHANMODE:
                if (A_channelModes.containsKey(mode))
                    return A_channelModes.get(mode);
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public String channelModeString() {
        List<String> params = new LinkedList<>();
        StringBuilder modeString = new StringBuilder("");
        for (Character mode : D_channelModes) {
            modeString.append(mode);
        }
        for (Map.Entry<Character, String> entry : C_channelModes.entrySet()) {
            modeString.append(entry.getKey());
            params.add(entry.getValue());
        }
        for (Map.Entry<Character, String> entry : B_channelModes.entrySet()) {
            modeString.append(entry.getKey());
            params.add(entry.getValue());
        }
        String result = modeString.toString();
        if (result.isEmpty())
            return result;
        else
            return String.format("+%s %s", result, Joiner.on(" ").join(params));
    }

    public String channelModeShort() {
        StringBuilder modeString = new StringBuilder("");
        for (Character mode : D_channelModes) {
            modeString.append(mode);
        }
        String result = modeString.toString();
        if (result.isEmpty())
            return result;
        else
            return String.format("+%s", result);
    }

    @Override
    public String codecForEncoding() {
        return codecForEncoding;
    }

    @Override
    public String codecForDecoding() {
        return codecForDecoding;
    }

    @Override
    public void setCodecForEncoding(String codecName) {
        this.codecForEncoding = codecName;
    }

    @Override
    public void setCodecForDecoding(String codecName) {
        this.codecForDecoding = codecName;
    }

    @Override
    public void _setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public void _setPassword(String password) {
        this.password = password;
    }

    @Override
    public void _setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    @Override
    public void _joinIrcUsers(@NonNull List<String> nicks, @NonNull List<String> modes) {
        assertEquals(nicks.size(), modes.size());
        for (int i = 0; i < nicks.size(); i++) {
            _joinIrcUser(network().ircUser(nicks.get(i)), modes.get(i));
        }
    }

    @Override
    public void _joinIrcUser(QIrcUser ircuser) {
        _joinIrcUser(ircuser, null);
    }

    public void _joinIrcUser(@Nullable QIrcUser ircuser, @NonNull String mode) {
        if (ircuser == null || userModes.containsKey(ircuser)) {
            _addUserMode(ircuser, mode);
        } else {
            userModes.put(ircuser, ModeUtils.toModes(mode));
            ircuser._joinChannel(this, true);
            _update();
        }
    }

    @Override
    public void _part(@NonNull QIrcUser ircuser) {
        if (isKnownUser(ircuser)) {
            userModes.remove(ircuser);
            ircuser.partChannel(this);

            if (network().isMe(ircuser) || userModes.isEmpty()) {
                Set<QIrcUser> users = userModes.keySet();
                userModes.clear();
                for (QIrcUser user : users) {
                    user.partChannel(this, true);
                }
                network().removeIrcChannel(this);
            }
            _update();
        }
    }

    @Override
    public void _part(String nick) {
        part(network().ircUser(nick));
    }

    @Override
    public void _setUserModes(QIrcUser ircuser, String modes) {
        if (isKnownUser(ircuser)) {

            userModes.put(ircuser, ModeUtils.toModes(modes));
            _update();
        }
    }

    @Override
    public void _setUserModes(String nick, String modes) {
        _setUserModes(network().ircUser(nick), modes);
    }

    @Override
    public void _addUserMode(QIrcUser ircuser, @NonNull String mode) {
        if (!isKnownUser(ircuser) || !isValidChannelUserMode(mode))
            return;

        if (!userModes.get(ircuser).contains(ModeUtils.toMode(mode))) {
            userModes.get(ircuser).add(ModeUtils.toMode(mode));
            _update();
        }
    }

    @Override
    public void _addUserMode(String nick, @NonNull String mode) {
        _addUserMode(network().ircUser(nick), mode);
    }

    @Override
    public void _removeUserMode(QIrcUser ircuser, @NonNull String mode) {
        if (!isKnownUser(ircuser) || !isValidChannelUserMode(mode))
            return;

        if (userModes.get(ircuser).contains(ModeUtils.toMode(mode))) {
            userModes.get(ircuser).remove(ModeUtils.toMode(mode));
            _update();
        }
    }

    @Override
    public void _removeUserMode(String nick, @NonNull String mode) {
        _removeUserMode(network().ircUser(nick), mode);
    }

    @Override
    public void _addChannelMode(char mode, String value) {
        QNetwork.ChannelModeType modeType = network().channelModeType(mode);

        switch (modeType) {
            case NOT_A_CHANMODE:
                return;
            case A_CHANMODE:
                if (!A_channelModes.containsKey(mode))
                    A_channelModes.put(mode, new ArrayList<>(Collections.singletonList(value)));
                else if (!A_channelModes.get(mode).contains(value))
                    A_channelModes.get(mode).add(value);
                break;

            case B_CHANMODE:
                B_channelModes.put(mode, value);
                break;

            case C_CHANMODE:
                C_channelModes.put(mode, value);
                break;

            case D_CHANMODE:
                D_channelModes.add(mode);
                break;
        }
        _update();
    }

    @Override
    public void _removeChannelMode(char mode, String value) {
        QNetwork.ChannelModeType modeType = network().channelModeType(mode);
        switch (modeType) {
            case NOT_A_CHANMODE:
                return;
            case A_CHANMODE:
                if (A_channelModes.containsKey(mode))
                    A_channelModes.get(mode).remove(value);
                break;

            case B_CHANMODE:
                B_channelModes.remove(mode);
                break;

            case C_CHANMODE:
                C_channelModes.remove(mode);
                break;

            case D_CHANMODE:
                D_channelModes.remove(mode);
                break;
        }
        _update();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(QNetwork network, QClient client) {
        this.client = client;
        this.network = network;


        if (cachedUserModes != null) {
            for (String username : cachedUserModes.keySet()) {
                userModes.put(network().ircUser(username), ModeUtils.toModes(cachedUserModes.get(username)));
            }
        }
        if (cachedChanModes != null) {
            if (cachedChanModes.get("A") != null)
            A_channelModes = (Map<Character, List<String>>) cachedChanModes.get("A");

            if (cachedChanModes.get("B") != null)
            B_channelModes = (Map<Character, String>) cachedChanModes.get("B");

            if (cachedChanModes.get("C") != null)
            C_channelModes = (Map<Character, String>) cachedChanModes.get("C");

            if (cachedChanModes.get("D") != null)
            D_channelModes = ModeUtils.toModes((String) cachedChanModes.get("D"));
        }

        cachedUserModes = null;
        cachedChanModes = null;

        this.network._addIrcChannel(this);

        client.bufferManager().postInit(network.networkId() + "/" + name(), this);
        _update();
    }

    @Override
    public void update(@NonNull Map<String, QVariant> from) {
        update(IrcChannelSerializer.get().fromDatastream(from));
    }

    @Override
    public void update(IrcChannel from) {

    }

    @NonNull
    public Map<String, String> userModes() {
        Map<String, String> result = new HashMap<>();
        for (QIrcUser user : userModes.keySet()) {
            result.put(user.nick(), userModes(user));
        }
        return result;
    }

    @NonNull
    public Map<String, Object> chanModes() {
        Map<String, Object> result = new HashMap<>();
        result.put("A", A_channelModes);
        result.put("B", B_channelModes);
        result.put("C", C_channelModes);
        result.put("D", ModeUtils.fromModes(D_channelModes));
        return result;
    }

    @Override
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull QClient client) {
        super.init(objectName, provider, client);
        String[] split = objectName.split("/", 2);
        assertEquals(split.length, 2);
        init(client.networkManager().network(Integer.parseInt(split[0])), client);
    }
}
