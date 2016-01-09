package de.kuschku.libquassel.syncables.serializers;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.objects.serializers.StringObjectMapSerializer;
import de.kuschku.libquassel.syncables.types.IrcChannel;
import de.kuschku.libquassel.primitives.types.QVariant;

public class IrcChannelSerializer implements ObjectSerializer<IrcChannel> {
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(IrcChannel data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<Map<String, QVariant>>(new HashMap<String, QVariant>());
        map.data.put("name", new QVariant<>(data.name));
        map.data.put("topic", new QVariant<>(data.topic));
        map.data.put("password", new QVariant<>(data.password));
        map.data.put("UserModes", new StringObjectMapSerializer<String>().toVariantMap(data.UserModes));
        map.data.put("ChanModes", new QVariant<>(data.ChanModes));
        map.data.put("encrypted", new QVariant<>(data.encrypted));
        return map;
    }

    @Override
    public IrcChannel fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public IrcChannel fromLegacy(Map<String, QVariant> map) {
        return new IrcChannel(
                (String) map.get("name").data,
                (String) map.get("topic").data,
                (String) map.get("password").data,
                new StringObjectMapSerializer<String>().fromLegacy(((QVariant<Map<String, QVariant>>) map.get("UserModes")).data),
                (Map<String, Object>) map.get("ChanModes").data,
                (boolean) map.get("encrypted").data
        );
    }

    @Override
    public IrcChannel from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
