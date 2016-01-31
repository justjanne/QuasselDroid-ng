package de.kuschku.libquassel.events;

import android.support.annotation.NonNull;

public class LagChangedEvent {
    public final long lag;

    public LagChangedEvent(long lag) {
        this.lag = lag;
    }

    @NonNull
    @Override
    public String toString() {
        return "LagChangedEvent{" +
                "lag=" + lag +
                '}';
    }
}
