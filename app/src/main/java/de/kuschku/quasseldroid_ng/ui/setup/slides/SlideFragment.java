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
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import de.kuschku.quasseldroid_ng.R;
import de.kuschku.quasseldroid_ng.ui.setup.ValidUpdateCallback;

public abstract class SlideFragment extends Fragment {
    private final Set<ValidUpdateCallback> callbacks = new HashSet<>();

    public abstract Bundle getData(Bundle in);


    public abstract boolean isValid();

    public void addChangeListener(ValidUpdateCallback callback) {
        callbacks.add(callback);
    }

    public void removeChangeListener(ValidUpdateCallback callback) {
        callbacks.remove(callback);
    }

    protected void updateValidity() {
        updateValidity(isValid());
    }

    protected void updateValidity(boolean validity) {
        for (ValidUpdateCallback callback : callbacks) {
            callback.updateValidity(validity);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide, container, false);
        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.content_host);
        viewGroup.addView(onCreateContent(inflater, viewGroup, savedInstanceState));

        ((TextView) view.findViewById(R.id.title)).setText(getTitle());
        ((TextView) view.findViewById(R.id.description)).setText(getDescription());

        return view;
    }

    protected abstract View onCreateContent(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState);

    @StringRes
    protected abstract int getTitle();

    @StringRes
    protected abstract int getDescription();

    public void setData(Bundle in) {

    }
}
