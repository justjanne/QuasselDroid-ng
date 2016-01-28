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
    public List<String> names;
    public List<String> expansions;

    public AliasManager(List<String> names, List<String> expansions) {
        this.names = names;
        this.expansions = expansions;
    }

    @Override
    public void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {

    }

    @Override
    public void update(AliasManager from) {
        names = from.names;
        expansions = from.expansions;
    }

    @Override
    public void update(Map<String, QVariant> from) {
        update(AliasManagerSerializer.get().fromDatastream(from));
    }
}
