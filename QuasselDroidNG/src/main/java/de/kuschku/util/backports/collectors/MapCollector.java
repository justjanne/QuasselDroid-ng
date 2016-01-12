package de.kuschku.util.backports.collectors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.util.backports.ICollector;
import de.kuschku.util.backports.Stream;

public class MapCollector<T> implements ICollector<T, Map<T, T>> {
    @Override
    public Map<T, T> collect(Stream<T> stream) {
        Map<T, T> map = new HashMap<>(stream.count() / 2);

        if (stream.count() % 2 == 1)
            throw new IllegalArgumentException("This only works with equally many keys and values");

        List<T> list = Collectors.toList(stream);

        for (int i = 0; i < stream.count(); i += 2) {
            map.put(list.get(i), list.get(i + 1));
        }

        return map;
    }
}
