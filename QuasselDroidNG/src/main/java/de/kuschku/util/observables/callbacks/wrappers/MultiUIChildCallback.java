package de.kuschku.util.observables.callbacks.wrappers;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.kuschku.util.observables.callbacks.UIChildCallback;

@UiThread
public class MultiUIChildCallback implements UIChildCallback {
    @NonNull
    private final Set<UIChildCallback> callbacks = new HashSet<>();

    private MultiUIChildCallback(@NonNull Collection<UIChildCallback> callbacks) {
        this.callbacks.addAll(callbacks);
    }

    @NonNull
    public static MultiUIChildCallback of(@NonNull UIChildCallback... callbacks) {
        return new MultiUIChildCallback(Arrays.asList(callbacks));
    }


    public void addCallback(@NonNull UIChildCallback callback) {
        this.callbacks.add(callback);
    }

    public void removeCallback(@NonNull UIChildCallback callback) {
        this.callbacks.remove(callback);
    }

    @Override
    public void notifyChildItemInserted(int group, int position) {
        for (UIChildCallback callback : callbacks)
            callback.notifyChildItemInserted(group, position);
    }

    @Override
    public void notifyChildItemChanged(int group, int position) {
        for (UIChildCallback callback : callbacks)
            callback.notifyChildItemChanged(group, position);
    }

    @Override
    public void notifyChildItemRemoved(int group, int position) {
        for (UIChildCallback callback : callbacks)
            callback.notifyChildItemRemoved(group, position);
    }
}
