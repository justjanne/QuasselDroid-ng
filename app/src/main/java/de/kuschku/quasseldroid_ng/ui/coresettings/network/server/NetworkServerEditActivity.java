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
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.coresettings.network.server.helper.NetworkServerSerializeHelper;
import de.kuschku.util.backports.NumberHelper;
import de.kuschku.util.servicebound.BoundActivity;
import de.kuschku.util.ui.AnimationHelper;

public class NetworkServerEditActivity extends BoundActivity {

    public static final int RESULT_DELETE = -2;

    @Bind(R.id.toolbar)
    Toolbar toolbar;


    @Bind(R.id.host)
    EditText host;

    @Bind(R.id.port)
    EditText port;

    @Bind(R.id.useSSL)
    CheckBox useSSL;

    @Bind(R.id.password)
    EditText password;


    @Bind(R.id.useProxy)
    SwitchCompat useProxy;
    @Bind(R.id.groupProxy)
    ViewGroup groupProxy;
    @Bind(R.id.proxyHost)
    EditText proxyHost;
    @Bind(R.id.proxyPort)
    EditText proxyPort;
    @Bind(R.id.proxyType)
    Spinner proxyType;
    @Bind(R.id.proxyUser)
    EditText proxyUser;
    @Bind(R.id.proxyPassword)
    EditText proxyPassword;

    int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_networkserver_edit);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        useProxy.setOnCheckedChangeListener(this::updateProxyVisible);
        updateProxyVisible(null, useProxy.isChecked());

        ProxyTypeAdapter adapter = new ProxyTypeAdapter();
        proxyType.setAdapter(adapter);

        Intent intent;
        Bundle bundle = null;
        if ((intent = getIntent()) != null) {
            id = intent.getIntExtra("id", -1);

            bundle = intent.getBundleExtra("server");
        }

        if (bundle != null) {
            NetworkServer server = NetworkServerSerializeHelper.deserialize(bundle);

            host.setText(server.host);
            port.setText(String.valueOf(server.port));
            useSSL.setChecked(server.useSSL);
            password.setText(server.password);
            useProxy.setChecked(server.useProxy);
            proxyHost.setText(server.proxyHost);
            proxyPort.setText(String.valueOf(server.proxyPort));
            proxyUser.setText(server.proxyUser);
            proxyPassword.setText(server.proxyPass);
            proxyType.setSelection(adapter.indexOf(server.proxyType));
        } else {
            port.setText("6667");
        }

        useSSL.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (port.getText().toString().trim().equals("6667"))
                    port.setText("6697");
            } else {
                if (port.getText().toString().trim().equals("6697"))
                    port.setText("6667");
            }
        });
    }

    private void updateProxyVisible(CompoundButton button, boolean visible) {
        proxyHost.setEnabled(visible);
        proxyPort.setEnabled(visible);
        proxyUser.setEnabled(visible);
        proxyPassword.setEnabled(visible);

        updateViewGroupStatus(groupProxy, visible);
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
        if (id == -1)
            getMenuInflater().inflate(R.menu.confirm, menu);
        else
            getMenuInflater().inflate(R.menu.confirm_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete: {
                Intent intent = new Intent();
                intent.putExtra("id", id);
                setResult(RESULT_DELETE, intent);
                finish();
            }
            return true;
            case R.id.action_confirm: {
                Intent intent = new Intent();
                intent.putExtra("server", NetworkServerSerializeHelper.serialize(new NetworkServer(
                        useSSL.isChecked(),
                        0,
                        host.getText().toString(),
                        NumberHelper.parseInt(port.getText().toString(), -1),
                        password.getText().toString(),
                        useProxy.isChecked(),
                        NetworkServer.ProxyType.fromId((int) proxyType.getSelectedItemId()),
                        proxyHost.getText().toString(),
                        NumberHelper.parseInt(proxyPort.getText().toString(), -1),
                        proxyUser.getText().toString(),
                        proxyPassword.getText().toString()
                )));
                intent.putExtra("id", id);
                setResult(RESULT_OK, intent);
                finish();
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ProxyTypeAdapter implements SpinnerAdapter {
        final List<NetworkServer.ProxyType> list = Arrays.asList(
                NetworkServer.ProxyType.Socks5Proxy,
                NetworkServer.ProxyType.HttpProxy
        );

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            TextView view = (TextView) inflater.inflate(R.layout.widget_spinner_item_toolbar, parent, false);
            NetworkServer.ProxyType type = getItem(position);
            view.setText(type == null ? "" : context.themeUtil().translations.proxyType(type));
            return view;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public NetworkServer.ProxyType getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).id;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            TextView view = (TextView) inflater.inflate(R.layout.widget_spinner_item_inline, parent, false);
            NetworkServer.ProxyType type = getItem(position);
            view.setText(type == null ? "" : context.themeUtil().translations.proxyType(type));
            return view;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }

        public int indexOf(NetworkServer.ProxyType proxyType) {
            return list.indexOf(proxyType);
        }
    }
}
