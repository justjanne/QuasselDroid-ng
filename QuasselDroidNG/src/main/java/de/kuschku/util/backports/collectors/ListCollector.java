package de.kuschku.util.backports.collectors;

import android.support.annotation.NonNull;

import java.util.List;

import de.kuschku.util.backports.ICollector;
import de.kuschku.util.backports.Stream;

public class ListCollector<T> implements ICollector<T, List<T>> {
    @NonNull
    @Override
    public List<T> collect(@NonNull Stream<T> stream) {
        return stream.list();
    }
}
