package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.ClientInit;
import de.kuschku.libquassel.primitives.types.QVariant;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class ClientInitSerializer implements ObjectSerializer<ClientInit> {
    @NonNull
    private static final ClientInitSerializer serializer = new ClientInitSerializer();

    private ClientInitSerializer() {
    }

    @NonNull
    public static ClientInitSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull final ClientInit data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        map.data.put("ClientDate", new QVariant<>(data.ClientDate));
        map.data.put("UseSsl", new QVariant<>(data.UseSsl));
        map.data.put("ClientVersion", new QVariant<>(data.ClientVersion));
        map.data.put("UseCompression", new QVariant<>(data.UseCompression));
        map.data.put("ProtocolVersion", new QVariant<>(data.ProtocolVersion));
        return map;
    }

    @NonNull
    @Override
    public ClientInit fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public ClientInit fromLegacy(@NonNull final Map<String, QVariant> map) {
        return new ClientInit(
                ((QVariant<String>) map.get("ClientDate")).data,
                ((QVariant<Boolean>) map.get("UseSsl")).data,
                ((QVariant<String>) map.get("ClientVersion")).data,
                ((QVariant<Boolean>) map.get("UseCompression")).data,
                ((QVariant<Integer>) map.get("ProtocolVersion")).data
        );
    }

    @Override
    public ClientInit from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
