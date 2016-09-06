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
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.libquassel.syncables.types.impl.NetworkInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QIdentity;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.coresettings.identity.IdentitySpinnerAdapter;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.NetworkServerListActivity;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper.NetworkServerSerializeHelper;
import de.kuschku.util.backports.NumberHelper;
import de.kuschku.util.backports.Objects;
import de.kuschku.util.servicebound.BoundActivity;
import de.kuschku.util.ui.AnimationHelper;

public class NetworkEditActivity extends BoundActivity {

    private static final int REQUEST_SERVER_LIST = 1;
    private static final int REQUEST_PERFORM = 2;

    @Bind(R.id.toolbar)
    Toolbar toolbar;


    @Bind(R.id.networkName)
    EditText networkName;

    @Bind(R.id.identity)
    Spinner identity;

    @Bind(R.id.rejoinChannels)
    CheckBox rejoinChannels;

    @Bind(R.id.servers)
    Button servers;

    @Bind(R.id.useCustomCodecs)
    SwitchCompat useCustomCodecs;
    @Bind(R.id.groupCustomCodecs)
    ViewGroup groupCustomCodecs;
    @Bind(R.id.codecForServer)
    EditText codecForServer;
    @Bind(R.id.codecForEncoding)
    EditText codecForEncoding;
    @Bind(R.id.codecForDecoding)
    EditText codecForDecoding;

    @Bind(R.id.useAutoIdentify)
    SwitchCompat useAutoIdentify;
    @Bind(R.id.groupAutoIdentify)
    ViewGroup groupAutoIdentify;
    @Bind(R.id.autoIdentifyService)
    EditText autoIdentifyService;
    @Bind(R.id.autoIdentifyPassword)
    EditText autoIdentifyPassword;

    @Bind(R.id.useSasl)
    SwitchCompat useSasl;
    @Bind(R.id.groupSasl)
    ViewGroup groupSasl;
    @Bind(R.id.saslAccount)
    EditText saslAccount;
    @Bind(R.id.saslPassword)
    EditText saslPassword;

    @Bind(R.id.useAutoReconnect)
    SwitchCompat useAutoReconnect;
    @Bind(R.id.groupAutoReconnect)
    ViewGroup groupAutoReconnect;
    @Bind(R.id.autoReconnectInterval)
    EditText autoReconnectInterval;
    @Bind(R.id.autoReconnectRetries)
    EditText autoReconnectRetries;
    @Bind(R.id.unlimitedAutoReconnectRetries)
    CheckBox unlimitedAutoReconnectRetries;

    int id;
    IdentitySpinnerAdapter spinnerAdapter = new IdentitySpinnerAdapter();
    private QNetwork network;

    private List<NetworkServer> serverList = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        id = intent.getIntExtra("id", -1);

        setContentView(R.layout.activity_network_edit);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        identity.setAdapter(spinnerAdapter);

        useCustomCodecs.setOnCheckedChangeListener(this::updateCustomCodecsVisible);
        updateCustomCodecsVisible(null, useCustomCodecs.isChecked());

        useAutoIdentify.setOnCheckedChangeListener(this::updateAutoIdentifyVisible);
        updateAutoIdentifyVisible(null, useAutoIdentify.isChecked());

        useSasl.setOnCheckedChangeListener(this::updateSaslVisible);
        updateSaslVisible(null, useSasl.isChecked());

        useAutoReconnect.setOnCheckedChangeListener(this::updateAutoReconnectVisible);
        updateAutoReconnectVisible(null, useAutoReconnect.isChecked());

        unlimitedAutoReconnectRetries.setOnCheckedChangeListener(this::updateAutoReconnectRetriesUnlimited);
        updateAutoReconnectRetriesUnlimited(null, unlimitedAutoReconnectRetries.isChecked());

        servers.setOnClickListener(v -> {
            Intent intent1 = new Intent(NetworkEditActivity.this, NetworkServerListActivity.class);
            intent1.putExtra("servers", NetworkServerSerializeHelper.serialize(network.serverList()));
            startActivityForResult(intent1, REQUEST_SERVER_LIST, null);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PERFORM: {

            } break;
            case REQUEST_SERVER_LIST: {
                Parcelable[] servers = data.getParcelableArrayExtra("servers");
                Log.d("DEBUG", Arrays.toString(servers));
                if (servers != null) {
                    serverList = NetworkServerSerializeHelper.deserialize(servers);
                }
            } break;
        }
    }

    private void updateCustomCodecsVisible(CompoundButton button, boolean visible) {
        codecForServer.setEnabled(visible);
        codecForEncoding.setEnabled(visible);
        codecForDecoding.setEnabled(visible);

        NetworkEditActivity.this.updateViewGroupStatus(groupCustomCodecs, visible);
    }

