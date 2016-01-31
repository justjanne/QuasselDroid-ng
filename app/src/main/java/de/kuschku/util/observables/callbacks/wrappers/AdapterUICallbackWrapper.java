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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

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
