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
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.quasseldroid_ng.R;

public class AccountSetupNameSlide extends SlideFragment {
    final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateValidity();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    @Bind(R.id.name)
    TextInputEditText nameField;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View onCreateContent(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_account_name, container, false);
        ButterKnife.bind(this, view);
        nameField.addTextChangedListener(watcher);
        return view;
    }

    @Override
    public Bundle getData(Bundle in) {
        in.putString("name", nameField.getText().toString());
        return in;
    }

    @Override
    public boolean isValid() {
        return validName();
    }

    @Override
    @StringRes
    public int getTitle() {
        return R.string.slideAccountNameTitle;
    }

    @Override
    @StringRes
    public int getDescription() {
        return R.string.slideAccountNameDescription;
    }

    private boolean validName() {
        if (nameField == null)
            return false;

        String hostText = nameField.getText().toString();
        return !hostText.isEmpty();
    }
}
