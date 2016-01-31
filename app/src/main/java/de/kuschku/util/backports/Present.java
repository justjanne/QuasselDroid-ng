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
import com.google.common.base.Supplier;

public class Present<T> implements Optional<T> {
    private final T value;

    Present(T value) {
        this.value = value;
    }

    @NonNull
    @Override
    public Optional<T> filter(@NonNull Predicate<? super T> predicate) {
        if (predicate.apply(value)) return this;
        else return Optionals.absent();
    }

    @Nullable
    @Override
    public <U> Optional<U> flatMap(@NonNull Function<? super T, Optional<U>> mapper) {
        return mapper.apply(value);
    }

    @NonNull
    @Override
    public <U> Optional<U> map(@NonNull Function<? super T, U> mapper) {
        return Optionals.ofNullable(mapper.apply(value));
    }

    @Nullable
    @Override
    public T get() {
        return value;
    }

    @Override
    public void ifPresent(@NonNull Consumer<? super T> consumer) {
        consumer.apply(value);
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @NonNull
    @Override
    public T orElse(@NonNull T other) {
        return value;
    }

    @Nullable
    @Override
    public T orElseGet(@NonNull Supplier<? extends T> other) {
        return value;
    }

    @NonNull
    @Override
    public <X extends Throwable> T orElseThrow(@NonNull Supplier<? extends X> exceptionSupplier) throws X {
        return value;
    }
}
