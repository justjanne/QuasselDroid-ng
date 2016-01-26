package de.kuschku.libquassel.functions.types;

import android.support.annotation.NonNull;

public class HandshakeFunction {
    public final Object data;

    public HandshakeFunction(Object data) {
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        return "HandshakeFunction{" +
                "data=" + data +
                '}';
    }
}
