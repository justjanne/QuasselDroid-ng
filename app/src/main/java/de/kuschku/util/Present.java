package de.kuschku.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

public class Present<T> implements Optional<T> {
    T value;

    public Present(T value) {
        this.value = value;
    }

    @Override
    public Optional<T> filter(Predicate<? super T> predicate) {
        if (predicate.apply(value)) return this;
        else return Optionals.absent();
    }

    @Override
    public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
        return mapper.apply(value);
    }

    @Override
    public <U> Optional<U> map(Function<? super T, U> mapper) {
        return Optionals.of(mapper.apply(value));
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void ifPresent(Consumer<? super T> consumer) {
        consumer.apply(value);
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public T orElse(T other) {
        return value;
    }

    @Override
    public T orElseGet(Supplier<? extends T> other) {
        return value;
    }

    @Override
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return value;
    }
}
