package de.kuschku.libquassel.syncables.serializers;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.BufferViewManager;

public class BufferViewManagerSerializer implements ObjectSerializer<BufferViewManager> {
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(BufferViewManager data) {
        return null;
    }

    @Override
    public BufferViewManager fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public BufferViewManager fromLegacy(Map<String, QVariant> map) {
        return new BufferViewManager(
                (List<Integer>) map.get("BufferViewIds").data
        );
    }

    @Override
    public BufferViewManager from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
