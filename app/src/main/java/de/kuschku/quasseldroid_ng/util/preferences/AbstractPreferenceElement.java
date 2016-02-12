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

package de.kuschku.quasseldroid_ng.util.preferences;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractPreferenceElement<T> implements PreferenceElement<T>, OnChangeListener<T> {
    private final Set<OnChangeListener<T>> listeners = new HashSet<>();
    protected SharedPreferences pref;
    protected SharedPreferences.Editor edit;
    protected String key;
    protected T defValue;

    public AbstractPreferenceElement(SharedPreferences pref, String key, T defValue) {
        this.pref = pref;
        this.key = key;
        this.defValue = defValue;
    }

    public void change() {
        change(get());
    }

    public void change(T value) {
        for (OnChangeListener<T> listener : listeners)
            listener.change(value);
    }

    public void addChangeListener(OnChangeListener<T> listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(OnChangeListener<T> listener) {
        listeners.remove(listener);
    }

    protected abstract void put(T value);

    public void batch(SharedPreferences.Editor edit) {
        if (this.edit != null) this.edit.commit();
        this.edit = edit;
    }

    public T get() {
        return or(defValue);
    }

    public void set(T value) {
        edit = pref.edit();
        put(value);
        edit.commit();
    }
}
