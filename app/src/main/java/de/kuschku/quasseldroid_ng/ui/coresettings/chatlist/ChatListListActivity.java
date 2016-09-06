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

package de.kuschku.quasseldroid_ng.ui.coresettings.chatlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewManager;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;
import de.kuschku.util.servicebound.BoundActivity;

public class ChatListListActivity extends BoundActivity {

    QBufferViewManager manager;

    @Bind(R.id.list)
    RecyclerView list;

    @Bind(R.id.add)
    FloatingActionButton add;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    ChatListAdapter adapter;
    OnQBufferViewConfigClickListener clickListener = config -> {
        if (config != null) {
            Intent intent = new Intent(this, ChatListEditActivity.class);
            intent.putExtra("id", config.bufferViewId());
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist_list);
        ButterKnife.bind(this);

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChatListAdapter();
        list.setAdapter(adapter);

        add.setOnClickListener(view -> {
            startActivity(new Intent(this, ChatListCreateActivity.class));
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onConnected() {
        manager = context.client().bufferViewManager();
        adapter.setManager(manager);
    }

    @Override
    protected void onDisconnected() {
        manager = null;
        adapter.setManager(null);
    }

    interface OnQBufferViewConfigClickListener {
        void onClick(QBufferViewConfig config);
    }

    private class ChatListAdapter extends RecyclerView.Adapter<ChatListViewHolder> {
        QBufferViewManager manager;
        AdapterUICallbackWrapper wrapper = new AdapterUICallbackWrapper(this);

        public void setManager(QBufferViewManager manager) {
            if (this.manager != null)
                this.manager.bufferViewConfigs().removeCallback(wrapper);

            this.manager = manager;

            if (this.manager != null)
                this.manager.bufferViewConfigs().addCallback(wrapper);
        }

        @Override
        public ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.widget_chatlist, parent, false);
            return new ChatListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ChatListViewHolder holder, int position) {
            holder.bind(manager != null ? manager.bufferViewConfigs().get(position) : null);
        }

        @Override
        public int getItemCount() {
            return manager == null ? 0 : manager.bufferViewConfigs().size();
        }
    }

    class ChatListViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.chatlist_name)
        TextView name;

        private QBufferViewConfig config;

        public ChatListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> clickListener.onClick(config));
        }

        public void bind(QBufferViewConfig config) {
            this.config = config;
            name.setText(config == null ? "" : config.bufferViewName());
        }
    }
}
