package de.kuschku.util.observables;

public interface IObservable<T> {
    void addCallback(T callback);
    void removeCallback(T callback);
}
