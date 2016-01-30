package de.kuschku.libquassel.localtypes;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.syncables.types.Identity;
import de.kuschku.libquassel.syncables.types.Network;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;

public class NotificationManager {
    private SparseArray<ObservableComparableSortedList<Message>> notifications = new SparseArray<>();
    private List<HighlightRule> highlights = new ArrayList<>();
    private Client client;

    public NotificationManager(Client client) {
        this.client = client;
    }

    public ObservableComparableSortedList<Message> getNotifications(int bufferid) {
        if (notifications.get(bufferid) == null)
            notifications.put(bufferid, new ObservableComparableSortedList<>(Message.class));

        return notifications.get(bufferid);
    }

    public void init(int id) {
        notifications.put(id, new ObservableComparableSortedList<>(Message.class));
    }

    public void receiveMessage(Message message) {
        if (checkMessage(message)) {
            getNotifications(message.bufferInfo.id).add(message);
        }
    }

    public boolean checkMessage(Message message) {
        Buffer buffer = client.getBuffer(message.bufferInfo.id);
        if (buffer == null) return false;
        Network network = client.getNetwork(buffer.getInfo().networkId);
        if (network == null) return false;
        Identity identity = client.getIdentity(network.getIdentityId());
        if (identity == null) return false;

        for (String nick : identity.getNicks()) {
            if (message.content.contains(nick))
                return true;
        }
        for (HighlightRule rule : highlights) {
            if (rule.matches(message.content, buffer.getName()))
                return true;
        }
        return false;
    }

    public void receiveMessages(List<Message> messages) {
        for (Message message : messages) {
            receiveMessage(message);
        }
    }

    private class HighlightRule {
        public final Pattern rule;
        public final Pattern channelRule;
        public final boolean invertChannelRule;
        public final boolean caseSensitive;

        public HighlightRule(String rule, String channelRule, boolean invertChannelRule, boolean caseSensitive) {
            this.rule = rule.isEmpty() ? Pattern.compile(".*") : Pattern.compile(rule);
            this.channelRule = channelRule.isEmpty() ? Pattern.compile(".*") : Pattern.compile(channelRule);
            this.invertChannelRule = invertChannelRule;
            this.caseSensitive = caseSensitive;
        }

        public boolean matches(String message, String channelName) {
            return (invertChannelRule ^ channelRule.matcher(channelName).matches() && rule.matcher(message).matches());
        }
    }
}
