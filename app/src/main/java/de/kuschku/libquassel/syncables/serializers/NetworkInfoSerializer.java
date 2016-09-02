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
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.impl.NetworkInfo;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class NetworkInfoSerializer implements ObjectSerializer<NetworkInfo> {
    @NonNull
    private static final NetworkInfoSerializer serializer = new NetworkInfoSerializer();

    private NetworkInfoSerializer() {

    }

    @NonNull
    public static NetworkInfoSerializer get() {
        return serializer;
    }

    @Nullable
    @Override
    public Map<String, QVariant<Object>> toVariantMap(@NonNull NetworkInfo data) {
        Map<String, QVariant<Object>> map = new HashMap<>();
        map.put("networkName", new QVariant<>(data.networkName()));
        map.put("identity", new QVariant<>(data.identity()));

        map.put("codecForServer", new QVariant<>(data.codecForServer()));
        map.put("codecForEncoding", new QVariant<>(data.codecForEncoding()));
        map.put("codecForDecoding", new QVariant<>(data.codecForDecoding()));

        map.put("ServerList", new QVariant<>(data.serverList()));
        map.put("useRandomServer", new QVariant<>(data.useRandomServer()));

        map.put("perform", new QVariant<>(data.perform()));

        map.put("useAutoIdentify", new QVariant<>(data.useAutoIdentify()));
        map.put("autoIdentifyService", new QVariant<>(data.autoIdentifyService()));
        map.put("autoIdentifyPassword", new QVariant<>(data.autoIdentifyPassword()));

        map.put("useSasl", new QVariant<>(data.useSasl()));
        map.put("saslAccount", new QVariant<>(data.saslAccount()));
        map.put("saslPassword", new QVariant<>(data.saslPassword()));

        map.put("useAutoReconnect", new QVariant<>(data.useAutoReconnect()));
        map.put("autoReconnectInterval", new QVariant<>(data.autoReconnectInterval()));
        map.put("autoReconnectRetries", new QVariant<>(data.autoReconnectRetries()));
        map.put("unlimitedReconnectRetries", new QVariant<>(data.unlimitedReconnectRetries()));
        map.put("rejoinChannels", new QVariant<>(data.rejoinChannels()));
        return map;
    }

    @NonNull
    @Override
    public NetworkInfo fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public NetworkInfo fromLegacy(@NonNull Map<String, QVariant> map) {
        return new NetworkInfo(
                -1,
                (String) map.get("networkName").data,
                (int) map.get("identityId").data,

                (String) map.get("codecForServer").data,
                (String) map.get("codecForEncoding").data,
                (String) map.get("codecForDecoding").data,

                (List<NetworkServer>) map.get("ServerList").data,
                (boolean) map.get("useRandomServer").data,

                (List<String>) map.get("perform").data,

                (boolean) map.get("useAutoIdentify").data,
                (String) map.get("autoIdentifyService").data,
                (String) map.get("autoIdentifyPassword").data,

                (boolean) map.get("useSasl").data,
                (String) map.get("saslAccount").data,
                (String) map.get("saslPassword").data,

                (boolean) map.get("useAutoReconnect").data,
                (int) map.get("autoReconnectInterval").data,
                (short) map.get("autoReconnectRetries").data,
                (boolean) map.get("unlimitedReconnectRetries").data,
                (boolean) map.get("rejoinChannels").data
        );
    }

    @Nullable
    @Override
    public NetworkInfo from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
