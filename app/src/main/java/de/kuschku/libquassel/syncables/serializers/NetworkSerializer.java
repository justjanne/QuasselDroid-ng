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

package de.kuschku.libquassel.syncables.serializers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.objects.serializers.StringObjectMapSerializer;
import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.impl.IrcChannel;
import de.kuschku.libquassel.syncables.types.impl.IrcUser;
import de.kuschku.libquassel.syncables.types.impl.Network;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class NetworkSerializer implements ObjectSerializer<Network> {
    @NonNull
    private static final NetworkSerializer serializer = new NetworkSerializer();

    private NetworkSerializer() {
    }

    @NonNull
    public static NetworkSerializer get() {
        return serializer;
    }

    @Nullable
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull Network data) {
        // FIXME: IMPLEMENT
        throw new IllegalArgumentException();
    }

    @NonNull
    @Override
    public Network fromDatastream(@NonNull Map<String, QVariant> map) {
        final Map<String, QVariant<Map<String, QVariant<List>>>> usersAndChannels = ((Map<String, QVariant<Map<String, QVariant<List>>>>) map.get("IrcUsersAndChannels").data);

        final List<QIrcChannel> channels = extractChannels(QVariant.orNull(usersAndChannels.get("Channels")));
        final List<QIrcUser> users = extractUsers(QVariant.orNull(usersAndChannels.get("Users")));

        final Map<String, QIrcChannel> channelMap = new HashMap<>(channels.size());
        for (QIrcChannel channel : channels) {
            channelMap.put(channel.name(), channel);
        }

        final Map<String, QIrcUser> userMap = new HashMap<>(users.size());
        for (QIrcUser user : users) {
            userMap.put(user.nick(), user);
        }

        return new Network(
                channelMap,
                userMap,
                (List<NetworkServer>) map.get("ServerList").data,
                StringObjectMapSerializer.<String>get().fromLegacy((Map<String, QVariant>) map.get("Supports").data),
                (int) map.get("connectionState").data,
                (String) map.get("currentServer").data,
                (boolean) map.get("isConnected").data,
                (int) map.get("latency").data,
                (String) map.get("myNick").data,
                NetworkInfoSerializer.get().fromLegacy(map)
        );
    }

    @NonNull
    private List<QIrcUser> extractUsers(@Nullable Map<String, QVariant<List>> users) {
        final List<QIrcUser> ircUsers;
        if (users == null)
            ircUsers = new ArrayList<>();
        else {
            final int max = users.get("server").data.size();
            ircUsers = new ArrayList<>(max);
            for (int i = 0; i < max; i++) {
                ircUsers.add(new IrcUser(
                        (String) users.get("server").data.get(i),
                        (String) users.get("ircOperator").data.get(i),
                        (boolean) users.get("away").data.get(i),
                        (int) users.get("lastAwayMessage").data.get(i),
                        (DateTime) users.get("idleTime").data.get(i),
                        (String) users.get("whoisServiceReply").data.get(i),
                        (String) users.get("suserHost").data.get(i),
                        (String) users.get("nick").data.get(i),
                        (String) users.get("realName").data.get(i),
                        (String) users.get("awayMessage").data.get(i),
                        (DateTime) users.get("loginTime").data.get(i),
                        (boolean) users.get("encrypted").data.get(i),
                        (List<String>) users.get("channels").data.get(i),
                        (String) users.get("host").data.get(i),
                        (String) users.get("userModes").data.get(i),
                        (String) users.get("user").data.get(i)
                ));
            }
        }
        return ircUsers;
    }

    @NonNull
    private List<QIrcChannel> extractChannels(@Nullable Map<String, QVariant<List>> channels) {
        final List<QIrcChannel> ircChannels;
        if (channels == null)
            ircChannels = new ArrayList<>();
        else {
            final int max = channels.get("name").data.size();
            ircChannels = new ArrayList<>(max);
            for (int i = 0; i < max; i++) {
                ircChannels.add(new IrcChannel(
                        (String) channels.get("name").data.get(i),
                        (String) channels.get("topic").data.get(i),
                        (String) channels.get("password").data.get(i),
                        StringObjectMapSerializer.<String>get().fromLegacy((Map<String, QVariant>) channels.get("UserModes").data.get(i)),
                        StringObjectMapSerializer.get().fromLegacy((Map<String, QVariant>) channels.get("ChanModes").data.get(i)),
                        (boolean) channels.get("encrypted").data.get(i)
                ));
            }
        }
        return ircChannels;
    }

    @NonNull
    @Override
    public Network fromLegacy(@NonNull Map<String, QVariant> map) {
        final Map<String, QVariant<Map<String, QVariant<Map<String, QVariant>>>>> usersAndChannels = ((QVariant<Map<String, QVariant<Map<String, QVariant<Map<String, QVariant>>>>>>) map.get("QIrcUsersAndChannels")).data;
        final Map<String, QVariant<Map<String, QVariant>>> wrappedChannels = usersAndChannels.get("channels").data;
        final Map<String, QVariant<Map<String, QVariant>>> wrappedUsers = usersAndChannels.get("users").data;
        final Map<String, QIrcChannel> channels = new HashMap<>(wrappedChannels.size());
        for (Map.Entry<String, QVariant<Map<String, QVariant>>> entry : wrappedChannels.entrySet()) {
            final QIrcChannel ircChannel = IrcChannelSerializer.get().fromLegacy(entry.getValue().data);
            channels.put(ircChannel.name(), ircChannel);
        }
        final Map<String, QIrcUser> users = new HashMap<>(wrappedUsers.size());
        for (Map.Entry<String, QVariant<Map<String, QVariant>>> entry : wrappedUsers.entrySet()) {
            final QIrcUser ircUser = IrcUserSerializer.get().fromLegacy(entry.getValue().data);
            users.put(ircUser.nick(), ircUser);
        }
        final Map<String, String> supports = StringObjectMapSerializer.<String>get().fromLegacy((Map<String, QVariant>) map.get("Supports").data);
        return new Network(
                channels,
                users,
                supports,
                (int) map.get("connectionState").data,
                (String) map.get("currentServer").data,
                (boolean) map.get("isConnected").data,
                (int) map.get("latency").data,
                (String) map.get("myNick").data,
                NetworkInfoSerializer.get().fromLegacy(map)
        );
    }

    @Override
    public Network from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
