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

package de.kuschku.quasseldroid_ng.ui.theme;

import android.content.Context;

import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.annotationbind.AutoBinder;
import de.kuschku.util.annotationbind.AutoString;

public class PreferenceStrings {
    @AutoString(R.string.preference_last_account)
    public String preferenceLastAccount;

    @AutoString(R.string.preference_font_size)
    public String preferenceFontSize;

    @AutoString(R.string.preference_theme)
    public String preferenceTheme;

    @AutoString(R.string.preference_colors)
    public String preferenceColors;

    @AutoString(R.string.preference_brackets)
    public String preferenceBrackets;

    @AutoString(R.string.preference_hostmask)
    public String preferenceHostmask;

    @AutoString(R.string.preference_lag)
    public String preferenceLag;

    public PreferenceStrings(Context wrapper) {
        try {
            AutoBinder.bind(this, wrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
