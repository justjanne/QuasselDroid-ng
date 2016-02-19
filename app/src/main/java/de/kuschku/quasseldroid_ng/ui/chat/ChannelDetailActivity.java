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

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    @Bind(R.id.modes)
    LinearLayout modes;

    @Bind(R.id.edit_topic)
    AppCompatButton edit_topic;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

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
        if (buffer == null) return;

        QIrcChannel channel = buffer.getChannel();
        if (channel == null) return;

        if (channel.topic() == null) {
            topic.setText(R.string.no_topic_set);
            topic.setTextColor(context.themeUtil().res.colorForegroundSecondary);
            topic.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
        } else {
            topic.setText(new IrcFormatHelper(context).formatIrcMessage(context.client(), channel.topic(), buffer.getInfo(), v -> finish()));
            topic.setMovementMethod(LinkMovementMethod.getInstance());
            topic.setTextColor(context.themeUtil().res.colorForeground);
            topic.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }

        toolbar.setTitle(channel.name());
        setSupportActionBar(toolbar);

        modes.removeAllViews();
        IrcModeProvider provider = channel.network().modeProvider();

        String myModes = channel.userModes(channel.network().me());
        boolean isOp = false;
        for (String c : channel.network().prefixModes()) {
            if (!c.equalsIgnoreCase("v") && myModes.contains(c)) {
                isOp = true;
                break;
            }
        }

        boolean topicEditable = true;
        for (char c : channel.modeList()) {
            ChanMode mode = provider.modeFromChar(c);
            if (mode != null) {
                View v = getLayoutInflater().inflate(R.layout.widget_channel_mode, modes, false);
                TextView name = (TextView) v.findViewById(R.id.name);
                TextView description = (TextView) v.findViewById(R.id.description);
                TextView value = (TextView) v.findViewById(R.id.value);

                String modeName = context.themeUtil().translations.chanModeToName(mode);
                name.setText(String.format("%s (+%s)", modeName, c));

                String modeDescription = context.themeUtil().translations.chanModeToDescription(mode);
                description.setText(modeDescription);

                String modeValue = channel.modeValue(c);

                if (modeValue != null && !modeValue.isEmpty()) {
                    value.setText(modeValue);
                    value.setVisibility(View.VISIBLE);
                }

                modes.addView(v);

                if (mode == ChanMode.RESTRICT_TOPIC) topicEditable = isOp;
            }
        }

        edit_topic.setVisibility(topicEditable ? View.VISIBLE : View.GONE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onEventMainThread(GeneralErrorEvent event) {
        Log.e("DEBUG", String.valueOf(event));
    }
}
