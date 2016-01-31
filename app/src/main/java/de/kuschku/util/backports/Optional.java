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

public interface Optional<T> {
    @NonNull
    Optional<T> filter(@NonNull Predicate<? super T> predicate);

    @Nullable
    <U> Optional<U> flatMap(@NonNull Function<? super T, Optional<U>> mapper);

    @NonNull
    <U> Optional<U> map(@NonNull Function<? super T, U> mapper);

    @Nullable
    T get();

    void ifPresent(@NonNull Consumer<? super T> consumer);

    boolean isPresent();

    @NonNull
    T orElse(@NonNull T other);

    @Nullable
    T orElseGet(@NonNull Supplier<? extends T> other);

    @NonNull
    <X extends Throwable> T orElseThrow(@NonNull Supplier<? extends X> exceptionSupplier) throws X;
}
