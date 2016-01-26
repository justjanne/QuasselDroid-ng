package de.kuschku.libquassel.syncables.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.Identity;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class IdentitySerializer implements ObjectSerializer<Identity> {
    @NonNull
    private static final IdentitySerializer serializer = new IdentitySerializer();
    private IdentitySerializer() {}
    @NonNull
    public static IdentitySerializer get(){
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull Identity data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        map.data.put("identityName", new QVariant(data.getIdentityName()));
        map.data.put("nicks", new QVariant(data.getNicks()));
        map.data.put("ident", new QVariant(data.getIdent()));
        map.data.put("realName", new QVariant(data.getRealName()));
        map.data.put("identityId", new QVariant(data.getIdentityId()));
        map.data.put("autoAwayEnabled", new QVariant(data.isAutoAwayEnabled()));
        map.data.put("autoAwayReasonEnabled", new QVariant(data.isAutoAwayReasonEnabled()));
        map.data.put("autoAwayTime", new QVariant(data.getAutoAwayTime()));
        map.data.put("awayNickEnabled", new QVariant(data.isAwayNickEnabled()));
        map.data.put("awayReasonEnabled", new QVariant(data.isAwayReasonEnabled()));
        map.data.put("detachAwayEnabled", new QVariant(data.isDetachAwayEnabled()));
        map.data.put("detachAwayReasonEnabled", new QVariant(data.isDetachAwayReasonEnabled()));
        map.data.put("awayReason", new QVariant(data.getAwayReason()));
        map.data.put("autoAwayReason", new QVariant(data.getAutoAwayReason()));
        map.data.put("detachAwayReason", new QVariant(data.getDetachAwayReason()));
        map.data.put("partReason", new QVariant(data.getPartReason()));
        map.data.put("quitReason", new QVariant(data.getQuitReason()));
        map.data.put("awayNick", new QVariant(data.getAwayNick()));
        map.data.put("kickReason", new QVariant(data.getKickReason()));
        return map;
    }

    @NonNull
    @Override
    public Identity fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public Identity fromLegacy(@NonNull Map<String, QVariant> map) {
        return new Identity(
                (String) map.get("identityName").data,
                (List<String>) map.get("nicks").data,
                (String) map.get("ident").data,
                (String) map.get("realName").data,
                (int) map.get("identityId").data,
                (boolean) map.get("autoAwayEnabled").data,
                (boolean) map.get("autoAwayReasonEnabled").data,
                (int) map.get("autoAwayTime").data,
                (boolean) map.get("awayNickEnabled").data,
                (boolean) map.get("awayReasonEnabled").data,
                (boolean) map.get("detachAwayEnabled").data,
                (boolean) map.get("detachAwayReasonEnabled").data,
                (String) map.get("awayReason").data,
                (String) map.get("autoAwayReason").data,
                (String) map.get("detachAwayReason").data,
                (String) map.get("partReason").data,
                (String) map.get("quitReason").data,
                (String) map.get("awayNick").data,
                (String) map.get("kickReason").data
        );
    }

    @Override
    public Identity from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
