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

import static de.kuschku.util.irc.chanmodes.ChanMode.ALLOW_FORWARD;
import static de.kuschku.util.irc.chanmodes.ChanMode.ALLOW_INVITE;
import static de.kuschku.util.irc.chanmodes.ChanMode.BAN;
import static de.kuschku.util.irc.chanmodes.ChanMode.BAN_EXCEPTION;
import static de.kuschku.util.irc.chanmodes.ChanMode.BLOCK_ACTION;
import static de.kuschku.util.irc.chanmodes.ChanMode.BLOCK_AUTOREJOIN;
import static de.kuschku.util.irc.chanmodes.ChanMode.BLOCK_CAPS;
import static de.kuschku.util.irc.chanmodes.ChanMode.BLOCK_CTCP;
import static de.kuschku.util.irc.chanmodes.ChanMode.BLOCK_EXTERNAL;
import static de.kuschku.util.irc.chanmodes.ChanMode.BLOCK_FORWARDING;
import static de.kuschku.util.irc.chanmodes.ChanMode.BLOCK_KICK;
import static de.kuschku.util.irc.chanmodes.ChanMode.BLOCK_NICKCHANGE;
import static de.kuschku.util.irc.chanmodes.ChanMode.BLOCK_NOTICE;
import static de.kuschku.util.irc.chanmodes.ChanMode.BLOCK_REPEAT;
import static de.kuschku.util.irc.chanmodes.ChanMode.BLOCK_UNIDENTIFIED;
import static de.kuschku.util.irc.chanmodes.ChanMode.INVITE_EXCEPTION;
import static de.kuschku.util.irc.chanmodes.ChanMode.JOIN_THROTTLE;
import static de.kuschku.util.irc.chanmodes.ChanMode.LIMIT;
import static de.kuschku.util.irc.chanmodes.ChanMode.MODERATED;
import static de.kuschku.util.irc.chanmodes.ChanMode.ONLY_ADMIN;
import static de.kuschku.util.irc.chanmodes.ChanMode.ONLY_INVITE;
import static de.kuschku.util.irc.chanmodes.ChanMode.ONLY_OPER;
import static de.kuschku.util.irc.chanmodes.ChanMode.ONLY_SSL;
import static de.kuschku.util.irc.chanmodes.ChanMode.PARANOID;
import static de.kuschku.util.irc.chanmodes.ChanMode.PASSWORD;
import static de.kuschku.util.irc.chanmodes.ChanMode.PERMANENT;
import static de.kuschku.util.irc.chanmodes.ChanMode.REDUCED_MODERATION;
import static de.kuschku.util.irc.chanmodes.ChanMode.RESTRICT_TOPIC;
import static de.kuschku.util.irc.chanmodes.ChanMode.STRIP_COLOR;
import static de.kuschku.util.irc.chanmodes.ChanMode.UNLISTED;

public class ShadowIrcModeProvider extends AbstractIrcModeProvider {

    protected Set<Character> supportedModes = new HashSet<>(Arrays.asList(
            'A', 'C', 'D', 'E', 'F', 'G', 'J', 'K', 'O', 'P', 'Q', 'S', 'T', 'c', 'd', 'g', 'i', 'j', 'k', 'l', 'm', 'n', 'p', 'r', 's', 't', 'z'
    ));

    @Override
    public ChanMode modeFromChar(char mode) {
        switch (mode) {
            case 'A': return ONLY_ADMIN;
            case 'C': return BLOCK_CTCP;
            case 'D': return BLOCK_ACTION;
            case 'E': return BLOCK_KICK;
            case 'F': return ALLOW_FORWARD;
            case 'G': return BLOCK_CAPS;
            case 'J': return BLOCK_AUTOREJOIN;
            case 'K': return BLOCK_REPEAT;
            case 'O': return ONLY_OPER;
            case 'P': return PERMANENT;
            case 'Q': return BLOCK_FORWARDING;
            case 'S': return ONLY_SSL;
            case 'T': return BLOCK_NOTICE;
            case 'c': return STRIP_COLOR;
            case 'd': return BLOCK_NICKCHANGE;
            case 'g': return ALLOW_INVITE;
            case 'i': return ONLY_INVITE;
            case 'j': return JOIN_THROTTLE;
            case 'k': return PASSWORD;
            case 'l': return LIMIT;
            case 'm': return MODERATED;
            case 'n': return BLOCK_EXTERNAL;
            case 'p': return PARANOID;
            case 'r': return BLOCK_UNIDENTIFIED;
            case 's': return UNLISTED;
            case 't': return RESTRICT_TOPIC;
            case 'z': return REDUCED_MODERATION;

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
            case ONLY_ADMIN: return 'A';
            case BLOCK_CTCP: return 'C';
            case BLOCK_ACTION: return 'D';
            case BLOCK_KICK: return 'E';
            case ALLOW_FORWARD: return 'F';
            case BLOCK_CAPS: return 'G';
            case BLOCK_AUTOREJOIN: return 'J';
            case BLOCK_REPEAT: return 'K';
            case ONLY_OPER: return 'O';
            case PERMANENT: return 'P';
            case BLOCK_FORWARDING: return 'Q';
            case ONLY_SSL: return 'S';
            case BLOCK_NOTICE: return 'T';
            case STRIP_COLOR: return 'c';
            case BLOCK_NICKCHANGE: return 'd';
            case ALLOW_INVITE: return 'g';
            case ONLY_INVITE: return 'i';
            case JOIN_THROTTLE: return 'j';
            case PASSWORD: return 'k';
            case LIMIT: return 'l';
            case MODERATED: return 'm';
            case BLOCK_EXTERNAL: return 'n';
            case PARANOID: return 'p';
            case BLOCK_UNIDENTIFIED: return 'r';
            case UNLISTED: return 's';
            case RESTRICT_TOPIC: return 't';
            case REDUCED_MODERATION: return 'z';

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
