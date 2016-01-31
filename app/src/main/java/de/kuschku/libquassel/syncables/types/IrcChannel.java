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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.syncables.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.Synced;
import de.kuschku.libquassel.syncables.serializers.IrcChannelSerializer;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class IrcChannel extends SyncableObject<IrcChannel> {
    @Synced
    private String name;
    @Synced
    private String topic;
    @Synced
    private String password;
    @Synced
    private Map<String, String> UserModes;
    @Synced
    private ChanModes chanModes;
    @Synced
    private boolean encrypted;

    @Nullable
    private Network network;

    public IrcChannel(String name, String topic, String password, Map<String, String> userModes,
                      @NonNull Map<String, Object> chanModes, boolean encrypted) {
        this.name = name;
        this.topic = topic;
        this.password = password;
        this.UserModes = userModes;
        this.chanModes = new ChanModes(chanModes);
        this.encrypted = encrypted;
    }

    @NonNull
    @Override
    public String toString() {
        return "IrcChannel{" +
                "name='" + name + '\'' +
                ", topic='" + topic + '\'' +
                ", password='" + password + '\'' +
                ", UserModes=" + UserModes +
                ", ChanModes=" + chanModes +
                ", encrypted=" + encrypted +
                '}';
    }

    @Nullable
    public Network getNetwork() {
        return network;
    }

    public void setNetwork(@Nullable Network network) {
        this.network = network;
    }

    public void joinIrcUsers(@NonNull List<String> users, @NonNull List<String> modes) {
        for (int i = 0; i < users.size(); i++) {
            joinIrcUser(users.get(i), modes.get(i));
        }
    }

    public void joinIrcUser(String nick, String mode) {
        UserModes.put(nick, mode);
    }

    public void part(String user) {
        UserModes.remove(user);
    }

    public void setUserModes(final String nick, final String modes) {
        UserModes.put(nick, modes);
    }

    public void addUserMode(String nick, @NonNull String mode) {
        if (UserModes.get(nick) == null)
            UserModes.put(nick, mode);
        else if (!UserModes.get(nick).contains(mode))
            UserModes.put(nick, UserModes.get(nick) + mode);
    }

    public void removeUserMode(String nick, @NonNull String mode) {
        if (UserModes.get(nick) == null && UserModes.get(nick).contains(mode))
            UserModes.put(nick, UserModes.get(nick).replace(mode, ""));
    }

    public void addChannelMode(Character mode, String params) {
        addChannelMode(String.copyValueOf(new char[]{mode}), params);
    }

    public void addChannelMode(char mode, String params) {
        addChannelMode(String.copyValueOf(new char[]{mode}), params);
    }

    public void addChannelMode(@NonNull String mode, String params) {
        assertNotNull(network);

        Network.ChannelModeType type = network.channelModeType(mode);
        switch (type) {
            case NOT_A_CHANMODE:
                return;
            case A_CHANMODE:
                if (!chanModes.A.containsKey(mode)) {
                    chanModes.A.put(mode, new ArrayList<>(Collections.singleton(params)));
                } else {
                    chanModes.A.get(mode).add(params);
                }
                break;
            case B_CHANMODE:
                chanModes.B.put(mode, params);
                break;
            case C_CHANMODE:
                chanModes.C.put(mode, params);
                break;
            case D_CHANMODE:

                chanModes.D.add(mode);
                break;
        }
    }

    public void removeChannelMode(Character mode, String params) {
        removeChannelMode(String.copyValueOf(new char[]{mode}), params);
    }

    public void removeChannelMode(char mode, String params) {
        removeChannelMode(String.copyValueOf(new char[]{mode}), params);
    }

    public void removeChannelMode(@NonNull String mode, String params) {
        assertNotNull(network);

        Network.ChannelModeType type = network.channelModeType(mode);
        switch (type) {
            case NOT_A_CHANMODE:
                return;
            case A_CHANMODE:
                if (chanModes.A.containsKey(mode))
                    chanModes.A.get(mode).removeAll(Collections.singleton(params));
                break;
            case B_CHANMODE:
                chanModes.B.remove(mode);
                break;
            case C_CHANMODE:
                chanModes.C.remove(mode);
                break;
            case D_CHANMODE:
                chanModes.D.remove(mode);
                break;
        }
    }

    public boolean hasMode(Character mode) {
        return hasMode(String.copyValueOf(new char[]{mode}));
    }

    public boolean hasMode(char mode) {
        return hasMode(String.copyValueOf(new char[]{mode}));
    }

    public boolean hasMode(@NonNull String mode) {
        assertNotNull(network);

        Network.ChannelModeType type = network.channelModeType(mode);
        switch (type) {
            case A_CHANMODE:
                return chanModes.A.containsKey(mode);
            case B_CHANMODE:
                return chanModes.B.containsKey(mode);
            case C_CHANMODE:
                return chanModes.C.containsKey(mode);
            case D_CHANMODE:
                return chanModes.D.contains(mode);

            default:
                return false;
        }
    }

    public void renameUser(String oldNick, String newNick) {
        UserModes.put(newNick, UserModes.get(oldNick));
        UserModes.remove(oldNick);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, String> getUserModes() {
        return UserModes;
    }

    public void setUserModes(Map<String, String> userModes) {
        UserModes = userModes;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    @Override
    public void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {

    }

    @Override
    public void update(@NonNull IrcChannel from) {
        this.name = from.name;
        this.topic = from.topic;
        this.password = from.password;
        this.UserModes = from.UserModes;
        this.chanModes = from.chanModes;
        this.encrypted = from.encrypted;
    }

    @Override
    public void update(@NonNull Map<String, QVariant> from) {
        update(IrcChannelSerializer.get().fromDatastream(from));
    }

    public ChanModes getChanModes() {
        return chanModes;
    }

    public static class ChanModes {
        @NonNull
        public final Map<String, List<String>> A;
        @NonNull
        public final Map<String, String> B;
        @NonNull
        public final Map<String, String> C;
        @NonNull
        public final Set<String> D;

        @SuppressWarnings("unchecked")
        public ChanModes(@NonNull Map<String, Object> rawModes) {
            A = (Map<String, List<String>>) rawModes.get("A");
            B = (Map<String, String>) rawModes.get("B");
            C = (Map<String, String>) rawModes.get("C");
            D = new HashSet<>(Arrays.asList(((String) rawModes.get("D")).split("")));
        }

        @NonNull
        public Map<String, Object> toMap() {
            Map<String, Object> out = new HashMap<>();
            out.put("A", A);
            out.put("B", B);
            out.put("C", C);
            out.put("D", D);
            return out;
        }

        @NonNull
        @Override
        public String toString() {
            return "ChanModes{" +
                    "A=" + A +
                    ", B=" + B +
                    ", C=" + C +
                    ", D=" + D +
                    '}';
        }
    }
}
