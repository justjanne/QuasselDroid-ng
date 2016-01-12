package de.kuschku.libquassel.events;

public class BacklogReceivedEvent {
    public final int bufferId;

    public BacklogReceivedEvent(int bufferId) {
        this.bufferId = bufferId;
    }
}
