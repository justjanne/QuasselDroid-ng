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
