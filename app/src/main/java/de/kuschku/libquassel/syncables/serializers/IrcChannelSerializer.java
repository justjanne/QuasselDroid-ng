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
 * any later version, or under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and the
 * GNU Lesser General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
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
        map.data.put("name", new QVariant<>(data.getName()));
        map.data.put("topic", new QVariant<>(data.getTopic()));
        map.data.put("password", new QVariant<>(data.getPassword()));
        map.data.put("UserModes", StringObjectMapSerializer.<String>get().toVariantMap(data.getUserModes()));
        map.data.put("ChanModes", new QVariant<>(data.getChanModes()));
        map.data.put("encrypted", new QVariant<>(data.isEncrypted()));
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
