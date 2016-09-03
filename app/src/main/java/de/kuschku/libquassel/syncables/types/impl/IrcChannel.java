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
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.localtypes.buffers.ChannelBuffer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.IrcChannelSerializer;
import de.kuschku.libquassel.syncables.types.abstracts.AIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.util.irc.ModeUtils;
import de.kuschku.util.observables.lists.ObservableSet;

import static de.kuschku.util.AndroidAssert.assertEquals;

public class IrcChannel extends AIrcChannel {
    private final String name;
    @NonNull
    private final Map<String, Set<Character>> userModes = new HashMap<>();
    private final ObservableSet<String> users = new ObservableSet<>();
    @NonNull
    public Map<Character, List<String>> A_channelModes = new HashMap<>();
    @NonNull
    public Map<Character, String> B_channelModes = new HashMap<>();
    @NonNull
    public Map<Character, String> C_channelModes = new HashMap<>();
    @NonNull
    public Set<Character> D_channelModes = new HashSet<>();
    private String topic;
    private String password;
    private boolean encrypted;
    private QNetwork network;
    private String codecForEncoding;
    private String codecForDecoding;
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
    public static IrcChannel create(@NonNull String channelName) {
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
    public boolean isKnownUser(@Nullable QIrcUser ircuser) {
        return ircuser != null && userModes.containsKey(ircuser.nick());
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
    public List<String> ircUsers() {
        return new ArrayList<>(userModes.keySet());
    }

    @Override
    public String userModes(String nick) {
        if (userModes.containsKey(nick))
            return Joiner.on("").join(userModes.get(nick));
        else
            return "";
    }

    @Override
    public String userModes(@NonNull QIrcUser ircuser) {
        return userModes(ircuser.nick());
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
        _update();
        updateDisplay();
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
        if (ircuser == null || userModes.containsKey(ircuser.nick())) {
            _addUserMode(ircuser, mode);
        } else {
            userModes.put(ircuser.nick(), ModeUtils.toModes(mode));
            users.add(ircuser.nick());
            ircuser._joinChannel(this, true);
            _update();
        }
    }

    @Override
    public void _part(@NonNull QIrcUser ircuser) {
        if (isKnownUser(ircuser)) {
            userModes.remove(ircuser.nick());
            users.remove(ircuser.nick());
            ircuser._partChannel(this);

            if (network().isMe(ircuser) || userModes.isEmpty()) {
                Set<String> users = userModes.keySet();
                userModes.clear();
                users.clear();
                for (String user : users) {
                    network().ircUser(user)._partChannel(this, true);
                }
                network()._removeIrcChannel(this);
            }
            _update();
        }
    }

    @Override
    public void _part(String nick) {
        part(network().ircUser(nick));
    }

    @Override
    public void _setUserModes(@NonNull QIrcUser ircuser, String modes) {
        if (isKnownUser(ircuser)) {

            userModes.put(ircuser.nick(), ModeUtils.toModes(modes));
            users.add(ircuser.nick());
            _update();
        }
    }

    @Override
    public void _setUserModes(String nick, String modes) {
        _setUserModes(network().ircUser(nick), modes);
    }

    @Override
    public void _addUserMode(@NonNull QIrcUser ircuser, @NonNull String mode) {
        if (!isKnownUser(ircuser) || !isValidChannelUserMode(mode))
            return;

        if (!userModes.get(ircuser.nick()).contains(ModeUtils.toMode(mode))) {
            userModes.get(ircuser.nick()).add(ModeUtils.toMode(mode));
            users.notifyItemChanged(ircuser.nick());
            _update();
        }
    }

    @Override
    public void _addUserMode(String nick, @NonNull String mode) {
        _addUserMode(network().ircUser(nick), mode);
    }

    @Override
    public void _removeUserMode(@NonNull QIrcUser ircuser, @NonNull String mode) {
        if (!isKnownUser(ircuser) || !isValidChannelUserMode(mode))
            return;

        if (userModes.get(ircuser.nick()).contains(ModeUtils.toMode(mode))) {
            userModes.get(ircuser.nick()).remove(ModeUtils.toMode(mode));
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
    public void init(QNetwork network, Client client) {
        this.client = client;
        this.network = network;


        /* TODO: Use just the nick in userModes and users instead – that should make sync things a lot easier */
        if (cachedUserModes != null) {
            for (String username : cachedUserModes.keySet()) {
                QIrcUser ircUser = network().ircUser(username);
                if (ircUser != null) {
                    userModes.put(ircUser.nick(), ModeUtils.toModes(cachedUserModes.get(username)));
                    users.add(ircUser.nick());
                }
            }
        }
        if (cachedChanModes != null) {
            if (cachedChanModes.get("A") != null) {
                for (Map.Entry<String, QVariant<List<String>>> entry : ((Map<String, QVariant<List<String>>>) cachedChanModes.get("A")).entrySet()) {
                    A_channelModes.put(entry.getKey().charAt(0), entry.getValue().data);
                }
            }

            if (cachedChanModes.get("B") != null)
                for (Map.Entry<String, QVariant<String>> entry : ((Map<String, QVariant<String>>) cachedChanModes.get("B")).entrySet()) {
                    B_channelModes.put(entry.getKey().charAt(0), entry.getValue().data);
                }

            if (cachedChanModes.get("C") != null)
                for (Map.Entry<String, QVariant<String>> entry : ((Map<String, QVariant<String>>) cachedChanModes.get("C")).entrySet()) {
                    C_channelModes.put(entry.getKey().charAt(0), entry.getValue().data);
                }

            if (cachedChanModes.get("D") != null)
                D_channelModes = ModeUtils.toModes((String) cachedChanModes.get("D"));
        }

        cachedUserModes = null;
        cachedChanModes = null;

        this.network._addIrcChannel(this);
        _update();
    }

    @Override
    public void _update(@NonNull Map<String, QVariant> from) {
        _update(IrcChannelSerializer.get().fromDatastream(from));
    }

    @Override
    public void _update(QIrcChannel from) {

    }

    @NonNull
    public Map<String, String> userModes() {
        Map<String, String> result = new HashMap<>();
        for (String nick : userModes.keySet()) {
            result.put(nick, userModes(nick));
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
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull Client client) {
        super.init(objectName, provider, client);
        String[] split = objectName.split("/", 2);
        assertEquals(split.length, 2);
        init(client.networkManager().network(Integer.parseInt(split[0])), client);
    }

    @NonNull
    public ObservableSet<String> users() {
        return users;
    }

    @Override
    public void _ircUserNickChanged(String oldNick, String newNick) {
        users.remove(oldNick);
        users.add(newNick);
        userModes.put(newNick, userModes.get(oldNick));
    }

    @Override
    public List<Character> modeList() {
        List<Character> modes = new ArrayList<>();
        modes.addAll(D_channelModes);
        modes.addAll(C_channelModes.keySet());
        modes.addAll(B_channelModes.keySet());
        modes.addAll(A_channelModes.keySet());
        return modes;
    }

    @Override
    public void _update() {
        super._update();
    }

    private void updateDisplay() {
        if (client.connectionStatus() != ConnectionChangeEvent.Status.INITIALIZING_DATA) {
            ChannelBuffer buffer = client.bufferManager().channel(this);
            if (buffer != null) {
                for (QBufferViewConfig qBufferViewConfig : client.bufferViewManager().bufferViewConfigs()) {
                    qBufferViewConfig.bufferIds().notifyItemChanged(buffer.getInfo().id);
                }
            }
        }
    }
}