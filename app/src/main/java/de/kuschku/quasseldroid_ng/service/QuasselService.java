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

package de.kuschku.quasseldroid_ng.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.util.accounts.Account;
import de.kuschku.util.backports.Consumer;

public class QuasselService extends Service {
    @NonNull
    private final IBinder binder = new LocalBinder();

    @Nullable
    private ClientBackgroundThread bgThread;

    private Set<Consumer<ClientBackgroundThread>> consumers = new HashSet<>();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public class LocalBinder extends Binder {
        public void startBackgroundThread(@NonNull BusProvider provider, @NonNull Account account) {
            bgThread = new ClientBackgroundThread(provider, account, QuasselService.this);
            new Thread(bgThread).start();
            notify(bgThread);
        }

        @Nullable
        public ClientBackgroundThread getBackgroundThread() {
            return bgThread;
        }

        public void stopBackgroundThread() {
            if (bgThread != null) bgThread.close();
            bgThread = null;
        }

        public void addCallback(Consumer<ClientBackgroundThread> consumer) {
            consumers.add(consumer);
        }
        public void removeCallback(Consumer<ClientBackgroundThread> consumer) {
            consumers.remove(consumer);
        }
        private void notify(ClientBackgroundThread thread) {
            for (Consumer<ClientBackgroundThread> consumer : consumers) {
                consumer.apply(thread);
            }
        }
    }
}
