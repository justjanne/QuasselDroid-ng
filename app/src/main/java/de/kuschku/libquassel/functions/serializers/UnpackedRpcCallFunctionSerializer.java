package de.kuschku.libquassel.functions.serializers;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.RpcCallFunction;
import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.types.QVariant;

public class UnpackedRpcCallFunctionSerializer implements FunctionSerializer<RpcCallFunction> {
    private static final UnpackedRpcCallFunctionSerializer serializer = new UnpackedRpcCallFunctionSerializer();

    private UnpackedRpcCallFunctionSerializer() {
    }

    public static UnpackedRpcCallFunctionSerializer get() {
        return serializer;
    }

    @Override
    public List serialize(final RpcCallFunction data) {
        final List func = new ArrayList<>();
        func.add(new QVariant<>(FunctionType.RPCCALL.id));
        func.add(new QVariant<>(QMetaType.Type.QByteArray, data.functionName));
        func.addAll(data.params);
        return func;
    }

    @Override
    public RpcCallFunction deserialize(final List packedFunc) {
        return new RpcCallFunction(
                (String) packedFunc.remove(0),
                packedFunc
        );
    }
}
