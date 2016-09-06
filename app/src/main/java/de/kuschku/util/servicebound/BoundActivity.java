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

package de.kuschku.util.servicebound;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.service.ClientBackgroundThread;
import de.kuschku.quasseldroid_ng.service.QuasselService;
import de.kuschku.quasseldroid_ng.ui.chat.util.ServiceHelper;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.quasseldroid_ng.ui.theme.AppTheme;
import de.kuschku.util.accounts.Account;
import de.kuschku.util.annotationbind.AutoBinder;
import de.kuschku.util.ui.MenuTint;

public abstract class BoundActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected AppContext context = new AppContext();
    protected QuasselService.LocalBinder binder;
    @StyleRes
    private int themeId;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof QuasselService.LocalBinder) {
                binder = (QuasselService.LocalBinder) service;
                binder.addCallback(BoundActivity.this::onConnectToThread);
                onConnectToThread(binder.getBackgroundThread());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
            onConnectToThread(null);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        themeId = ServiceHelper.initContext(context, this);
        super.onCreate(savedInstanceState);
        context.settings().preferenceTheme.addChangeListener(s -> recreate());
        ServiceHelper.startServiceIfNotRunning(this);
        ServiceHelper.connectToService(this, connection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ServiceHelper.connectToService(this, connection);
        if (themeId != AppTheme.themeFromString(context.settings().preferenceTheme.get()).themeId) {
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        setProvider(null);
        ServiceHelper.disconnect(this, connection);
    }

    protected void connectToServer(Account account) {
        BusProvider provider = new BusProvider();
        binder.startBackgroundThread(provider, account);
    }

    protected void onConnectToThread(@Nullable ClientBackgroundThread thread) {
        if (thread == null) {
            context.withClient(null);
            setProvider(null);
        } else {
            context.withClient(thread.client().client);
            setProvider(thread.client().provider);
        }
    }

    protected void setProvider(BusProvider provider) {
        BusProvider oldProvider = context.provider();
        if (oldProvider != null)
            oldProvider.event.unregister(this);
        context.withProvider(provider);
        if (provider != null)
            provider.event.register(this);
    }

    protected void onConnected() {
    }

    protected void onDisconnected() {
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onConnectionStatusChange(ConnectionChangeEvent event) {
        if (event.status == ConnectionChangeEvent.Status.CONNECTED)
            onConnected();
        if (event.status == ConnectionChangeEvent.Status.DISCONNECTED)
            onDisconnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean value = super.onCreateOptionsMenu(menu);
        MenuTint.colorIcons(this, menu, AutoBinder.obtainColor(R.attr.colorFill, getSupportActionBar().getThemedContext().getTheme()));
        return value;
    }

    protected void stopConnection() {
        if (binder != null) {
            binder.stopBackgroundThread();
        }
    }
}
