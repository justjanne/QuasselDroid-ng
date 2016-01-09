package de.kuschku.util.collectors;

import java.util.List;

import de.kuschku.util.ICollector;
import de.kuschku.util.Stream;

public class ListCollector<T> implements ICollector<T, List<T>> {
    @Override
    public List<T> collect(Stream<T> stream) {
        return stream.list();
    }
}
