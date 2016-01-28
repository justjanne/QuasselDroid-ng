package de.kuschku.util.observables.lists;

import android.support.annotation.NonNull;

import de.kuschku.util.observables.IObservable;
import de.kuschku.util.observables.callbacks.UICallback;
import de.kuschku.util.observables.callbacks.UIChildCallback;
import de.kuschku.util.observables.callbacks.UIChildParentCallback;
import de.kuschku.util.observables.callbacks.UIParentCallback;
import de.kuschku.util.observables.callbacks.wrappers.MultiUIChildParentCallback;
import de.kuschku.util.observables.callbacks.wrappers.ParentUICallbackWrapper;

public class ChildParentObservableSortedList<T extends IObservable<UIChildCallback>> extends ObservableSortedList<T> {

    private MultiUIChildParentCallback callback = MultiUIChildParentCallback.of();

    public ChildParentObservableSortedList(@NonNull Class<T> cl, @NonNull ItemComparator<T> comparator) {
        super(cl, comparator);
        registerCallbacks();
    }

    public ChildParentObservableSortedList(@NonNull Class<T> cl, @NonNull ItemComparator<T> comparator, boolean reverse) {
        super(cl, comparator, reverse);
        registerCallbacks();
    }

    private void registerCallbacks() {
        super.addCallback(new MyWrapper(callback));
    }

    public void addChildParentCallback(@NonNull UIChildParentCallback callback) {
        this.callback.addCallback(callback);
    }

    public void removeChildParentCallback(@NonNull UIChildParentCallback callback) {
        this.callback.removeCallback(callback);
    }

    private class MyWrapper extends ParentUICallbackWrapper {
        public MyWrapper(@NonNull UIParentCallback wrapped) {
            super(wrapped);
        }

        @Override
        public void notifyItemInserted(int position) {
            super.notifyItemInserted(position);
            get(position).addCallback(callback);
        }

        @Override
        public void notifyItemChanged(int position) {
            super.notifyItemChanged(position);
        }

        @Override
        public void notifyItemRemoved(int position) {
            super.notifyItemRemoved(position);
        }

        @Override
        public void notifyItemMoved(int from, int to) {
            super.notifyItemMoved(from, to);
        }

        @Override
        public void notifyItemRangeInserted(int position, int count) {
            super.notifyItemRangeInserted(position, count);
            for (int i = position; i < position + count; i++) {
                get(position).addCallback(callback);
            }
        }

        @Override
        public void notifyItemRangeChanged(int position, int count) {
            super.notifyItemRangeChanged(position, count);
        }

        @Override
        public void notifyItemRangeRemoved(int position, int count) {
            super.notifyItemRangeRemoved(position, count);
        }
    }
}
