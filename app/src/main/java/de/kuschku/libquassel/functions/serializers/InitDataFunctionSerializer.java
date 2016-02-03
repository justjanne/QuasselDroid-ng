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

import java.util.List;

import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.PackedInitDataFunction;
import de.kuschku.libquassel.functions.types.UnpackedInitDataFunction;

public class InitDataFunctionSerializer implements FunctionSerializer<InitDataFunction> {
    @NonNull
    private static final InitDataFunctionSerializer serializer = new InitDataFunctionSerializer();

    private InitDataFunctionSerializer() {
    }

    @NonNull
    public static InitDataFunctionSerializer get() {
        return serializer;
    }

    @NonNull
    @Override
    public List serialize(@NonNull final InitDataFunction data) {
        if (data instanceof UnpackedInitDataFunction) {
            return PackedInitDataFunctionSerializer.get().serialize((UnpackedInitDataFunction) data);
        } else if (data instanceof PackedInitDataFunction) {
            return UnpackedInitDataFunctionSerializer.get().serialize((PackedInitDataFunction) data);
        } else {
            throw new IllegalArgumentException("Can not be applied to these arguments");
        }
    }

    @NonNull
    @Override
    public InitDataFunction deserialize(@NonNull final List packedFunc) {
        throw new IllegalArgumentException("Can not be applied to these arguments");
    }
}
