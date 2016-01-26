package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.CoreSetupReject;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class CoreSetupRejectSerializer implements ObjectSerializer<CoreSetupReject> {
    @NonNull
    private static final CoreSetupRejectSerializer serializer = new CoreSetupRejectSerializer();

    private CoreSetupRejectSerializer() {
    }

    @NonNull
    public static CoreSetupRejectSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull final CoreSetupReject data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        assertNotNull(map.data);

        map.data.put("Error", new QVariant<>(data.Error));
        return map;
    }

    @NonNull
    @Override
    public CoreSetupReject fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public CoreSetupReject fromLegacy(@NonNull Map<String, QVariant> map) {
        return new CoreSetupReject(
                (String) map.get("Error").data
        );
    }

    @Override
    public CoreSetupReject from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
