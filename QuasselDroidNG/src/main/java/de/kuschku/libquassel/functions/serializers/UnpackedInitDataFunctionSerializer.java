package de.kuschku.libquassel.functions.serializers;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.PackedInitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.*;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class UnpackedInitDataFunctionSerializer implements FunctionSerializer<PackedInitDataFunction> {
    @NonNull
    private static final UnpackedInitDataFunctionSerializer serializer = new UnpackedInitDataFunctionSerializer();

    private UnpackedInitDataFunctionSerializer() {
    }

    @NonNull
    public static UnpackedInitDataFunctionSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public List serialize(@NonNull final PackedInitDataFunction data) {
        final List func = new ArrayList<>();
        func.add(FunctionType.INITDATA.id);
        func.add(data.className);
        func.add(data.objectName);
        func.add(data.getData());
        return func;
    }

    @NonNull
    @Override
    public PackedInitDataFunction deserialize(@NonNull final List packedFunc) {
        assertTrue(packedFunc.size() >= 3);

        return new PackedInitDataFunction(
                (String) packedFunc.remove(0),
                (String) packedFunc.remove(0),
                (Map<String, QVariant>) packedFunc.remove(0)
        );
    }
}
