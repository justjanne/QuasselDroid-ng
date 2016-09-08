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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.NetworkSpinnerAdapter;
import de.kuschku.util.backports.Objects;
import de.kuschku.util.servicebound.BoundActivity;

public class ChatListEditActivity extends BoundActivity {

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

    private int id;
    private QBufferViewConfig config;
    private NetworkSpinnerAdapter networkSpinnerAdapter;
    private MinimumActivityAdapter minimumActivityAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        id = intent.getIntExtra("id", -1);

        setContentView(R.layout.activity_chatlist_edit);
        ButterKnife.bind(this);

        networkSpinnerAdapter = new NetworkSpinnerAdapter(this);
        network.setAdapter(networkSpinnerAdapter);

        minimumActivityAdapter = new MinimumActivityAdapter(context);
        minimumActivity.setAdapter(minimumActivityAdapter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.confirm_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (hasChanged()) {
            new MaterialDialog.Builder(this)
                    .content(R.string.youhavemadechangesdoyouwishtosavethem)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .positiveColor(context.themeUtil().res.colorAccent)
                    .negativeColor(context.themeUtil().res.colorForeground)
                    .onPositive((dialog, which) -> {
                        save();
                        super.onBackPressed();
                    })
                    .onNegative((dialog, which) -> super.onBackPressed())
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete: {
                new MaterialDialog.Builder(this)
                        .content(getString(R.string.areyousureyouwanttodelete, config.bufferViewName()))
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .positiveColor(context.themeUtil().res.colorAccent)
                        .negativeColor(context.themeUtil().res.colorForeground)
                        .onPositive((dialog, which) -> {
                            finish();
                            context.client().bufferViewManager().deleteBufferView(config.bufferViewId());
                        })
                        .build()
                        .show();
            }
            return true;
            case R.id.action_confirm: {
                save();
                finish();
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void save() {
        if (config != null) {
            String name = this.name.getText().toString();
            if (!Objects.equals(name, config.bufferViewName()))
                config.setBufferViewName(name);

            if (config.networkId() != (int) network.getSelectedItemId())
                config.setNetworkId((int) network.getSelectedItemId());

            if (config.minimumActivity() != QBufferViewConfig.MinimumActivity.fromId((int) minimumActivity.getSelectedItemId()))
                config.setMinimumActivity(QBufferViewConfig.MinimumActivity.fromId((int) minimumActivity.getSelectedItemId()));

            int allowedBufferTypes = config.allowedBufferTypes();
            config.setBufferTypeAllowed(BufferInfo.Type.CHANNEL, this.showChannels.isChecked());
            config.setBufferTypeAllowed(BufferInfo.Type.QUERY, this.showQueries.isChecked());
            if (config.allowedBufferTypes() != allowedBufferTypes)
                config.setAllowedBufferTypes(allowedBufferTypes);

            boolean hideInactiveChats = this.hideInactiveChats.isChecked();
            if (hideInactiveChats != config.hideInactiveBuffers())
                config.setHideInactiveBuffers(hideInactiveChats);

            boolean hideInactiveNetworks = this.hideInactiveNetworks.isChecked();
            if (hideInactiveNetworks != config.hideInactiveNetworks())
                config.setHideInactiveNetworks(hideInactiveNetworks);

            boolean addAutomatically = this.addAutomatically.isChecked();
            if (addAutomatically != config.addNewBuffersAutomatically())
                config.setAddNewBuffersAutomatically(addAutomatically);

            boolean sortAlphabetically = this.sortAlphabetically.isChecked();
            if (sortAlphabetically != config.sortAlphabetically())
                config.setSortAlphabetically(sortAlphabetically);
        }
    }

    private boolean hasChanged() {
        return !Objects.equals(name.getText().toString(), config.bufferViewName()) ||
                config.networkId() != (int) network.getSelectedItemId() ||
                config.minimumActivity() != QBufferViewConfig.MinimumActivity.fromId((int) minimumActivity.getSelectedItemId()) ||
                config.isBufferTypeAllowed(BufferInfo.Type.CHANNEL) != this.showChannels.isChecked() ||
                config.isBufferTypeAllowed(BufferInfo.Type.QUERY) != this.showQueries.isChecked() ||
                this.hideInactiveChats.isChecked() != config.hideInactiveBuffers() ||
                this.hideInactiveNetworks.isChecked() != config.hideInactiveNetworks() ||
                this.addAutomatically.isChecked() != config.addNewBuffersAutomatically() ||
                this.sortAlphabetically.isChecked() != config.sortAlphabetically();
    }

    @Override
    protected void onConnected() {
        networkSpinnerAdapter.setNetworkManager(context.client().networkManager());
        setConfig(context.client().bufferViewManager().bufferViewConfig(id));
    }

    private void setConfig(QBufferViewConfig config) {
        this.config = config;

        if (config != null) {
            name.setText(config.bufferViewName());
            network.setSelection(getSelectedNetworkIndex(config));
            showChannels.setChecked(config.isBufferTypeAllowed(BufferInfo.Type.CHANNEL));
            showQueries.setChecked(config.isBufferTypeAllowed(BufferInfo.Type.QUERY));
            hideInactiveChats.setChecked(config.hideInactiveBuffers());
            hideInactiveNetworks.setChecked(config.hideInactiveNetworks());
            addAutomatically.setChecked(config.addNewBuffersAutomatically());
            sortAlphabetically.setChecked(config.sortAlphabetically());
            minimumActivity.setSelection(minimumActivityAdapter.indexOf(config.minimumActivity()));
        }
    }

    private int getSelectedNetworkIndex(QBufferViewConfig config) {
        QNetwork network;
        if (context.client() == null) {
            return 0;
        } else if ((network = context.client().networkManager().network(config.networkId())) == null) {
            return 0;
        } else {
            return context.client().networkManager().networks().indexOf(network) + 1;
        }
    }

    @Override
    protected void onDisconnected() {
        networkSpinnerAdapter.setNetworkManager(null);
        setConfig(null);
    }
}
