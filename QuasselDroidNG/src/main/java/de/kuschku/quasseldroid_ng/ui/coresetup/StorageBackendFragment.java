package de.kuschku.quasseldroid_ng.ui.coresetup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.objects.types.StorageBackend;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.ui.parcelableUtil.StorageBackendParcelable;

@UiThread
public class StorageBackendFragment extends Fragment {
    public static final String BACKENDS = "backends";

    private OnListFragmentInteractionListener mListener;
    private StorageBackendRecyclerViewAdapter adapter = new StorageBackendRecyclerViewAdapter(mListener);

    public static StorageBackendFragment newInstance(List<StorageBackend> backends) {
        return newInstance(StorageBackendParcelable.wrap(backends));
    }

    public static StorageBackendFragment newInstance(ArrayList<StorageBackendParcelable> backends) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BACKENDS, backends);
        StorageBackendFragment fragment = new StorageBackendFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storagebackend_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }
        if (getArguments() != null) {
            List<StorageBackendParcelable> parcelables = getArguments().getParcelableArrayList(BACKENDS);
            adapter.setBackends(parcelables);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
            adapter.setListener(mListener);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(StorageBackend item);
    }
}
