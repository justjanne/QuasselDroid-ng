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

package de.kuschku.libquassel.functions.serializers;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.UnpackedInitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.protocols.DatastreamPeer;

import static de.kuschku.util.AndroidAssert.assertTrue;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class PackedInitDataFunctionSerializer implements FunctionSerializer<UnpackedInitDataFunction> {
    @NonNull
    private static final PackedInitDataFunctionSerializer serializer = new PackedInitDataFunctionSerializer();

    private PackedInitDataFunctionSerializer() {
    }

    @NonNull
    public static PackedInitDataFunctionSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public List serialize(@NonNull final UnpackedInitDataFunction data) {
        final List func = new ArrayList<>();
        func.add(FunctionType.INITDATA.id);
        func.add(data.className);
        func.add(data.objectName);
        func.add(data.getData());
        return func;
    }

    @NonNull
    @Override
    public UnpackedInitDataFunction deserialize(@NonNull final List packedFunc) {
        assertTrue(packedFunc.size() >= 2);

        return new UnpackedInitDataFunction(
                ((QVariant<String>) packedFunc.remove(0)).data,
                ((QVariant<String>) packedFunc.remove(0)).data,
                DatastreamPeer.listToMap(packedFunc)
        );
    }
}
