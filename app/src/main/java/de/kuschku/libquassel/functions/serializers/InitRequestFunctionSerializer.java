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

import com.google.common.collect.Lists;

import java.util.List;

import de.kuschku.libquassel.functions.FunctionType;
import de.kuschku.libquassel.functions.types.InitRequestFunction;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.libquassel.primitives.QMetaType.Type.Int;
import static de.kuschku.libquassel.primitives.QMetaType.Type.QByteArray;
import static de.kuschku.util.AndroidAssert.assertNotNull;
import static de.kuschku.util.AndroidAssert.assertTrue;

@SuppressWarnings("unchecked")
public class InitRequestFunctionSerializer implements FunctionSerializer<InitRequestFunction> {
    @NonNull
    private static final InitRequestFunctionSerializer serializer = new InitRequestFunctionSerializer();

    private InitRequestFunctionSerializer() {
    }

    @NonNull
    public static InitRequestFunctionSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public List serialize(@NonNull final InitRequestFunction data) {
        return Lists.newArrayList(
                FunctionType.INITREQUEST.id,
                data.className,
                data.objectName
        );
    }

    // TODO: Add this for all such serializers
    @NonNull
    public List serializePacked(@NonNull final InitRequestFunction data) {
        return Lists.newArrayList(
                new QVariant<>(Int, FunctionType.INITREQUEST.id),
                new QVariant<>(QByteArray, data.className),
                new QVariant<>(QByteArray, data.objectName)
        );
    }

    @NonNull
    @Override
    public InitRequestFunction deserialize(@NonNull final List packedFunc) {
        assertTrue(packedFunc.size() >= 2);

        String className = (String) packedFunc.remove(0);
        String objectName = (String) packedFunc.remove(0);
        assertNotNull(className);

        return new InitRequestFunction(
                className,
                objectName
        );
    }
}
