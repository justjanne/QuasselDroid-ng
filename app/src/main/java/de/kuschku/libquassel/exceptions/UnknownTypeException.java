package de.kuschku.libquassel.exceptions;

import android.support.annotation.Nullable;

public class UnknownTypeException extends IllegalArgumentException {
    public final String typeName;

    public UnknownTypeException(String typeName) {
        this.typeName = typeName;
    }

    @Nullable
    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    @Override
    public String getMessage() {
        return String.format("Unknown type: %s", typeName);
    }
}
