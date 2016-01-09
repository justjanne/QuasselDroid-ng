package de.kuschku.util.collectors;

import java.util.List;
import java.util.Map;

import de.kuschku.util.Stream;

public class Collectors {
    private Collectors() {

    }

    public static <T> List<T> toList(Stream<T> stream) {
        return new ListCollector<T>().collect(stream);
    }

    public static <T> Map<T, T> toMap(Stream<T> stream) {
        return new MapCollector<T>().collect(stream);
    }
}
