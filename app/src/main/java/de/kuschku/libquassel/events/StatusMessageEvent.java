package de.kuschku.libquassel.events;

public class StatusMessageEvent {
    public final String scope;
    public final String message;

    public StatusMessageEvent(String scope, String message) {
        this.scope = scope;
        this.message = message;
    }
}
