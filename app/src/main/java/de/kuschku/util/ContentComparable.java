package de.kuschku.util;

public interface ContentComparable<T extends ContentComparable<T>> extends Comparable<T> {
    boolean equalsContent(T other);
}
