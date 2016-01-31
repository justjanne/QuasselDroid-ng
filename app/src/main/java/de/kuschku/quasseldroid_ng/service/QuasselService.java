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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid_ng.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.util.ServerAddress;

public class QuasselService extends Service {
    @NonNull
    private final IBinder binder = new LocalBinder();

    @Nullable
    private ClientBackgroundThread bgThread;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public class LocalBinder extends Binder {
        public void startBackgroundThread(@NonNull BusProvider provider, @NonNull ServerAddress address) {
            bgThread = new ClientBackgroundThread(provider, address, QuasselService.this);
            new Thread(bgThread).start();
        }

        @Nullable
        public ClientBackgroundThread getBackgroundThread() {
            return bgThread;
        }

        public void stopBackgroundThread() {
            if (bgThread != null) bgThread.close();
            bgThread = null;
        }
    }
}
