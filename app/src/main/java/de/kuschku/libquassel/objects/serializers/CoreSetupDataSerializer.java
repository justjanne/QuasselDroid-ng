package de.kuschku.libquassel.objects.serializers;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.CoreSetupData;
import de.kuschku.libquassel.primitives.types.QVariant;

public class CoreSetupDataSerializer implements ObjectSerializer<CoreSetupData> {
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(final CoreSetupData data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<Map<String, QVariant>>(new HashMap<String, QVariant>());
        map.data.put("SetupData", new SetupDataInitializer().toVariantMap(data.SetupData));
        return map;
    }

    @Override
    public CoreSetupData fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public CoreSetupData fromLegacy(Map<String, QVariant> map) {
        return new CoreSetupData(
                new SetupDataInitializer().fromLegacy((Map<String, QVariant>) map.get("SetupData").data)
        );
    }

    @Override
    public CoreSetupData from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
