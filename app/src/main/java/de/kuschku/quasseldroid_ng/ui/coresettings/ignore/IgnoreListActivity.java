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

package de.kuschku.quasseldroid_ng.ui.coresettings.ignore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.syncables.types.impl.IgnoreListManager;
import de.kuschku.libquassel.syncables.types.interfaces.QIgnoreListManager;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.coresettings.ignore.helper.IgnoreRuleSerializerHelper;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;
import de.kuschku.util.servicebound.BoundActivity;
import de.kuschku.util.ui.DividerItemDecoration;

public class IgnoreListActivity extends BoundActivity {
    final OnIgnoreRuleClickListener clickListener = ignoreRule -> {
        if (ignoreRule != null) {
            Intent intent = new Intent(this, IgnoreRuleEditActivity.class);
            intent.putExtra("rule", IgnoreRuleSerializerHelper.serialize(ignoreRule));
            startActivityForResult(intent, 0, null);
        }
    };
    QIgnoreListManager manager;
    final OnIgnoreRuleActiveListener activeListener = (ignoreRule, active) -> {
        if (ignoreRule != null) {
            manager._toggleIgnoreRule(ignoreRule, active);
            manager.requestUpdate();
        }
    };

    @Bind(R.id.list)
    RecyclerView list;
    @Bind(R.id.add)
    FloatingActionButton add;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    IgnoreRuleAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_list);
        ButterKnife.bind(this);

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setItemAnimator(new DefaultItemAnimator());
        list.addItemDecoration(new DividerItemDecoration(this));
        adapter = new IgnoreRuleAdapter();
        list.setAdapter(adapter);

        add.setOnClickListener(view -> startActivityForResult(new Intent(this, IgnoreRuleEditActivity.class), 0, null));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onConnected() {
        manager = context.client().ignoreListManager();
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
                IgnoreListManager.IgnoreListItem item = IgnoreRuleSerializerHelper.deserialize(data.getBundleExtra("rule"));
                if (data.getStringExtra("original") != null)
                    manager._removeIgnoreListItem(data.getStringExtra("original"));
                manager._addIgnoreListItem(item);
                manager.requestUpdate();
            } else if (resultCode == IgnoreRuleEditActivity.RESULT_DELETE) {
                manager._removeIgnoreListItem(data.getStringExtra("original"));
                manager.requestUpdate();
            }
        }
    }

    interface OnIgnoreRuleClickListener {
        void onClick(IgnoreListManager.IgnoreListItem network);
    }

    interface OnIgnoreRuleActiveListener {
        void onChange(IgnoreListManager.IgnoreListItem network, boolean active);
    }

    private class IgnoreRuleAdapter extends RecyclerView.Adapter<IgnoreRuleViewHolder> {
        final AdapterUICallbackWrapper wrapper = new AdapterUICallbackWrapper(this);
        QIgnoreListManager manager;

        public void setManager(QIgnoreListManager manager) {
            if (this.manager != null)
                this.manager.ignoreList().addCallback(wrapper);

            this.manager = manager;

            if (this.manager != null)
                this.manager.ignoreList().addCallback(wrapper);
        }

        @Override
        public IgnoreRuleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.widget_settings_ignorerule, parent, false);
            return new IgnoreRuleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(IgnoreRuleViewHolder holder, int position) {
            holder.bind(manager != null ? manager.ignoreList().get(position) : null);
        }

        @Override
        public int getItemCount() {
            return manager == null ? 0 : manager.ignoreList().size();
        }
    }

    class IgnoreRuleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.text)
        TextView name;

        @Bind(R.id.active)
        SwitchCompat active;

        private IgnoreListManager.IgnoreListItem ignoreRule;

        public IgnoreRuleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> clickListener.onClick(ignoreRule));
            active.setOnCheckedChangeListener((view, checked) -> activeListener.onChange(ignoreRule, checked));
        }

        public void bind(IgnoreListManager.IgnoreListItem ignoreRule) {
            this.ignoreRule = ignoreRule;
            name.setText(ignoreRule == null ? "" : ignoreRule.getIgnoreRule().rule());
            active.setChecked(ignoreRule.isActive());
        }
    }
}
