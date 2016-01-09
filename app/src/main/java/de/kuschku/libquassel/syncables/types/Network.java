package de.kuschku.libquassel.syncables.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.localtypes.Buffer;
import de.kuschku.libquassel.objects.types.NetworkServer;

public class Network extends SyncableObject {
    private Map<String, IrcUser> users;
    private Map<String, IrcChannel> channels;

    private Set<Buffer> buffers = new HashSet<>();

    private List<NetworkServer> ServerList;
    private Map<String, String> Supports;

    private String autoIdentifyPassword;
    private String autoIdentifyService;

    private int autoReconnectInterval;
    private short autoReconnectRetries;

    private String codecForDecoding;
    private String codecForEncoding;
    private String codecForServer;

    private int connectionState;
    private String currentServer;
    private int identityId;
    private boolean isConnected;
    private int latency;
    private String myNick;
    private String networkName;
    private List<String> perform;
    private boolean rejoinChannels;

    private String saslAccount;
    private String saslPassword;

    private boolean unlimitedReconnectRetries;
    private boolean useAutoIdentify;
    private boolean useAutoReconnect;
    private boolean useRandomServer;
    private boolean useSasl;

    private Map<String, IrcMode> supportedModes;
    private int networkId;

    public Network(Map<String, IrcChannel> channels, Map<String, IrcUser> users, List<NetworkServer> serverList, Map<String, String> supports, String autoIdentifyPassword,
                   String autoIdentifyService, int autoReconnectInterval, short autoReconnectRetries,
                   String codecForDecoding, String codecForEncoding, String codecForServer, int connectionState,
                   String currentServer, int identityId, boolean isConnected, int latency, String myNick,
                   String networkName, List<String> perform, boolean rejoinChannels, String saslAccount,
                   String saslPassword, boolean unlimitedReconnectRetries, boolean useAutoIdentify,
                   boolean useAutoReconnect, boolean useRandomServer, boolean useSasl) {
        this.setChannels(channels);
        this.setUsers(users);
        this.setServerList(serverList);
        this.setSupports(supports);
        this.setAutoIdentifyPassword(autoIdentifyPassword);
        this.setAutoIdentifyService(autoIdentifyService);
        this.setAutoReconnectInterval(autoReconnectInterval);
        this.setAutoReconnectRetries(autoReconnectRetries);
        this.setCodecForDecoding(codecForDecoding);
        this.setCodecForEncoding(codecForEncoding);
        this.setCodecForServer(codecForServer);
        this.setConnectionState(connectionState);
        this.setCurrentServer(currentServer);
        this.setIdentityId(identityId);
        this.setConnected(isConnected);
        this.setLatency(latency);
        this.setMyNick(myNick);
        this.setNetworkName(networkName);
        this.setPerform(perform);
        this.setRejoinChannels(rejoinChannels);
        this.setSaslAccount(saslAccount);
        this.setSaslPassword(saslPassword);
        this.setUnlimitedReconnectRetries(unlimitedReconnectRetries);
        this.setUseAutoIdentify(useAutoIdentify);
        this.setUseAutoReconnect(useAutoReconnect);
        this.setUseRandomServer(useRandomServer);
        this.setUseSasl(useSasl);
        parsePrefix();
    }

    public void initUsers() {
        for (IrcUser user : getUsers().values()) {
            provider.dispatch(new InitRequestFunction("IrcUser", getNetworkId() + "/" + user.nick));
        }
    }

    private void parsePrefix() {
        if (!isConnected()) {
            setSupportedModes(new HashMap<>());
            return;
        } else if (!getSupports().containsKey("PREFIX")) {
            setSupportedModes(new HashMap<>());
            System.err.println("Network has no modes declared: " + getNetworkName());
            return;
        }

        final String prefixdata = getSupports().get("PREFIX").trim();
        final String[] split = prefixdata.substring(1).split("\\)");
        final String[] keys = split[0].trim().split("");
        final String[] values = split[1].trim().split("");
        if (keys.length != values.length) return;


        final Map<String, IrcMode> map = new HashMap<>(keys.length);
        for (int i = 1; i < keys.length; i++) {
            map.put(keys[i], new IrcMode(keys.length - i, values[i]));
        }
        setSupportedModes(map);
    }

