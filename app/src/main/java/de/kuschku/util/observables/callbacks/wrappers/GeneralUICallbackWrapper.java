package de.kuschku.util.observables.callbacks.wrappers;

import de.kuschku.util.observables.callbacks.UICallback;

public abstract class GeneralUICallbackWrapper implements UICallback {
    public abstract void notifyChanged();

    @Override
    public void notifyItemInserted(int position) {
        notifyChanged();
    }

    @Override
    public void notifyItemChanged(int position) {
        notifyChanged();
    }

    @Override
    public void notifyItemRemoved(int position) {
        notifyChanged();
    }

    @Override
    public void notifyItemMoved(int from, int to) {
        notifyChanged();
    }

    @Override
    public void notifyItemRangeInserted(int position, int count) {
        notifyChanged();
    }

    @Override
    public void notifyItemRangeChanged(int position, int count) {
        notifyChanged();
    }

    @Override
    public void notifyItemRangeRemoved(int position, int count) {
        notifyChanged();
    }
}
