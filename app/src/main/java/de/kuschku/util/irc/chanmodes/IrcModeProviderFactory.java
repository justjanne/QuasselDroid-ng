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

import de.kuschku.util.irc.chanmodes.impl.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IrcModeProviderFactory {
    private static List<IrcModeProvider> servers = Arrays.asList(
            new CharybdisIrcModeProvider(),
            new DalIrcModeProvider(),
            new DancerIrcModeProvider(),
            new FqIrcModeProvider(),
            new HybridIrcModeProvider(),
            new HyperionIrcModeProvider(),
            new InspireIrcModeProvider(),
            new NeoIrcModeProvider(),
            new ShadowIrcModeProvider(),
            new SolidIrcModeProvider(),
            new UnrealIrcModeProvider()
    );

    private static Set<Character> toModeSet(String chanModes) {
        String replaced = chanModes.replaceAll(",","");
        Set<Character> modeSet = new HashSet<>();
        for (char c : replaced.toCharArray()) {
            modeSet.add(c);
        }
        return modeSet;
    }

    public static IrcModeProvider identifyServer(String modeString) {
        return identifyServer(toModeSet(modeString));
    }

    public static IrcModeProvider identifyServer(Set<Character> characters) {
        IrcModeProvider bestMatch = null;
        int bestMatchCount = Integer.MAX_VALUE;

        for (IrcModeProvider server : servers) {
            int matchQuality = server.matchQuality(characters);
            if (bestMatchCount > matchQuality) {
                bestMatch = server;
                bestMatchCount = matchQuality;
            }
        }

        return bestMatch;
    }
}
