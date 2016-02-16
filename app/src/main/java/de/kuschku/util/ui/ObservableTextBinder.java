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

package de.kuschku.util.ui;

import android.databinding.Observable;
import android.databinding.ObservableField;
import android.widget.TextView;

public class ObservableTextBinder {
    private ObservableField<CharSequence> text;
    private TextView view;

    public ObservableTextBinder(ObservableField<CharSequence> text) {
        this.text = text;
        this.text.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                updateView();
            }
        });
    }

    private void updateView() {
        if (view != null) {
            view.setText(text.get());
        }
    }

    public void bind(TextView view) {
        this.view = view;
        updateView();
    }
}
