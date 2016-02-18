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

public class SolidIrcModeProvider extends AbstractIrcModeProvider {

    protected Set<Character> supportedModes = new HashSet<>(Arrays.asList(
            'B', 'C', 'G', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'c', 'f', 'i', 'k', 'l', 'm', 'n', 'p', 's', 't', 'u', 'z'
    ));

    @Override
    public ChanMode modeFromChar(char mode) {
        switch (mode) {
            case 'B': return BLOCK_CAPS;
            case 'C': return BLOCK_CTCP;
            case 'G': return CENSOR;
            case 'K': return BLOCK_KNOCK;
            case 'M': return QUIET_UNIDENTIFIED;
            case 'N': return BLOCK_NICKCHANGE;
            case 'P': return PERMANENT;
            case 'Q': return BLOCK_KICK;
            case 'R': return BLOCK_UNIDENTIFIED;
            case 'S': return STRIP_COLOR;
            case 'T': return BLOCK_NOTICE;
            case 'V': return DISABLE_INVITE;
            case 'c': return BLOCK_COLOR;
            case 'f': return ANTIFLOOD;
            case 'i': return ONLY_INVITE;
            case 'k': return PASSWORD;
            case 'l': return LIMIT;
            case 'm': return MODERATED;
            case 'n': return BLOCK_EXTERNAL;
            case 'p': return PARANOID;
            case 's': return UNLISTED;
            case 't': return RESTRICT_TOPIC;
            case 'u': return AUDITORIUM;
            case 'z': return ONLY_SSL;
        }
        return null;
    }

    @Override
    public char charFromMode(ChanMode mode) {
        switch (mode) {
            case BLOCK_CAPS: return 'B';
            case BLOCK_CTCP: return 'C';
            case CENSOR: return 'G';
            case BLOCK_KNOCK: return 'K';
            case QUIET_UNIDENTIFIED: return 'M';
            case BLOCK_NICKCHANGE: return 'N';
            case PERMANENT: return 'P';
            case BLOCK_KICK: return 'Q';
            case BLOCK_UNIDENTIFIED: return 'R';
            case STRIP_COLOR: return 'S';
            case BLOCK_NOTICE: return 'T';
            case DISABLE_INVITE: return 'V';
            case BLOCK_COLOR: return 'c';
            case ANTIFLOOD: return 'f';
            case ONLY_INVITE: return 'i';
            case PASSWORD: return 'k';
            case LIMIT: return 'l';
            case MODERATED: return 'm';
            case BLOCK_EXTERNAL: return 'n';
            case PARANOID: return 'p';
            case UNLISTED: return 's';
            case RESTRICT_TOPIC: return 't';
            case AUDITORIUM: return 'u';
            case ONLY_SSL: return 'z';
        }
        return ' ';
    }

    @Override
    protected Collection<Character> supportedModes() {
        return supportedModes;
    }
}
