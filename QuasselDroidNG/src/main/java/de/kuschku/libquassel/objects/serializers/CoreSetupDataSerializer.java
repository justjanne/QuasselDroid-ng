package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.CoreSetupData;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.*;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class CoreSetupDataSerializer implements ObjectSerializer<CoreSetupData> {
    @NonNull
    private static final CoreSetupDataSerializer serializer = new CoreSetupDataSerializer();

    private CoreSetupDataSerializer() {
    }

    @NonNull
    public static CoreSetupDataSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull final CoreSetupData data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        assertNotNull(map.data);

        map.data.put("SetupData", SetupDataInitializer.get().toVariantMap(data.SetupData));
        return map;
    }

    @NonNull
    @Override
    public CoreSetupData fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public CoreSetupData fromLegacy(@NonNull Map<String, QVariant> map) {
        return new CoreSetupData(
                SetupDataInitializer.get().fromLegacy((Map<String, QVariant>) map.get("SetupData").data)
        );
    }

    @Override
    public CoreSetupData from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
