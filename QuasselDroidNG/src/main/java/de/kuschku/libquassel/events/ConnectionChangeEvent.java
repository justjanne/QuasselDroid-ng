package de.kuschku.libquassel.events;

public class ConnectionChangeEvent {
    public final Status status;
    public final String reason;

    public ConnectionChangeEvent(Status status) {
        this(status, "");
    }

    public ConnectionChangeEvent(Status status, String reason) {
        this.status = status;
        this.reason = reason;
    }

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
