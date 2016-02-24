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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.kuschku.util.irc.chanmodes.AbstractIrcModeProvider;
import de.kuschku.util.irc.chanmodes.ChanMode;

import static de.kuschku.util.irc.chanmodes.ChanMode.BAN;
import static de.kuschku.util.irc.chanmodes.ChanMode.BAN_EXCEPTION;
import static de.kuschku.util.irc.chanmodes.ChanMode.BLOCK_EXTERNAL;
import static de.kuschku.util.irc.chanmodes.ChanMode.INVITE_EXCEPTION;
import static de.kuschku.util.irc.chanmodes.ChanMode.LIMIT;
import static de.kuschku.util.irc.chanmodes.ChanMode.MODERATED;
import static de.kuschku.util.irc.chanmodes.ChanMode.ONLY_INVITE;
import static de.kuschku.util.irc.chanmodes.ChanMode.PARANOID;
import static de.kuschku.util.irc.chanmodes.ChanMode.PASSWORD;
import static de.kuschku.util.irc.chanmodes.ChanMode.RESTRICT_TOPIC;
import static de.kuschku.util.irc.chanmodes.ChanMode.UNLISTED;

public class RfcIrcModeProvider extends AbstractIrcModeProvider {

    protected Set<Character> supportedModes = new HashSet<>(Arrays.asList(
            'p', 's', 'i', 't', 'n', 'm', 'l', 'k'
    ));

    @Override
    public ChanMode modeFromChar(char mode) {
        switch (mode) {
            case 'p': return PARANOID;
            case 's': return UNLISTED;
            case 'i': return ONLY_INVITE;
            case 't': return RESTRICT_TOPIC;
            case 'n': return BLOCK_EXTERNAL;
            case 'm': return MODERATED;
            case 'l': return LIMIT;
            case 'k': return PASSWORD;

            case 'b':
                return BAN;
            case 'e':
                return BAN_EXCEPTION;
            case 'I':
                return INVITE_EXCEPTION;
        }
        return null;
    }

    @Override
    public char charFromMode(ChanMode mode) {
        switch (mode) {
            case PARANOID: return 'p';
            case UNLISTED: return 's';
            case ONLY_INVITE: return 'i';
            case RESTRICT_TOPIC: return 't';
            case BLOCK_EXTERNAL: return 'n';
            case MODERATED: return 'm';
            case LIMIT: return 'l';
            case PASSWORD: return 'k';

            case BAN:
                return 'b';
            case BAN_EXCEPTION:
                return 'e';
            case INVITE_EXCEPTION:
                return 'I';
        }
        return ' ';
    }

    @Override
    protected Collection<Character> supportedModes() {
        return supportedModes;
    }
}
