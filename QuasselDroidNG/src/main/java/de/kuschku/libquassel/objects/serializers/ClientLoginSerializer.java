package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.ClientLogin;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.*;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class ClientLoginSerializer implements ObjectSerializer<ClientLogin> {
    @NonNull
    private static final ClientLoginSerializer serializer = new ClientLoginSerializer();

    private ClientLoginSerializer() {
    }

    @NonNull
    public static ClientLoginSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull final ClientLogin data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        assertNotNull(map.data);

        map.data.put("User", new QVariant<>(data.User));
        map.data.put("Password", new QVariant<>(data.Password));
        return map;
    }

    @NonNull
    @Override
    public ClientLogin fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public ClientLogin fromLegacy(@NonNull Map<String, QVariant> map) {
        return new ClientLogin(
                (String) map.get("User").data,
                (String) map.get("Password").data
        );
    }

    @Override
    public ClientLogin from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
