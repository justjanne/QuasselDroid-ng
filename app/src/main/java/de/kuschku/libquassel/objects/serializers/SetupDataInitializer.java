package de.kuschku.libquassel.objects.serializers;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.SetupData;
import de.kuschku.libquassel.primitives.types.QVariant;

public class SetupDataInitializer implements ObjectSerializer<SetupData> {
    private static final SetupDataInitializer serializer = new SetupDataInitializer();

    private SetupDataInitializer() {
    }

    public static SetupDataInitializer get() {
        return serializer;
    }

    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(final SetupData data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        map.data.put("AdminPasswd", new QVariant<>(data.AdminPasswd));
        map.data.put("AdminUser", new QVariant<>(data.AdminUser));
        map.data.put("Backend", new QVariant<>(data.Backend));
        map.data.put("ConnectionProperties", new QVariant<>(data.ConnectionProperties));
        return map;
    }

    @Override
    public SetupData fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public SetupData fromLegacy(Map<String, QVariant> map) {
        return new SetupData(
                (String) map.get("AdminPasswd").data,
                (String) map.get("AdminUser").data,
                (String) map.get("Backend").data,
                (Map<String, QVariant>) map.get("ConnectionProperties").data
        );
    }

    @Override
    public SetupData from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
