package de.kuschku.libquassel.events;

@SuppressWarnings("WeakerAccess")
public class GeneralErrorEvent {
    public String debugInfo;
    public Exception exception;

    public GeneralErrorEvent(String debugInfo) {
        this.debugInfo = debugInfo;
    }

    public GeneralErrorEvent(Exception exception) {
        this.exception = exception;
    }

    public GeneralErrorEvent(Exception exception, String debugInfo) {
        this.debugInfo = debugInfo;
        this.exception = exception;
    }

    @Override
    public String toString() {
        if (debugInfo == null)
            return String.format("%s: %s", exception.getClass().getSimpleName(), exception.getLocalizedMessage());
        else if (exception == null)
            return debugInfo;
        else
            return String.format("%s: %s\n%s", exception.getClass().getSimpleName(), exception.getLocalizedMessage(), debugInfo);
    }
}
