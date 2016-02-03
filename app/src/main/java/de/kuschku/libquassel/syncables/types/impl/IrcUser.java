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
import android.util.SparseArray;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.QClient;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.abstracts.AIrcUser;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.util.backports.Objects;
import de.kuschku.util.irc.IrcUserUtils;
import de.kuschku.util.irc.ModeUtils;

import static de.kuschku.util.AndroidAssert.assertEquals;

public class IrcUser extends AIrcUser<IrcUser> {
    @NonNull
    private final SparseArray<DateTime> lastActivity = new SparseArray<>();
    @NonNull
    private final SparseArray<DateTime> lastSpokenTo = new SparseArray<>();
    private final List<String> cachedChannels;
    private String user;
    private String host;
    private String nick;
    private String realName;
    private boolean away;
    private String awayMessage;
    private DateTime idleTime;
    private DateTime loginTime;
    private String server;
    private String ircOperator;
    private int lastAwayMessage;
    private String whoisServiceReply;
    private String suserHost;
    private boolean encrypted;
    private QNetwork network;
    private Set<Character> userModes;
    private Set<QIrcChannel> channels;

    public IrcUser(String server, String ircOperator, boolean away, int lastAwayMessage, DateTime idleTime, String whoisServiceReply, String suserHost, String nick, String realName, String awayMessage, DateTime loginTime, boolean encrypted, List<String> channels, String host, String userModes, String user) {
        this.server = server;
        this.ircOperator = ircOperator;
        this.away = away;
        this.lastAwayMessage = lastAwayMessage;
        this.idleTime = idleTime;
        this.whoisServiceReply = whoisServiceReply;
        this.suserHost = suserHost;
        this.nick = nick;
        this.realName = realName;
        this.awayMessage = awayMessage;
        this.loginTime = loginTime;
        this.encrypted = encrypted;
        this.cachedChannels = channels;
        this.host = host;
        this.userModes = ModeUtils.toModes(userModes);
        this.user = user;
    }

    @NonNull
    public static IrcUser create(@NonNull String mask) {
        String nick;
        String host;
        String user;

        // Sometimes this is invoked with a nick instead, so we check
        if (mask.contains("@")) {
            nick = IrcUserUtils.getNick(mask);
            host = IrcUserUtils.getHost(mask);
            user = IrcUserUtils.getUser(mask);
        } else {
            nick = mask;
            host = null;
            user = null;
        }

        return new IrcUser(
                null,
                null,
                false,
                -1,
                null,
                null,
                null,
                nick,
                null,
                null,
                null,
                false,
                null,
                host,
                null,
                user
        );
    }

