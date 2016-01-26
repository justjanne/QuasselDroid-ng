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
