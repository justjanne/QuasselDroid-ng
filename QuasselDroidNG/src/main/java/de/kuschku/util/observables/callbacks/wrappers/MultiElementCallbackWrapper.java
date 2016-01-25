package de.kuschku.util.observables.callbacks.wrappers;

import android.support.annotation.UiThread;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.kuschku.util.observables.callbacks.ElementCallback;
import de.kuschku.util.observables.callbacks.UICallback;

@UiThread
public class MultiElementCallbackWrapper<T> implements ElementCallback<T> {
    Set<ElementCallback<T>> callbacks = new HashSet<>();

    private MultiElementCallbackWrapper(Collection<ElementCallback<T>> callbacks) {
        this.callbacks.addAll(callbacks);
    }

    public static <T> MultiElementCallbackWrapper of(ElementCallback<T>... callbacks) {
        return new MultiElementCallbackWrapper<>(Arrays.asList(callbacks));
    }

    public void addCallback(ElementCallback<T> callback) {
        callbacks.add(callback);
    }

    public void removeCallback(ElementCallback<T> callback) {
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
