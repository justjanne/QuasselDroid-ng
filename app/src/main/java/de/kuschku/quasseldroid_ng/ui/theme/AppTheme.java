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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;

import de.kuschku.quasseldroid_ng.R;

public enum AppTheme {
    QUASSEL_LIGHT(R.style.Quassel_Light),
    QUASSEL_DARK(R.style.Quassel_Dark),
    SOLARIZED_LIGHT(R.style.Solarized_Light),
    SOLARIZED_DARK(R.style.Solarized_Dark);

    public final int themeId;

    AppTheme(@StyleRes int themeId) {
        this.themeId = themeId;
    }

    public static int resFromString(String s) {
        return themeFromString(s).themeId;
    }

    @NonNull
    public static AppTheme themeFromString(@Nullable String s) {
        if (s == null) s = "";
        switch (s) {
            case "QUASSEL_DARK":
                return QUASSEL_DARK;
            case "SOLARIZED_LIGHT":
                return SOLARIZED_LIGHT;
            case "SOLARIZED_DARK":
                return SOLARIZED_DARK;

            default:
            case "QUASSEL_LIGHT":
                return QUASSEL_LIGHT;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return name() + "{" +
                "themeId=" + themeId +
                '}';
    }
}
