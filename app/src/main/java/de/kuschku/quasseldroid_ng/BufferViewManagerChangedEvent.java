package de.kuschku.quasseldroid_ng;

public class BufferViewManagerChangedEvent {
    public final int id;
    public final Action action;
    public BufferViewManagerChangedEvent(int id, Action action) {
        this.id = id;
        this.action = action;
    }

    public enum Action {
        ADD,
        REMOVE,
        MODIFY
    }
}
