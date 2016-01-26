package de.kuschku.util.observables.callbacks;

import android.support.annotation.UiThread;

public interface UIParentCallback {
    @UiThread
    void notifyParentItemInserted(int position);

    @UiThread
    void notifyParentItemRemoved(int position);

    @UiThread
    void notifyParentItemChanged(int position);

    @UiThread
    void notifyParentItemRangeInserted(int from, int to);
}
