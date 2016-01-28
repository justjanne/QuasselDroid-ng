package de.kuschku.libquassel.syncables.serializers;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.IrcUser;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class IrcUserSerializer implements ObjectSerializer<IrcUser> {
    @NonNull
    private static final IrcUserSerializer serializer = new IrcUserSerializer();

    private IrcUserSerializer() {
    }

    @NonNull
    public static IrcUserSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull IrcUser data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        assertNotNull(map.data);

        map.data.put("server", new QVariant<>(data.getServer()));
        map.data.put("ircOperator", new QVariant<>(data.getIrcOperator()));
        map.data.put("away", new QVariant<>(data.isAway()));
        map.data.put("lastAwayMessage", new QVariant<>(data.getLastAwayMessage()));
        map.data.put("idleTime", new QVariant<>(data.getIdleTime()));
        map.data.put("whoisServiceReply", new QVariant<>(data.getWhoisServiceReply()));
        map.data.put("suserHost", new QVariant<>(data.getSuserHost()));
        map.data.put("nick", new QVariant<>(data.getNick()));
        map.data.put("realName", new QVariant<>(data.getRealName()));
        map.data.put("awayMessage", new QVariant<>(data.getAwayMessage()));
        map.data.put("loginTime", new QVariant<>(data.getLoginTime()));
        map.data.put("encrypted", new QVariant<>(data.isEncrypted()));
        map.data.put("channels", new QVariant<>(data.getChannels()));
        map.data.put("host", new QVariant<>(data.getHost()));
        map.data.put("userModes", new QVariant<>(data.getUserModes()));
        map.data.put("user", new QVariant<>(data.getUser()));
        return map;
    }

    @NonNull
    @Override
    public IrcUser fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public IrcUser fromLegacy(@NonNull Map<String, QVariant> map) {
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
    public IrcUser from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
