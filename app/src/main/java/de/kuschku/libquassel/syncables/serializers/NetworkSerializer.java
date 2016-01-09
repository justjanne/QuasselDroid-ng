package de.kuschku.libquassel.syncables.serializers;

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

public class NetworkSerializer implements ObjectSerializer<Network> {
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(Network data) {
        // TODO: Implement this
        return null;
    }

    @Override
    public Network fromDatastream(Map<String, QVariant> map) {
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
                (Map<String, String>) new StringObjectMapSerializer().fromLegacy((Map<String, QVariant>) map.get("Supports").data),
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

    private List<IrcUser> extractUsers(Map<String, QVariant<List>> users) {
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

    private List<IrcChannel> extractChannels(Map<String, QVariant<List>> channels) {
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
                        (Map<String, String>) channels.get("UserModes").data.get(i),
                        (Map<String, Object>) channels.get("ChanModes").data.get(i),
                        (boolean) channels.get("encrypted").data.get(i)
                ));
            }
        }
        return ircChannels;
    }

    @Override
    public Network fromLegacy(Map<String, QVariant> map) {
        final Map<String, QVariant<Map<String, QVariant<Map<String, QVariant>>>>> usersAndChannels = ((QVariant<Map<String, QVariant<Map<String, QVariant<Map<String, QVariant>>>>>>) map.get("IrcUsersAndChannels")).data;
        final Map<String, QVariant<Map<String, QVariant>>> wrappedChannels = usersAndChannels.get("channels").data;
        final Map<String, QVariant<Map<String, QVariant>>> wrappedUsers = usersAndChannels.get("users").data;
        final Map<String, IrcChannel> channels = new HashMap<>(wrappedChannels.size());
        for (Map.Entry<String, QVariant<Map<String, QVariant>>> entry : wrappedChannels.entrySet()) {
            final IrcChannel ircChannel = new IrcChannelSerializer().fromLegacy(entry.getValue().data);
            channels.put(ircChannel.name, ircChannel);
        }
        final Map<String, IrcUser> users = new HashMap<>(wrappedUsers.size());
        final Map<String, String> supports = (Map<String, String>) new StringObjectMapSerializer().fromLegacy((Map<String, QVariant>) map.get("Supports").data);
        Network network = new Network(
                channels,
                new HashMap<>(wrappedUsers.size()),
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
            final IrcUser ircUser = new IrcUserSerializer().fromLegacy(entry.getValue().data);
            ircUser.setNetwork(network);
        }
        return network;
    }

    @Override
    public Network from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
