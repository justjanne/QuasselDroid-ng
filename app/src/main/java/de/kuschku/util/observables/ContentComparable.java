package de.kuschku.util.observables;

public interface ContentComparable<T extends ContentComparable<T>> extends Comparable<T> {
    boolean areItemsTheSame(T other);
    boolean areContentsTheSame(T other);
}
