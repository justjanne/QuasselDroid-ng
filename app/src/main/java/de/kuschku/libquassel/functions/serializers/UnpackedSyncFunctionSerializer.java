package de.kuschku.libquassel.functions.serializers;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertTrue;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class UnpackedSyncFunctionSerializer<T> implements FunctionSerializer<SyncFunction<T>> {
    @NonNull
    private static final UnpackedSyncFunctionSerializer serializer = new UnpackedSyncFunctionSerializer();

    private UnpackedSyncFunctionSerializer() {
    }

    @NonNull
    public static UnpackedSyncFunctionSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public List serialize(@NonNull final SyncFunction data) {
        final List func = new ArrayList<>();
        func.add(new QVariant<>(FunctionType.SYNC.id));
        func.add(new QVariant<>(QMetaType.Type.QByteArray, data.className));
        func.add(new QVariant<>(QMetaType.Type.QByteArray, data.objectName));
        func.add(new QVariant<>(QMetaType.Type.QByteArray, data.methodName));
        func.addAll(data.params);
        return func;
    }

    @NonNull
    @Override
    public SyncFunction<T> deserialize(@NonNull final List packedFunc) {
        assertTrue(packedFunc.size() >= 3);

        return new SyncFunction<>(
                (String) packedFunc.remove(0),
                (String) packedFunc.remove(0),
                (String) packedFunc.remove(0),
                packedFunc
        );
    }
}
