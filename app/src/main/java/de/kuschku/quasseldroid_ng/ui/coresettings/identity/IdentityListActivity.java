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

package de.kuschku.quasseldroid_ng.ui.coresettings.identity;

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
import de.kuschku.libquassel.client.IdentityManager;
import de.kuschku.libquassel.syncables.types.interfaces.QIdentity;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;
import de.kuschku.util.servicebound.BoundActivity;

public class IdentityListActivity extends BoundActivity {

    IdentityManager manager;

    @Bind(R.id.list)
    RecyclerView list;

    @Bind(R.id.add)
    FloatingActionButton add;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    IdentityAdapter adapter;
    OnQIdentityClickListener clickListener = identity -> {
        if (identity != null) {
            Intent intent = new Intent(this, IdentityEditActivity.class);
            intent.putExtra("id", identity.id());
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_list);
        ButterKnife.bind(this);

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setItemAnimator(new DefaultItemAnimator());
        adapter = new IdentityAdapter();
        list.setAdapter(adapter);

        add.setOnClickListener(view -> {
            startActivity(new Intent(this, IdentityCreateActivity.class));
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onConnected() {
        manager = context.client().identityManager();
        adapter.setManager(manager);
    }

    @Override
    protected void onDisconnected() {
        manager = null;
        adapter.setManager(null);
    }

    interface OnQIdentityClickListener {
        void onClick(QIdentity network);
    }

    private class IdentityAdapter extends RecyclerView.Adapter<IdentityViewHolder> {
        IdentityManager manager;
        AdapterUICallbackWrapper wrapper = new AdapterUICallbackWrapper(this);

        public void setManager(IdentityManager manager) {
            if (this.manager != null)
                this.manager.identities().addCallback(wrapper);

            this.manager = manager;

            if (this.manager != null)
                this.manager.identities().addCallback(wrapper);
        }

        @Override
        public IdentityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.widget_settings_network, parent, false);
            return new IdentityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(IdentityViewHolder holder, int position) {
            holder.bind(manager != null ? manager.identities().get(position) : null);
        }

        @Override
        public int getItemCount() {
            return manager == null ? 0 : manager.identities().size();
        }
    }

    class IdentityViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.network_name)
        TextView name;

        private QIdentity identity;

        public IdentityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> clickListener.onClick(identity));
        }

        public void bind(QIdentity identity) {
            this.identity = identity;
            name.setText(identity == null ? "" : identity.identityName());
        }
    }
}