    public void addIrcUser(String sender) {
        provider.dispatch(new InitRequestFunction("IrcUser", getObjectName() + "/" + sender));
    }

    public IrcUser getUser(String name) {
        return getUsers().get(name);
    }

    public IrcMode getMode(String modes) {
        if (modes == null) return new IrcMode(0, "");

        final List<IrcMode> usermodes = new ArrayList<>(modes.length());
        for (String mode : modes.split("")) {
            if (getSupportedModes().containsKey(mode))
                usermodes.add(getSupportedModes().get(mode));
        }
        Collections.sort(usermodes, (o1, o2) -> o1.rank - o2.rank);
        return usermodes.size() > 0 ? usermodes.get(0) : new IrcMode(0, "");
    }

    public Set<Buffer> getBuffers() {
        return buffers;
    }

    public Map<String, IrcUser> getUsers() {
        return users;
    }

    public void setUsers(Map<String, IrcUser> users) {
        this.users = users;
    }

    public Map<String, IrcChannel> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, IrcChannel> channels) {
        this.channels = channels;
    }

    public List<NetworkServer> getServerList() {
        return ServerList;
    }

    public void setServerList(List<NetworkServer> serverList) {
        ServerList = serverList;
    }

    public Map<String, String> getSupports() {
        return Supports;
    }

    public void setSupports(Map<String, String> supports) {
        Supports = supports;
    }

    public String getAutoIdentifyPassword() {
        return autoIdentifyPassword;
    }

    public void setAutoIdentifyPassword(String autoIdentifyPassword) {
        this.autoIdentifyPassword = autoIdentifyPassword;
    }

    public String getAutoIdentifyService() {
        return autoIdentifyService;
    }

    public void setAutoIdentifyService(String autoIdentifyService) {
        this.autoIdentifyService = autoIdentifyService;
    }

    public int getAutoReconnectInterval() {
        return autoReconnectInterval;
    }

    public void setAutoReconnectInterval(int autoReconnectInterval) {
        this.autoReconnectInterval = autoReconnectInterval;
    }

    public short getAutoReconnectRetries() {
        return autoReconnectRetries;
    }

    public void setAutoReconnectRetries(short autoReconnectRetries) {
        this.autoReconnectRetries = autoReconnectRetries;
    }

    public String getCodecForDecoding() {
        return codecForDecoding;
    }

    public void setCodecForDecoding(String codecForDecoding) {
        this.codecForDecoding = codecForDecoding;
    }

    public String getCodecForEncoding() {
        return codecForEncoding;
    }

    public void setCodecForEncoding(String codecForEncoding) {
        this.codecForEncoding = codecForEncoding;
    }

    public String getCodecForServer() {
        return codecForServer;
    }

    public void setCodecForServer(String codecForServer) {
        this.codecForServer = codecForServer;
    }

    public int getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(int connectionState) {
        this.connectionState = connectionState;
    }

    public String getCurrentServer() {
        return currentServer;
    }

    public void setCurrentServer(String currentServer) {
        this.currentServer = currentServer;
    }

    public int getIdentityId() {
        return identityId;
    }

    public void setIdentityId(int identityId) {
        this.identityId = identityId;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    public String getMyNick() {
        return myNick;
    }

    public void setMyNick(String myNick) {
        this.myNick = myNick;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public List<String> getPerform() {
        return perform;
    }

    public void setPerform(List<String> perform) {
        this.perform = perform;
    }

    public boolean isRejoinChannels() {
        return rejoinChannels;
    }

    public void setRejoinChannels(boolean rejoinChannels) {
        this.rejoinChannels = rejoinChannels;
    }

    public String getSaslAccount() {
        return saslAccount;
    }

    public void setSaslAccount(String saslAccount) {
        this.saslAccount = saslAccount;
    }

    public String getSaslPassword() {
        return saslPassword;
    }

    public void setSaslPassword(String saslPassword) {
        this.saslPassword = saslPassword;
    }

    public boolean isUnlimitedReconnectRetries() {
        return unlimitedReconnectRetries;
    }

    public void setUnlimitedReconnectRetries(boolean unlimitedReconnectRetries) {
        this.unlimitedReconnectRetries = unlimitedReconnectRetries;
    }

    public boolean isUseAutoIdentify() {
        return useAutoIdentify;
    }

    public void setUseAutoIdentify(boolean useAutoIdentify) {
        this.useAutoIdentify = useAutoIdentify;
    }

    public boolean isUseAutoReconnect() {
        return useAutoReconnect;
    }

    public void setUseAutoReconnect(boolean useAutoReconnect) {
        this.useAutoReconnect = useAutoReconnect;
    }

    public boolean isUseRandomServer() {
        return useRandomServer;
    }

    public void setUseRandomServer(boolean useRandomServer) {
        this.useRandomServer = useRandomServer;
    }

    public boolean isUseSasl() {
        return useSasl;
    }

    public void setUseSasl(boolean useSasl) {
        this.useSasl = useSasl;
    }

    public Map<String, IrcMode> getSupportedModes() {
        return supportedModes;
    }

    public void setSupportedModes(Map<String, IrcMode> supportedModes) {
        this.supportedModes = supportedModes;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    @Override
    public String toString() {
        return "Network{" +
                "users=" + users +
                ", channels=" + channels +
                ", ServerList=" + ServerList +
                ", Supports=" + Supports +
                ", autoIdentifyPassword='" + autoIdentifyPassword + '\'' +
                ", autoIdentifyService='" + autoIdentifyService + '\'' +
                ", autoReconnectInterval=" + autoReconnectInterval +
                ", autoReconnectRetries=" + autoReconnectRetries +
                ", codecForDecoding='" + codecForDecoding + '\'' +
                ", codecForEncoding='" + codecForEncoding + '\'' +
                ", codecForServer='" + codecForServer + '\'' +
                ", connectionState=" + connectionState +
                ", currentServer='" + currentServer + '\'' +
                ", identityId=" + identityId +
                ", isConnected=" + isConnected +
                ", latency=" + latency +
                ", myNick='" + myNick + '\'' +
                ", networkName='" + networkName + '\'' +
                ", perform=" + perform +
                ", rejoinChannels=" + rejoinChannels +
                ", saslAccount='" + saslAccount + '\'' +
                ", saslPassword='" + saslPassword + '\'' +
                ", unlimitedReconnectRetries=" + unlimitedReconnectRetries +
                ", useAutoIdentify=" + useAutoIdentify +
                ", useAutoReconnect=" + useAutoReconnect +
                ", useRandomServer=" + useRandomServer +
                ", useSasl=" + useSasl +
                ", supportedModes=" + supportedModes +
                ", networkId=" + networkId +
                '}';
    }

    public void renameUser(String oldNick, String newNick) {
        users.put(newNick, users.get(oldNick));
        users.remove(oldNick);
    }

    public void quit(String nick) {
        users.remove(nick);
    }

    @Override
    public void init(InitDataFunction function, BusProvider provider, Client client) {
        setObjectName(function.objectName);
        setBusProvider(provider);
        setNetworkId(Integer.parseInt(function.objectName));
        getBuffers().addAll(client.getBuffers(getNetworkId()));
        initUsers();
        client.putNetwork(this);
    }

    public static class IrcMode {
        public final int rank;
        public final String prefix;

        public IrcMode(int rank, String prefix) {
            this.rank = rank;
            this.prefix = prefix;
        }

        @Override
        public String toString() {
            return "IrcMode{" +
                    "rank=" + rank +
                    ", prefix='" + prefix + '\'' +
                    '}';
        }
    }
}
