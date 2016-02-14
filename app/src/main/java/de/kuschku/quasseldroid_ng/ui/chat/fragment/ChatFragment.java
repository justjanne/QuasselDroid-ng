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

package de.kuschku.quasseldroid_ng.ui.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.events.BacklogReceivedEvent;
import de.kuschku.libquassel.events.BufferChangeEvent;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.syncables.types.interfaces.QBacklogManager;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.chat.chatview.MessageAdapter;
import de.kuschku.quasseldroid_ng.ui.chat.util.SlidingPanelHandler;
import de.kuschku.quasseldroid_ng.util.BoundFragment;
import de.kuschku.util.observables.AutoScroller;
import de.kuschku.util.observables.lists.ObservableComparableSortedList;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class ChatFragment extends BoundFragment {
    /**
     * The list containing the messages to be displayed
     */
    @Bind(R.id.messages)
    RecyclerView messages;
    @Bind(R.id.swipe_view)
    SwipeRefreshLayout swipeView;
    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout sliderMain;

    private MessageAdapter messageAdapter;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        new SlidingPanelHandler(getActivity(), sliderMain, context);

        assertNotNull(messages);

        messages.setItemAnimator(new DefaultItemAnimator());
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
        messages.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(getActivity(), context, new AutoScroller(messages));
        messages.setAdapter(messageAdapter);

        swipeView.setColorSchemeColors(context.themeUtil().res.colorPrimary);
        swipeView.setEnabled(false);
        swipeView.setOnRefreshListener(() -> {
            Client client = context.client();
            assertNotNull(client);
            QBacklogManager<? extends QBacklogManager> backlogManager = client.backlogManager();
            assertNotNull(backlogManager);
            backlogManager.requestMoreBacklog(client.backlogManager().open(), 20);
        });

        return view;
    }

    public void onEventMainThread(BufferChangeEvent event) {
        setMarkerline();

        Client client = context.client();
        if (client != null) {
            QBacklogManager<? extends QBacklogManager> backlogManager = client.backlogManager();
            int id = backlogManager.open();
            ObservableComparableSortedList<Message> messageList = backlogManager.filtered(id);
            messageAdapter.setMessageList(messageList);
            swipeView.setEnabled(id != -1);

            int markerLine = client.bufferSyncer().markerLine(id);
            for (int i = 0; i < messageList.size(); i++) {
                if (messageList.get(i).messageId == markerLine) {
                    Log.d("DEBUG", "Load: " + markerLine + ":" + i);
                    messages.scrollToPosition(i);
                    break;
                }
            }
        } else {
            swipeView.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        setMarkerline();
        super.onPause();
    }

    private void setMarkerline() {
        Client client = context.client();
        if (client == null) return;

        int buffer = client.backlogManager().open();
        if (buffer == -1) return;

        int messageposition = layoutManager.findFirstCompletelyVisibleItemPosition();
        if (messageposition == -1) return;

        Message message = messageAdapter.getItem(messageposition);
        if (message == null) return;

        client.bufferSyncer().requestSetMarkerLine(buffer, message.messageId);
        Log.d("DEBUG", "Store: " + message.messageId + ":" + messageposition);
    }

    public void onEventMainThread(BacklogReceivedEvent event) {
        Client client = context.client();
        if (client != null && client.backlogManager().open() == event.bufferId) {
            swipeView.setRefreshing(false);
        }
    }
}
