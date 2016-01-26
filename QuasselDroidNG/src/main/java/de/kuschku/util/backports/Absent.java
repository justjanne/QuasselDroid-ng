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
