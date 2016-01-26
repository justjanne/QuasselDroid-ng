package de.kuschku.libquassel.syncables.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.objects.serializers.StringObjectMapSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.IrcChannel;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class IrcChannelSerializer implements ObjectSerializer<IrcChannel> {
    @NonNull
    private static final IrcChannelSerializer serializer = new IrcChannelSerializer();

    private IrcChannelSerializer() {
    }

    @NonNull
    public static IrcChannelSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull IrcChannel data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        map.data.put("name", new QVariant<>(data.name));
        map.data.put("topic", new QVariant<>(data.topic));
        map.data.put("password", new QVariant<>(data.password));
        map.data.put("UserModes", StringObjectMapSerializer.<String>get().toVariantMap(data.UserModes));
        map.data.put("ChanModes", new QVariant<>(data.ChanModes));
        map.data.put("encrypted", new QVariant<>(data.encrypted));
        return map;
    }

    @NonNull
    @Override
    public IrcChannel fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public IrcChannel fromLegacy(@NonNull Map<String, QVariant> map) {
        return new IrcChannel(
                (String) map.get("name").data,
                (String) map.get("topic").data,
                (String) map.get("password").data,
                StringObjectMapSerializer.<String>get().fromLegacy(((QVariant<Map<String, QVariant>>) map.get("UserModes")).data),
                StringObjectMapSerializer.get().fromLegacy((Map<String, QVariant>) map.get("ChanModes").data),
                (boolean) map.get("encrypted").data
        );
    }

    @Override
    public IrcChannel from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
