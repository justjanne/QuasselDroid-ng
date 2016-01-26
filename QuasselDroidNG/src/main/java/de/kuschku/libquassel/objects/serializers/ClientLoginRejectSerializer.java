package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.ClientLoginReject;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.*;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class ClientLoginRejectSerializer implements ObjectSerializer<ClientLoginReject> {
    @NonNull
    private static final ClientLoginRejectSerializer serializer = new ClientLoginRejectSerializer();

    private ClientLoginRejectSerializer() {
    }

    @NonNull
    public static ClientLoginRejectSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull final ClientLoginReject data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        assertNotNull(map.data);

        map.data.put("Error", new QVariant<>(data.Error));
        return map;
    }

    @NonNull
    @Override
    public ClientLoginReject fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public ClientLoginReject fromLegacy(@NonNull Map<String, QVariant> map) {
        return new ClientLoginReject(
                (String) map.get("Error").data
        );
    }

    @Override
    public ClientLoginReject from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
