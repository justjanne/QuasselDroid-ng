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
