package de.kuschku.libquassel.syncables.types;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.AliasManagerSerializer;

public class AliasManager extends SyncableObject<AliasManager> {
    private List<String> names;
    private List<String> expansions;

    public AliasManager(List<String> names, List<String> expansions) {
        this.names = names;
        this.expansions = expansions;
    }

    @Override
    public void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {

    }

    @Override
    public void update(@NonNull AliasManager from) {
        names = from.names;
        expansions = from.expansions;
    }

    @Override
    public void update(@NonNull Map<String, QVariant> from) {
        update(AliasManagerSerializer.get().fromDatastream(from));
    }

    public List<String> getNames() {
        return names;
    }

    public List<String> getExpansions() {
        return expansions;
    }
}
