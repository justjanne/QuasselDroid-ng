package de.kuschku.util;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;

public class ObservableList<T extends ContentComparable<T>> {
    public final SortedList<T> list;
    Callback internal = new Callback();
    UICallback callback;
    RecyclerView.Adapter x;

    public ObservableList(Class<T> cl) {
        list = new SortedList<>(cl, internal);
    }

    public ObservableList(Class<T> cl, int initialcapacity) {
        list = new SortedList<>(cl, internal, initialcapacity);
    }

    public void setCallback(UICallback callback) {
        this.callback = callback;
    }

    public T last() {
        if (list.size() == 0) return null;

        return list.get(list.size() - 1);
    }

    public T first() {
        if (list.size() == 0) return null;

        return list.get(0);
    }

    public interface UICallback {
        void notifyItemInserted(int position);

        void notifyItemChanged(int position);

        void notifyItemRemoved(int position);

        void notifyItemMoved(int from, int to);

        void notifyItemRangeInserted(int position, int count);

        void notifyItemRangeChanged(int position, int count);

        void notifyItemRangeRemoved(int position, int count);
    }

    public static class RecyclerViewAdapterCallback implements UICallback {
        private final RecyclerView.Adapter adapter;

        public RecyclerViewAdapterCallback(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void notifyItemInserted(int position) {
            adapter.notifyItemInserted(position);
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

    class Callback extends SortedList.Callback<T> {
        @Override
        public int compare(T o1, T o2) {
            return o1.compareTo(o2);
        }

        @Override
        public void onInserted(int position, int count) {
            if (callback != null)
                if (count == 1)
                    callback.notifyItemInserted(position);
                else
                    callback.notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            if (callback != null)
                if (count == 1)
                    callback.notifyItemRemoved(position);
                else
                    callback.notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            if (callback != null)
                callback.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            if (callback != null)
                if (count == 1)
                    callback.notifyItemChanged(position);
                else
                    callback.notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(T oldItem, T newItem) {
            return oldItem.equalsContent(newItem);
        }

        @Override
        public boolean areItemsTheSame(T item1, T item2) {
            return item1.equals(item2);
        }
    }
}
