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

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.impl.IrcUser;

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

        map.data.put("server", new QVariant<>(data.server()));
        map.data.put("ircOperator", new QVariant<>(data.ircOperator()));
        map.data.put("away", new QVariant<>(data.isAway()));
        map.data.put("lastAwayMessage", new QVariant<>(data.lastAwayMessage()));
        map.data.put("idleTime", new QVariant<>(data.idleTime()));
        map.data.put("whoisServiceReply", new QVariant<>(data.whoisServiceReply()));
        map.data.put("suserHost", new QVariant<>(data.suserHost()));
        map.data.put("nick", new QVariant<>(data.nick()));
        map.data.put("realName", new QVariant<>(data.realName()));
        map.data.put("awayMessage", new QVariant<>(data.awayMessage()));
        map.data.put("loginTime", new QVariant<>(data.loginTime()));
        map.data.put("encrypted", new QVariant<>(data.encrypted()));
        map.data.put("channels", new QVariant<>(data.channels()));
        map.data.put("host", new QVariant<>(data.host()));
        map.data.put("userModes", new QVariant<>(data.userModes()));
        map.data.put("user", new QVariant<>(data.user()));
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
