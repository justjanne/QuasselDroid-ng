package de.kuschku.util;

public interface ICollector<T, R> {
    R collect(Stream<T> stream);
}
