package de.kuschku.quasseldroid_ng.ui.coresetup;

import android.support.annotation.UiThread;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.objects.types.StorageBackend;
import de.kuschku.quasseldroid_ng.R;

@UiThread
public class StorageBackendRecyclerViewAdapter extends RecyclerView.Adapter<StorageBackendRecyclerViewAdapter.ViewHolder> {

    private List<? extends StorageBackend> backends = new ArrayList<>();
    private StorageBackendFragment.OnListFragmentInteractionListener listener;

    public StorageBackendRecyclerViewAdapter(StorageBackendFragment.OnListFragmentInteractionListener listener) {
        this.listener = listener;
    }

    public void setBackends(List<? extends StorageBackend> backends) {
        this.backends = backends;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_storagebackend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        StorageBackend backend = backends.get(position);

        holder.mItem = backend;
        holder.mIdView.setText(backend.DisplayName);
        holder.mContentView.setText(backend.Description);

        holder.mConfigure.setOnClickListener(v -> {
            if (null != listener) {
                listener.onListFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return backends.size();
    }

    public void setListener(StorageBackendFragment.OnListFragmentInteractionListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final AppCompatButton mConfigure;
        public StorageBackend mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mConfigure = (AppCompatButton) view.findViewById(R.id.configure);
        }
    }
}
