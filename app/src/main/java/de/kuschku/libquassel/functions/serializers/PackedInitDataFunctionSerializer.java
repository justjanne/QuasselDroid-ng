package de.kuschku.libquassel.functions.serializers;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.UnpackedInitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.protocols.DatastreamPeer;

public class PackedInitDataFunctionSerializer implements FunctionSerializer<UnpackedInitDataFunction> {
    @Override
    public List serialize(final UnpackedInitDataFunction data) {
        final List func = new ArrayList<>();
        func.add(FunctionType.INITDATA.id);
        func.add(data.className);
        func.add(data.objectName);
        func.add(data.getData());
        return func;
    }

    @Override
    public UnpackedInitDataFunction deserialize(final List packedFunc) {
        return new UnpackedInitDataFunction(
                ((QVariant<String>) packedFunc.remove(0)).data,
                ((QVariant<String>) packedFunc.remove(0)).data,
                DatastreamPeer.listToMap(packedFunc)
        );
    }
}
