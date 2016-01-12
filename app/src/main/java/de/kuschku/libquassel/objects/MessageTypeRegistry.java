package de.kuschku.libquassel.objects;

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
import de.kuschku.libquassel.primitives.types.QVariant;

public class MessageTypeRegistry {
    private static Map<String, ObjectSerializer> serializerMap = new HashMap<String, ObjectSerializer>();

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

    public static <T> T from(final Map<String, QVariant> function) {
        final String msgType = (String) function.get("MsgType").data;
        if (serializerMap.containsKey(msgType))
            return (T) serializerMap.get(msgType).fromLegacy(function);
        else
            throw new IllegalArgumentException(String.format("Unknown MessageType: %s", msgType));
    }

    public static <T> QVariant<Map<String, QVariant>> toVariantMap(final T data) {
        if (serializerMap.containsKey(data.getClass().getSimpleName())) {
            final QVariant<Map<String, QVariant>> map = (QVariant<Map<String, QVariant>>) serializerMap.get(data.getClass().getSimpleName()).toVariantMap(data);
            map.data.put("MsgType", new QVariant(data.getClass().getSimpleName()));
            return map;
        } else
            throw new IllegalArgumentException(String.format("Unknown MessageType: %s", data.getClass().getSimpleName()));
    }
}
