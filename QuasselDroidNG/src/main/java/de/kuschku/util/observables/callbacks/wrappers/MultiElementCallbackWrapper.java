package de.kuschku.util.observables.callbacks.wrappers;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.kuschku.util.observables.callbacks.ElementCallback;

@UiThread
public class MultiElementCallbackWrapper<T> implements ElementCallback<T> {
    @NonNull
    private final Set<ElementCallback<T>> callbacks = new HashSet<>();

    private MultiElementCallbackWrapper(@NonNull Collection<ElementCallback<T>> callbacks) {
        this.callbacks.addAll(callbacks);
    }

    @SafeVarargs
    @NonNull
    public static <T> MultiElementCallbackWrapper of(@NonNull ElementCallback<T>... callbacks) {
        return new MultiElementCallbackWrapper<>(Arrays.asList(callbacks));
    }

    public void addCallback(@NonNull ElementCallback<T> callback) {
        callbacks.add(callback);
    }

    public void removeCallback(@NonNull ElementCallback<T> callback) {
        callbacks.remove(callback);
    }

    @Override
    public void notifyItemInserted(T element) {
        for (ElementCallback<T> callback : callbacks) {
            callback.notifyItemInserted(element);
        }
    }

    @Override
    public void notifyItemRemoved(T element) {
        for (ElementCallback<T> callback : callbacks) {
            callback.notifyItemInserted(element);
        }
    }

    @Override
    public void notifyItemChanged(T element) {
        for (ElementCallback<T> callback : callbacks) {
            callback.notifyItemInserted(element);
        }
    }
}
