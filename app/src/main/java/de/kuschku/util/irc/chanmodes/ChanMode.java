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

public enum ChanMode {
    ALLOW_FORWARD,
    ALLOW_INVITE,
    ANTIFLOOD,
    AUDITORIUM,
    BLOCK_ACTION,
    BLOCK_AUTOREJOIN,
    BLOCK_CAPS,
    BLOCK_CTCP,
    BLOCK_COLOR,
    BLOCK_EXTERNAL,
    BLOCK_FORWARDING,
    BLOCK_KICK,
    BLOCK_KNOCK,
    BLOCK_NICKCHANGE,
    BLOCK_NOTICE,
    BLOCK_REPEAT,
    BLOCK_UNIDENTIFIED,
    CENSOR,
    DISABLE_INVITE,
    HIDE_JOINS,
    IS_SECURE,
    JOIN_THROTTLE,
    LIMIT,
    MODERATED,
    ONLY_HELPOPER,
    ONLY_INVITE,
    ONLY_OPER,
    ONLY_ADMIN,
    ONLY_SSL,
    PARANOID,
    PASSWORD,
    PERMANENT,
    QUIET_UNIDENTIFIED,
    REDUCED_MODERATION,
    REGISTERED,
    RESTRICT_TOPIC,
    STRIP_COLOR,
    UNLISTED,
    FORWARD
}
