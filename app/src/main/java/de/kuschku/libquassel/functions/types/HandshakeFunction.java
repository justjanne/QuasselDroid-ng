package de.kuschku.libquassel.functions.types;

public class HandshakeFunction {
    public final Object data;

    public HandshakeFunction(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HandshakeFunction{" +
                "data=" + data +
                '}';
    }
}
