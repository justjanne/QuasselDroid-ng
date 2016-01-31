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

package de.kuschku.libquassel.objects.serializers;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.functions.types.PackedFunction;
import de.kuschku.libquassel.functions.types.SerializedFunction;
import de.kuschku.libquassel.functions.types.UnpackedFunction;
import de.kuschku.libquassel.objects.types.SetupData;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class SetupDataInitializer implements ObjectSerializer<SetupData> {
    @NonNull
    private static final SetupDataInitializer serializer = new SetupDataInitializer();

    private SetupDataInitializer() {
    }

    @NonNull
    public static SetupDataInitializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public QVariant<Map<String, QVariant>> toVariantMap(@NonNull final SetupData data) {
        final QVariant<Map<String, QVariant>> map = new QVariant<>(new HashMap<>());
        assertNotNull(map.data);

        map.data.put("AdminPasswd", new QVariant<>(data.AdminPasswd));
        map.data.put("AdminUser", new QVariant<>(data.AdminUser));
        map.data.put("Backend", new QVariant<>(data.Backend));
        map.data.put("ConnectionProperties", new QVariant<>(data.ConnectionProperties));
        return map;
    }

    @NonNull
    @Override
    public SetupData fromDatastream(@NonNull Map<String, QVariant> map) {
        return fromLegacy(map);
    }

    @NonNull
    @Override
    public SetupData fromLegacy(@NonNull Map<String, QVariant> map) {
        return new SetupData(
                (String) map.get("AdminPasswd").data,
                (String) map.get("AdminUser").data,
                (String) map.get("Backend").data,
                (Map<String, QVariant>) map.get("ConnectionProperties").data
        );
    }

    @Override
    public SetupData from(@NonNull SerializedFunction function) {
        if (function instanceof PackedFunction)
            return fromLegacy(((PackedFunction) function).getData());
        else if (function instanceof UnpackedFunction)
            return fromDatastream(((UnpackedFunction) function).getData());
        else throw new IllegalArgumentException();
    }
}
