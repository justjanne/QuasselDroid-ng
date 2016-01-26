package de.kuschku.libquassel.functions.serializers;

import android.support.annotation.NonNull;

import com.google.common.collect.Lists;

import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.libquassel.primitives.QMetaType.Type.QByteArray;
import static de.kuschku.util.AndroidAssert.assertNotNull;
import static de.kuschku.util.AndroidAssert.assertTrue;

@SuppressWarnings("unchecked")
public class InitRequestFunctionSerializer implements FunctionSerializer<InitRequestFunction> {
    @NonNull
    private static final InitRequestFunctionSerializer serializer = new InitRequestFunctionSerializer();

    private InitRequestFunctionSerializer() {
    }

    @NonNull
    public static InitRequestFunctionSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public List serialize(@NonNull final InitRequestFunction data) {
        return Lists.newArrayList(
                FunctionType.INITREQUEST.id,
                data.className,
                data.objectName
        );
    }

    // TODO: Add this for all such serializers
    @NonNull
    public List serializePacked(@NonNull final InitRequestFunction data) {
        return Lists.newArrayList(
                new QVariant<>(FunctionType.INITREQUEST.id),
                new QVariant<>(QByteArray, data.className),
                new QVariant<>(QByteArray, data.objectName)
        );
    }

    @NonNull
    @Override
    public InitRequestFunction deserialize(@NonNull final List packedFunc) {
        assertTrue(packedFunc.size() >= 2);

        String className = (String) packedFunc.remove(0);
        String objectName = (String) packedFunc.remove(0);
        assertNotNull(className);

        return new InitRequestFunction(
                className,
                objectName
        );
    }
}
