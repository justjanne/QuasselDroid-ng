package de.kuschku.util.observables;

import de.kuschku.util.observables.callbacks.UICallback;

public interface IObservable<T> {
    void addCallback(T callback);
    void removeCallback(T callback);
}
