/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

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
