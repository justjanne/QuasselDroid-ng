package de.kuschku.libquassel.functions.types;

import android.support.annotation.NonNull;

public interface SerializedFunction<T> {
    @NonNull
    T getData();
}
