package de.kuschku.util.backports;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.kuschku.util.backports.collectors.Collectors;

public class Stream<T> {
    @NonNull
    private final List<T> list;

    public Stream(@NonNull List<T> list) {
        this.list = list;
    }

    public Stream(@NonNull Collection<T> observers) {
        list = new ArrayList<>(observers);
    }

    public boolean allMatch(@NonNull Predicate<T> predicate) {
        return filter(predicate).count() == count();
    }


    public boolean anyMatch(@NonNull Predicate<T> predicate) {
        return filter(predicate).count() > 0;
    }


    public int count() {
        return list.size();
    }


    @NonNull
    public Stream<T> filter(Predicate<? super T> predicate) {
        return new Stream<>(Lists.newArrayList(Collections2.filter(list, predicate)));
    }


    @NonNull
    public Optional<T> findFirst() {
        if (list.size() > 0) {
            return Optionals.of(list.get(0));
        } else {
            return Optionals.absent();
        }
    }


    @NonNull
    public Optional<T> findAny() {
        return findFirst();
    }


    @NonNull
    public <S> Stream<S> map(@NonNull Function<T, S> function) {
        return new Stream<>(Lists.transform(list, function));
    }


    public void forEach(@NonNull Consumer<T> function) {
        for (T elem : Collectors.toList(this)) {
            function.apply(elem);
        }
    }


    @NonNull
    public Stream<T> limit(int maxSize) {
        return new Stream<>(list.subList(0, Math.min(maxSize, list.size())));
    }


    public boolean noneMatch(@NonNull Predicate<T> predicate) {
        return !anyMatch(predicate);
    }


    @NonNull
    public Stream<T> skip(int n) {
        if (count() <= n) {
            return new Stream<>(Lists.newArrayList());
        } else {
            return new Stream<>(list.subList(n, list.size()));
        }
    }


    @Nullable
    public <R> R collect(@NonNull ICollector<T, R> collector) {
        return collector.collect(this);
    }


    @NonNull
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @NonNull
    public List<T> list() {
        return list;
    }
}
