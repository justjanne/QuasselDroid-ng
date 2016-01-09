package de.kuschku.libquassel.events;

public class CoreSetupFailedEvent {
    public final String reason;

    public CoreSetupFailedEvent(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "CoreSetupFailedEvent{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
