package de.kuschku.util.observablelists;

public interface ContentComparable<T extends ContentComparable<T>> extends Comparable<T> {
    boolean equalsContent(T other);
}
