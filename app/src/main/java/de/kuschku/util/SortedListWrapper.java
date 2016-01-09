package de.kuschku.util;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SortedListWrapper<T> implements List<T> {
    final SortedList<T> list;

    public SortedListWrapper(SortedList<T> list) {
        this.list = list;
    }


    @Override
    public void add(int location, T object) {
        list.add(object);
    }

    @Override
    public boolean add(T object) {
        list.add(object);
        return false;
    }

    @Override
    public boolean addAll(int location, @NonNull Collection<? extends T> collection) {
        list.addAll((Collection<T>) collection);
        return false;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends T> collection) {
        list.addAll((Collection<T>) collection);
        return false;
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object object) {
        return indexOf(object) != SortedList.INVALID_POSITION;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return new Stream<>(collection).allMatch(this::contains);
    }

    @Override
    public T get(int location) {
        return get(location);
    }

    @Override
    public int indexOf(Object object) {
        return list.indexOf((T) object);
    }

    @Override
    public boolean isEmpty() {
        return list.size() == 0;
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public int lastIndexOf(Object object) {
        return indexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() {
        return null;
    }

    @Override
    public ListIterator<T> listIterator(int location) {
        return null;
    }

    @Override
    public T remove(int location) {
        T val = get(location);
        remove(val);
        return val;
    }

    @Override
    public boolean remove(Object object) {
        return list.remove((T) object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return new Stream<>(collection).anyMatch(this::remove);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    @Override
    public T set(int location, T object) {
        return null;
    }

    @Override
    public int size() {
        return list.size();
    }

    @NonNull
    @Override
    public List<T> subList(int start, int end) {
        return null;
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(T1[] array) {
        return null;
    }
}
