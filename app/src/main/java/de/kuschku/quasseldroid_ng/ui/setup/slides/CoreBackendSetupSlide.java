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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.quasseldroid_ng.R;

public class CoreBackendSetupSlide extends SlideFragment {
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
    private final Map<String, InputItemWrapper> items = new HashMap<>();
    private final Map<String, Bundle> storageBackends = new HashMap<>();
    @Bind(R.id.container)
    LinearLayout container;
    Bundle storageBackend;

    @Override
    public void setArguments(Bundle args) {
        ArrayList<Bundle> storageBackends = args.getParcelableArrayList("storageBackends");
        if (storageBackends != null) {
            for (Bundle bundle : storageBackends) {
                this.storageBackends.put(bundle.getString("displayName"), bundle);
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View onCreateContent(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_core_setup_backend, container, false);
        ButterKnife.bind(this, view);

        updateContent();

        return view;
    }

    private void updateContent() {
        container.removeAllViews();

        for (InputItemWrapper wrapper : items.values()) {
            wrapper.onDestroy();
        }
        items.clear();

        if (storageBackend == null)
            return;

        Bundle types = storageBackend.getBundle("types");
        Bundle defaults = storageBackend.getBundle("defaults");

        if (types == null || defaults == null)
            return;

        for (String key : types.keySet()) {
            String type = types.getString(key);
            Object defValue = defaults.get(key);
            InputItemWrapper wrapper = getInputItemWrapper(key, type, defValue);
            items.put(key, wrapper);
            container.addView(wrapper.getView(container.getContext(), container));
        }
    }

    @Override
    public void setData(Bundle in) {
        storageBackend = storageBackends.get(in.getString("selectedBackend"));
        updateContent();
        updateValidity();
    }

    @Override
    public Bundle getData(Bundle in) {
        Bundle config = new Bundle();
        for (InputItemWrapper inputItemWrapper : items.values()) {
            if (inputItemWrapper.isValid())
                inputItemWrapper.putValue(config);
        }
        in.putBundle("config", config);
        return in;
    }

    @Override
    public boolean isValid() {
        for (InputItemWrapper inputItemWrapper : items.values()) {
            if (!inputItemWrapper.isValid())
                return false;
        }
        return true;
    }

    @Override
    @StringRes
    public int getTitle() {
        return R.string.slideCoreBackendSetupTitle;
    }

    @Override
    @StringRes
    public int getDescription() {
        return R.string.slideCoreBackendSetupDescription;
    }

    @SuppressWarnings("unchecked")
    private <T> InputItemWrapper<T> getInputItemWrapper(String key, String type, T defValue) {
        InputItemWrapper result;
        switch (type) {
            case "boolean": {
                result = new BooleanInputItem(key, (Boolean) defValue);
            }
            break;
            case "short":
            case "int":
            case "long":
            case "float":
            case "double": {
                result = new NumberInputItem(key, (Number) defValue, type);
            }
            break;
            default:
            case "string": {
                result = new StringInputItem(key, (String) defValue);
            }
            break;
        }
        return result;
    }

    private interface InputItemWrapper<T> {
        String key();

        T getValue();

        boolean isValid();

        View getView(Context context, ViewGroup parent);

        void onDestroy();

        void putValue(Bundle in);
    }

    class BooleanInputItem implements InputItemWrapper<Boolean> {
        private final String key;
        private final Boolean defValue;
        @Bind(R.id.checkBox)
        AppCompatCheckBox checkBox;
        private View view;

        public BooleanInputItem(String key, Boolean defValue) {
            this.key = key;
            this.defValue = defValue;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public Boolean getValue() {
            return checkBox.isChecked();
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public View getView(Context context, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.widget_input_boolean, parent, false);
                ButterKnife.bind(this, view);
                checkBox.setText(key);
                if (defValue != null)
                    checkBox.setChecked(defValue);
            }
            return view;
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public void putValue(Bundle in) {
            in.putBoolean(key, getValue());
        }
    }

    class NumberInputItem implements InputItemWrapper<Number> {
        private final String key;
        private final Number defValue;
        private final String type;
        @Bind(R.id.inputLayout)
        TextInputLayout inputLayout;
        @Bind(R.id.editText)
        TextInputEditText editText;
        private View view;

        public NumberInputItem(String key, Number defValue, String type) {
            this.key = key;
            this.defValue = defValue;
            this.type = type;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public Number getValue() {
            String text = editText.getText().toString();
            try {
                switch (type) {
                    case "short":
                        return Short.parseShort(text);
                    case "int":
                        return Integer.parseInt(text);
                    case "long":
                        return Long.parseLong(text);
                    case "float":
                        return Float.parseFloat(text);
                    case "double":
                        return Double.parseDouble(text);
                    default:
                        return null;
                }
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        public boolean isValid() {
            return getValue() != null;
        }

        @Override
        public View getView(Context context, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.widget_input_string, parent, false);
                ButterKnife.bind(this, view);
                if (defValue != null)
                    editText.setText(defValue.toString());
                editText.addTextChangedListener(watcher);
                switch (type) {
                    case "short":
                    case "int":
                    case "long":
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                        break;
                    case "float":
                    case "double":
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        break;
                }
                inputLayout.setHint(key);
            }
            return view;
        }

        @Override
        public void onDestroy() {
            editText.removeTextChangedListener(watcher);
        }

        @Override
        public void putValue(Bundle in) {
            switch (type) {
                case "short": {
                    in.putShort(key, (short) getValue());
                }
                break;
                case "int": {
                    in.putInt(key, (int) getValue());
                }
                break;
                case "long": {
                    in.putLong(key, (long) getValue());
                }
                break;
                case "float": {
                    in.putFloat(key, (float) getValue());
                }
                break;
                case "double": {
                    in.putDouble(key, (double) getValue());
                }
                break;
            }
        }
    }

    class StringInputItem implements InputItemWrapper<String> {
        private final String key;
        private final String defValue;
        @Bind(R.id.inputLayout)
        TextInputLayout inputLayout;
        @Bind(R.id.editText)
        TextInputEditText editText;
        private View view;

        public StringInputItem(String key, String defValue) {
            this.key = key;
            this.defValue = defValue;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public String getValue() {
            return editText.getText().toString();
        }

        @Override
        public boolean isValid() {
            return !editText.getText().toString().isEmpty();
        }

        @Override
        public View getView(Context context, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.widget_input_string, parent, false);
                ButterKnife.bind(this, view);
                if (defValue != null)
                    editText.setText(defValue);
                editText.addTextChangedListener(watcher);
                inputLayout.setHint(key);
            }
            return view;
        }

        @Override
        public void onDestroy() {
            editText.removeTextChangedListener(watcher);
        }

        @Override
        public void putValue(Bundle in) {
            in.putString(key, getValue());
        }
    }
}
