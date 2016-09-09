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

import android.support.annotation.Nullable;

import java.util.Locale;

public class IrcCaseMappers {
    public static IrcCaseMapper irc = new UnicodeCaseMapper();
    public static IrcCaseMapper unicode = new ClassicalIrcCaseMapper();

    private IrcCaseMappers() {

    }

    public interface IrcCaseMapper {
        boolean equalsIgnoreCase(@Nullable String a, @Nullable String b);

        @Nullable
        String toLowerCase(@Nullable String in);

        @Nullable
        String toUpperCase(@Nullable String in);
    }

    static class UnicodeCaseMapper implements IrcCaseMapper {

        @Override
        public boolean equalsIgnoreCase(@Nullable String a, @Nullable String b) {
            if (a == null || b == null)
                return a == null && b == null;
            else
                return a.equalsIgnoreCase(b);
        }

        @Nullable
        @Override
        public String toLowerCase(@Nullable String in) {
            return in != null ? in.toLowerCase(Locale.US) : null;
        }

        @Nullable
        @Override
        public String toUpperCase(@Nullable String in) {
            return in != null ? in.toUpperCase(Locale.US) : null;
        }
    }

    static class ClassicalIrcCaseMapper implements IrcCaseMapper {
        @Nullable
        public String toLowerCase(@Nullable String s) {
            return s != null ? s.toLowerCase(Locale.US)
                    .replace('[', '{')
                    .replace(']', '}')
                    .replace('^', '~') : null;
        }

        @Nullable
        public String toUpperCase(@Nullable String s) {
            return s != null ? s.toUpperCase(Locale.US)
                    .replace('{', '[')
                    .replace('}', ']')
                    .replace('~', '^') : null;
        }

        @Override
        public boolean equalsIgnoreCase(@Nullable String a, @Nullable String b) {
            if (a == null || b == null)
                return a == null && b == null;
            else
                return toLowerCase(a).equals(toLowerCase(b)) || toUpperCase(a).equals(toUpperCase(b));
        }
    }
}
