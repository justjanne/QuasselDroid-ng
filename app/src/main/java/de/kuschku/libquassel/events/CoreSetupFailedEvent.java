package de.kuschku.libquassel.events;

import android.support.annotation.NonNull;

@SuppressWarnings("WeakerAccess")
public class CoreSetupFailedEvent {
    public final String reason;

    public CoreSetupFailedEvent(String reason) {
        this.reason = reason;
    }

    @NonNull
    @Override
    public String toString() {
        return "CoreSetupFailedEvent{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
