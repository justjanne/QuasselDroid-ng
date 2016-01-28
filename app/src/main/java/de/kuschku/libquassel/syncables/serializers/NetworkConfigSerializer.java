package de.kuschku.libquassel.syncables.serializers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.NetworkConfig;
import de.kuschku.libquassel.syncables.types.SyncableObject;

public class NetworkConfigSerializer implements ObjectSerializer<NetworkConfig> {
    private static NetworkConfigSerializer serializer = new NetworkConfigSerializer();
    public static NetworkConfigSerializer get() {
        return serializer;
    }

    private NetworkConfigSerializer() {

    }

    @Nullable
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull NetworkConfig data) {
        // FIXME: IMPLEMENT
        throw new IllegalArgumentException();
    }

    @NonNull
    @Override
    public NetworkConfig fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public NetworkConfig fromLegacy(@NonNull Map<String, QVariant> map) {
        return new NetworkConfig(
                (int) map.get("autoWhoNickLimit").data,
                (int) map.get("autoWhoDelay").data,
                (boolean) map.get("autoWhoEnabled").data,
                (boolean) map.get("standardCtcp").data,
                (int) map.get("pingInterval").data,
                (int) map.get("autoWhoInterval").data,
                (int) map.get("maxPingCount").data,
                (boolean) map.get("pingTimeoutEnabled").data
        );
    }

    @Nullable
    @Override
    public NetworkConfig from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
