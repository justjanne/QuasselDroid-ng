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

package de.kuschku.quasseldroid_ng.ui.coresettings.aliases;

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
import de.kuschku.libquassel.syncables.types.impl.AliasManager;
import de.kuschku.libquassel.syncables.types.interfaces.QAliasManager;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.coresettings.aliases.helper.AliasSerializerHelper;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;
import de.kuschku.util.servicebound.BoundActivity;
import de.kuschku.util.ui.DividerItemDecoration;

public class AliasListActivity extends BoundActivity {
    final OnAliasClickListener clickListener = alias -> {
        if (alias != null) {
            Intent intent = new Intent(this, AliasEditActivity.class);
            intent.putExtra("alias", AliasSerializerHelper.serialize(alias));
            startActivityForResult(intent, 0, null);
        }
    };

    @Bind(R.id.list)
    RecyclerView list;
    @Bind(R.id.add)
    FloatingActionButton add;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    AliasAdapter adapter;
    private QAliasManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_list);
        ButterKnife.bind(this);

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setItemAnimator(new DefaultItemAnimator());
        list.addItemDecoration(new DividerItemDecoration(this));
        adapter = new AliasAdapter();
        list.setAdapter(adapter);

        add.setOnClickListener(view -> startActivityForResult(new Intent(this, AliasEditActivity.class), 0, null));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onConnected() {
        manager = context.client().aliasManager();
        adapter.setManager(manager);
    }

    @Override
    protected void onDisconnected() {
        manager = null;
        adapter.setManager(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (resultCode == RESULT_OK) {
                AliasManager.Alias item = AliasSerializerHelper.deserialize(data.getBundleExtra("alias"));
                if (data.getStringExtra("original") != null)
                    manager._removeAlias(manager.alias(data.getStringExtra("original")));
                manager._addAlias(item);
                manager.requestUpdate();
            } else if (resultCode == AliasEditActivity.RESULT_DELETE) {
                manager._removeAlias(manager.alias(data.getStringExtra("original")));
                manager.requestUpdate();
            }
        }
    }

    interface OnAliasClickListener {
        void onClick(AliasManager.Alias network);
    }

    private class AliasAdapter extends RecyclerView.Adapter<AliasViewHolder> {
        final AdapterUICallbackWrapper wrapper = new AdapterUICallbackWrapper(this);
        QAliasManager manager;

        public void setManager(QAliasManager manager) {
            if (this.manager != null)
                this.manager.aliases().addCallback(wrapper);

            this.manager = manager;

            if (this.manager != null)
                this.manager.aliases().addCallback(wrapper);
        }

        @Override
        public AliasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.widget_settings_alias, parent, false);
            return new AliasViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AliasViewHolder holder, int position) {
            holder.bind(manager != null ? manager.aliases().get(position) : null);
        }

        @Override
        public int getItemCount() {
            return manager == null ? 0 : manager.aliases().size();
        }
    }

    class AliasViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.alias_name)
        TextView name;

        @Bind(R.id.alias_description)
        TextView description;

        private AliasManager.Alias alias;

        public AliasViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> clickListener.onClick(alias));
        }

        public void bind(AliasManager.Alias alias) {
            this.alias = alias;
            name.setText(alias == null ? "" : alias.name);
            description.setText(alias == null ? "" : alias.expansion);
        }
    }
}
