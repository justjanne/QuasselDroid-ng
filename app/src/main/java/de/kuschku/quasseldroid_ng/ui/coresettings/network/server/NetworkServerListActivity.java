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

package de.kuschku.quasseldroid_ng.ui.coresettings.network.server;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper.NetworkServerSerializeHelper;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper.OnStartDragListener;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper.SimpleItemTouchHelperCallback;
import de.kuschku.util.observables.callbacks.wrappers.AdapterUICallbackWrapper;
import de.kuschku.util.observables.lists.ObservableList;
import de.kuschku.util.servicebound.BoundActivity;
import de.kuschku.util.ui.DividerItemDecoration;

public class NetworkServerListActivity extends BoundActivity implements OnStartDragListener {

    @Bind(R.id.list)
    RecyclerView list;

    @Bind(R.id.add)
    FloatingActionButton add;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    NetworkServerAdapter adapter;
    ItemTouchHelper itemTouchHelper;
    ObservableList<NetworkServer> servers;
    final OnNetworkServerClickListener clickListener = server -> {
        if (server != null) {
            Intent intent1 = new Intent(this, NetworkServerEditActivity.class);
            intent1.putExtra("server", NetworkServerSerializeHelper.serialize(server));
            intent1.putExtra("id", servers.indexOf(server));
            startActivityForResult(intent1, 0, null);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_list);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Parcelable[] serverList;
        if (intent != null && (serverList = intent.getParcelableArrayExtra("servers")) != null) {
            servers = new ObservableList<>(NetworkServerSerializeHelper.deserialize(serverList));
        } else {
            servers = new ObservableList<>();
        }
        adapter = new NetworkServerAdapter(servers, this);
        servers.addCallback(new AdapterUICallbackWrapper(adapter));

        list.setAdapter(adapter);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.addItemDecoration(new DividerItemDecoration(this));
        adapter.setOnItemClickListener(clickListener);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(list);

        add.setOnClickListener(v -> {
            Intent intent1 = new Intent(NetworkServerListActivity.this, NetworkServerEditActivity.class);
            startActivityForResult(intent1, 0, null);
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm: {
                Intent intent = new Intent();
                intent.putExtra("servers", NetworkServerSerializeHelper.serialize(servers));
                setResult(RESULT_OK, intent);
                finish();
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle;
        if (resultCode == RESULT_OK && data != null && (bundle = data.getBundleExtra("server")) != null) {
            NetworkServer server = NetworkServerSerializeHelper.deserialize(bundle);
            int id = data.getIntExtra("id", -1);
            if (id == -1) {
                servers.add(server);
            } else {
                servers.set(id, server);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    interface OnNetworkServerClickListener {
        void onClick(NetworkServer network);
    }
}
