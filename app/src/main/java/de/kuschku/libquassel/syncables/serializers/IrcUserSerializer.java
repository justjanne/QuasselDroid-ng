package de.kuschku.libquassel.syncables.serializers;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.syncables.types.IrcUser;
import de.kuschku.libquassel.primitives.types.QVariant;

public class IrcUserSerializer implements ObjectSerializer<IrcUser> {
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(IrcUser data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<Map<String, QVariant>>(new HashMap<String, QVariant>());
        map.data.put("server", new QVariant<>(data.server));
        map.data.put("ircOperator", new QVariant<>(data.ircOperator));
        map.data.put("away", new QVariant<>(data.away));
        map.data.put("lastAwayMessage", new QVariant<>(data.lastAwayMessage));
        map.data.put("idleTime", new QVariant<>(data.idleTime));
        map.data.put("whoisServiceReply", new QVariant<>(data.whoisServiceReply));
        map.data.put("suserHost", new QVariant<>(data.suserHost));
        map.data.put("nick", new QVariant<>(data.nick));
        map.data.put("realName", new QVariant<>(data.realName));
        map.data.put("awayMessage", new QVariant<>(data.awayMessage));
        map.data.put("loginTime", new QVariant<>(data.loginTime));
        map.data.put("encrypted", new QVariant<>(data.encrypted));
        map.data.put("channels", new QVariant<>(data.channels));
        map.data.put("host", new QVariant<>(data.host));
        map.data.put("userModes", new QVariant<>(data.userModes));
        map.data.put("user", new QVariant<>(data.user));
        return map;
    }

    @Override
    public IrcUser fromDatastream(Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @Override
    public IrcUser fromLegacy(Map<String, QVariant> map) {
        return new IrcUser(
                (String) map.get("server").data,
                (String) map.get("ircOperator").data,
                (boolean) map.get("away").data,
                (int) map.get("lastAwayMessage").data,
                (DateTime) map.get("idleTime").data,
                (String) map.get("whoisServiceReply").data,
                (String) map.get("suserHost").data,
                (String) map.get("nick").data,
                (String) map.get("realName").data,
                (String) map.get("awayMessage").data,
                (DateTime) map.get("loginTime").data,
                (boolean) map.get("encrypted").data,
                (List<String>) map.get("channels").data,
                (String) map.get("host").data,
                (String) map.get("userModes").data,
                (String) map.get("user").data
        );
    }

    @Override
    public IrcUser from(SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
