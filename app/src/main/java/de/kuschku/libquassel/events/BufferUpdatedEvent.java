package de.kuschku.libquassel.events;

public class BufferUpdatedEvent {
    public final int bufferId;

    public BufferUpdatedEvent(int bufferId) {
        this.bufferId = bufferId;
    }

    @Override
    public String toString() {
        return "BufferUpdatedEvent{" +
                "bufferId=" + bufferId +
                '}';
    }
}
