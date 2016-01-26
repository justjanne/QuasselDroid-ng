package de.kuschku.util.observables.callbacks;

import android.support.annotation.UiThread;

public interface UIChildCallback {
    @UiThread
    void notifyChildItemInserted(int group, int position);

    @UiThread
    void notifyChildItemChanged(int group, int position);

    @UiThread
    void notifyChildItemRemoved(int group, int position);
}
