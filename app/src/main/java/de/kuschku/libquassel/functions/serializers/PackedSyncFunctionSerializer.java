package de.kuschku.libquassel.functions.serializers;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.protocols.DatastreamPeer;

public class PackedSyncFunctionSerializer implements FunctionSerializer<SyncFunction> {
    @Override
    public List serialize(final SyncFunction data) {
        final List func = new ArrayList<>();
        func.add(new QVariant<>(FunctionType.SYNC.id));
        func.add(new QVariant<>(QMetaType.Type.QByteArray, data.className));
        func.add(new QVariant<>(QMetaType.Type.QByteArray, data.objectName));
        func.add(new QVariant<>(QMetaType.Type.QByteArray, data.methodName));
        func.addAll(data.params);
        return func;
    }

    @Override
    public SyncFunction deserialize(final List packedFunc) {
        return new SyncFunction(
                ((QVariant<String>) packedFunc.remove(0)).data,
                ((QVariant<String>) packedFunc.remove(0)).data,
                ((QVariant<String>) packedFunc.remove(0)).data,
                DatastreamPeer.unboxList(packedFunc)
        );
    }
}
