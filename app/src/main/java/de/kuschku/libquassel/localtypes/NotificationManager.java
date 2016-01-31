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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.localtypes;

import android.support.annotation.NonNull;
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
    @NonNull
    private final SparseArray<ObservableComparableSortedList<Message>> notifications = new SparseArray<>();
    @NonNull
    private final List<HighlightRule> highlights = new ArrayList<>();
    @NonNull
    private final Client client;

    public NotificationManager(@NonNull Client client) {
        this.client = client;
    }

    @NonNull
    public ObservableComparableSortedList<Message> getNotifications(int bufferid) {
        if (notifications.get(bufferid) == null)
            notifications.put(bufferid, new ObservableComparableSortedList<>(Message.class));

        return notifications.get(bufferid);
    }

    public void init(int id) {
        notifications.put(id, new ObservableComparableSortedList<>(Message.class));
    }

    public void receiveMessage(@NonNull Message message) {
        if (checkMessage(message)) {
            getNotifications(message.bufferInfo.id).add(message);
        }
    }

    public boolean checkMessage(@NonNull Message message) {
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
        if (buffer.getName() == null)
            return false;
        for (HighlightRule rule : highlights) {
            if (rule.matches(message.content, buffer.getName()))
                return true;
        }
        return false;
    }

    public void receiveMessages(@NonNull List<Message> messages) {
        for (Message message : messages) {
            receiveMessage(message);
        }
    }

    private class HighlightRule {
        public final Pattern rule;
        public final Pattern channelRule;
        public final boolean invertChannelRule;
        public final boolean caseSensitive;

        public HighlightRule(@NonNull String rule, @NonNull String channelRule, boolean invertChannelRule, boolean caseSensitive) {
            this.rule = rule.isEmpty() ? Pattern.compile(".*") : Pattern.compile(rule);
            this.channelRule = channelRule.isEmpty() ? Pattern.compile(".*") : Pattern.compile(channelRule);
            this.invertChannelRule = invertChannelRule;
            this.caseSensitive = caseSensitive;
        }

        public boolean matches(@NonNull String message, @NonNull String channelName) {
            return (invertChannelRule ^ channelRule.matcher(channelName).matches() && rule.matcher(message).matches());
        }
    }
}
