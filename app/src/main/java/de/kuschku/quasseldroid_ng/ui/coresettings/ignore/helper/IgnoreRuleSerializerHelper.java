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

package de.kuschku.quasseldroid_ng.ui.coresettings.ignore.helper;

import android.os.Bundle;

import de.kuschku.libquassel.syncables.types.impl.IgnoreListManager;

public class IgnoreRuleSerializerHelper {
    private IgnoreRuleSerializerHelper() {
    }

    public static Bundle serialize(IgnoreListManager.IgnoreListItem ignoreRule) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", ignoreRule.getType().value);
        bundle.putString("ignoreRule", ignoreRule.getIgnoreRule().rule());
        bundle.putBoolean("isRegEx", ignoreRule.isRegEx());
        bundle.putInt("strictness", ignoreRule.getStrictness().value);
        bundle.putInt("scope", ignoreRule.getScope().value);
        bundle.putString("scopeRule", ignoreRule.getScopeRule());
        bundle.putBoolean("isActive", ignoreRule.isActive());
        return bundle;
    }

    public static IgnoreListManager.IgnoreListItem deserialize(Bundle bundle) {
        return new IgnoreListManager.IgnoreListItem(
                bundle.getInt("type"),
                bundle.getString("ignoreRule"),
                bundle.getBoolean("isRegEx"),
                bundle.getInt("strictness"),
                bundle.getInt("scope"),
                bundle.getString("scopeRule"),
                bundle.getBoolean("isActive")
        );
    }
}
