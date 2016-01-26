package de.kuschku.libquassel.events;

import android.support.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public class LoginFailedEvent {
    public final String reason;

    public LoginFailedEvent(String reason) {
        this.reason = reason;
    }

    @NonNull
    @Override
    public String toString() {
        return "LoginFailedEvent{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
