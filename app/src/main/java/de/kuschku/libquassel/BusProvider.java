package de.kuschku.libquassel;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.UUID;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.NoSubscriberEvent;

public class BusProvider {
    @NonNull
    public final EventBus handle;
    @NonNull
    public final EventBus dispatch;
    @NonNull
    public final EventBus event;
    @NonNull
    private final String id;

    @NonNull
    private final BusHandler handleHandler = new BusHandler("QHANDLE");
    @NonNull
    private final BusHandler dispatchHandler = new BusHandler("QDISPATCH");
    @NonNull
    private final BusHandler eventHandler = new BusHandler("QEVENT");

    public BusProvider() {
        this.id = UUID.randomUUID().toString();
        this.handle = new EventBus();
        this.handle.register(handleHandler);
        this.dispatch = new EventBus();
        this.dispatch.register(dispatchHandler);
        this.event = new EventBus();
        this.event.register(eventHandler);
    }

    public void handle(Object o) {
        this.handle.post(o);
    }

    public void dispatch(Object o) {
        this.dispatch.post(o);
    }

    public void sendEvent(Object o) {
        this.event.post(o);
    }

    @NonNull
    @Override
    public String toString() {
        return "BusProvider{" +
                "id='" + id + '\'' +
                '}';
    }

    public static class BusHandler {
        private final String identifier;

        public BusHandler(String identifier) {
            this.identifier = identifier;
        }

        public void onEvent(NoSubscriberEvent event) {
            Log.e(identifier, String.valueOf(event));
        }
    }
}
