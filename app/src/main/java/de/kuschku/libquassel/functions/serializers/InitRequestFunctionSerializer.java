package de.kuschku.libquassel.functions.serializers;

import com.google.common.collect.Lists;

import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.libquassel.primitives.QMetaType.Type.QByteArray;

public class InitRequestFunctionSerializer implements FunctionSerializer<InitRequestFunction> {
    private static final InitRequestFunctionSerializer serializer = new InitRequestFunctionSerializer();

    private InitRequestFunctionSerializer() {
    }

    public static InitRequestFunctionSerializer get() {
        return serializer;
    }

    @Override
    public List serialize(final InitRequestFunction data) {
        return Lists.newArrayList(
                FunctionType.INITREQUEST.id,
                data.className,
                data.objectName
        );
    }

    // TODO: Add this for all such serializers
    public List serializePacked(final InitRequestFunction data) {
        return Lists.newArrayList(
                new QVariant<>(FunctionType.INITREQUEST.id),
                new QVariant<>(QByteArray, data.className),
                new QVariant<>(QByteArray, data.objectName)
        );
    }

    @Override
    public InitRequestFunction deserialize(final List packedFunc) {
        return new InitRequestFunction(
                (String) packedFunc.remove(0),
                (String) packedFunc.remove(0)
        );
    }
}
