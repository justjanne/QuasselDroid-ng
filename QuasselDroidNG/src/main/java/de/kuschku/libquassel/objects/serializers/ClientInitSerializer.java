package de.kuschku.libquassel.objects.serializers;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.ClientInit;
import de.kuschku.libquassel.primitives.types.QVariant;

public class ClientInitSerializer implements ObjectSerializer<ClientInit> {
    private static final ClientInitSerializer serializer = new ClientInitSerializer();

    private ClientInitSerializer() {
    }

    public static ClientInitSerializer get() {
        return serializer;
    }

    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(final ClientInit data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        map.data.put("ClientDate", new QVariant<>(data.ClientDate));
        map.data.put("UseSsl", new QVariant<>(data.UseSsl));
        map.data.put("ClientVersion", new QVariant<>(data.ClientVersion));
        map.data.put("UseCompression", new QVariant<>(data.UseCompression));
        map.data.put("ProtocolVersion", new QVariant<>(data.ProtocolVersion));
        return map;
    }

    @Override
    public ClientInit fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public ClientInit fromLegacy(final Map<String, QVariant> map) {
        return new ClientInit(
                ((QVariant<String>) map.get("ClientDate")).data,
                ((QVariant<Boolean>) map.get("UseSsl")).data,
                ((QVariant<String>) map.get("ClientVersion")).data,
                ((QVariant<Boolean>) map.get("UseCompression")).data,
                ((QVariant<Integer>) map.get("ProtocolVersion")).data
        );
    }

    @Override
    public ClientInit from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
