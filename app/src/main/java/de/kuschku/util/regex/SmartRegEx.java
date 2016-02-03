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

package de.kuschku.util.regex;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SmartRegEx {
    @NonNull
    private final Pattern pattern;
    private final String rule;

    public SmartRegEx(@NonNull String rule, int flags, @NonNull Syntax syntax) {
        this.pattern = fromSyntax(syntax, rule, flags);
        this.rule = rule;
    }

    public String rule() {
        return rule;
    }

    public boolean matches(@NonNull String text, boolean inexact) {
        Matcher matcher = pattern.matcher(text);
        if (inexact) {
            return matcher.find();
        } else {
            return matcher.matches();
        }
    }

    private Pattern fromSyntax(@NonNull Syntax syntax, @NonNull String rule, int flags) {
        switch (syntax) {
            case WILDCARD:
                return transformWildcard(rule, flags);
            case REGEX:
            default:
                return Pattern.compile(rule, flags);
        }
    }

    private Pattern transformWildcard(String glob, int flags) {
        return Pattern.compile(GlobTransformer.convertGlobToRegex(glob), flags);
    }

    public enum Syntax {
        WILDCARD,
        REGEX
    }
}
