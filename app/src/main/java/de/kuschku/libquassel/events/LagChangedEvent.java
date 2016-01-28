package de.kuschku.libquassel.events;

public class LagChangedEvent {
    public final long lag;

    public LagChangedEvent(long lag) {
        this.lag = lag;
    }

    @Override
    public String toString() {
        return "LagChangedEvent{" +
                "lag=" + lag +
                '}';
    }
}
