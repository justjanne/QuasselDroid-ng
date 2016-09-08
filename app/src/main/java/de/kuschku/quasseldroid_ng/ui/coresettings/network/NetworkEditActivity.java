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
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.MaterialDialog;

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
    final IdentitySpinnerAdapter spinnerAdapter = new IdentitySpinnerAdapter();
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
        if (data != null) {
            switch (requestCode) {
                case REQUEST_PERFORM: {

                }
                break;
                case REQUEST_SERVER_LIST: {
                    Parcelable[] servers = data.getParcelableArrayExtra("servers");
                    if (servers != null) {
                        serverList = NetworkServerSerializeHelper.deserialize(servers);
                    }
                }
                break;
            }
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
        getMenuInflater().inflate(R.menu.confirm_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (hasChanged(build())) {
            new MaterialDialog.Builder(this)
                    .content(R.string.confirmationUnsavedChanges)
                    .positiveText(R.string.actionYes)
                    .negativeText(R.string.actionNo)
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
                        .content(getString(R.string.confirmationDelete, network.networkName()))
                        .positiveText(R.string.actionYes)
                        .negativeText(R.string.actionNo)
                        .positiveColor(context.themeUtil().res.colorAccent)
                        .negativeColor(context.themeUtil().res.colorForeground)
                        .onPositive((dialog, which) -> {
                            finish();
                            context.client().removeNetwork(network.networkId());
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
        NetworkInfo info = build();
        if (hasChanged(info))
            network.setNetworkInfo(info);
    }

    private boolean hasChanged(NetworkInfo info) {
        return network != null && network.networkInfo() != null && info != null && !Objects.equals(network.networkInfo(), info);
    }

    private NetworkInfo build() {
        NetworkInfo networkInfo = this.network.networkInfo();
        if (networkInfo == null) {
            return null;
        } else {
            return new NetworkInfo(
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
        }
    }

    @Override
    protected void onConnected() {
        spinnerAdapter.setIdentityManager(context.client().identityManager());
        setNetwork(context.client().networkManager().network(id));
    }

    private void setNetwork(QNetwork network) {
        NetworkInfo oldInfo = this.network == null ? null : this.network.networkInfo();
        this.network = network;

        if (network != null) {
            NetworkInfo networkInfo = this.network.networkInfo();
            if (networkInfo != null) {
                if (oldInfo == null || oldInfo.networkName().equals(networkName.getText().toString()))
                    networkName.setText(networkInfo.networkName());

                if (oldInfo == null || getIdentityPosition(oldInfo) == identity.getSelectedItemPosition())
                    identity.setSelection(getIdentityPosition(networkInfo));

                if (oldInfo == null || (oldInfo.codecForServer() != null || oldInfo.codecForEncoding() != null || oldInfo.codecForDecoding() != null) == useCustomCodecs.isChecked())
                    useCustomCodecs.setChecked(networkInfo.codecForServer() != null || networkInfo.codecForEncoding() != null || networkInfo.codecForDecoding() != null);

                if (oldInfo == null || oldInfo.codecForServer().equals(codecForServer.getText().toString()))
                    codecForServer.setText(networkInfo.codecForServer());

                if (oldInfo == null || oldInfo.codecForEncoding().equals(codecForEncoding.getText().toString()))
                    codecForEncoding.setText(networkInfo.codecForEncoding());

                if (oldInfo == null || oldInfo.codecForDecoding().equals(codecForDecoding.getText().toString()))
                    codecForDecoding.setText(networkInfo.codecForDecoding());

                if (oldInfo == null || oldInfo.useAutoIdentify() == useAutoIdentify.isChecked())
                    useAutoIdentify.setChecked(networkInfo.useAutoIdentify());

                if (oldInfo == null || oldInfo.autoIdentifyService().equals(autoIdentifyService.getText().toString()))
                    autoIdentifyService.setText(networkInfo.autoIdentifyService());

                if (oldInfo == null || oldInfo.autoIdentifyPassword().equals(autoIdentifyPassword.getText().toString()))
                    autoIdentifyPassword.setText(networkInfo.autoIdentifyPassword());

                if (oldInfo == null || oldInfo.useSasl() == useSasl.isChecked())
                    useSasl.setChecked(networkInfo.useSasl());

                if (oldInfo == null || oldInfo.saslAccount().equals(saslAccount.getText().toString()))
                    saslAccount.setText(networkInfo.saslAccount());

                if (oldInfo == null || oldInfo.saslPassword().equals(saslPassword.getText().toString()))
                    saslPassword.setText(networkInfo.saslPassword());

                if (oldInfo == null || oldInfo.useAutoReconnect() == useAutoReconnect.isChecked())
                    useAutoReconnect.setChecked(networkInfo.useAutoReconnect());

                if (oldInfo == null || oldInfo.autoReconnectInterval() == NumberHelper.parseInt(autoReconnectInterval.getText().toString(), 0))
                    autoReconnectInterval.setText(String.valueOf(networkInfo.autoReconnectInterval()));

                if (oldInfo == null || oldInfo.autoReconnectRetries() == NumberHelper.parseInt(autoReconnectRetries.getText().toString(), 0))
                    autoReconnectRetries.setText(String.valueOf(networkInfo.autoReconnectRetries()));

                if (oldInfo == null || oldInfo.unlimitedReconnectRetries() == unlimitedAutoReconnectRetries.isChecked())
                    unlimitedAutoReconnectRetries.setChecked(networkInfo.unlimitedReconnectRetries());

                if (oldInfo == null || oldInfo.rejoinChannels() == rejoinChannels.isChecked())
                    rejoinChannels.setChecked(networkInfo.rejoinChannels());
            }
        }
    }

    private int getIdentityPosition(NetworkInfo networkInfo) {
        QIdentity identity = context.client().identityManager().identity(networkInfo.identity());
        return context.client().identityManager().identities().indexOf(identity);
    }

    @Override
    protected void onDisconnected() {
        spinnerAdapter.setIdentityManager(null);
        setNetwork(null);
    }
}
