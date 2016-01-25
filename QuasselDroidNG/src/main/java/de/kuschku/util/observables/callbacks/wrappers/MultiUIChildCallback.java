package de.kuschku.util.observables.callbacks.wrappers;

import android.support.annotation.UiThread;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.kuschku.util.observables.callbacks.UIChildCallback;

@UiThread
public class MultiUIChildCallback implements UIChildCallback {
    Set<UIChildCallback> callbacks = new HashSet<>();

    private MultiUIChildCallback(Collection<UIChildCallback> callbacks) {
        this.callbacks.addAll(callbacks);
    }

    public static MultiUIChildCallback of(UIChildCallback... callbacks) {
        return new MultiUIChildCallback(Arrays.asList(callbacks));
    }


    public void addCallback(UIChildCallback callback) {
        this.callbacks.add(callback);
    }

    public void removeCallback(UIChildCallback callback) {
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
