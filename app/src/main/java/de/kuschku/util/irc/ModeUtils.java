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

package de.kuschku.util.irc;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ModeUtils {
    @NonNull
    public static Set<Character> toModes(@Nullable String modes) {
        Set<Character> modeSet = new HashSet<>();
        if (modes == null)
            return modeSet;
        for (String mode : modes.split("")) {
            if (mode.length() == 1)
                modeSet.add(toMode(mode));
        }
        return modeSet;
    }

    public static String fromModes(@NonNull Set<Character> d_channelModes) {
        StringBuilder builder = new StringBuilder(d_channelModes.size());
        for (char c : d_channelModes) {
            builder.append(new char[]{c});
        }
        return builder.toString();
    }

    public static char toMode(@NonNull String mode) {
        return mode.charAt(0);
    }

    public static String fromMode(char ch) {
        return String.copyValueOf(new char[]{ch});
    }
}
