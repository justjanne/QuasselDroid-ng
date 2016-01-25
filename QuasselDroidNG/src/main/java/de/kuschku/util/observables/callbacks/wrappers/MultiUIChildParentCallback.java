package de.kuschku.util.observables.callbacks.wrappers;

import android.support.annotation.UiThread;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.kuschku.util.observables.callbacks.UIChildParentCallback;

@UiThread
public class MultiUIChildParentCallback implements UIChildParentCallback {
    Set<UIChildParentCallback> callbacks = new HashSet<>();

    private MultiUIChildParentCallback(Collection<UIChildParentCallback> callbacks) {
        this.callbacks.addAll(callbacks);
    }

    public static MultiUIChildParentCallback of(UIChildParentCallback... callbacks) {
        return new MultiUIChildParentCallback(Arrays.asList(callbacks));
    }


    public void addCallback(UIChildParentCallback callback) {
        this.callbacks.add(callback);
    }

    public void removeCallback(UIChildParentCallback callback) {
        this.callbacks.remove(callback);
    }

    @Override
    public void notifyChildItemInserted(int group, int position) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyChildItemInserted(group, position);
    }

    @Override
    public void notifyChildItemChanged(int group, int position) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyChildItemChanged(group, position);
    }

    @Override
    public void notifyChildItemRemoved(int group, int position) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyChildItemRemoved(group, position);
    }

    @Override
    public void notifyParentItemInserted(int position) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyParentItemInserted(position);
    }

    @Override
    public void notifyParentItemRemoved(int position) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyParentItemRemoved(position);
    }

    @Override
    public void notifyParentItemChanged(int position) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyParentItemChanged(position);
    }

    @Override
    public void notifyParentItemRangeInserted(int from, int to) {
        for (UIChildParentCallback callback : callbacks)
            callback.notifyParentItemRangeInserted(from, to);
    }
}
