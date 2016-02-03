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
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.util.backports;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

public class Absent<T> implements Optional<T> {
    Absent() {

    }

    @NonNull
    @Override
    public Optional<T> filter(@NonNull Predicate<? super T> predicate) {
        return this;
    }

    @Nullable
    @Override
    public <U> Optional<U> flatMap(@NonNull Function<? super T, Optional<U>> mapper) {
        return Optionals.absent();
    }

    @NonNull
    @Override
    public <U> Optional<U> map(@NonNull Function<? super T, U> mapper) {
        return Optionals.absent();
    }

    @Nullable
    @Override
    public T get() {
        return null;
    }

    @Override
    public void ifPresent(@NonNull Consumer<? super T> consumer) {
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @NonNull
    @Override
    public T orElse(@NonNull T other) {
        return other;
    }

    @Nullable
    @Override
    public T orElseGet(@NonNull Supplier<? extends T> other) {
        return other.get();
    }

    @NonNull
    @Override
    public <X extends Throwable> T orElseThrow(@NonNull Supplier<? extends X> exceptionSupplier) throws X {
        throw exceptionSupplier.get();
    }
}
