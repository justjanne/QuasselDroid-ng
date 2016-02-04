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

package de.kuschku.libquassel.syncables.types.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.IgnoreListManagerSerializer;
import de.kuschku.libquassel.syncables.types.abstracts.AIgnoreListManager;
import de.kuschku.util.regex.SmartRegEx;

import static de.kuschku.util.AndroidAssert.assertEquals;

public class IgnoreListManager extends AIgnoreListManager<IgnoreListManager> {
    @NonNull
    private final List<IgnoreListItem> ignoreList = new ArrayList<>();

    public IgnoreListManager(@NonNull List<Integer> scope, @NonNull List<Integer> ignoreType,
                             @NonNull List<Boolean> isActive, @NonNull List<String> scopeRule, @NonNull List<Boolean> isRegEx,
                             @NonNull List<Integer> strictness, @NonNull List<String> ignoreRule) {
        assertEquals(scope.size(), ignoreType.size(), isActive.size(), scopeRule.size(), isRegEx.size(), strictness.size(), ignoreRule.size());

        for (int i = 0; i < scope.size(); i++) {
            ignoreList.add(new IgnoreListItem(
                    ignoreType.get(i),
                    ignoreRule.get(i),
                    isRegEx.get(i),
                    strictness.get(i),
                    scope.get(i),
                    scopeRule.get(i),
                    isActive.get(i)
            ));
        }
    }

    @Override
    public void _requestRemoveIgnoreListItem(String ignoreRule) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _removeIgnoreListItem(String ignoreRule) {
        ignoreList.remove(indexOf(ignoreRule));
        _update();
    }

    private int indexOf(String ignoreRule) {
        for (int i = 0; i < ignoreList.size(); i++) {
            if (ignoreList.get(i).ignoreRule.rule().equals(ignoreRule))
                return i;
        }
        return -1;
    }

    @Override
    public void _requestToggleIgnoreRule(String ignoreRule) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _toggleIgnoreRule(String ignoreRule) {
        IgnoreListItem item = ignoreList.get(indexOf(ignoreRule));
        item.isActive = !item.isActive;
        _update();
    }

    @Override
    public void _requestAddIgnoreListItem(int type, String ignoreRule, boolean isRegEx, int strictness, int scope, String scopeRule, boolean isActive) {
        // Do nothing, we’re on the client – the server will receive the sync just as expected
    }

    @Override
    public void _addIgnoreListItem(int type, @NonNull String ignoreRule, boolean isRegEx, int strictness, int scope, @NonNull String scopeRule, boolean isActive) {
        if (contains(ignoreRule))
            return;

        ignoreList.add(new IgnoreListItem(type, ignoreRule, isRegEx, strictness, scope, scopeRule, isActive));
        _update();
    }

    private boolean contains(String ignoreRule) {
        return indexOf(ignoreRule) != -1;
    }

    @Override
    public StrictnessType match(String msgContents, String msgSender, Message.Type msgType, @NonNull String network, @NonNull String bufferName) {
        if (msgType != Message.Type.Plain && msgType != Message.Type.Notice && msgType != Message.Type.Action)
            return StrictnessType.UnmatchedStrictness;

        for (IgnoreListItem item : ignoreList) {
            if (!item.isActive || item.type == IgnoreType.CtcpIgnore)
                continue;

            if (item.scopeMatch(network, bufferName)) {
                String str;
                if (item.type == IgnoreType.MessageIgnore)
                    str = msgContents;
                else
                    str = msgSender;

                if (item.matches(str))
                    return item.strictness;
            }
        }
        return StrictnessType.UnmatchedStrictness;
    }

    @Override
    public void update(@NonNull Map<String, QVariant> from) {
        update(IgnoreListManagerSerializer.get().fromDatastream(from));
    }

    @Override
    public void update(IgnoreListManager from) {

    }

    @Override
    public void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull Client client) {
        super.init(objectName, provider, client);
        client.setIgnoreListManager(this);
    }

    public class IgnoreListItem {
        private final IgnoreType type;
        private final SmartRegEx ignoreRule;
        private final boolean isRegEx;
        private final StrictnessType strictness;
        private final ScopeType scope;
        private final SmartRegEx[] scopeRules;
        private boolean isActive;

        public IgnoreListItem(int type, @Nullable String ignoreRule, boolean isRegEx, int strictness, int scope, @Nullable String scopeRule, boolean isActive) {
            this(IgnoreType.of(type), ignoreRule, isRegEx, StrictnessType.of(strictness), ScopeType.of(scope), scopeRule, isActive);
        }

        public IgnoreListItem(IgnoreType type, @Nullable String ignoreRule, boolean isRegEx, StrictnessType strictness, ScopeType scope, @Nullable String scopeRule, boolean isActive) {
            if (scopeRule == null)
                scopeRule = "";
            if (ignoreRule == null)
                ignoreRule = "";

            this.type = type;
            this.ignoreRule = new SmartRegEx(ignoreRule, Pattern.CASE_INSENSITIVE, SmartRegEx.Syntax.WILDCARD);
            this.isRegEx = isRegEx;
            this.strictness = strictness;
            this.scope = scope;
            this.isActive = isActive;

            String[] scopeRules = scopeRule.split(";");
            this.scopeRules = new SmartRegEx[scopeRules.length];
            for (int i = 0; i < scopeRules.length; i++) {
                this.scopeRules[i] = new SmartRegEx(scopeRules[i].trim(), Pattern.CASE_INSENSITIVE, SmartRegEx.Syntax.WILDCARD);
            }
        }

        public boolean matches(@NonNull String text) {
            return ignoreRule.matches(text, !isRegEx);
        }

        private boolean scopeMatch(@NonNull String network, @NonNull String bufferName) {
            switch (scope) {
                case NetworkScope:
                    return scopeMatch(network);
                case ChannelScope:
                    return scopeMatch(bufferName);
                default:
                case GlobalScope:
                    return true;
            }
        }

        private boolean scopeMatch(@NonNull String scope) {
            for (SmartRegEx scopeRule : scopeRules) {
                if (scopeRule.matches(scope, !isRegEx))
                    return true;
            }
            return false;
        }
    }
}
