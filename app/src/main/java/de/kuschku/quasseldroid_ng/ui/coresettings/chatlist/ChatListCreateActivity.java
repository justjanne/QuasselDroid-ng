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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.impl.BufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewManager;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.NetworkSpinnerAdapter;
import de.kuschku.util.servicebound.BoundActivity;

public class ChatListCreateActivity extends BoundActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.name)
    EditText name;

    @Bind(R.id.network)
    Spinner network;

    @Bind(R.id.showChannels)
    CheckBox showChannels;

    @Bind(R.id.showQueries)
    CheckBox showQueries;

    @Bind(R.id.hideInactiveChats)
    CheckBox hideInactiveChats;

    @Bind(R.id.hideInactiveNetworks)
    CheckBox hideInactiveNetworks;

    @Bind(R.id.addAutomatically)
    CheckBox addAutomatically;

    @Bind(R.id.sortAlphabetically)
    CheckBox sortAlphabetically;

    @Bind(R.id.minimumActivity)
    Spinner minimumActivity;

    private QBufferViewManager bufferViewManager;
    private NetworkSpinnerAdapter networkSpinnerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chatlist_edit);
        ButterKnife.bind(this);

        networkSpinnerAdapter = new NetworkSpinnerAdapter(this);
        network.setAdapter(networkSpinnerAdapter);

        minimumActivity.setAdapter(new MinimumActivityAdapter(context));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeDefaults();
    }

    private void initializeDefaults() {

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
                if (bufferViewManager != null) {
                    QBufferViewConfig config = new BufferViewConfig(
                            "",
                            Collections.<Integer>emptyList(),
                            false,
                            Collections.<Integer>emptyList(),
                            15,
                            true,
                            false,
                            true,
                            0,
                            QBufferViewConfig.MinimumActivity.NONE,
                            false,
                            Collections.<Integer>emptyList()
                    );

                    config._setBufferViewName(this.name.getText().toString());
                    config.setBufferTypeAllowed(BufferInfo.Type.CHANNEL, this.showChannels.isChecked());
                    config.setBufferTypeAllowed(BufferInfo.Type.QUERY, this.showQueries.isChecked());
                    config._setHideInactiveBuffers(this.hideInactiveChats.isChecked());
                    config._setHideInactiveNetworks(this.hideInactiveNetworks.isChecked());
                    config._setAddNewBuffersAutomatically(this.addAutomatically.isChecked());
                    config._setSortAlphabetically(this.sortAlphabetically.isChecked());
                    config._setNetworkId((int) network.getSelectedItemId());
                    config._setMinimumActivity(QBufferViewConfig.MinimumActivity.fromId((int) minimumActivity.getSelectedItemId()));

                    bufferViewManager.createBufferView(config);
                }
                finish();
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onConnected() {
        bufferViewManager = context.client().bufferViewManager();
    }

    @Override
    protected void onDisconnected() {
        bufferViewManager = null;
    }
}
