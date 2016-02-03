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

import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.impl.NetworkConfig;
import de.kuschku.libquassel.syncables.types.interfaces.QNetworkConfig;

public class NetworkConfigSerializer implements ObjectSerializer<QNetworkConfig> {
    @NonNull
    private static final NetworkConfigSerializer serializer = new NetworkConfigSerializer();

    private NetworkConfigSerializer() {

    }

    @NonNull
    public static NetworkConfigSerializer get() {
        return serializer;
    }

    @Nullable
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull QNetworkConfig data) {
        // FIXME: IMPLEMENT
        throw new IllegalArgumentException();
    }

    @NonNull
    @Override
    public QNetworkConfig fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public QNetworkConfig fromLegacy(@NonNull Map<String, QVariant> map) {
        return new NetworkConfig(
                (boolean) map.get("standardCtcp").data,
                (boolean) map.get("autoWhoEnabled").data,
                (int) map.get("autoWhoDelay").data,
                (int) map.get("autoWhoNickLimit").data,
                (int) map.get("autoWhoInterval").data,
                (boolean) map.get("pingTimeoutEnabled").data,
                (int) map.get("pingInterval").data,
                (int) map.get("maxPingCount").data
        );
    }

    @Nullable
    @Override
    public QNetworkConfig from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