    private void updateAutoIdentifyVisible(CompoundButton button, boolean visible) {
        autoIdentifyService.setEnabled(visible);
        autoIdentifyPassword.setEnabled(visible);

        updateViewGroupStatus(groupAutoIdentify, visible);
    }

    private void updateSaslVisible(CompoundButton button, boolean visible) {
        saslAccount.setEnabled(visible);
        saslPassword.setEnabled(visible);

        updateViewGroupStatus(groupSasl, visible);
    }

    private void updateAutoReconnectRetriesUnlimited(CompoundButton button, boolean visible) {
        autoReconnectRetries.setEnabled(!visible);
    }

    private void updateAutoReconnectVisible(CompoundButton button, boolean visible) {
        autoReconnectInterval.setEnabled(visible);
        autoReconnectRetries.setEnabled(visible);
        unlimitedAutoReconnectRetries.setEnabled(visible);

        updateViewGroupStatus(this.groupAutoReconnect, visible);
    }

    private void updateViewGroupStatus(ViewGroup group, boolean visible) {
        if (visible) {
            AnimationHelper.expand(group);
        } else {
            AnimationHelper.collapse(group);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm: {
                NetworkInfo networkInfo = this.network.networkInfo();
                if (networkInfo != null) {
                    NetworkInfo after = new NetworkInfo(
                            networkInfo.networkId(),
                            networkName.getText().toString(),
                            (int) identity.getSelectedItemId(),
                            useCustomCodecs.isChecked() ? this.codecForServer.getText().toString() : null,
                            useCustomCodecs.isChecked() ? this.codecForEncoding.getText().toString() : null,
                            useCustomCodecs.isChecked() ? this.codecForDecoding.getText().toString() : null,
                            serverList == null ? networkInfo.serverList() : serverList,
                            networkInfo.useRandomServer(),
                            //FIXME: IMPLEMENT
                            networkInfo.perform(),
                            useAutoIdentify.isChecked(),
                            autoIdentifyService.getText().toString(),
                            autoIdentifyPassword.getText().toString(),
                            useSasl.isChecked(),
                            saslAccount.getText().toString(),
                            saslPassword.getText().toString(),
                            useAutoReconnect.isChecked(),
                            NumberHelper.parseInt(autoReconnectInterval.getText().toString(), 0),
                            NumberHelper.parseShort(autoReconnectRetries.getText().toString(), (short) 0),
                            unlimitedAutoReconnectRetries.isChecked(),
                            rejoinChannels.isChecked()
                    );

                    if (!Objects.equals(networkInfo, after))
                        network.setNetworkInfo(after);

                    finish();
                }
            } return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onConnected() {
        setNetwork(context.client().networkManager().network(id));
        spinnerAdapter.setIdentityManager(context.client().identityManager());
    }

    private void setNetwork(QNetwork network) {
        this.network = network;

        NetworkInfo networkInfo = this.network.networkInfo();
        if (networkInfo != null) {
            networkName.setText(networkInfo.networkName());
            identity.setSelection(getIdentityPosition(networkInfo));
            useCustomCodecs.setChecked(networkInfo.codecForServer() != null || networkInfo.codecForEncoding() != null || networkInfo.codecForDecoding() != null);
            codecForServer.setText(networkInfo.codecForServer());
            codecForEncoding.setText(networkInfo.codecForEncoding());
            codecForDecoding.setText(networkInfo.codecForDecoding());
            useAutoIdentify.setChecked(networkInfo.useAutoIdentify());
            autoIdentifyService.setText(networkInfo.autoIdentifyService());
            autoIdentifyPassword.setText(networkInfo.autoIdentifyPassword());
            useSasl.setChecked(networkInfo.useSasl());
            saslAccount.setText(networkInfo.saslAccount());
            saslPassword.setText(networkInfo.saslPassword());
            useAutoReconnect.setChecked(networkInfo.useAutoReconnect());
            autoReconnectInterval.setText(String.valueOf(networkInfo.autoReconnectInterval()));
            autoReconnectRetries.setText(String.valueOf(networkInfo.autoReconnectRetries()));
            unlimitedAutoReconnectRetries.setChecked(networkInfo.unlimitedReconnectRetries());
            rejoinChannels.setChecked(networkInfo.rejoinChannels());

            updateCustomCodecsVisible(null, useCustomCodecs.isChecked());
            updateAutoIdentifyVisible(null, useAutoIdentify.isChecked());
            updateSaslVisible(null, useSasl.isChecked());
            updateAutoReconnectVisible(null, useAutoReconnect.isChecked());
        }
    }

    private int getIdentityPosition(NetworkInfo networkInfo) {
        QIdentity identity = context.client().identityManager().identity(networkInfo.identity());
        return context.client().identityManager().identities().indexOf(identity);
    }

    @Override
    protected void onDisconnected() {
        setNetwork(null);
        spinnerAdapter.setIdentityManager(null);
    }
}
