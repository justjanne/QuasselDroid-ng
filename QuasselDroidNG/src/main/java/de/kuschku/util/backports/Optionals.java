package de.kuschku.util.backports;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Optionals {
    private Optionals() {

    }

    @NonNull
    public static <T> Optional<T> of(@NonNull T elem) {
        return new Present<>(elem);
    }

    @NonNull
    public static <T> Optional<T> absent() {
        return new Absent<>();
    }

    @NonNull
    public static <T> Optional<T> ofNullable(@Nullable T elem) {
        if (elem == null) {
            return absent();
        } else {
            return of(elem);
        }
    }
}
