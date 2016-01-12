package de.kuschku.libquassel.functions.serializers;

import java.util.List;

public interface FunctionSerializer<T> {
    List serialize(T data);

    T deserialize(List packedFunc);
}
