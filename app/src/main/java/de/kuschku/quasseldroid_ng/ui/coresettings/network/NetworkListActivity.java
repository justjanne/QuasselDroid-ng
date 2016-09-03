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

package de.kuschku.quasseldroid_ng.ui.coresettings.network;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatImageButton;
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
import de.kuschku.libquassel.client.NetworkManager;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;
import de.kuschku.util.servicebound.BoundActivity;

public class NetworkListActivity extends BoundActivity {

    NetworkManager manager;

    @Bind(R.id.list)
    RecyclerView list;

    @Bind(R.id.add)
    FloatingActionButton add;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    ChatListAdapter adapter;
    OnQNetworkClickListener clickListener = network -> {
        if (network != null) {
            Intent intent = new Intent(this, NetworkEditActivity.class);
            intent.putExtra("id", network.networkId());
            startActivity(intent);
        }
    };
    OnQNetworkDeleteListener deleteListener = network -> {
        if (manager != null && network != null) {
            context.client().removeNetwork(network.networkId());
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_list);
        ButterKnife.bind(this);

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChatListAdapter();
        list.setAdapter(adapter);

        add.setOnClickListener(view -> {
            startActivity(new Intent(this, NetworkCreateActivity.class));
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onConnected() {
        manager = context.client().networkManager();
        adapter.setManager(manager);
    }

    @Override
    protected void onDisconnected() {
        manager = null;
        adapter.setManager(null);
    }

    interface OnQNetworkClickListener {
        void onClick(QNetwork network);
    }

    interface OnQNetworkDeleteListener {
        void onDelete(QNetwork network);
    }

    private class ChatListAdapter extends RecyclerView.Adapter<NetworkViewHolder> {
        NetworkManager manager;
        AdapterUICallbackWrapper wrapper = new AdapterUICallbackWrapper(this);

        public void setManager(NetworkManager manager) {
            if (this.manager != null)
                this.manager.networks().addCallback(wrapper);

            this.manager = manager;

            if (this.manager != null)
                this.manager.networks().addCallback(wrapper);
        }

        @Override
        public NetworkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.widget_settings_network, parent, false);
            return new NetworkViewHolder(view);
        }

        @Override
        public void onBindViewHolder(NetworkViewHolder holder, int position) {
            holder.bind(manager != null ? manager.networks().get(position) : null);
        }

        @Override
        public int getItemCount() {
            return manager == null ? 0 : manager.networks().size();
        }
    }

    class NetworkViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.network_name)
        TextView name;

        @Bind(R.id.network_delete)
        AppCompatImageButton delete;

        private QNetwork network;

        public NetworkViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> clickListener.onClick(network));
            delete.setOnClickListener(view -> deleteListener.onDelete(network));
        }

        public void bind(QNetwork network) {
            this.network = network;
            name.setText(network == null ? "" : network.networkName());
        }
    }
}
