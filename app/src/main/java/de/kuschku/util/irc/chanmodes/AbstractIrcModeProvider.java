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

package de.kuschku.util.irc.chanmodes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractIrcModeProvider implements IrcModeProvider {

    @Override
    public int matchQuality(Set<Character> modes) {
        HashSet<Character> diff = new HashSet<>();
        for (char c : modes) {
            if (!supportedModes().contains(c))
                diff.add(c);
        }
        for (char c : supportedModes()) {
            if (!modes.contains(c))
                diff.add(c);
        }
        return (diff.size());
    }

    public Set<ChanMode> modesFromString(String chanModes) {
        Set<ChanMode> result = new HashSet<>();
        for (char c : chanModes.toCharArray()) {
            ChanMode mode = modeFromChar(c);
            if (mode != null)
                result.add(mode);
        }
        return result;
    }

    protected abstract Collection<Character> supportedModes();
}
