package de.kuschku.util.observables.callbacks.wrappers;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;

import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.callbacks.UIChildCallback;

@UiThread
public class ChildUICallbackWrapper implements UICallback {
    @NonNull
    private final UIChildCallback wrapped;
    private int groupPosition;

    public ChildUICallbackWrapper(@NonNull UIChildCallback wrapped) {
        this.wrapped = wrapped;
    }

    public int getGroupPosition() {
        return groupPosition;
    }

    public void setGroupPosition(int groupPosition) {
        this.groupPosition = groupPosition;
    }

    @Override
    public void notifyItemInserted(int position) {
        wrapped.notifyChildItemInserted(groupPosition, position);
    }

    @Override
    public void notifyItemChanged(int position) {
        wrapped.notifyChildItemChanged(groupPosition, position);
    }

    @Override
    public void notifyItemRemoved(int position) {
        wrapped.notifyChildItemRemoved(groupPosition, position);
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
