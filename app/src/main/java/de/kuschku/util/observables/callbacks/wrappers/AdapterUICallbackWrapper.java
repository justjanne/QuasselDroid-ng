package de.kuschku.util.observables.callbacks.wrappers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;

import de.kuschku.util.observables.AutoScroller;
import de.kuschku.util.observables.callbacks.UICallback;

@UiThread
public class AdapterUICallbackWrapper implements UICallback {
    @NonNull
    private final RecyclerView.Adapter adapter;

    @Nullable
    private final AutoScroller scroller;

    public AdapterUICallbackWrapper(@NonNull RecyclerView.Adapter adapter) {
        this(adapter, null);
    }

    public AdapterUICallbackWrapper(@NonNull RecyclerView.Adapter adapter, @Nullable AutoScroller scroller) {
        this.adapter = adapter;
        this.scroller = scroller;
    }

    @Override
    public void notifyItemInserted(int position) {
        adapter.notifyItemInserted(position);
        if (position == 0 && scroller != null) scroller.notifyScroll();
    }

    @Override
    public void notifyItemChanged(int position) {
        adapter.notifyItemChanged(position);
    }

    @Override
    public void notifyItemRemoved(int position) {
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyItemMoved(int from, int to) {
        adapter.notifyItemMoved(from, to);
    }

    @Override
    public void notifyItemRangeInserted(int position, int count) {
        adapter.notifyItemRangeInserted(position, count);
        if (position == 0 && scroller != null) scroller.notifyScroll();
    }

    @Override
    public void notifyItemRangeChanged(int position, int count) {
        adapter.notifyItemRangeChanged(position, count);
    }

    @Override
    public void notifyItemRangeRemoved(int position, int count) {
        adapter.notifyItemRangeRemoved(position, count);
    }
}
