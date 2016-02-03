/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import de.kuschku.libquassel.syncables.types.impl.Identity;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class IdentitySerializer implements ObjectSerializer<Identity> {
    @NonNull
    private static final IdentitySerializer serializer = new IdentitySerializer();

    private IdentitySerializer() {
    }

    @NonNull
    public static IdentitySerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull Identity data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        map.data.put("identityName", new QVariant(data.identityName()));
        map.data.put("nicks", new QVariant(data.nicks()));
        map.data.put("ident", new QVariant(data.ident()));
        map.data.put("realName", new QVariant(data.realName()));
        map.data.put("identityId", new QVariant(data.id()));
        map.data.put("autoAwayEnabled", new QVariant(data.autoAwayEnabled()));
        map.data.put("autoAwayReasonEnabled", new QVariant(data.autoAwayReasonEnabled()));
        map.data.put("autoAwayTime", new QVariant(data.autoAwayTime()));
        map.data.put("awayNickEnabled", new QVariant(data.awayNickEnabled()));
        map.data.put("awayReasonEnabled", new QVariant(data.awayReasonEnabled()));
        map.data.put("detachAwayEnabled", new QVariant(data.detachAwayEnabled()));
        map.data.put("detachAwayReasonEnabled", new QVariant(data.detachAwayReasonEnabled()));
        map.data.put("awayReason", new QVariant(data.awayReason()));
        map.data.put("autoAwayReason", new QVariant(data.autoAwayReason()));
        map.data.put("detachAwayReason", new QVariant(data.detachAwayReason()));
        map.data.put("partReason", new QVariant(data.partReason()));
        map.data.put("quitReason", new QVariant(data.quitReason()));
        map.data.put("awayNick", new QVariant(data.awayNick()));
        map.data.put("kickReason", new QVariant(data.kickReason()));
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
                (int) map.get("identityId").data,
                (String) map.get("identityName").data,
                (String) map.get("realName").data,
                (List<String>) map.get("nicks").data,
                (String) map.get("awayNick").data,
                (boolean) map.get("awayNickEnabled").data,
                (String) map.get("awayReason").data,
                (boolean) map.get("awayReasonEnabled").data,
                (boolean) map.get("autoAwayEnabled").data,
                (int) map.get("autoAwayTime").data,
                (String) map.get("autoAwayReason").data,
                (boolean) map.get("autoAwayReasonEnabled").data,
                (boolean) map.get("detachAwayEnabled").data,
                (String) map.get("detachAwayReason").data,
                (boolean) map.get("detachAwayReasonEnabled").data,
                (String) map.get("ident").data,
                (String) map.get("kickReason").data,
                (String) map.get("partReason").data,
                (String) map.get("quitReason").data
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
