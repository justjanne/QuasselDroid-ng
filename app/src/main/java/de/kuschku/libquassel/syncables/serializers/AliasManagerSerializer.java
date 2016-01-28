package de.kuschku.libquassel.syncables.serializers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.AliasManager;

public class AliasManagerSerializer implements ObjectSerializer<AliasManager> {
    @NonNull
    private static final AliasManagerSerializer serializer = new AliasManagerSerializer();

    @NonNull
    public static AliasManagerSerializer get() {
        return serializer;
    }

    private AliasManagerSerializer() {

    }

    @Nullable
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull AliasManager data) {
        throw new IllegalArgumentException();
    }

    @NonNull
    @Override
    public AliasManager fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public AliasManager fromLegacy(@NonNull Map<String, QVariant> map) {
        Map<String, QVariant<List<String>>> aliases = (Map<String, QVariant<List<String>>>) map.get("Aliases").data;
        return new AliasManager(
                aliases.get("names").data,
                aliases.get("expansions").data
        );
    }

    @Nullable
    @Override
    public AliasManager from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
