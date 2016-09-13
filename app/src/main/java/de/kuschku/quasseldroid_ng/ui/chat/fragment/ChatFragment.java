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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.events.BacklogReceivedEvent;
import de.kuschku.libquassel.events.BufferChangeEvent;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.syncables.types.interfaces.QBacklogManager;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.service.ClientBackgroundThread;
import de.kuschku.quasseldroid_ng.ui.chat.chatview.MessageAdapter;
import de.kuschku.quasseldroid_ng.ui.chat.util.SlidingPanelHandler;
import de.kuschku.util.observables.AutoScroller;
import de.kuschku.util.observables.lists.AndroidObservableComparableSortedList;
import de.kuschku.util.servicebound.BoundFragment;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class ChatFragment extends BoundFragment {
    /**
     * The list containing the messages to be displayed
     */
    @Bind(R.id.messages)
    RecyclerView messages;

    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout sliderMain;

    @Bind(R.id.scroll_down)
    FloatingActionButton scrollDown;

    private MessageAdapter messageAdapter;
    private SlidingPanelHandler slidingPanelHandler;
    private boolean loading = false;

    private int recyclerViewMeasuredHeight = 0;
    private RecyclerView.OnScrollListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        slidingPanelHandler = new SlidingPanelHandler(getActivity(), sliderMain, context);

        assertNotNull(messages);

        messages.setItemAnimator(new DefaultItemAnimator());
        messages.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
        messageAdapter = new MessageAdapter(getActivity(), context, new AutoScroller(messages));
        messages.setAdapter(messageAdapter);

        listener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!loading && !recyclerView.canScrollVertically(-1)) {
                    Client client = context.client();
                    assertNotNull(client);
                    QBacklogManager backlogManager = client.backlogManager();
                    assertNotNull(backlogManager);
                    backlogManager.requestMoreBacklog(client.backlogManager().open(), 20);
                    loading = true;
                }
                if (recyclerViewMeasuredHeight == 0)
                    recyclerViewMeasuredHeight = recyclerView.getMeasuredHeight();
                boolean canScrollDown = recyclerView.canScrollVertically(1);
                boolean isScrollingDown = dy > 0;
                int scrollOffsetFromBottom = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollOffset() - recyclerViewMeasuredHeight;
                boolean isMoreThanOneScreenFromBottom = scrollOffsetFromBottom > recyclerViewMeasuredHeight;
                boolean smartVisibility = scrollDown.getVisibility() == View.VISIBLE || isMoreThanOneScreenFromBottom;
                scrollDown.setVisibility((canScrollDown && isScrollingDown && smartVisibility) ? View.VISIBLE : View.GONE);
            }
        };
        messages.addOnScrollListener(listener);

        scrollDown.setOnClickListener(view1 -> messages.smoothScrollToPosition(0));

        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMainThread(BufferChangeEvent event) {
        setMarkerline();

        Client client = context.client();
        if (client != null && client.connectionStatus() == ConnectionChangeEvent.Status.CONNECTED) {
            QBacklogManager backlogManager = client.backlogManager();
            int id = backlogManager.open();
            AndroidObservableComparableSortedList<Message> messageList = backlogManager.filtered(id);
            messageAdapter.setMessageList(messageList);
        }
    }

    @Override
    public void onPause() {
        setMarkerline();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        messageAdapter.setMessageList(null);
        messages.removeOnScrollListener(listener);
        scrollDown.setOnClickListener(null);
        slidingPanelHandler.onDestroy();
        super.onDestroy();
    }

    private void setMarkerline() {
        //int lastVisibleMessageId;
        //context.client().bufferSyncer().setMarkerLine(context.client().backlogManager().open(), lastVisibleMessageId);
    }

    @Override
    protected void onConnectToThread(@Nullable ClientBackgroundThread thread) {
        super.onConnectToThread(thread);
        onEventMainThread(new BufferChangeEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BacklogReceivedEvent event) {
        Client client = context.client();
        if (client != null && client.backlogManager().open() == event.bufferId) {
            loading = false;
        }
    }

    public boolean onBackPressed() {
        if (sliderMain.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            if (slidingPanelHandler.onBackPressed()) {
                return true;
            } else {
                sliderMain.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                return true;
            }
        } else {
            return false;
        }
    }
}
