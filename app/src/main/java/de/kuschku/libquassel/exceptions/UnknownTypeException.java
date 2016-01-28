package de.kuschku.libquassel.exceptions;

import android.support.annotation.Nullable;

public class UnknownTypeException extends IllegalArgumentException {
    public final String typeName;
    public final String additionalData;

    public UnknownTypeException(String typeName) {
        this(typeName, null);
    }

    public UnknownTypeException(String typeName, Object additionalData) {
        this.typeName = typeName;
        this.additionalData = String.valueOf(additionalData);
    }

    @Nullable
    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    @Override
    public String getMessage() {
        return String.format("Unknown type: %s; %s", typeName, additionalData);
    }
}
