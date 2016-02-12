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
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.quasseldroid_ng.R;

public class AccountSetupUserSlide extends SlideFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Bind(R.id.user)
    AppCompatEditText userField;

    @Bind(R.id.pass)
    AppCompatEditText passField;

    @Override
    public View onCreateContent(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_account_user, container, false);
        ButterKnife.bind(this, view);
        userField.addTextChangedListener(watcher);
        passField.addTextChangedListener(watcher);
        return view;
    }

    @Override
    public Bundle getData(Bundle in) {
        in.putString("user", userField.getText().toString());
        in.putString("pass", passField.getText().toString());
        return in;
    }

    @Override
    public boolean isValid() {
        return validUser() && validPass();
    }

    @Override
    @StringRes
    public int getTitle() {
        return R.string.slideAccountuserTitle;
    }

    @Override
    @StringRes
    public int getDescription() {
        return R.string.slideAccountuserDescription;
    }

    private boolean validUser() {
        if (userField == null)
            return false;

        String hostText = userField.getText().toString();
        return !hostText.isEmpty();
    }

    private boolean validPass() {
        if (passField == null)
            return false;

        String hostText = passField.getText().toString();
        return !hostText.isEmpty();
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            updateValidity();
        }
    };
}
