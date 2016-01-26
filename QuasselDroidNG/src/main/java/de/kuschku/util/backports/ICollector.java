package de.kuschku.util.backports;

import android.support.annotation.NonNull;

public interface ICollector<T, R> {
    @NonNull
    R collect(Stream<T> stream);
}
