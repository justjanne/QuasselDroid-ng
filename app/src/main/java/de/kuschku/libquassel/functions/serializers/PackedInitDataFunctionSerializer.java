package de.kuschku.libquassel.functions.serializers;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.UnpackedInitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.protocols.DatastreamPeer;

import static de.kuschku.util.AndroidAssert.assertTrue;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class PackedInitDataFunctionSerializer implements FunctionSerializer<UnpackedInitDataFunction> {
    @NonNull
    private static final PackedInitDataFunctionSerializer serializer = new PackedInitDataFunctionSerializer();

    private PackedInitDataFunctionSerializer() {
    }

    @NonNull
    public static PackedInitDataFunctionSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public List serialize(@NonNull final UnpackedInitDataFunction data) {
        final List func = new ArrayList<>();
        func.add(FunctionType.INITDATA.id);
        func.add(data.className);
        func.add(data.objectName);
        func.add(data.getData());
        return func;
    }

    @NonNull
    @Override
    public UnpackedInitDataFunction deserialize(@NonNull final List packedFunc) {
        assertTrue(packedFunc.size() >= 2);

        return new UnpackedInitDataFunction(
                ((QVariant<String>) packedFunc.remove(0)).data,
                ((QVariant<String>) packedFunc.remove(0)).data,
                DatastreamPeer.listToMap(packedFunc)
        );
    }
}
