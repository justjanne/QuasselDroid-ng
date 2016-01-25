package de.kuschku.util.observables;

public interface ContentComparable<T extends ContentComparable<T>> extends Comparable<T> {
    boolean equalsContent(T other);
}
