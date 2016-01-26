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
import de.kuschku.libquassel.syncables.types.IrcChannel;
import de.kuschku.libquassel.syncables.types.IrcUser;
import de.kuschku.libquassel.syncables.types.Network;

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
        // FIXME: Implement this
        return null;
    }

    @NonNull
    @Override
    public Network fromDatastream(@NonNull Map<String, QVariant> map) {
        final Map<String, QVariant<Map<String, QVariant<List>>>> usersAndChannels = ((Map<String, QVariant<Map<String, QVariant<List>>>>) map.get("IrcUsersAndChannels").data);

        final List<IrcChannel> channels = extractChannels(QVariant.orNull(usersAndChannels.get("Channels")));
        final List<IrcUser> users = extractUsers(QVariant.orNull(usersAndChannels.get("Users")));

        final Map<String, IrcChannel> channelMap = new HashMap<>(channels.size());
        for (IrcChannel channel : channels) {
            channelMap.put(channel.name, channel);
        }

        final Map<String, IrcUser> userMap = new HashMap<>(users.size());
        Network network = new Network(
                channelMap,
                userMap,
                (List<NetworkServer>) map.get("ServerList").data,
                StringObjectMapSerializer.<String>get().fromLegacy((Map<String, QVariant>) map.get("Supports").data),
                (String) map.get("autoIdentifyPassword").data,
                (String) map.get("autoIdentifyService").data,
                (int) map.get("autoReconnectInterval").data,
                (short) map.get("autoReconnectRetries").data,
                (String) map.get("codecForDecoding").data,
                (String) map.get("codecForEncoding").data,
                (String) map.get("codecForServer").data,
                (int) map.get("connectionState").data,
                (String) map.get("currentServer").data,
                (int) map.get("identityId").data,
                (boolean) map.get("isConnected").data,
                (int) map.get("latency").data,
                (String) map.get("myNick").data,
                (String) map.get("networkName").data,
                (List<String>) map.get("perform").data,
                (boolean) map.get("rejoinChannels").data,
                (String) map.get("saslAccount").data,
                (String) map.get("saslPassword").data,
                (boolean) map.get("unlimitedReconnectRetries").data,
                (boolean) map.get("useAutoIdentify").data,
                (boolean) map.get("useAutoReconnect").data,
                (boolean) map.get("useRandomServer").data,
                (boolean) map.get("useSasl").data
        );
        for (IrcUser user : users) {
            user.setNetwork(network);
        }

        return network;
    }

    @NonNull
    private List<IrcUser> extractUsers(@Nullable Map<String, QVariant<List>> users) {
        final List<IrcUser> ircUsers;
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
    private List<IrcChannel> extractChannels(@Nullable Map<String, QVariant<List>> channels) {
        final List<IrcChannel> ircChannels;
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
        final Map<String, QVariant<Map<String, QVariant<Map<String, QVariant>>>>> usersAndChannels = ((QVariant<Map<String, QVariant<Map<String, QVariant<Map<String, QVariant>>>>>>) map.get("IrcUsersAndChannels")).data;
        final Map<String, QVariant<Map<String, QVariant>>> wrappedChannels = usersAndChannels.get("channels").data;
        final Map<String, QVariant<Map<String, QVariant>>> wrappedUsers = usersAndChannels.get("users").data;
        final Map<String, IrcChannel> channels = new HashMap<>(wrappedChannels.size());
        for (Map.Entry<String, QVariant<Map<String, QVariant>>> entry : wrappedChannels.entrySet()) {
            final IrcChannel ircChannel = IrcChannelSerializer.get().fromLegacy(entry.getValue().data);
            channels.put(ircChannel.name, ircChannel);
        }
        final Map<String, IrcUser> users = new HashMap<>(wrappedUsers.size());
        for (Map.Entry<String, QVariant<Map<String, QVariant>>> entry : wrappedUsers.entrySet()) {
            final IrcUser ircUser = IrcUserSerializer.get().fromLegacy(entry.getValue().data);
            users.put(ircUser.nick, ircUser);
        }
        final Map<String, String> supports = StringObjectMapSerializer.<String>get().fromLegacy((Map<String, QVariant>) map.get("Supports").data);
        Network network = new Network(
                channels,
                users,
                (List<NetworkServer>) map.get("ServerList").data,
                supports,
                (String) map.get("autoIdentifyPassword").data,
                (String) map.get("autoIdentifyService").data,
                (int) map.get("autoReconnectInterval").data,
                (short) map.get("autoReconnectRetries").data,
                (String) map.get("codecForDecoding").data,
                (String) map.get("codecForEncoding").data,
                (String) map.get("codecForServer").data,
                (int) map.get("connectionState").data,
                (String) map.get("currentServer").data,
                (int) map.get("identityId").data,
                (boolean) map.get("isConnected").data,
                (int) map.get("latency").data,
                (String) map.get("myNick").data,
                (String) map.get("networkName").data,
                (List<String>) map.get("perform").data,
                (boolean) map.get("rejoinChannels").data,
                (String) map.get("saslAccount").data,
                (String) map.get("saslPassword").data,
                (boolean) map.get("unlimitedReconnectRetries").data,
                (boolean) map.get("useAutoIdentify").data,
                (boolean) map.get("useAutoReconnect").data,
                (boolean) map.get("useRandomServer").data,
                (boolean) map.get("useSasl").data
        );
        for (Map.Entry<String, QVariant<Map<String, QVariant>>> entry : wrappedUsers.entrySet()) {
            final IrcUser ircUser = IrcUserSerializer.get().fromLegacy(entry.getValue().data);
            ircUser.setNetwork(network);
        }
        return network;
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
