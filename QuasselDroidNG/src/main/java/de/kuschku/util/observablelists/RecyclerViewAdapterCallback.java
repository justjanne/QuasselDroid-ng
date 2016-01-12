package de.kuschku.util.observablelists;

import android.support.v7.widget.RecyclerView;

public class RecyclerViewAdapterCallback implements UICallback {
    private final RecyclerView.Adapter adapter;
    private final AutoScroller scroller;

    public RecyclerViewAdapterCallback(RecyclerView.Adapter adapter, AutoScroller scroller) {
        this.adapter = adapter;
        this.scroller = scroller;
    }

    @Override
    public void notifyItemInserted(int position) {
        adapter.notifyItemInserted(position);
        if (position == 0) scroller.notifyScroll();
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
        if (position == 0) scroller.notifyScroll();
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