    @Override
    public String user() {
        return user;
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public String nick() {
        return nick;
    }

    @Override
    public String realName() {
        return realName;
    }

    @Override
    public String hostmask() {
        return String.format("%s!%s@%s", nick(), user(), host());
    }

    @Override
    public boolean isAway() {
        return away;
    }

    @Override
    public String awayMessage() {
        return awayMessage;
    }

    @Override
    public DateTime idleTime() {
        return idleTime;
    }

    @Override
    public DateTime loginTime() {
        return loginTime;
    }

    @Override
    public String server() {
        return server;
    }

    @Override
    public String ircOperator() {
        return ircOperator;
    }

    @Override
    public int lastAwayMessage() {
        return lastAwayMessage;
    }

    @Override
    public String whoisServiceReply() {
        return whoisServiceReply;
    }

    @Override
    public String suserHost() {
        return suserHost;
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
    public String userModes() {
        return ModeUtils.fromModes(userModes);
    }

    @NonNull
    @Override
    public List<String> channels() {
        List<String> chanList = new ArrayList<>(channels.size());
        for (QIrcChannel channel : channels) {
            chanList.add(channel.name());
        }
        return chanList;
    }

    @Override
    public DateTime lastChannelActivity(int id) {
        return lastActivity.get(id);
    }

    @Override
    public void setLastChannelActivity(int id, DateTime time) {
        lastActivity.put(id, time);
        _update();
    }

    @Override
    public DateTime lastSpokenTo(int id) {
        return lastSpokenTo.get(id);
    }

    @Override
    public void setLastSpokenTo(int id, DateTime time) {
        lastSpokenTo.put(id, time);
        _update();
    }

    @Override
    public void _setUser(String user) {
        this.user = user;
        _update();
    }

    @Override
    public void _setHost(String host) {
        this.host = host;
        _update();
    }

    @Override
    public void _setNick(String nick) {
        this.nick = nick;
        updateObjectName();
        _update();
    }

    private void updateObjectName() {
        setObjectName(getObjectName());
        _update();
    }

    @Nullable
    @Override
    public String getObjectName() {
        return String.format(Locale.US, "%d/%s", network().networkId(), nick());
    }

    @Override
    public void _setRealName(String realName) {
        this.realName = realName;
        _update();
    }

    @Override
    public void _setAway(boolean away) {
        this.away = away;
        _update();
    }

    @Override
    public void _setAwayMessage(String awayMessage) {
        this.awayMessage = awayMessage;
        _update();
    }

    @Override
    public void _setIdleTime(DateTime idleTime) {
        this.idleTime = idleTime;
        _update();
    }

    @Override
    public void _setLoginTime(DateTime loginTime) {
        this.loginTime = loginTime;
        _update();
    }

    @Override
    public void _setServer(String server) {
        this.server = server;
        _update();
    }

    @Override
    public void _setIrcOperator(String ircOperator) {
        this.ircOperator = ircOperator;
        _update();
    }

    @Override
    public void _setLastAwayMessage(int lastAwayMessage) {
        this.lastAwayMessage = lastAwayMessage;
        _update();
    }

    @Override
    public void _setWhoisServiceReply(String whoisServiceReply) {
        this.whoisServiceReply = whoisServiceReply;
        _update();
    }

    @Override
    public void _setSuserHost(String suserHost) {
        this.suserHost = suserHost;
        _update();
    }

    @Override
    public void _setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
        _update();
    }

    @Override
    public void _updateHostmask(@NonNull String mask) {
        if (Objects.equals(mask, hostmask()))
            return;

        setUser(IrcUserUtils.getUser(mask));
        setHost(IrcUserUtils.getHost(mask));
        _update();
    }

    @Override
    public void _setUserModes(String modes) {
        this.userModes = ModeUtils.toModes(modes);
        _update();
    }

    @Override
    public void _joinChannel(@NonNull QIrcChannel channel) {
        _joinChannel(channel, false);
        _update();
    }

    @Override
    public void _joinChannel(@NonNull QIrcChannel channel, boolean skip_channel_join) {
        if (!channels.contains(channel)) {
            channels.add(channel);
            if (!skip_channel_join)
                channel.joinIrcUser(this);
        }
        _update();
    }

    @Override
    public void _joinChannel(String channelname) {
        _joinChannel(network().newIrcChannel(channelname));
        _update();
    }

    @Override
    public void _partChannel(@NonNull QIrcChannel channel) {
        _partChannel(channel, false);
        _update();
    }

    @Override
    public void _partChannel(@NonNull QIrcChannel channel, boolean skip_channel_part) {
        if (channels.contains(channel)) {
            channels.remove(channel);
            if (!skip_channel_part)
                channel.joinIrcUser(this);
            if (channels.isEmpty() && !network().isMe(this))
                quit();
        }
        _update();
    }

    @Override
    public void _partChannel(String channelname) {
        _partChannel(network().ircChannel(channelname));
        _update();
    }

    @Override
    public void _quit() {
        List<QIrcChannel> channels = new ArrayList<>(this.channels);
        this.channels.clear();
        for (QIrcChannel channel : channels) {
            channel.part(this);
        }
        network().removeIrcUser(this);
        _update();
    }

    @Override
    public void _addUserModes(@Nullable String modes) {
        if (modes == null || modes.isEmpty())
            return;

        userModes.addAll(ModeUtils.toModes(modes));
        _update();
    }

    @Override
    public void _removeUserModes(@Nullable String modes) {
        if (modes == null || modes.isEmpty())
            return;

        userModes.removeAll(ModeUtils.toModes(modes));
        _update();
    }

    @Override
    public void init(QNetwork network) {
        this.network = network;
        channels = new HashSet<>();
        if (cachedChannels != null)
        for (String channelName : cachedChannels) {
            channels.add(network().newIrcChannel(channelName));
        }
        _update();
    }

    @Override
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull QClient client) {
        super.init(objectName, provider, client);
        String[] split = objectName.split("/");
        assertEquals(split.length, 2);
        init(client.networkManager().network(Integer.parseInt(split[0])));
    }

    @Override
    public void update(Map<String, QVariant> from) {
    }

    @Override
    public void update(IrcUser from) {
    }
}
