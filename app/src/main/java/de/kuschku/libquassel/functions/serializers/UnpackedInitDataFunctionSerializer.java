package de.kuschku.libquassel.functions.serializers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.PackedInitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;

public class UnpackedInitDataFunctionSerializer implements FunctionSerializer<PackedInitDataFunction> {
    @Override
    public List serialize(final PackedInitDataFunction data) {
        final List func = new ArrayList<>();
        func.add(FunctionType.INITDATA.id);
        func.add(data.className);
        func.add(data.objectName);
        func.add(data.getData());
        return func;
    }

    @Override
    public PackedInitDataFunction deserialize(final List packedFunc) {
        return new PackedInitDataFunction(
                (String) packedFunc.remove(0),
                (String) packedFunc.remove(0),
                (Map<String, QVariant>) packedFunc.remove(0)
        );
    }
}
