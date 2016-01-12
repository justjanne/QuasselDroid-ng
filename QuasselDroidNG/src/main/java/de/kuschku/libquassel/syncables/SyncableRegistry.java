package de.kuschku.libquassel.syncables;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.exceptions.UnknownTypeException;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.syncables.serializers.BufferSyncerSerializer;
import de.kuschku.libquassel.syncables.serializers.BufferViewConfigSerializer;
import de.kuschku.libquassel.syncables.serializers.BufferViewManagerSerializer;
import de.kuschku.libquassel.syncables.serializers.IdentitySerializer;
import de.kuschku.libquassel.syncables.serializers.IrcChannelSerializer;
import de.kuschku.libquassel.syncables.serializers.IrcUserSerializer;
import de.kuschku.libquassel.syncables.serializers.NetworkSerializer;
import de.kuschku.libquassel.syncables.types.SyncableObject;

public class SyncableRegistry {
    private static final Map<String, ObjectSerializer<? extends SyncableObject>> map = new HashMap<>();

    static {
        map.put("BufferSyncer", BufferSyncerSerializer.get());
        map.put("BufferViewConfig", BufferViewConfigSerializer.get());
        map.put("BufferViewManager", BufferViewManagerSerializer.get());
        map.put("Identity", IdentitySerializer.get());
        map.put("IrcChannel", IrcChannelSerializer.get());
        map.put("IrcUser", IrcUserSerializer.get());
        map.put("Network", NetworkSerializer.get());
    }

    private SyncableRegistry() {

    }

    public static SyncableObject from(InitDataFunction function) throws UnknownTypeException {
        ObjectSerializer<? extends SyncableObject> serializer = map.get(function.className);
        if (serializer == null) throw new UnknownTypeException(function.className);
        return serializer.from(function);
    }
}
