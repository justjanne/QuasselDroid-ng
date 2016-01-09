package de.kuschku.quasseldroid_ng;

public class BufferViewManagerChangedEvent {
    public enum Action {
        ADD,
        REMOVE,
        MODIFY
    }

    public final int id;
    public final Action action;

    public BufferViewManagerChangedEvent(int id, Action action) {
        this.id = id;
        this.action = action;
    }
}
