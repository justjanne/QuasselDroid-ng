package de.kuschku.libquassel.syncables.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
import de.kuschku.util.observables.ContentComparable;

import static de.kuschku.util.AndroidAssert.*;

public class Network extends SyncableObject implements ContentComparable<Network> {
    @NonNull
    private Map<String, IrcUser> users;
    @NonNull
    private Map<String, IrcChannel> channels;

    @NonNull
    private final Set<Buffer> buffers = new HashSet<>();

    @NonNull
    private List<NetworkServer> ServerList;
    @NonNull
    private Map<String, String> Supports;

    @Nullable
    private String autoIdentifyPassword;
    @Nullable
    private String autoIdentifyService;

    private int autoReconnectInterval;
    private short autoReconnectRetries;

    @Nullable
    private String codecForDecoding;
    @Nullable
    private String codecForEncoding;
    @Nullable
    private String codecForServer;

    private int connectionState;
    @Nullable
    private String currentServer;
    private int identityId;
    private boolean isConnected;
    private int latency;
    @Nullable
    private String myNick;
    @Nullable
    private String networkName;
    @NonNull
    private List<String> perform;
    private boolean rejoinChannels;

    @Nullable
    private String saslAccount;
    @Nullable
    private String saslPassword;

    private boolean unlimitedReconnectRetries;
    private boolean useAutoIdentify;
    private boolean useAutoReconnect;
    private boolean useRandomServer;
    private boolean useSasl;

    @Nullable
    private Map<String, IrcMode> supportedModes;
    private int networkId;

    public Network(@NonNull Map<String, IrcChannel> channels, @NonNull Map<String, IrcUser> users,
                   @NonNull List<NetworkServer> serverList, @NonNull Map<String, String> supports,
                   @NonNull String autoIdentifyPassword,  @NonNull String autoIdentifyService,
                   int autoReconnectInterval, short autoReconnectRetries,
                   @NonNull String codecForDecoding, @NonNull String codecForEncoding,
                   @NonNull String codecForServer, int connectionState,
                   @NonNull String currentServer, int identityId, boolean isConnected, int latency,
                   @NonNull String myNick, @NonNull String networkName,
                   @NonNull List<String> perform, boolean rejoinChannels,
                   @NonNull String saslAccount, @NonNull String saslPassword,
                   boolean unlimitedReconnectRetries, boolean useAutoIdentify,
                   boolean useAutoReconnect, boolean useRandomServer, boolean useSasl) {
        this.channels = channels;
        this.users = users;
        this.ServerList = serverList;
        this.Supports = supports;
        this.autoIdentifyPassword = autoIdentifyPassword;
        this.autoIdentifyService = autoIdentifyService;
        this.autoReconnectInterval = autoReconnectInterval;
        this.autoReconnectRetries = autoReconnectRetries;
        this.codecForDecoding = codecForDecoding;
        this.codecForEncoding = codecForEncoding;
        this.codecForServer = codecForServer;
        this.connectionState = connectionState;
        this.currentServer = currentServer;
        this.identityId = identityId;
        this.isConnected = isConnected;
        this.latency = latency;
        this.myNick = myNick;
        this.networkName = networkName;
        this.perform = perform;
        this.rejoinChannels = rejoinChannels;
        this.saslAccount = saslAccount;
        this.saslPassword = saslPassword;
        this.unlimitedReconnectRetries = unlimitedReconnectRetries;
        this.useAutoIdentify = useAutoIdentify;
        this.useAutoReconnect = useAutoReconnect;
        this.useRandomServer = useRandomServer;
        this.useSasl = useSasl;
        parsePrefix();
        assertNotNull(supportedModes);
    }

