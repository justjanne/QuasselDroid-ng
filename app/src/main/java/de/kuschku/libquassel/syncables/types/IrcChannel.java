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
import java.util.concurrent.ConcurrentHashMap;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.Synced;
import de.kuschku.libquassel.syncables.serializers.IrcChannelSerializer;
import de.kuschku.util.AndroidAssert;

import static de.kuschku.util.AndroidAssert.*;

public class IrcChannel extends SyncableObject<IrcChannel> {
    @Synced private String name;
    @Synced private String topic;
    @Synced private String password;
    @Synced private Map<String, String> UserModes;
    @Synced private Map<String, Object> ChanModes;
    @Synced private boolean encrypted;

    @Nullable
    private Network network;

    public IrcChannel(String name, String topic, String password, Map<String, String> userModes,
                      Map<String, Object> chanModes, boolean encrypted) {
        this.name = name;
        this.topic = topic;
        this.password = password;
        this.UserModes = userModes;
        this.ChanModes = chanModes;
        this.encrypted = encrypted;
    }

    @NonNull
    public Map<String, List<String>> getA_ChanModes() {
        if (ChanModes.get("A") == null) ChanModes.put("A", new HashMap<>());
        return (Map<String, List<String>>) ChanModes.get("A");
    }

    @NonNull
    public Map<String, String> getB_ChanModes() {
        if (ChanModes.get("B") == null) ChanModes.put("B", new HashMap<>());
        return (Map<String, String>) ChanModes.get("B");
    }

    @NonNull
    public Map<String, String> getC_ChanModes() {
        if (ChanModes.get("C") == null) ChanModes.put("C", new HashMap<>());
        return (Map<String, String>) ChanModes.get("C");
    }

    @NonNull
    public Set<String> getD_ChanModes() {
        if (ChanModes.get("D") instanceof String) {
            List<String> list = Arrays.asList(((String) ChanModes.get("D")).split(""));
            ChanModes.put("D", new HashSet<>(list));
        } else if (ChanModes.get("D") == null) {
            ChanModes.put("D", new HashSet<>());
        }
        return (Set<String>) ChanModes.get("D");
    }

    @NonNull
    @Override
    public String toString() {
        return "IrcChannel{" +
                "name='" + name + '\'' +
                ", topic='" + topic + '\'' +
                ", password='" + password + '\'' +
                ", UserModes=" + UserModes +
                ", ChanModes=" + ChanModes +
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
    public void addChannelMode(String mode, String params) {
        assertNotNull(network);

        Network.ChannelModeType type = network.channelModeType(mode);
        switch (type) {
            case NOT_A_CHANMODE:
                return;
            case A_CHANMODE:
                if (!getA_ChanModes().containsKey(mode)) {
                    getA_ChanModes().put(mode, new ArrayList<>(Collections.singleton(params)));
                } else {
                    getA_ChanModes().get(mode).add(params);
                }
                break;
            case B_CHANMODE:
                getB_ChanModes().put(mode, params);
                break;
            case C_CHANMODE:
                getB_ChanModes().put(mode, params);
                break;
            case D_CHANMODE:

                getD_ChanModes().add(mode);
                break;
        }
    }
    public void removeChannelMode(Character mode, String params) {
        removeChannelMode(String.copyValueOf(new char[]{mode}), params);
    }
    public void removeChannelMode(char mode, String params) {
        removeChannelMode(String.copyValueOf(new char[]{mode}), params);
    }
    public void removeChannelMode(String mode, String params) {
        assertNotNull(network);

        Network.ChannelModeType type = network.channelModeType(mode);
        switch (type) {
            case NOT_A_CHANMODE:
                return;
            case A_CHANMODE:
                if (getA_ChanModes().containsKey(mode))
                    getA_ChanModes().get(mode).removeAll(Collections.singleton(params));
                break;
            case B_CHANMODE:
                getB_ChanModes().remove(mode);
                break;
            case C_CHANMODE:
                getB_ChanModes().remove(mode);
                break;
            case D_CHANMODE:
                getB_ChanModes().remove(mode);
                break;
        }
    }

    public boolean hasMode(Character mode) {
        return hasMode(String.copyValueOf(new char[]{mode}));
    }
    public boolean hasMode(char mode) {
        return hasMode(String.copyValueOf(new char[]{mode}));
    }
    public boolean hasMode(String mode) {
        assertNotNull(network);

        Network.ChannelModeType type = network.channelModeType(mode);
        switch (type) {
            case A_CHANMODE:
                return getA_ChanModes().containsKey(mode);
            case B_CHANMODE:
                return getA_ChanModes().containsKey(mode);
            case C_CHANMODE:
                return getA_ChanModes().containsKey(mode);
            case D_CHANMODE:
                return getA_ChanModes().containsKey(mode);

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
    public void update(IrcChannel from) {
        this.name = from.name;
        this.topic = from.topic;
        this.password = from.password;
        this.UserModes = from.UserModes;
        this.ChanModes = from.ChanModes;
        this.encrypted = from.encrypted;
    }

    @Override
    public void update(Map<String, QVariant> from) {
        update(IrcChannelSerializer.get().fromDatastream(from));
    }

    public Map<String, Object> getChanModes() {
        return ChanModes;
    }
}
