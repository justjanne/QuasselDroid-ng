package de.kuschku.util.observables.callbacks;

import android.support.annotation.UiThread;

public interface UICallback {
    @UiThread
    void notifyItemInserted(int position);

    @UiThread
    void notifyItemChanged(int position);

    @UiThread
    void notifyItemRemoved(int position);

    @UiThread
    void notifyItemMoved(int from, int to);

    @UiThread
    void notifyItemRangeInserted(int position, int count);

    @UiThread
    void notifyItemRangeChanged(int position, int count);

    @UiThread
    void notifyItemRangeRemoved(int position, int count);
}
