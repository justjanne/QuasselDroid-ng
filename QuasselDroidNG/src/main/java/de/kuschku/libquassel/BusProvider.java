package de.kuschku.libquassel;

import android.support.annotation.NonNull;

import java.util.UUID;

import de.greenrobot.event.EventBus;

public class BusProvider {
    @NonNull private final String id;
    @NonNull public final EventBus handle;
    @NonNull public final EventBus dispatch;
    @NonNull
    public final EventBus event;

    public BusProvider() {
        this.id = UUID.randomUUID().toString();
        this.handle = new EventBus();
        this.dispatch = new EventBus();
        this.event = new EventBus();
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

    @Override
    public String toString() {
        return "BusProvider{" +
                "id='" + id + '\'' +
                '}';
    }
}
