package de.kuschku.util.backports;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

public interface Optional<T> {
    Optional<T> filter(Predicate<? super T> predicate);

    <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper);

    <U> Optional<U> map(Function<? super T, U> mapper);

    T get();

    void ifPresent(Consumer<? super T> consumer);

    boolean isPresent();

    T orElse(T other);

    T orElseGet(Supplier<? extends T> other);

    <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X;
}
