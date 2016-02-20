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

package de.kuschku.quasseldroid_ng.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import de.kuschku.quasseldroid_ng.ui.theme.PreferenceStrings;
import de.kuschku.util.preferences.BooleanPreference;
import de.kuschku.util.preferences.IntPreference;
import de.kuschku.util.preferences.StringPreference;

public class Settings {
    public final StringPreference preferenceLastAccount;

    public final StringPreference preferenceTheme;
    public final BooleanPreference preferenceColors;
    public final BooleanPreference preferenceBrackets;
    public final BooleanPreference preferenceHostmask;
    public final BooleanPreference preferenceLag;
    public final IntPreference preferenceFontSize;

    public Settings(PreferenceStrings prefs, SharedPreferences pref) {
        this.preferenceLastAccount = new StringPreference(pref, prefs.preferenceLastAccount, "");

        this.preferenceTheme = new StringPreference(pref, prefs.preferenceTheme, "QUASSEL_LIGHT");
        this.preferenceColors = new BooleanPreference(pref, prefs.preferenceColors, true);
        this.preferenceBrackets = new BooleanPreference(pref, prefs.preferenceBrackets, false);
        this.preferenceHostmask = new BooleanPreference(pref, prefs.preferenceHostmask, false);
        this.preferenceLag = new BooleanPreference(pref, prefs.preferenceLag, false);
        this.preferenceFontSize = new IntPreference(pref, prefs.preferenceFontSize, 14);

        pref.registerOnSharedPreferenceChangeListener((preferences, key) -> {
            if (prefs.preferenceLastAccount.equals(key)) preferenceLastAccount.change();

            if (prefs.preferenceTheme.equals(key)) preferenceTheme.change();
            if (prefs.preferenceColors.equals(key)) preferenceColors.change();
            if (prefs.preferenceBrackets.equals(key)) preferenceBrackets.change();
            if (prefs.preferenceHostmask.equals(key)) preferenceHostmask.change();
            if (prefs.preferenceLag.equals(key)) preferenceLag.change();
            if (prefs.preferenceFontSize.equals(key)) preferenceFontSize.change();
        });
    }

    public Settings(Context ctx) {
        this(new PreferenceStrings(ctx), PreferenceManager.getDefaultSharedPreferences(ctx));
    }
}
