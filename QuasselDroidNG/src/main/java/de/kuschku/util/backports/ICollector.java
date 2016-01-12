package de.kuschku.util.backports;

public interface ICollector<T, R> {
    R collect(Stream<T> stream);
}
