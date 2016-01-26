package de.kuschku.libquassel.functions.serializers;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.protocols.DatastreamPeer;

import static de.kuschku.util.AndroidAssert.*;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class PackedSyncFunctionSerializer<T> implements FunctionSerializer<SyncFunction<T>> {
    @NonNull
    private static final PackedSyncFunctionSerializer serializer = new PackedSyncFunctionSerializer();

    private PackedSyncFunctionSerializer() {
    }

    @NonNull
    public static PackedSyncFunctionSerializer get() {
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
                ((QVariant<String>) packedFunc.remove(0)).data,
                ((QVariant<String>) packedFunc.remove(0)).data,
                ((QVariant<String>) packedFunc.remove(0)).data,
                DatastreamPeer.unboxList(packedFunc)
        );
    }
}
