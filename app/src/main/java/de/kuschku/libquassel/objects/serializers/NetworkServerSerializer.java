package de.kuschku.libquassel.objects.serializers;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.libquassel.primitives.types.QVariant;

public class NetworkServerSerializer implements ObjectSerializer<NetworkServer> {
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(NetworkServer data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<Map<String, QVariant>>(new HashMap<String, QVariant>());
        map.data.put("UseSSL", new QVariant<>(data.UseSSL));
        map.data.put("sslVersion", new QVariant<>(data.sslVersion));
        map.data.put("Host", new QVariant<>(data.Host));
        map.data.put("Port", new QVariant<>(data.Port));
        map.data.put("Password", new QVariant<>(data.Password));
        map.data.put("UseProxy", new QVariant<>(data.UseProxy));
        map.data.put("ProxyType", new QVariant<>(data.ProxyType));
        map.data.put("ProxyHost", new QVariant<>(data.ProxyHost));
        map.data.put("ProxyPort", new QVariant<>(data.ProxyPort));
        map.data.put("ProxyUser", new QVariant<>(data.ProxyUser));
        map.data.put("ProxyPass", new QVariant<>(data.ProxyPass));
        return map;
    }

    @Override
    public NetworkServer fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public NetworkServer fromLegacy(Map<String, QVariant> map) {
        return new NetworkServer(
                (boolean) map.get("UseSSL").data,
                (int) map.get("sslVersion").data,
                (String) map.get("Host").data,
                (int) map.get("Port").data,
                (String) map.get("Password").data,
                (boolean) map.get("UseProxy").data,
                (int) map.get("ProxyType").data,
                (String) map.get("ProxyHost").data,
                (int) map.get("ProxyPort").data,
                (String) map.get("ProxyUser").data,
                (String) map.get("ProxyPass").data
        );
    }

    @Override
    public NetworkServer from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
