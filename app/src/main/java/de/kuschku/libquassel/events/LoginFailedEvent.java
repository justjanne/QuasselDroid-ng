package de.kuschku.libquassel.events;

public class LoginFailedEvent {
    public final String reason;

    public LoginFailedEvent(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "LoginFailedEvent{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
