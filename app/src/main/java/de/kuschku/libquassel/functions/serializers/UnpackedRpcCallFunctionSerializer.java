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

package de.kuschku.libquassel.functions.serializers;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.RpcCallFunction;
import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertTrue;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class UnpackedRpcCallFunctionSerializer implements FunctionSerializer<RpcCallFunction> {
    @NonNull
    private static final UnpackedRpcCallFunctionSerializer serializer = new UnpackedRpcCallFunctionSerializer();

    private UnpackedRpcCallFunctionSerializer() {
    }

    @NonNull
    public static UnpackedRpcCallFunctionSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public List serialize(@NonNull final RpcCallFunction data) {
        final List func = new ArrayList<>();
        func.add(new QVariant<>(QMetaType.Type.Int, FunctionType.RPCCALL.id));
        func.add(new QVariant<>(QMetaType.Type.QByteArray, data.functionName));
        func.addAll(data.params);
        return func;
    }

    @NonNull
    @Override
    public RpcCallFunction deserialize(@NonNull final List packedFunc) {
        assertTrue(packedFunc.size() >= 1);

        return new RpcCallFunction(
                (String) packedFunc.remove(0),
                packedFunc
        );
    }
}
