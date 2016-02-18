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

package de.kuschku.quasseldroid_ng.ui.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.localtypes.buffers.ChannelBuffer;
import de.kuschku.libquassel.syncables.types.interfaces.QIrcChannel;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.service.ClientBackgroundThread;
import de.kuschku.util.irc.chanmodes.ChanMode;
import de.kuschku.util.irc.chanmodes.IrcModeProvider;
import de.kuschku.util.irc.format.IrcFormatHelper;
import de.kuschku.util.servicebound.BoundActivity;

public class ChannelDetailActivity extends BoundActivity {
    @Bind(R.id.topic)
    TextView topic;
    @Bind(R.id.mode)
    TextView mode;
    @Bind(R.id.modes)
    LinearLayout modes;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    ModeAdapter modeAdapter;
    private int buffer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        buffer = getIntent().getIntExtra("buffer", -1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_detail);
        ButterKnife.bind(this);
    }

    @Override
    protected void onConnectToThread(@Nullable ClientBackgroundThread thread) {
        super.onConnectToThread(thread);

        ChannelBuffer buffer = (ChannelBuffer) context.client().bufferManager().buffer(this.buffer);
        QIrcChannel channel = buffer.getChannel();
        String modeString = channel.channelModeString();

        topic.setText(new IrcFormatHelper(context).formatIrcMessage(context.client(), channel.topic(), buffer.getInfo(), v -> finish()));
        topic.setMovementMethod(LinkMovementMethod.getInstance());
        mode.setText(String.format("Channel Mode %s", modeString));

        if (getResources().getBoolean(R.bool.isTablet)) {
            findViewById(R.id.appBar).setPadding(0, 0, 0, 0);
        } else {
            findViewById(R.id.appBar).setPadding(0, (int) getResources().getDimension(R.dimen.materialize_statusbar), 0, 0);
        }

        toolbar.setTitle(channel.name());
        setSupportActionBar(toolbar);

        modes.removeAllViews();
        IrcModeProvider provider = channel.network().modeProvider();
        for (char c : modeString.toCharArray()) {
            ChanMode mode = provider.modeFromChar(c);
            if (mode != null) {
                View v = getLayoutInflater().inflate(R.layout.widget_channel_mode, modes, false);
                TextView name = (TextView) v.findViewById(R.id.name);
                TextView description = (TextView) v.findViewById(R.id.description);

                String modeName = context.themeUtil().translations.chanModeToName(mode);
                name.setText(String.format("%s (+%s)", modeName, c));
                String modeDescription = context.themeUtil().translations.chanModeToDescription(mode);
                description.setText(modeDescription);
                modes.addView(v);
            }
        }
    }

    public void onEventMainThread(GeneralErrorEvent event) {
        Log.e("DEBUG", String.valueOf(event));
    }

    public class ModeAdapter extends RecyclerView.Adapter<ChannelDetailActivity.ModeViewHolder> {
        private List<ChanMode> channelModes = new ArrayList<>();

        public void setModes(Collection<ChanMode> chanModes) {
            channelModes.clear();
            channelModes.addAll(chanModes);
            Log.e("DEBUG", String.valueOf(channelModes));
            notifyDataSetChanged();
        }

        @Override
        public ChannelDetailActivity.ModeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ModeViewHolder(inflater.inflate(R.layout.widget_channel_mode, parent, false));
        }

        @Override
        public void onBindViewHolder(ChannelDetailActivity.ModeViewHolder holder, int position) {
            holder.bind(channelModes.get(position));
        }

        @Override
        public int getItemCount() {
            return channelModes.size();
        }
    }

    public class ModeViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.name)
        TextView name;

        @Bind(R.id.description)
        TextView description;

        public ModeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ChanMode mode) {
            name.setText(context.themeUtil().translations.chanModeToName(mode));
            description.setText(context.themeUtil().translations.chanModeToDescription(mode));
        }
    }
}