    private void initUsers() {
        assertNotNull(provider);

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

    @Nullable
    public IrcUser getUser(String name) {
        return getUsers().get(name);
    }

    @NonNull
    public IrcMode getMode(@Nullable String modes) {
        if (modes == null) return new IrcMode(0, "");

        final List<IrcMode> usermodes = new ArrayList<>(modes.length());
        for (String mode : modes.split("")) {
            if (getSupportedModes().containsKey(mode))
                usermodes.add(getSupportedModes().get(mode));
        }
        Collections.sort(usermodes, (o1, o2) -> o1.rank - o2.rank);
        return usermodes.size() > 0 ? usermodes.get(0) : new IrcMode(0, "");
    }

    @NonNull
    public Set<Buffer> getBuffers() {
        return buffers;
    }

    @NonNull
    public Map<String, IrcUser> getUsers() {
        return users;
    }

    public void setUsers(@NonNull Map<String, IrcUser> users) {
        this.users = users;
    }

    @NonNull
    public Map<String, IrcChannel> getChannels() {
        return channels;
    }

    public void setChannels(@NonNull Map<String, IrcChannel> channels) {
        this.channels = channels;
    }

    @NonNull
    public List<NetworkServer> getServerList() {
        return ServerList;
    }

    public void setServerList(@NonNull List<NetworkServer> serverList) {
        ServerList = serverList;
    }

    @NonNull
    public Map<String, String> getSupports() {
        return Supports;
    }

    public void setSupports(@NonNull Map<String, String> supports) {
        Supports = supports;
    }

    @Nullable
    public String getAutoIdentifyPassword() {
        return autoIdentifyPassword;
    }

    public void setAutoIdentifyPassword(@NonNull String autoIdentifyPassword) {
        this.autoIdentifyPassword = autoIdentifyPassword;
    }

    @Nullable
    public String getAutoIdentifyService() {
        return autoIdentifyService;
    }

    public void setAutoIdentifyService(@NonNull String autoIdentifyService) {
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

    @Nullable
    public String getCodecForDecoding() {
        return codecForDecoding;
    }

    public void setCodecForDecoding(@Nullable String codecForDecoding) {
        this.codecForDecoding = codecForDecoding;
    }

    @Nullable
    public String getCodecForEncoding() {
        return codecForEncoding;
    }

    public void setCodecForEncoding(@Nullable String codecForEncoding) {
        this.codecForEncoding = codecForEncoding;
    }

    @Nullable
    public String getCodecForServer() {
        return codecForServer;
    }

    public void setCodecForServer(@Nullable String codecForServer) {
        this.codecForServer = codecForServer;
    }

    public int getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(int connectionState) {
        this.connectionState = connectionState;
    }

    @Nullable
    public String getCurrentServer() {
        return currentServer;
    }

    public void setCurrentServer(@Nullable String currentServer) {
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

    @Nullable
    public String getMyNick() {
        return myNick;
    }

    public void setMyNick(@Nullable String myNick) {
        this.myNick = myNick;
    }

    @Nullable
    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(@Nullable String networkName) {
        this.networkName = networkName;
    }

    @NonNull
    public List<String> getPerform() {
        return perform;
    }

    public void setPerform(@NonNull List<String> perform) {
        this.perform = perform;
    }

    public boolean isRejoinChannels() {
        return rejoinChannels;
    }

    public void setRejoinChannels(boolean rejoinChannels) {
        this.rejoinChannels = rejoinChannels;
    }

    @Nullable
    public String getSaslAccount() {
        return saslAccount;
    }

    public void setSaslAccount(@Nullable String saslAccount) {
        this.saslAccount = saslAccount;
    }

    @Nullable
    public String getSaslPassword() {
        return saslPassword;
    }

    public void setSaslPassword(@Nullable String saslPassword) {
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

    @Nullable
    public Map<String, IrcMode> getSupportedModes() {
        return supportedModes;
    }

    public void setSupportedModes(@NonNull Map<String, IrcMode> supportedModes) {
        this.supportedModes = supportedModes;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }

    @NonNull
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

    public void renameUser(@Nullable String oldNick, @Nullable String newNick) {
        users.put(newNick, users.get(oldNick));
        users.remove(oldNick);
    }

    public void quit(String nick) {
        users.remove(nick);
    }

    @Override
    public void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {
        setObjectName(function.objectName);
        setBusProvider(provider);
        setNetworkId(Integer.parseInt(function.objectName));
        getBuffers().addAll(client.getBuffers(getNetworkId()));
        initUsers();
        client.putNetwork(this);
    }

    @Override
    public boolean equalsContent(@NonNull Network other) {
        return networkId == other.networkId;
    }

    @Override
    public int compareTo(@NonNull Network another) {
        return networkId - another.networkId;
    }

    public static class IrcMode {
        public final int rank;
        public final String prefix;

        public IrcMode(int rank, String prefix) {
            this.rank = rank;
            this.prefix = prefix;
        }

        @NonNull
        @Override
        public String toString() {
            return "IrcMode{" +
                    "rank=" + rank +
                    ", prefix='" + prefix + '\'' +
                    '}';
        }
    }
}
