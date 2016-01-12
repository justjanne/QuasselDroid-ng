package de.kuschku.util.backports;

public class Optionals {
    private Optionals() {

    }

    public static <T> Optional<T> of(T elem) {
        return new Present<>(elem);
    }

    public static <T> Optional<T> absent() {
        return new Absent<>();
    }

    public static <T> Optional<T> ofNullable(T elem) {
        if (elem == null) {
            return absent();
        } else {
            return of(elem);
        }
    }
}
