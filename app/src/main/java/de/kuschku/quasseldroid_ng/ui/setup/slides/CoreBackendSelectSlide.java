/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid_ng.ui.setup.slides;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.List;

import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.setup.StorageBackendItem;

public class CoreBackendSelectSlide extends SlideFragment {
    private FastAdapter<IItem> fastAdapter;

    private List<Bundle> storageBackends;
    private String selectedBackend;

    @Override
    public void setArguments(Bundle args) {
        storageBackends = args.getParcelableArrayList("storageBackends");
    }

    @Override
    public Bundle getData(Bundle in) {
        in.putString("selectedBackend", selectedBackend);
        return in;
    }

    @Override
    public boolean isValid() {
        return fastAdapter != null && fastAdapter.getSelections().size() == 1;
    }

    @Override
    protected View onCreateContent(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.slide_select, container, false);

        fastAdapter = new FastAdapter<>();
        ItemAdapter<IItem> itemAdapter = new ItemAdapter<>();
        itemAdapter.wrap(fastAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        for (Bundle backend : storageBackends) {
            itemAdapter.add(new StorageBackendItem(backend.getString("displayName"), backend.getString("description")));
        }

        recyclerView.setAdapter(fastAdapter);

        fastAdapter.withSelectWithItemUpdate(true);
        fastAdapter.withOnClickListener((v, adapter, item, position) -> {
            fastAdapter.deselect();
            fastAdapter.select(position);
            selectedBackend = ((StorageBackendItem) item).getDisplayName();
            updateValidity();
            return false;
        });


        return recyclerView;
    }

    @Override
    protected int getTitle() {
        return R.string.slideCoreBackendSelectTitle;
    }

    @Override
    protected int getDescription() {
        return R.string.slideCoreBackendSelectDescription;
    }

}
