package de.kuschku.util.observables.callbacks;

import android.support.annotation.UiThread;

public interface ElementCallback<T> {
    @UiThread
    void notifyItemInserted(T element);

    @UiThread
    void notifyItemRemoved(T element);

    @UiThread
    void notifyItemChanged(T element);
}
