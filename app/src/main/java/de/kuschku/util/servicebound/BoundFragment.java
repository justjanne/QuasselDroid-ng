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
import android.support.v4.app.Fragment;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.quasseldroid_ng.service.ClientBackgroundThread;
import de.kuschku.quasseldroid_ng.service.QuasselService;
import de.kuschku.quasseldroid_ng.ui.chat.util.ServiceHelper;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;
import de.kuschku.util.accounts.Account;
import de.kuschku.util.backports.Consumer;

public abstract class BoundFragment extends Fragment {
    protected final AppContext context = new AppContext();
    private QuasselService.LocalBinder binder;
    private Consumer<ClientBackgroundThread> consumer;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof QuasselService.LocalBinder) {
                binder = (QuasselService.LocalBinder) service;
                consumer = BoundFragment.this::onConnectToThread;
                binder.addCallback(consumer);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ServiceHelper.initContext(context, getActivity());
        super.onCreate(savedInstanceState);
        ServiceHelper.startServiceIfNotRunning(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        ServiceHelper.connectToService(getContext(), connection);
    }

    protected void connectToServer(Account account) {
        BusProvider provider = new BusProvider();
        binder.startBackgroundThread(provider, account);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (binder != null)
            binder.removeCallback(consumer);
        ServiceHelper.disconnect(getContext(), connection);
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
        if (provider != null)
            provider.event.register(this);
        context.withProvider(provider);
    }

    protected void stopConnection() {
        if (binder != null) {
            binder.stopBackgroundThread();
        }
    }
}
