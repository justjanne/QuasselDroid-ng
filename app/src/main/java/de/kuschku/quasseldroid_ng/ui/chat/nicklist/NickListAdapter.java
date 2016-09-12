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

package de.kuschku.quasseldroid_ng.ui.chat.nicklist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.CompatibilityUtils;
import de.kuschku.util.backports.Objects;
import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;
import de.kuschku.util.observables.lists.ObservableSortedList;

public class NickListAdapter extends RecyclerView.Adapter<NickListAdapter.NickViewHolder> {
    private final AppContext context;
    QIrcChannel channel;

    List<QIrcUser> list = new ArrayList<>();
    ObservableSortedList<QIrcUser> users = new ObservableSortedList<>(QIrcUser.class, new ObservableSortedList.ItemComparator<QIrcUser>() {
        @Override
        public int compare(QIrcUser o1, QIrcUser o2) {
            if (channel.userModes(o1).equals(channel.userModes(o2))) {
                return o1.nick().compareToIgnoreCase(o2.nick());
            } else {
                return channel.network().lowestModeIndex(channel.userModes(o1)) - channel.network().lowestModeIndex(channel.userModes(o2));
            }
        }

        @Override
        public boolean areContentsTheSame(QIrcUser oldItem, QIrcUser newItem) {
            return Objects.equals(oldItem.userModes(), newItem.userModes()) && Objects.equals(oldItem.realName(), newItem.realName());
        }

        @Override
        public boolean areItemsTheSame(QIrcUser item1, QIrcUser item2) {
            return Objects.equals(item1.nick(), item2.nick());
        }
    });
    private ElementCallback<String> callback = new ElementCallback<String>() {
        @Override
        public void notifyItemInserted(String element) {
            QIrcUser qIrcUser = channel.network().ircUser(element);
            users.add(qIrcUser);
            list.add(users.indexOf(qIrcUser), qIrcUser);
        }

        @Override
        public void notifyItemRemoved(String element) {
            for (int i = 0; i < users.size(); i++) {
                QIrcUser user = users.get(i);
                if (user.nick().equals(element)) {
                    users.remove(i);
                    list.remove(user);
                }
            }
        }

        @Override
        public void notifyItemChanged(String element) {
            QIrcUser object = channel.network().ircUser(element);
            if (object != null) {
                users.notifyItemChanged(list.indexOf(object));
                list.remove(object);
                list.add(users.indexOf(object), object);
            }
        }
    };

    public NickListAdapter(AppContext context) {
        this.context = context;
        users.addCallback(new AdapterUICallbackWrapper(this));
    }

    public void setChannel(QIrcChannel channel) {
        if (this.channel != null)
            this.channel.users().removeCallback(callback);
        this.channel = channel;
        this.users.clear();
        this.list.clear();
        if (this.channel != null) {
            for (String nick : channel.users()) {
                QIrcUser ircUser = channel.network().ircUser(nick);
                if (ircUser != null) {
                    users.add(ircUser);
                    list.add(users.indexOf(ircUser), ircUser);
                }
            }
            this.channel.users().addCallback(callback);
        }
        notifyDataSetChanged();
    }

    @Override
    public NickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.widget_nick, parent, false);
        return new NickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NickViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @NonNull
    public String getPrefixedFromMode(String text) {
        StringBuilder builder = new StringBuilder();
        for (String s : CompatibilityUtils.partStringByChar(text)) {
            builder.append(channel.network().modeToPrefix(s));
        }
        return builder.toString();
    }

    class NickViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.mode)
        TextView mode;

        @Bind(R.id.nick)
        TextView nick;

        @Bind(R.id.realname)
        TextView realname;

        public NickViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(QIrcUser qIrcUser) {
            nick.setText(qIrcUser.nick());
            realname.setText(qIrcUser.realName());
            String text = channel.userModes(qIrcUser);
            if (text.isEmpty()) {
                mode.setVisibility(View.INVISIBLE);
                mode.setText(null);
            } else {
                mode.setVisibility(View.VISIBLE);
                String prefixes = getPrefixedFromMode(text);
                mode.setBackground(context.themeUtil().res.badge(channel.network().lowestModeIndex(text)));
                mode.setText(prefixes);
            }
        }
    }
}
