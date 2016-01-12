package de.kuschku.util.backports.collectors;

import java.util.List;
import java.util.Map;

import de.kuschku.util.backports.Stream;

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
