package de.kuschku.libquassel.syncables.serializers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.BufferViewConfig;

public class BufferViewConfigSerializer implements ObjectSerializer<BufferViewConfig> {
    private static final BufferViewConfigSerializer serializer = new BufferViewConfigSerializer();
    private BufferViewConfigSerializer() {}
    public static BufferViewConfigSerializer get(){
        return serializer;
    }

    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(BufferViewConfig data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<Map<String, QVariant>>(new HashMap<>());
        map.data.put("bufferViewName", new QVariant<>(data.getBufferViewName()));
        map.data.put("TemporarilyRemovedBuffers", new QVariant<>(data.getTemporarilyRemovedBuffers()));
        map.data.put("hideInactiveNetworks", new QVariant<>(data.isHideInactiveNetworks()));
        map.data.put("BufferList", new QVariant<>(data.getBufferList()));
        map.data.put("allowedBufferTypes", new QVariant<>(data.getAllowedBufferTypes()));
        map.data.put("sortAlphabetically", new QVariant<>(data.isSortAlphabetically()));
        map.data.put("disableDecoration", new QVariant<>(data.isDisableDecoration()));
        map.data.put("addNewBuffersAutomatically", new QVariant<>(data.isAddNewBuffersAutomatically()));
        map.data.put("networkId", new QVariant<>(data.getNetworkId()));
        map.data.put("minimumActivity", new QVariant<>(data.getMinimumActivity()));
        map.data.put("hideInactiveBuffers", new QVariant<>(data.isHideInactiveBuffers()));
        map.data.put("RemovedBuffers", new QVariant<>(data.getRemovedBuffers()));
        return map;
    }

    @Override
    public BufferViewConfig fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public BufferViewConfig fromLegacy(Map<String, QVariant> map) {
        return new BufferViewConfig(
                (String) map.get("bufferViewName").data,
                (List<Integer>) map.get("TemporarilyRemovedBuffers").data,
                (boolean) map.get("hideInactiveNetworks").data,
                (List<Integer>) map.get("BufferList").data,
                (int) map.get("allowedBufferTypes").data,
                (boolean) map.get("sortAlphabetically").data,
                (boolean) map.get("disableDecoration").data,
                (boolean) map.get("addNewBuffersAutomatically").data,
                (int) map.get("networkId").data,
                (int) map.get("minimumActivity").data,
                (boolean) map.get("hideInactiveBuffers").data,
                (List<Integer>) map.get("RemovedBuffers").data
        );
    }

    @Override
    public BufferViewConfig from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
