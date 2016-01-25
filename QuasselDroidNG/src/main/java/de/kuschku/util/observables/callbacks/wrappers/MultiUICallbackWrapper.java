package de.kuschku.util.observables.callbacks.wrappers;

import android.support.annotation.UiThread;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.kuschku.util.observables.callbacks.UICallback;

@UiThread
public class MultiUICallbackWrapper implements UICallback {
    Set<UICallback> callbacks = new HashSet<>();

    private MultiUICallbackWrapper(Collection<UICallback> callbacks) {
        this.callbacks.addAll(callbacks);
    }

    public static MultiUICallbackWrapper of(UICallback... callbacks) {
        return new MultiUICallbackWrapper(Arrays.asList(callbacks));
    }

    public void addCallback(UICallback callback) {
        callbacks.add(callback);
    }

    public void removeCallback(UICallback callback) {
        callbacks.remove(callback);
    }

    @Override
    public void notifyItemInserted(int position) {
        for (UICallback callback : callbacks) {
            callback.notifyItemInserted(position);
        }
    }

    @Override
    public void notifyItemChanged(int position) {
        for (UICallback callback : callbacks) {
            callback.notifyItemChanged(position);
        }
    }

    @Override
    public void notifyItemRemoved(int position) {
        for (UICallback callback : callbacks) {
            callback.notifyItemRemoved(position);
        }
    }

    @Override
    public void notifyItemMoved(int from, int to) {
        for (UICallback callback : callbacks) {
            callback.notifyItemMoved(from, to);
        }
    }

    @Override
    public void notifyItemRangeInserted(int position, int count) {
        for (UICallback callback : callbacks) {
            callback.notifyItemRangeInserted(position, count);
        }
    }

    @Override
    public void notifyItemRangeChanged(int position, int count) {
        for (UICallback callback : callbacks) {
            callback.notifyItemRangeChanged(position, count);
        }
    }

    @Override
    public void notifyItemRangeRemoved(int position, int count) {
        for (UICallback callback : callbacks) {
            callback.notifyItemRangeRemoved(position, count);
        }
    }
}
