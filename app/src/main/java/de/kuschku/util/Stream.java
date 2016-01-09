package de.kuschku.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.kuschku.util.collectors.Collectors;

public class Stream<T> {
    List<T> list;

    public Stream(List<T> list) {
        this.list = list;
    }

    public Stream(Collection<T> observers) {
        list = new ArrayList<>(observers);
    }

    public boolean allMatch(Predicate<T> predicate) {
        return filter(predicate).count() == count();
    }


    public boolean anyMatch(Predicate<T> predicate) {
        return filter(predicate).count() > 0;
    }


    public int count() {
        return list.size();
    }


    public Stream<T> filter(Predicate<? super T> predicate) {
        return new Stream<>(Lists.newArrayList(Collections2.filter(list, predicate)));
    }


    public Optional<T> findFirst() {
        if (list.size() > 0) {
            return Optionals.of(list.get(0));
        } else {
            return Optionals.absent();
        }
    }


    public Optional<T> findAny() {
        return findFirst();
    }


    public <S> Stream<S> map(Function<T, S> function) {
        return new Stream<>(Lists.transform(list, function));
    }


    public void forEach(Consumer<T> function) {
        for (T elem : Collectors.toList(this)) {
            function.apply(elem);
        }
    }


    public Stream<T> limit(int maxSize) {
        return new Stream<>(list.subList(0, Math.min(maxSize, list.size())));
    }


    public boolean noneMatch(Predicate<T> predicate) {
        return !anyMatch(predicate);
    }


    public Stream<T> skip(int n) {
        if (count() <= n) {
            return new Stream<>(Lists.newArrayList());
        } else {
            return new Stream<>(list.subList(n, list.size()));
        }
    }


    public <R> R collect(ICollector<T, R> collector) {
        return null;
    }


    public Iterator<T> iterator() {
        return null;
    }

    public List<T> list() {
        return list;
    }
}
