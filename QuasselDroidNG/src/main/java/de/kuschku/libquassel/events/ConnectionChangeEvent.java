package de.kuschku.libquassel.events;

import android.support.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public class ConnectionChangeEvent {
    @NonNull
    public final Status status;
    @NonNull
    public final String reason;

    public ConnectionChangeEvent(@NonNull Status status) {
        this(status, "");
    }

    public ConnectionChangeEvent(@NonNull Status status, @NonNull String reason) {
        this.status = status;
        this.reason = reason;
    }

    @NonNull
    @Override
    public String toString() {
        return "ConnectionChangeEvent{" +
                "status=" + status +
                ", reason='" + reason + '\'' +
                '}';
    }

    public enum Status {
        CONNECTING,
        HANDSHAKE,
        CORE_SETUP_REQUIRED,
        LOGIN_REQUIRED,
        USER_SETUP_REQUIRED,
        INITIALIZING_DATA,
        LOADING_BACKLOG,
        CONNECTED,
        DISCONNECTED
    }
}
