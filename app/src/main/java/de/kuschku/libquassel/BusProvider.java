package de.kuschku.libquassel;

import java.util.UUID;

import de.greenrobot.event.EventBus;

public class BusProvider {
    public final String id;
    public final EventBus handle;
    public final EventBus dispatch;
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
}
