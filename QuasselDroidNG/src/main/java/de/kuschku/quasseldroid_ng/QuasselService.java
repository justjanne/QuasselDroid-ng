package de.kuschku.quasseldroid_ng;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.quasseldroid_ng.util.ServerAddress;

public class QuasselService extends Service {
    private final IBinder binder = new LocalBinder();

    private ClientBackgroundThread bgThread;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public class LocalBinder extends Binder {
        public void startBackgroundThread(BusProvider provider, ServerAddress address) {
            bgThread = new ClientBackgroundThread(provider, address);
            new Thread(bgThread).start();
        }

        public ClientBackgroundThread getBackgroundThread() {
            return bgThread;
        }

        public void stopBackgroundThread() {
            if (bgThread != null) bgThread.close();
            bgThread = null;
        }
    }
}
