package de.kuschku.util.observablelists;

import android.support.v7.util.SortedList;

public class ObservableSortedList<T extends ContentComparable<T>> {
    public final SortedList<T> list;
    Callback internal = new Callback();
    UICallback callback;
    boolean reverse;

    public ObservableSortedList(Class<T> cl) {
        list = new SortedList<>(cl, internal);
    }

    public ObservableSortedList(Class<T> cl, boolean reverse) {
        this(cl);
        this.reverse = reverse;
    }

    public void setCallback(UICallback callback) {
        this.callback = callback;
    }

    public T last() {
        if (list.size() == 0) return null;

        return list.get(list.size() - 1);
    }

    class Callback extends SortedList.Callback<T> {
        @Override
        public int compare(T o1, T o2) {
            return (reverse) ? o2.compareTo(o1): o1.compareTo(o2);
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
