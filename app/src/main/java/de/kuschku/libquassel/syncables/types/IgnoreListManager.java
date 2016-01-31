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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.syncables.types;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.IgnoreListManagerSerializer;

import static de.kuschku.util.AndroidAssert.assertEquals;

public class IgnoreListManager extends SyncableObject<IgnoreListManager> {
    @NonNull
    final List<IgnoreRule> ignoreRules = new ArrayList<>();

    public IgnoreListManager(@NonNull List<Integer> scope, @NonNull List<Integer> ignoreType,
                             @NonNull List<Boolean> isActive, @NonNull List<String> scopeRule, @NonNull List<Boolean> isRegEx,
                             @NonNull List<Integer> strictness, @NonNull List<String> ignoreRule) {
        assertEquals(scope.size(), ignoreType.size(), isActive.size(), scopeRule.size(), isRegEx.size(), strictness.size(), ignoreRule.size());

        for (int i = 0; i < scope.size(); i++) {
            ignoreRules.add(new IgnoreRule(
                    scope.get(i),
                    ignoreType.get(i),
                    isActive.get(i),
                    scopeRule.get(i),
                    isRegEx.get(i),
                    strictness.get(i),
                    ignoreRule.get(i)
            ));
        }
    }

    @Override
    public void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {
        client.setIgnoreListManager(this);
    }

    @Override
    public void update(IgnoreListManager from) {

    }

    @Override
    public void update(@NonNull Map<String, QVariant> from) {
        update(IgnoreListManagerSerializer.get().fromDatastream(from));
    }

    public boolean matches(Message message) {
        return false;
    }

    public static class IgnoreRule {
        private final Scope scope;
        private final Type ignoreType;
        private final boolean isActive;
        private final String scopeRule;
        private final boolean isRegEx;
        private final Strictness strictness;
        private final String ignoreRule;

        public IgnoreRule(Integer scope, Integer ignoreType, boolean isActive, String scopeRule, boolean isRegEx, Integer strictness, String ignoreRule) {
            this(
                    Scope.of(scope),
                    Type.of(ignoreType),
                    isActive,
                    scopeRule,
                    isRegEx,
                    Strictness.of(strictness),
                    ignoreRule
            );
        }

        public IgnoreRule(Scope scope, Type ignoreType, boolean isActive, String scopeRule, boolean isRegEx, Strictness strictness, String ignoreRule) {
            this.scope = scope;
            this.ignoreType = ignoreType;
            this.isActive = isActive;
            this.scopeRule = scopeRule;
            this.isRegEx = isRegEx;
            this.strictness = strictness;
            this.ignoreRule = ignoreRule;
        }

        public enum Strictness {
            INVALID(-1),
            UNMATCHED(0),
            SOFT(1),
            HARD(2);

            public final int id;

            Strictness(int id) {
                this.id = id;
            }

            @NonNull
            public static Strictness of(int id) {
                switch (id) {
                    case 0:
                        return UNMATCHED;
                    case 1:
                        return SOFT;
                    case 2:
                        return HARD;
                    default:
                        return INVALID;
                }
            }
        }

        public enum Type {
            INVALID(-1),
            SENDER_IGNORE(0),
            MESSAGE_IGNORE(1),
            CTCP_IGNORE(2);

            public final int id;

            Type(int id) {
                this.id = id;
            }

            @NonNull
            public static Type of(int id) {
                switch (id) {
                    case 0:
                        return SENDER_IGNORE;
                    case 1:
                        return MESSAGE_IGNORE;
                    case 2:
                        return CTCP_IGNORE;
                    default:
                        return INVALID;
                }
            }
        }

        public enum Scope {
            INVALID(-1),
            GLOBAL_SCOPE(0),
            NETWORK_SCOPE(1),
            CHANNEL_SCOPE(2);

            public final int id;

            Scope(int id) {
                this.id = id;
            }

            @NonNull
            public static Scope of(int id) {
                switch (id) {
                    case 0:
                        return GLOBAL_SCOPE;
                    case 1:
                        return NETWORK_SCOPE;
                    case 2:
                        return CHANNEL_SCOPE;
                    default:
                        return INVALID;
                }
            }
        }
    }
}
