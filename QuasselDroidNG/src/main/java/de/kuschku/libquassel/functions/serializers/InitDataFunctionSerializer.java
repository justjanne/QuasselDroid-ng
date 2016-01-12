package de.kuschku.libquassel.functions.serializers;

import java.util.List;

import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.PackedInitDataFunction;
import de.kuschku.libquassel.functions.types.UnpackedInitDataFunction;

public class InitDataFunctionSerializer implements FunctionSerializer<InitDataFunction> {
    private static final InitDataFunctionSerializer serializer = new InitDataFunctionSerializer();

    private InitDataFunctionSerializer() {
    }

    public static InitDataFunctionSerializer get() {
        return serializer;
    }

    @Override
    public List serialize(final InitDataFunction data) {
        if (data instanceof UnpackedInitDataFunction) {
            return PackedInitDataFunctionSerializer.get().serialize((UnpackedInitDataFunction) data);
        } else if (data instanceof PackedInitDataFunction) {
            return UnpackedInitDataFunctionSerializer.get().serialize((PackedInitDataFunction) data);
        } else {
            throw new IllegalArgumentException("Can not be applied to these arguments");
        }
    }

    @Override
    public InitDataFunction deserialize(final List packedFunc) {
        throw new IllegalArgumentException("Can not be applied to these arguments");
    }
}
