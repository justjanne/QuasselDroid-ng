package de.kuschku.util.observablelists;

public interface UICallback {
    void notifyItemInserted(int position);

    void notifyItemChanged(int position);

    void notifyItemRemoved(int position);

    void notifyItemMoved(int from, int to);

    void notifyItemRangeInserted(int position, int count);

    void notifyItemRangeChanged(int position, int count);

    void notifyItemRangeRemoved(int position, int count);
}
