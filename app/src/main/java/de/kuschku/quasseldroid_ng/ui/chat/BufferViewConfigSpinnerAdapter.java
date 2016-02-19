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

package de.kuschku.quasseldroid_ng.ui.chat;

import android.content.res.Resources;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QBufferViewManager;
import de.kuschku.quasseldroid_ng.ui.theme.AppContext;

public class BufferViewConfigSpinnerAdapter implements ThemedSpinnerAdapter {
    private final AppContext context;
    private final QBufferViewManager bufferViewManager;
    @Nullable
    private Resources.Theme theme;

    private Set<DataSetObserver> observers = new HashSet<>();

    public BufferViewConfigSpinnerAdapter(AppContext context, QBufferViewManager bufferViewManager) {
        this.context = context;
        this.bufferViewManager = bufferViewManager;
    }

    @Nullable
    @Override
    public Resources.Theme getDropDownViewTheme() {
        return theme;
    }

    @Override
    public void setDropDownViewTheme(@Nullable Resources.Theme theme) {
        this.theme = theme;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(new ContextThemeWrapper(parent.getContext(), theme));
        TextView view = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        view.setText(((QBufferViewConfig) getItem(position)).bufferViewName());
        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        observers.remove(observer);
    }

    @Override
    public int getCount() {
        return bufferViewManager.bufferViewConfigs().size();
    }

    @Override
    public Object getItem(int position) {
        return bufferViewManager.bufferViewConfigs().get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((QBufferViewConfig) getItem(position)).bufferViewId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(new ContextThemeWrapper(parent.getContext(), theme));
        TextView view = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        view.setText(((QBufferViewConfig) getItem(position)).bufferViewName());
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }
}
