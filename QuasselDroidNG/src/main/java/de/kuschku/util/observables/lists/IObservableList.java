package de.kuschku.util.observables.lists;

import java.util.List;

import de.kuschku.util.observables.IObservable;

public interface IObservableList<O, T> extends IObservable<O>, List<T> {
}
