package de.kuschku.libquassel.syncables.types;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;

public class IrcChannel extends SyncableObject {
    public final String name;
    public final String topic;
    public final String password;
    public final Map<String, String> UserModes;
    public final Map<String, Object> ChanModes;
    public final boolean encrypted;

    public IrcChannel(String name, String topic, String password, Map<String, String> userModes,
                      Map<String, Object> chanModes, boolean encrypted) {
        this.name = name;
        this.topic = topic;
        this.password = password;
        this.UserModes = userModes;
        this.ChanModes = chanModes;
        this.encrypted = encrypted;
    }

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

    public void joinIrcUsers(List<String> users, List<String> modes) {
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

    public void addUserMode(String nick, String mode) {
        if (UserModes.get(nick) == null)
            UserModes.put(nick, mode);
        else if (!UserModes.get(nick).contains(mode))
            UserModes.put(nick, UserModes.get(nick) + mode);
    }

    public void removeUserMode(String nick, String mode) {
        if (UserModes.get(nick) == null && UserModes.get(nick).contains(mode))
            UserModes.put(nick, UserModes.get(nick).replace(mode, ""));
    }

    public void renameUser(String oldNick, String newNick) {
        UserModes.put(newNick, UserModes.get(oldNick));
        UserModes.remove(oldNick);
    }

    @Override
    public void init(InitDataFunction function, BusProvider provider, Client client) {

    }
}
