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

public class AccountSetupCoreSlide extends SlideFragment {
    @Bind(R.id.host)
    TextInputEditText hostField;
    @Bind(R.id.port)
    TextInputEditText portField;
    TextWatcher watcher = new TextWatcher() {
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View onCreateContent(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_account_core, container, false);
        ButterKnife.bind(this, view);
        hostField.addTextChangedListener(watcher);
        portField.addTextChangedListener(watcher);
        return view;
    }

    @Override
    public Bundle getData(Bundle in) {
        in.putString("host", hostField.getText().toString());
        in.putInt("port", Integer.valueOf(portField.getText().toString()));
        return in;
    }

    @Override
    public boolean isValid() {
        return validHost() && validPort();
    }

    @Override
    @StringRes
    public int getTitle() {
        return R.string.slideAccountcoreTitle;
    }

    @Override
    @StringRes
    public int getDescription() {
        return R.string.slideAccountcoreDescription;
    }

    private boolean validPort() {
        if (portField == null)
            return false;

        String portText = portField.getText().toString();
        try {
            int port = Integer.parseInt(portText);
            return port <= 65536 && port > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean validHost() {
        if (hostField == null)
            return false;

        String hostText = hostField.getText().toString();
        return !hostText.isEmpty();
    }
}
