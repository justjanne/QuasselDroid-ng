package de.kuschku.libquassel.syncables.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.IrcUserSerializer;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class IrcUser extends SyncableObject<IrcUser> {
    private String server;
    private String ircOperator;
    private boolean away;
    private int lastAwayMessage;
    private DateTime idleTime;
    private String whoisServiceReply;
    private String suserHost;
    private String nick;
    private String realName;
    private String awayMessage;
    private DateTime loginTime;
    private boolean encrypted;
    @NonNull
    private List<String> channels;
    private String host;
    private String userModes;
    private String user;

    private Network network;

    public IrcUser(String server, String ircOperator, boolean away, int lastAwayMessage, DateTime idleTime,
                   String whoisServiceReply, String suserHost, String nick, String realName, String awayMessage,
                   DateTime loginTime, boolean encrypted, @NonNull List<String> channels, String host, String userModes,
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

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getIrcOperator() {
        return ircOperator;
    }

    public void setIrcOperator(String ircOperator) {
        this.ircOperator = ircOperator;
    }

    public boolean isAway() {
        return away;
    }

    public void setAway(boolean away) {
        this.away = away;
    }

    public int getLastAwayMessage() {
        return lastAwayMessage;
    }

    public void setLastAwayMessage(int lastAwayMessage) {
        this.lastAwayMessage = lastAwayMessage;
    }

    public DateTime getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(DateTime idleTime) {
        this.idleTime = idleTime;
    }

    public String getWhoisServiceReply() {
        return whoisServiceReply;
    }

    public void setWhoisServiceReply(String whoisServiceReply) {
        this.whoisServiceReply = whoisServiceReply;
    }

    public String getSuserHost() {
        return suserHost;
    }

    public void setSuserHost(String suserHost) {
        this.suserHost = suserHost;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    /* BEGIN SYNC */

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAwayMessage() {
        return awayMessage;
    }

    public void setAwayMessage(String awayMessage) {
        this.awayMessage = awayMessage;
    }

    public DateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(DateTime loginTime) {
        this.loginTime = loginTime;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    @NonNull
    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(@NonNull List<String> channels) {
        this.channels = channels;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserModes() {
        return userModes;
    }

    public void setUserModes(String userModes) {
        this.userModes = userModes;
    }

    public String getUser() {
        return user;
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

    public void renameObject(@Nullable String objectName) {
        assertNotNull(objectName);

        // TODO: Check if this is designed well
        String nick = objectName.split("/")[1];
        network.renameUser(this.getObjectName(), nick);
        for (String channelName : channels) {
            IrcChannel channel = network.getChannels().get(channelName);
            channel.renameUser(this.nick, nick);
        }
        super.setObjectName(nick);
        setNick(nick);
    }

    @Override
    public void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {
        final String networkId = function.objectName.split("/")[0];
        final Network network = client.getNetwork(Integer.parseInt(networkId));
        setObjectName(function.objectName);
        setNetwork(network);
    }

    @Override
    public void update(@NonNull IrcUser from) {
        this.server = from.server;
        this.ircOperator = from.ircOperator;
        this.away = from.away;
        this.lastAwayMessage = from.lastAwayMessage;
        this.idleTime = from.idleTime;
        this.whoisServiceReply = from.whoisServiceReply;
        this.suserHost = from.suserHost;
        this.nick = from.nick;
        this.realName = from.realName;
        this.awayMessage = from.awayMessage;
        this.loginTime = from.loginTime;
        this.encrypted = from.encrypted;
        this.channels = from.channels;
        this.host = from.host;
        this.userModes = from.userModes;
        this.user = from.user;
    }

    @Override
    public void update(@NonNull Map<String, QVariant> from) {
        update(IrcUserSerializer.get().fromDatastream(from));
    }

    public void quit() {
        network.quit(this.nick);
    }

    public void joinChannel(String channelName) {
        joinChannel(network.getChannels().get(channelName));
    }

    public void joinChannel(@NonNull IrcChannel channel) {
        channel.joinIrcUser(this.nick, null);
    }

    public void partChannel(String channelName) {
        partChannel(network.getChannels().get(channelName));
    }

    public void partChannel(@NonNull IrcChannel channel) {
        channel.part(this.nick);
    }

    @NonNull
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
