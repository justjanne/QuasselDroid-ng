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
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.objects.serializers.StringObjectMapSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.impl.IrcChannel;

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
    public Map<String, QVariant<Object>> toVariantMap(@NonNull IrcChannel data) {
        final Map<String, QVariant<Object>> map = new HashMap<>();
        map.put("name", new QVariant(data.name()));
        map.put("topic", new QVariant<>(data.topic()));
        map.put("password", new QVariant<>(data.password()));
        map.put("UserModes", new QVariant<>(StringObjectMapSerializer.<String>get().toVariantMap(data.userModes())));
        map.put("ChanModes", new QVariant<>(data.chanModes()));
        map.put("encrypted", new QVariant<>(data.encrypted()));
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
