package de.kuschku.util.observables.callbacks.wrappers;

import java.util.HashSet;
import java.util.Set;

import de.kuschku.util.observables.IObservable;
import de.kuschku.util.observables.callbacks.GeneralCallback;

public class GeneralCallbackWrapper implements IObservable<GeneralCallback>, GeneralCallback {
    Set<GeneralCallback> callbacks = new HashSet<>();

    @Override
    public void notifyChanged() {
        for (GeneralCallback callback : callbacks) {
            callback.notifyChanged();
        }
    }

    @Override
    public void addCallback(GeneralCallback callback) {
        callbacks.add(callback);
    }

    @Override
    public void removeCallback(GeneralCallback callback) {
        callbacks.remove(callback);
    }
}
