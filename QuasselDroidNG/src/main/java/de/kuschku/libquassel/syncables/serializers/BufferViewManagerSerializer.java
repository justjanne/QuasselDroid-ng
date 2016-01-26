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
import de.kuschku.libquassel.syncables.types.BufferViewManager;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class BufferViewManagerSerializer implements ObjectSerializer<BufferViewManager> {
    @NonNull
    private static final BufferViewManagerSerializer serializer = new BufferViewManagerSerializer();
    private BufferViewManagerSerializer() {}
    @NonNull
    public static BufferViewManagerSerializer get(){
        return serializer;
    }

    @Nullable
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull BufferViewManager data) {
        return null;
    }

    @NonNull
    @Override
    public BufferViewManager fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public BufferViewManager fromLegacy(@NonNull Map<String, QVariant> map) {
        return new BufferViewManager(
                (List<Integer>) map.get("BufferViewIds").data
        );
    }

    @Override
    public BufferViewManager from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
