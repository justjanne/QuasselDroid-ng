package de.kuschku.libquassel.exceptions;

import java.lang.reflect.InvocationTargetException;

public class SyncInvocationException extends InvocationTargetException {
    public SyncInvocationException() {
    }

    public SyncInvocationException(String detailMessage) {
        super(null, detailMessage);
    }

    public SyncInvocationException(Throwable exception) {
        super(exception);
    }

    public SyncInvocationException(Throwable exception, String detailMessage) {
        super(exception, detailMessage);
    }
}
