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

package de.kuschku.libquassel.syncables.types.abstracts;

import android.support.annotation.NonNull;

import java.util.Map;

import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.libquassel.syncables.types.interfaces.QIgnoreListManager;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public abstract class AIgnoreListManager extends SyncableObject<QIgnoreListManager> implements QIgnoreListManager {
    @Override
    public void removeIgnoreListItem(String ignoreRule) {
        _removeIgnoreListItem(ignoreRule);
        syncVar("requestRemoveIgnoreListItem", ignoreRule);
    }

    @Override
    public void toggleIgnoreRule(String ignoreRule) {
        _toggleIgnoreRule(ignoreRule);
        syncVar("requestToggleIgnoreRule", ignoreRule);
    }

    @Override
    public void addIgnoreListItem(int type, String ignoreRule, boolean isRegEx, int strictness, int scope, String scopeRule, boolean isActive) {
        _addIgnoreListItem(type, ignoreRule, isRegEx, strictness, scope, scopeRule, isActive);
        syncVar("requestAddIgnoreListItem", type, ignoreRule, isRegEx, strictness, scope, scopeRule, isActive);
    }

    @Override
    public void addIgnoreListItem(@NonNull IgnoreType type, String ignoreRule, boolean isRegEx, @NonNull StrictnessType strictness, @NonNull ScopeType scope, String scopeRule, boolean isActive) {
        addIgnoreListItem(type.value, ignoreRule, isRegEx, strictness.value, scope.value, scopeRule, isActive);
    }

    @Override
    public boolean matches(Message message, QNetwork network) {
        assertNotNull(network);
        return match(message.content, message.sender, message.type, network.networkName(), message.bufferInfo.name) != StrictnessType.UnmatchedStrictness;
    }

    @Override
    public void requestUpdate(Map<String, QVariant<Object>> variantMap) {
        syncVar("requestUpdate", variantMap);
    }
}
