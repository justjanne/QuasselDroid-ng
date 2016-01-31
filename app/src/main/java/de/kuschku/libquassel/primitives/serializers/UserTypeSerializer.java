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

package de.kuschku.libquassel.primitives.serializers;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Map;

import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class UserTypeSerializer<T> implements PrimitiveSerializer<T> {
    @NonNull
    private final ObjectSerializer<T> objectSerializer;

    public UserTypeSerializer(@NonNull ObjectSerializer<T> objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    @Override
    public void serialize(@NonNull ByteChannel channel, @NonNull T data) throws IOException {
        QVariant<Map<String, QVariant>> variantMap = objectSerializer.toVariantMap(data);
        assertNotNull(variantMap);

        VariantSerializer.<Map<String, QVariant>>get().serialize(channel, variantMap);
    }

    @SuppressWarnings("RedundantCast")
    @NonNull
    @Override
    public T deserialize(@NonNull ByteBuffer buffer) throws IOException {
        return (T) (Object) objectSerializer.fromLegacy(((VariantMapSerializer) VariantMapSerializer.get()).deserialize(buffer));
    }
}
