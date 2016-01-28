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
import de.kuschku.libquassel.syncables.serializers.BufferSyncerSerializer;
import de.kuschku.libquassel.syncables.serializers.IgnoreListManagerSerializer;

import static de.kuschku.util.AndroidAssert.assertEquals;

public class IgnoreListManager extends SyncableObject<IgnoreListManager> {
    List<IgnoreRule> ignoreRules = new ArrayList<>();

    public IgnoreListManager(List<Integer> scope, List<Integer> ignoreType,
                             List<Boolean> isActive, List<String> scopeRule, List<Boolean> isRegEx,
                             List<Integer> strictness, List<String> ignoreRule) {
        assertEquals(scope.size(),ignoreType.size(), isActive.size(),scopeRule.size(), isRegEx.size(), strictness.size(), ignoreRule.size());

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
    public void update(Map<String, QVariant> from) {
        update(IgnoreListManagerSerializer.get().fromDatastream(from));
    }

    public boolean matches(Message message) {
        return false;
    }

    public static class IgnoreRule {
        private Scope scope;
        private Type ignoreType;
        private boolean isActive;
        private String scopeRule;
        private boolean isRegEx;
        private Strictness strictness;
        private String ignoreRule;

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
            public static Strictness of(int id) {
                switch (id) {
                    case 0: return UNMATCHED;
                    case 1: return SOFT;
                    case 2: return HARD;
                    default: return INVALID;
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
            public static Type of(int id) {
                switch (id) {
                    case 0: return SENDER_IGNORE;
                    case 1: return MESSAGE_IGNORE;
                    case 2: return CTCP_IGNORE;
                    default: return INVALID;
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
            public static Scope of(int id) {
                switch (id) {
                    case 0: return GLOBAL_SCOPE;
                    case 1: return NETWORK_SCOPE;
                    case 2: return CHANNEL_SCOPE;
                    default: return INVALID;
                }
            }
        }
    }
}
