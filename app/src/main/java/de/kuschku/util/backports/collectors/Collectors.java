package de.kuschku.util.backports.collectors;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import de.kuschku.util.backports.Stream;

public class Collectors {
    private Collectors() {

    }

    @NonNull
    public static <T> List<T> toList(@NonNull Stream<T> stream) {
        return new ListCollector<T>().collect(stream);
    }

    @NonNull
    public static <T> Map<T, T> toMap(@NonNull Stream<T> stream) {
        return new MapCollector<T>().collect(stream);
    }
}
