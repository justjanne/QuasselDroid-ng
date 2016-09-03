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

package de.kuschku.libquassel.objects;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import de.kuschku.libquassel.objects.serializers.ClientInitAckSerializer;
import de.kuschku.libquassel.objects.serializers.ClientInitRejectSerializer;
import de.kuschku.libquassel.objects.serializers.ClientInitSerializer;
import de.kuschku.libquassel.objects.serializers.ClientLoginAckSerializer;
import de.kuschku.libquassel.objects.serializers.ClientLoginRejectSerializer;
import de.kuschku.libquassel.objects.serializers.ClientLoginSerializer;
import de.kuschku.libquassel.objects.serializers.CoreSetupAckSerializer;
import de.kuschku.libquassel.objects.serializers.CoreSetupDataSerializer;
import de.kuschku.libquassel.objects.serializers.CoreSetupRejectSerializer;
import de.kuschku.libquassel.objects.serializers.ObjectSerializer;
import de.kuschku.libquassel.objects.serializers.SessionInitSerializer;
import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertTrue;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class MessageTypeRegistry {
    @NonNull
    private static final Map<String, ObjectSerializer> serializerMap = new HashMap<>();

    static {
        serializerMap.put("ClientInit", ClientInitSerializer.get());
        serializerMap.put("ClientInitAck", ClientInitAckSerializer.get());
        serializerMap.put("ClientInitReject", ClientInitRejectSerializer.get());
        serializerMap.put("ClientLogin", ClientLoginSerializer.get());
        serializerMap.put("ClientLoginAck", ClientLoginAckSerializer.get());
        serializerMap.put("ClientLoginReject", ClientLoginRejectSerializer.get());
        serializerMap.put("CoreSetupData", CoreSetupDataSerializer.get());
        serializerMap.put("CoreSetupAck", CoreSetupAckSerializer.get());
        serializerMap.put("CoreSetupReject", CoreSetupRejectSerializer.get());
        serializerMap.put("SessionInit", SessionInitSerializer.get());
    }

    // Disable Constructor
    private MessageTypeRegistry() {

    }

    @NonNull
    public static <T> T from(@NonNull final Map<String, QVariant> function) {
        final String msgType = (String) function.get("MsgType").data;
        if (serializerMap.containsKey(msgType))
            return (T) serializerMap.get(msgType).fromLegacy(function);
        else
            throw new IllegalArgumentException(String.format("Unknown MessageType: %s", msgType));
    }

    @NonNull
    public static <T> QVariant<Map<String, QVariant>> toVariantMap(@NonNull final T data) {
        assertTrue(serializerMap.containsKey(data.getClass().getSimpleName()));

        final QVariant<Map<String, QVariant>> map = new QVariant<>(serializerMap.get(data.getClass().getSimpleName()).toVariantMap(data));
        map.data.put("MsgType", new QVariant(QMetaType.Type.QString, data.getClass().getSimpleName()));
        return map;
    }
}
