package de.kuschku.libquassel.events;

import android.support.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public class HandshakeFailedEvent {
    public final String reason;

    public HandshakeFailedEvent(String reason) {
        this.reason = reason;
    }

    @NonNull
    @Override
    public String toString() {
        return "HandshakeFailedEvent{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
