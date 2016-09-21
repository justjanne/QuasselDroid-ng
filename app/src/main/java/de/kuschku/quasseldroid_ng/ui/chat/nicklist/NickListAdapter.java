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

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcUser;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.CompatibilityUtils;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;

public class NickListAdapter extends RecyclerView.Adapter<NickListAdapter.NickViewHolder> {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_AWAY = 1;
    private final AppContext context;
    private final AdapterUICallbackWrapper callback;
    QIrcChannel channel;

    public NickListAdapter(AppContext context) {
        this.context = context;
        callback = new AdapterUICallbackWrapper(this);
    }

    public void setChannel(QIrcChannel channel) {
        if (this.channel != null)
            this.channel.users().removeCallback(callback);
        this.channel = channel;
        if (this.channel != null)
            this.channel.users().addCallback(callback);
        notifyDataSetChanged();
    }

    @Override
    public NickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(getLayoutResource(viewType), parent, false);
        return new NickViewHolder(view);
    }

    public int getLayoutResource(int viewType) {
        switch (viewType) {
            default:
            case TYPE_NORMAL:
                return R.layout.widget_nick;
            case TYPE_AWAY:
                return R.layout.widget_nick_away;
        }
    }

    @Override
    public void onBindViewHolder(NickViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return this.channel != null ? this.channel.users().size() : 0;
    }

    public QIrcUser getItem(int position) {
        return this.channel != null ? this.channel.network().ircUser(this.channel.users().get(position)) : null;
    }

    @NonNull
    public String getPrefixedFromMode(String text) {
        StringBuilder builder = new StringBuilder();
        for (String s : CompatibilityUtils.partStringByChar(text)) {
            builder.append(channel.network().modeToPrefix(s));
        }
        return builder.toString();
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isAway() ? TYPE_AWAY : TYPE_NORMAL;
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
