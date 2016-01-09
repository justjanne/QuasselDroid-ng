package de.kuschku.libquassel.functions.serializers;

import java.util.List;

import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.PackedInitDataFunction;
import de.kuschku.libquassel.functions.types.UnpackedInitDataFunction;

public class InitDataFunctionSerializer implements FunctionSerializer<InitDataFunction> {
    @Override
    public List serialize(final InitDataFunction data) {
        if (data instanceof UnpackedInitDataFunction) {
            return new PackedInitDataFunctionSerializer().serialize((UnpackedInitDataFunction) data);
        } else if (data instanceof PackedInitDataFunction) {
            return new UnpackedInitDataFunctionSerializer().serialize((PackedInitDataFunction) data);
        } else {
            throw new IllegalArgumentException("Can not be applied to these arguments");
        }
    }

    @Override
    public InitDataFunction deserialize(final List packedFunc) {
        throw new IllegalArgumentException("Can not be applied to these arguments");
    }
}
