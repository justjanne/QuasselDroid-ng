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

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.objects.types.Command;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.AliasManagerSerializer;
import de.kuschku.libquassel.syncables.types.abstracts.AAliasManager;
import de.kuschku.libquassel.syncables.types.interfaces.QAliasManager;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.util.backports.Objects;
import de.kuschku.util.observables.lists.AndroidObservableSortedList;

public class AliasManager extends AAliasManager {
    @NonNull
    private static final Alias[] DEFAULTS = new Alias[]{
            new Alias("j", "/join $0"),
            new Alias("ns", "/msg nickserv $0"),
            new Alias("nickserv", "/msg nickserv $0"),
            new Alias("cs", "/msg chanserv $0"),
            new Alias("chanserv", "/msg chanserv $0"),
            new Alias("hs", "/msg hostserv $0"),
            new Alias("hostserv", "/msg hostserv $0"),
            new Alias("wii", "/whois $0 $0"),
            new Alias("back", "/quote away")
    };

    private Map<String, Alias> map = new HashMap<>();
    private AndroidObservableSortedList<Alias> aliases = new AndroidObservableSortedList<>(Alias.class, new AndroidObservableSortedList.ItemComparator<Alias>() {
        @Override
        public int compare(Alias o1, Alias o2) {
            return o1.name.compareTo(o2.name);
        }

        @Override
        public boolean areContentsTheSame(Alias oldItem, Alias newItem) {
            return Objects.equals(oldItem.name, newItem.name);
        }

        @Override
        public boolean areItemsTheSame(Alias item1, Alias item2) {
            return Objects.equals(item1.expansion, item2.expansion);
        }
    });

    private Client client;

    public AliasManager(@NonNull List<String> names, @NonNull List<String> extensions) {
        for (int i = 0; i < names.size(); i++) {
            _addAlias(names.get(i), extensions.get(i));
        }
    }

    @NonNull
    private static List<Command> expand(@NonNull String expansion, @NonNull BufferInfo info, @NonNull QNetwork network, @NonNull String args) {
        List<Command> results = new LinkedList<>();

        Pattern paramRange = Pattern.compile("\\$(\\d+)\\.\\.(\\d*)");
        List<String> commands = Arrays.asList(expansion.split("; ?"));
        List<String> params = Arrays.asList(args.split(" "));
        List<String> expandedCommands = new LinkedList<>();

        for (int i = 0; i < commands.size(); i++) {
            String command = commands.get(i);

            if (params.size() != 0) {
                Matcher m = paramRange.matcher(command);
                while (m.find()) {
                    int start = Integer.valueOf(m.group(1));
                    String replacement;
                    // $1.. would be "arg1 and all following"
                    if (m.group(2).isEmpty()) {
                        replacement = Joiner.on(" ").join(params.subList(start, params.size()));
                    } else {
                        int end = Integer.valueOf(m.group(2));
                        if (end < start) {
                            replacement = "";
                        } else {
                            replacement = Joiner.on(" ").join(params.subList(start, end));
                        }
                    }
                    command = command.substring(0, m.start()) + replacement + command.substring(m.end());
                }
            }

            for (int j = params.size(); j > 0; j--) {
                QIrcUser user = network.ircUser(params.get(j - 1));
                String host = user == null ? "*" : user.host();
                command = command.replaceAll(String.format(Locale.US, "$%d:hostname", j), host);
                command = command.replaceAll(String.format(Locale.US, "$%d", j), params.get(j - 1));
            }
            command = command.replace("$0", args);
            command = command.replace("$channelname", info.name != null ? info.name : "");
            command = command.replace("$channel", info.name != null ? info.name : "");
            command = command.replace("$currentnick", network.myNick());
            command = command.replace("$nick", network.myNick());
            command = command.replace("$network", network.networkName());
            expandedCommands.add(command);
        }
        while (!expandedCommands.isEmpty()) {
            String command;
            if (expandedCommands.get(0).trim().toLowerCase(Locale.US).startsWith("/wait ")) {
                command = Joiner.on("; ").join(expandedCommands);
                expandedCommands.clear();
            } else {
                command = expandedCommands.get(0);
            }
            results.add(new Command(info, command));
        }
        return results;
    }

    @Override
    public boolean contains(String name) {
        return map.containsKey(name.toLowerCase(Locale.US));
    }

    @Override
    public boolean isEmpty() {
        return aliases.isEmpty();
    }

    @Override
    public int count() {
        return aliases.size();
    }

    @Override
    public AndroidObservableSortedList<Alias> aliases() {
        return aliases;
    }

    @Override
    public AndroidObservableSortedList<Alias> defaults() {
        map.clear();
        aliases.clear();
        for (Alias alias : DEFAULTS) {
            _addAlias(alias.name, alias.expansion);
        }
        return aliases;
    }

    @NonNull
    @Override
    public List<Command> processInput(@NonNull BufferInfo info, @NonNull String message) {
        List<Command> list = new LinkedList<>();

        // Escaped slash
        if (message.startsWith("//")) {
            // Unescape slash
            list.add(new Command(info, message.substring(1)));
        } else if (
            // Not a command
                !message.startsWith("/") ||
                        // Or path
                        (message.startsWith("/") && message.substring(1).split(" ")[0].contains("/"))) {
            list.add(new Command(info, message));
        } else {
            int space = message.indexOf(" ");
            String command;
            String args;
            if (space == -1) {
                command = message;
                args = "";
            } else {
                command = message.substring(1, space);
                args = message.substring(space + 1);
            }
            Alias alias = map.get(command.toLowerCase(Locale.US));
            QNetwork network = client.networkManager().network(info.networkId);
            if (alias != null && network != null) {
                list.addAll(expand(alias.expansion, info, network, args));
            } else {
                list.add(new Command(info, message));
            }
        }
        return list;
    }

    @Override
    public void _addAlias(String name, String expansion) {
        Alias alias = new Alias(name, expansion);
        _addAlias(alias);
        _update();
    }

    @Override
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull Client client) {
        this.client = client;
        super.init(objectName, provider, client);
        client.setAliasManager(this);
        _update();
    }

    @Override
    public void _update(@NonNull Map<String, QVariant> from) {
        _update(AliasManagerSerializer.get().fromLegacy(from));
    }

    @Override
    public void _update(@NonNull QAliasManager from) {
        List<Alias> toRemove = new ArrayList<>();
        for (Alias alias : aliases) {
            if (!from.aliases().contains(alias))
                toRemove.add(alias);
        }
        for (Alias alias : toRemove) {
            _removeAlias(alias);
        }
        for (Alias alias : from.aliases()) {
            if (!aliases.contains(alias)) {
                _addAlias(alias);
            }
        }
        _update();
    }

    @Override
    public void _removeAlias(Alias alias) {
        aliases.remove(alias);
        map.remove(alias.name);
    }

    @Override
    public Alias alias(String name) {
        return map.get(name);
    }


    @Override
    public void requestUpdate() {
        requestUpdate(AliasManagerSerializer.get().toVariantMap(this));
    }

    @Override
    public void _addAlias(Alias alias) {
        aliases.add(alias);
        map.put(alias.name, alias);
    }
}
