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

package de.kuschku.quasseldroid_ng.ui;

import android.view.View;
import android.widget.TextView;

import com.google.common.base.Function;

import java.util.HashSet;
import java.util.Set;

import de.kuschku.util.observables.callbacks.GeneralCallback;
import de.kuschku.util.observables.lists.ObservableElement;

public class ViewIntBinder {
    private final ObservableElement<Integer> field;
    private final Set<GeneralCallback<Integer>> callbacks = new HashSet<>();

    public ViewIntBinder(ObservableElement<Integer> field) {
        this.field = field;
    }

    public void bindBackgroundColor(View v, Function<Integer, Integer> mapper) {
        v.setBackgroundColor(mapper.apply(field.get()));
        GeneralCallback<Integer> callback = object -> v.setBackgroundColor(mapper.apply(object));
        callbacks.add(callback);
        field.addCallback(callback);
    }

    public void bindTextColor(TextView v, Function<Integer, Integer> mapper) {
        v.setTextColor(mapper.apply(field.get()));
        GeneralCallback<Integer> callback = object -> v.setTextColor(mapper.apply(object));
        callbacks.add(callback);
        field.addCallback(callback);
    }

    public void unbind() {
        field.removeCallbacks();
    }
}
