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

package de.kuschku.util.irc.chanmodes.impl;

import de.kuschku.util.irc.chanmodes.AbstractIrcModeProvider;
import de.kuschku.util.irc.chanmodes.ChanMode;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static de.kuschku.util.irc.chanmodes.ChanMode.*;

public class HyperionIrcModeProvider extends AbstractIrcModeProvider {

    protected Set<Character> supportedModes = new HashSet<>(Arrays.asList(
            'Q', 'R', 'c', 'g', 'i', 'k', 'l', 'm', 'n', 'r', 's', 'z'
    ));

    @Override
    public ChanMode modeFromChar(char mode) {
        switch (mode) {
            case 'Q': return BLOCK_FORWARDING;
            case 'R': return QUIET_UNIDENTIFIED;
            case 'c': return BLOCK_COLOR;
            case 'g': return ALLOW_INVITE;
            case 'i': return ONLY_INVITE;
            case 'k': return PASSWORD;
            case 'l': return LIMIT;
            case 'm': return MODERATED;
            case 'n': return BLOCK_EXTERNAL;
            case 'r': return BLOCK_UNIDENTIFIED;
            case 's': return UNLISTED;
            case 'z': return REDUCED_MODERATION;
        }
        return null;
    }

    @Override
    public char charFromMode(ChanMode mode) {
        switch (mode) {
            case BLOCK_FORWARDING: return 'Q';
            case QUIET_UNIDENTIFIED: return 'R';
            case BLOCK_COLOR: return 'c';
            case ALLOW_INVITE: return 'g';
            case ONLY_INVITE: return 'i';
            case PASSWORD: return 'k';
            case LIMIT: return 'l';
            case MODERATED: return 'm';
            case BLOCK_EXTERNAL: return 'n';
            case BLOCK_UNIDENTIFIED: return 'r';
            case UNLISTED: return 's';
            case REDUCED_MODERATION: return 'z';
        }
        return ' ';
    }

    @Override
    protected Collection<Character> supportedModes() {
        return supportedModes;
    }
}
