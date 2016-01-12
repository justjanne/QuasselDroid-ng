package de.kuschku.libquassel.syncables.types;

import org.joda.time.DateTime;

import java.util.List;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;

public class IrcUser extends SyncableObject {
    public String server;
    public String ircOperator;
    public boolean away;
    public int lastAwayMessage;
    public DateTime idleTime;
    public String whoisServiceReply;
    public String suserHost;
    public String nick;
    public String realName;
    public String awayMessage;
    public DateTime loginTime;
    public boolean encrypted;
    public List<String> channels;
    public String host;
    public String userModes;
    public String user;

    Network network;

    public IrcUser(String server, String ircOperator, boolean away, int lastAwayMessage, DateTime idleTime,
                   String whoisServiceReply, String suserHost, String nick, String realName, String awayMessage,
                   DateTime loginTime, boolean encrypted, List<String> channels, String host, String userModes,
                   String user) {
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
        this.channels = channels;
        this.host = host;
        this.userModes = userModes;
        this.user = user;
    }

    /* BEGIN SYNC */

    public void setServer(String server) {
        this.server = server;
    }

    public void setIrcOperator(String ircOperator) {
        this.ircOperator = ircOperator;
    }

    public void setAway(boolean away) {
        this.away = away;
    }

    public void setLastAwayMessage(int lastAwayMessage) {
        this.lastAwayMessage = lastAwayMessage;
    }

    public void setIdleTime(DateTime idleTime) {
        this.idleTime = idleTime;
    }

    public void setWhoisServiceReply(String whoisServiceReply) {
        this.whoisServiceReply = whoisServiceReply;
    }

    public void setSuserHost(String suserHost) {
        this.suserHost = suserHost;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setAwayMessage(String awayMessage) {
        this.awayMessage = awayMessage;
    }

    public void setLoginTime(DateTime loginTime) {
        this.loginTime = loginTime;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUserModes(String userModes) {
        this.userModes = userModes;
    }

    public void setUser(String user) {
        this.user = user;
    }

    /* END SYNC */

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
        this.network.getUsers().put(nick, this);
    }

    public void renameObject(String objectName) {
        String nick = objectName.split("/")[1];
        network.renameUser(this.nick, nick);
        for (String channelName : channels) {
            IrcChannel channel = network.getChannels().get(channelName);
            channel.renameUser(this.nick, nick);
        }
        super.setObjectName(nick);
        setNick(nick);
    }

    @Override
    public void init(InitDataFunction function, BusProvider provider, Client client) {
        final String networkId = function.objectName.split("/")[0];
        final Network network = client.getNetwork(Integer.parseInt(networkId));
        setObjectName(function.objectName);
        setNetwork(network);
    }

    public void quit() {
        network.quit(this.nick);
    }

    public void joinChannel(String channelName) {
        joinChannel(network.getChannels().get(channelName));
    }

    public void joinChannel(IrcChannel channel) {
        channel.joinIrcUser(this.nick, null);
    }

    public void partChannel(String channelName) {
        partChannel(network.getChannels().get(channelName));
    }

    public void partChannel(IrcChannel channel) {
        channel.part(this.nick);
    }

    @Override
    public String toString() {
        return "IrcUser{" +
                "server='" + server + '\'' +
                ", ircOperator='" + ircOperator + '\'' +
                ", away=" + away +
                ", lastAwayMessage=" + lastAwayMessage +
                ", idleTime=" + idleTime +
                ", whoisServiceReply='" + whoisServiceReply + '\'' +
                ", suserHost='" + suserHost + '\'' +
                ", nick='" + nick + '\'' +
                ", realName='" + realName + '\'' +
                ", awayMessage='" + awayMessage + '\'' +
                ", loginTime=" + loginTime +
                ", encrypted=" + encrypted +
                ", channels=" + channels +
                ", host='" + host + '\'' +
                ", userModes='" + userModes + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
