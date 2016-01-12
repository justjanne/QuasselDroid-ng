package de.kuschku.util.backports;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

public class Absent<T> implements Optional<T> {
    @Override
    public Optional<T> filter(Predicate<? super T> predicate) {
        return this;
    }

    @Override
    public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
        return Optionals.absent();
    }

    @Override
    public <U> Optional<U> map(Function<? super T, U> mapper) {
        return Optionals.absent();
    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public void ifPresent(Consumer<? super T> consumer) {
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public T orElse(T other) {
        return other;
    }

    @Override
    public T orElseGet(Supplier<? extends T> other) {
        return other.get();
    }

    @Override
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        throw exceptionSupplier.get();
    }
}
