package de.kuschku.libquassel.events;

public class HandshakeFailedEvent {
    public final String reason;

    public HandshakeFailedEvent(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "HandshakeFailedEvent{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
