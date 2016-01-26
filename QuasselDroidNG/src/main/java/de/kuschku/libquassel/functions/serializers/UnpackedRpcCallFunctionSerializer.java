package de.kuschku.libquassel.functions.serializers;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.RpcCallFunction;
import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertTrue;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class UnpackedRpcCallFunctionSerializer implements FunctionSerializer<RpcCallFunction> {
    @NonNull
    private static final UnpackedRpcCallFunctionSerializer serializer = new UnpackedRpcCallFunctionSerializer();

    private UnpackedRpcCallFunctionSerializer() {
    }

    @NonNull
    public static UnpackedRpcCallFunctionSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public List serialize(@NonNull final RpcCallFunction data) {
        final List func = new ArrayList<>();
        func.add(new QVariant<>(FunctionType.RPCCALL.id));
        func.add(new QVariant<>(QMetaType.Type.QByteArray, data.functionName));
        func.addAll(data.params);
        return func;
    }

    @NonNull
    @Override
    public RpcCallFunction deserialize(@NonNull final List packedFunc) {
        assertTrue(packedFunc.size() >= 1);

        return new RpcCallFunction(
                (String) packedFunc.remove(0),
                packedFunc
        );
    }
}
