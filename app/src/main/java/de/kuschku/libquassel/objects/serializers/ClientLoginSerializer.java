package de.kuschku.libquassel.objects.serializers;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.ClientLogin;
import de.kuschku.libquassel.primitives.types.QVariant;

public class ClientLoginSerializer implements ObjectSerializer<ClientLogin> {
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(final ClientLogin data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<Map<String, QVariant>>(new HashMap<String, QVariant>());
        map.data.put("User", new QVariant<>(data.User));
        map.data.put("Password", new QVariant<>(data.Password));
        return map;
    }

    @Override
    public ClientLogin fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public ClientLogin fromLegacy(Map<String, QVariant> map) {
        return new ClientLogin(
                (String) map.get("User").data,
                (String) map.get("Password").data
        );
    }

    @Override
    public ClientLogin from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
