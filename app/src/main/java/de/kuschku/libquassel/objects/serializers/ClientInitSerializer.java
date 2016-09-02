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

package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.ClientInit;
import de.kuschku.libquassel.primitives.types.QVariant;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class ClientInitSerializer implements ObjectSerializer<ClientInit> {
    @NonNull
    private static final ClientInitSerializer serializer = new ClientInitSerializer();

    private ClientInitSerializer() {
    }

    @NonNull
    public static ClientInitSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public Map<String, QVariant<Object>> toVariantMap(@NonNull final ClientInit data) {
        final Map<String, QVariant<Object>> map = new HashMap<>();
        map.put("ClientDate", new QVariant<>(data.ClientDate));
        map.put("UseSsl", new QVariant<>(data.UseSsl));
        map.put("ClientVersion", new QVariant<>(data.ClientVersion));
        map.put("UseCompression", new QVariant<>(data.UseCompression));
        map.put("ProtocolVersion", new QVariant<>(data.ProtocolVersion));
        return map;
    }

    @NonNull
    @Override
    public ClientInit fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public ClientInit fromLegacy(@NonNull final Map<String, QVariant> map) {
        return new ClientInit(
                ((QVariant<String>) map.get("ClientDate")).data,
                ((QVariant<Boolean>) map.get("UseSsl")).data,
                ((QVariant<String>) map.get("ClientVersion")).data,
                ((QVariant<Boolean>) map.get("UseCompression")).data,
                ((QVariant<Integer>) map.get("ProtocolVersion")).data
        );
    }

    @Override
    public ClientInit from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
