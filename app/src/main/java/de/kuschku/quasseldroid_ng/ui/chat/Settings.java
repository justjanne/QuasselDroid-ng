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

import android.content.Context;
import android.content.SharedPreferences;

import aspm.BooleanPreference;
import aspm.IntPreference;
import aspm.StringPreference;
import de.kuschku.util.backports.Objects;

public class Settings {
    public final StringPreference lastAccount;

    public final IntPreference textSize;

    public final BooleanPreference mircColors;

    public final BooleanPreference fullHostmask;

    public final StringPreference theme;

    public Settings(SharedPreferences pref) {
        this.lastAccount = new StringPreference(pref, "lastAccount", "");
        this.textSize = new IntPreference(pref, "textSize", 2);
        this.mircColors = new BooleanPreference(pref, "mircColors", true);
        this.fullHostmask = new BooleanPreference(pref, "fullHostmask", false);
        this.theme = new StringPreference(pref, "theme", "QUASSEL_LIGHT");

        pref.registerOnSharedPreferenceChangeListener((preferences, key) -> {
            if (Objects.equals(key, "lastAccount")) lastAccount.change();
            if (Objects.equals(key, "textSize")) textSize.change();
            if (Objects.equals(key, "mircColors")) mircColors.change();
            if (Objects.equals(key, "fullHostmask")) fullHostmask.change();
            if (Objects.equals(key, "theme")) theme.change();
        });
    }

    public Settings(Context ctx) {
        this(ctx.getSharedPreferences("de.kuschku.quasseldroid_ng", 0));
    }
}
