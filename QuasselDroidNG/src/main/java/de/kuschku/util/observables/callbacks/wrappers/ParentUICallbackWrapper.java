package de.kuschku.util.observables.callbacks.wrappers;

import android.support.annotation.UiThread;

import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.callbacks.UIParentCallback;

@UiThread
public class ParentUICallbackWrapper implements UICallback {
    private final UIParentCallback wrapped;

    public ParentUICallbackWrapper(UIParentCallback wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void notifyItemInserted(int position) {
        wrapped.notifyParentItemInserted(position);
    }

    @Override
    public void notifyItemChanged(int position) {
        wrapped.notifyParentItemChanged(position);
    }

    @Override
    public void notifyItemRemoved(int position) {
        wrapped.notifyParentItemRemoved(position);
    }

    @Override
    public void notifyItemMoved(int from, int to) {
        notifyItemRemoved(from);
        notifyItemInserted(to);
    }

    @Override
    public void notifyItemRangeInserted(int position, int count) {
        for (int i = position; i < position + count; i++) {
            notifyItemInserted(i);
        }
    }

    @Override
    public void notifyItemRangeChanged(int position, int count) {
        for (int i = position; i < position + count; i++) {
            notifyItemChanged(i);
        }
    }

    @Override
    public void notifyItemRangeRemoved(int position, int count) {
        for (int i = position; i < position + count; i++) {
            notifyItemRemoved(i);
        }
    }
}
