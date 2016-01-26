package de.kuschku.libquassel.functions.serializers;

import android.support.annotation.NonNull;

import java.util.List;

public interface FunctionSerializer<T> {
    @NonNull
    List serialize(@NonNull T data);

    @NonNull
    T deserialize(@NonNull List packedFunc);
}
