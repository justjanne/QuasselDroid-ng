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
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.syncables.types.impl.NetworkInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.backports.Objects;
import de.kuschku.util.servicebound.BoundActivity;

public class NetworkEditActivity extends BoundActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;


    @Bind(R.id.networkName)
    EditText networkName;

    @Bind(R.id.identity)
    Spinner identity;

    @Bind(R.id.rejoinChannels)
    CheckBox rejoinChannels;

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

    public static void expand(final ViewGroup v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.setAlpha(interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final ViewGroup v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.setAlpha(1 - interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

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

        useCustomCodecs.setOnCheckedChangeListener(this::updateCustomCodecsVisible);
        updateCustomCodecsVisible(null, useCustomCodecs.isChecked());

        useAutoIdentify.setOnCheckedChangeListener(this::updateAutoIdentifyVisible);
        updateAutoIdentifyVisible(null, useAutoIdentify.isChecked());

        useSasl.setOnCheckedChangeListener(this::updateSaslVisible);
        updateSaslVisible(null, useSasl.isChecked());

        useAutoReconnect.setOnCheckedChangeListener(this::updateAutoReconnectVisible);
        updateAutoReconnectVisible(null, useAutoReconnect.isChecked());
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

    private void updateAutoReconnectVisible(CompoundButton button, boolean visible) {
        autoReconnectInterval.setEnabled(visible);
        autoReconnectRetries.setEnabled(visible);
        unlimitedAutoReconnectRetries.setEnabled(visible);

        updateViewGroupStatus(this.groupAutoReconnect, visible);
    }

    private void updateViewGroupStatus(ViewGroup group, boolean visible) {
        if (visible) {
            expand(group);
        } else {
            collapse(group);
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
                            //FIXME: IMPLEMENT
                            networkInfo.identity(),
                            useCustomCodecs.isChecked() ? this.codecForServer.getText().toString() : null,
                            useCustomCodecs.isChecked() ? this.codecForEncoding.getText().toString() : null,
                            useCustomCodecs.isChecked() ? this.codecForDecoding.getText().toString() : null,
                            //FIXME: IMPLEMENT
                            networkInfo.serverList(),
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
                            Integer.parseInt(autoReconnectInterval.getText().toString()),
                            Short.parseShort(autoReconnectRetries.getText().toString()),
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
    }

    private void setNetwork(QNetwork network) {
        this.network = network;

        NetworkInfo networkInfo = this.network.networkInfo();
        if (networkInfo != null) {
            networkName.setText(networkInfo.networkName());
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

    @Override
    protected void onDisconnected() {
        setNetwork(null);
    }
}
