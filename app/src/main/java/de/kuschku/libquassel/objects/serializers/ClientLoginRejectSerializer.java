package de.kuschku.libquassel.objects.serializers;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.ClientLoginReject;
import de.kuschku.libquassel.primitives.types.QVariant;

public class ClientLoginRejectSerializer implements ObjectSerializer<ClientLoginReject> {
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(final ClientLoginReject data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<Map<String, QVariant>>(new HashMap<String, QVariant>());
        map.data.put("Error", new QVariant<>(data.Error));
        return map;
    }

    @Override
    public ClientLoginReject fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public ClientLoginReject fromLegacy(Map<String, QVariant> map) {
        return new ClientLoginReject(
                (String) map.get("Error").data
        );
    }

    @Override
    public ClientLoginReject from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
