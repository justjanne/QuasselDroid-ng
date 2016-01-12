package de.kuschku.libquassel.primitives;

import org.joda.time.DateTime;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.objects.serializers.NetworkServerSerializer;
import de.kuschku.libquassel.objects.types.NetworkServer;
import de.kuschku.libquassel.primitives.serializers.BoolSerializer;
import de.kuschku.libquassel.primitives.serializers.BufferInfoSerializer;
import de.kuschku.libquassel.primitives.serializers.ByteArraySerializer;
import de.kuschku.libquassel.primitives.serializers.ByteSerializer;
import de.kuschku.libquassel.primitives.serializers.CharSerializer;
import de.kuschku.libquassel.primitives.serializers.DateTimeSerializer;
import de.kuschku.libquassel.primitives.serializers.IntSerializer;
import de.kuschku.libquassel.primitives.serializers.LongSerializer;
import de.kuschku.libquassel.primitives.serializers.MessageSerializer;
import de.kuschku.libquassel.primitives.serializers.PrimitiveSerializer;
import de.kuschku.libquassel.primitives.serializers.ShortSerializer;
import de.kuschku.libquassel.primitives.serializers.StringListSerializer;
import de.kuschku.libquassel.primitives.serializers.StringSerializer;
import de.kuschku.libquassel.primitives.serializers.TimeSerializer;
import de.kuschku.libquassel.primitives.serializers.UserTypeSerializer;
import de.kuschku.libquassel.primitives.serializers.VariantListSerializer;
import de.kuschku.libquassel.primitives.serializers.VariantMapSerializer;
import de.kuschku.libquassel.primitives.serializers.VariantSerializer;
import de.kuschku.libquassel.primitives.serializers.VariantVariantListSerializer;
import de.kuschku.libquassel.primitives.serializers.VoidSerializer;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.IdentitySerializer;
import de.kuschku.libquassel.syncables.types.Identity;

public class QMetaTypeRegistry {
    private static final Map<QMetaType.Type, QMetaType> typeSerializerMap = new HashMap<>();
    private static final Map<String, QMetaType> stringSerializerMap = new HashMap<>();

    static {
        addType(Void.class, QMetaType.Type.Void, VoidSerializer.get());
        addType(boolean.class, QMetaType.Type.Bool, BoolSerializer.get());
        addType(int.class, QMetaType.Type.Int, IntSerializer.get());
        addType(int.class, QMetaType.Type.UserType, "BufferId", IntSerializer.get());
        addType(int.class, QMetaType.Type.UserType, "NetworkId", IntSerializer.get());
        addType(int.class, QMetaType.Type.UserType, "IdentityId", IntSerializer.get());
        addType(int.class, QMetaType.Type.UserType, "MsgId", IntSerializer.get());
        addType(BufferInfo.class, QMetaType.Type.UserType, "BufferInfo", BufferInfoSerializer.get());
        addType(Message.class, QMetaType.Type.UserType, "Message", MessageSerializer.get());
        addType(Identity.class, QMetaType.Type.UserType, "Identity", new UserTypeSerializer<>(IdentitySerializer.get()));
        addType(NetworkServer.class, QMetaType.Type.UserType, "Network::Server", new UserTypeSerializer<>(NetworkServerSerializer.get()));
        addType(int.class, QMetaType.Type.UInt, IntSerializer.get());
        addType(short.class, QMetaType.Type.UShort, ShortSerializer.get());

        // TODO: Implement more custom quassel types

        addType(DateTime.class, QMetaType.Type.QTime, TimeSerializer.get());
        addType(BigDecimal.class, QMetaType.Type.LongLong);
        addType(BigDecimal.class, QMetaType.Type.ULongLong);
        addType(double.class, QMetaType.Type.Double);
        addType(char.class, QMetaType.Type.QChar, CharSerializer.get());
        addType(List.class, QMetaType.Type.QVariantList, VariantListSerializer.get());
        addType(Map.class, QMetaType.Type.QVariantMap, VariantMapSerializer.get());
        addType(List.class, QMetaType.Type.QStringList, StringListSerializer.get());
        addType(String.class, QMetaType.Type.QString, StringSerializer.get());
        addType(String.class, QMetaType.Type.QByteArray, ByteArraySerializer.get());
        addType(void.class, QMetaType.Type.QBitArray);
        addType(void.class, QMetaType.Type.QDate);
        addType(DateTime.class, QMetaType.Type.QDateTime, DateTimeSerializer.get());
        addType(void.class, QMetaType.Type.QUrl);
        addType(void.class, QMetaType.Type.QLocale);
        addType(void.class, QMetaType.Type.QRect);
        addType(void.class, QMetaType.Type.QRectF);
        addType(void.class, QMetaType.Type.QSize);
        addType(void.class, QMetaType.Type.QSizeF);
        addType(void.class, QMetaType.Type.QLine);
        addType(void.class, QMetaType.Type.QLineF);
        addType(void.class, QMetaType.Type.QPoint);
        addType(void.class, QMetaType.Type.QPointF);
        // TODO: Handle QRegExp for the IgnoreListManager
        addType(void.class, QMetaType.Type.QRegExp);
        addType(void.class, QMetaType.Type.QVariantHash);
        addType(void.class, QMetaType.Type.QEasingCurve);

        // UI Types
        addType(void.class, QMetaType.Type.QFont);
        addType(void.class, QMetaType.Type.QPixmap);
        addType(void.class, QMetaType.Type.QBrush);
        addType(void.class, QMetaType.Type.QColor);
        addType(void.class, QMetaType.Type.QPalette);
        addType(void.class, QMetaType.Type.QIcon);
        addType(void.class, QMetaType.Type.QImage);
        addType(void.class, QMetaType.Type.QPolygon);
        addType(void.class, QMetaType.Type.QRegion);
        addType(void.class, QMetaType.Type.QBitmap);
        addType(void.class, QMetaType.Type.QCursor);
        addType(void.class, QMetaType.Type.QSizePolicy);
        addType(void.class, QMetaType.Type.QKeySequence);
        addType(void.class, QMetaType.Type.QPen);
        addType(void.class, QMetaType.Type.QTextLength);
        addType(void.class, QMetaType.Type.QTextFormat);
        addType(void.class, QMetaType.Type.QMatrix);
        addType(void.class, QMetaType.Type.QTransform);
        addType(void.class, QMetaType.Type.QMatrix4x4);
        addType(void.class, QMetaType.Type.QVector2D);
        addType(void.class, QMetaType.Type.QVector3D);
        addType(void.class, QMetaType.Type.QVector4D);
        addType(void.class, QMetaType.Type.QQuaternion);

        addType(void.class, QMetaType.Type.VoidStar, "void*");
        addType(long.class, QMetaType.Type.Long, LongSerializer.get());
        addType(short.class, QMetaType.Type.Short, ShortSerializer.get());
        addType(byte.class, QMetaType.Type.Char, ByteSerializer.get());
        addType(long.class, QMetaType.Type.ULong, LongSerializer.get());
        addType(byte.class, QMetaType.Type.UChar, ByteSerializer.get());
        addType(void.class, QMetaType.Type.Float);
        addType(void.class, QMetaType.Type.QObjectStar, "QObject*");
        addType(void.class, QMetaType.Type.QWidgetStar, "QWidget*");
        addType(QVariant.class, QMetaType.Type.QVariant, VariantSerializer.get());
    }

    // Disable Constructor
    private QMetaTypeRegistry() {

    }

    private static <T> void addType(final Class cl, final QMetaType.Type type, final String name, final PrimitiveSerializer<T> serializer) {
        addType(new QMetaType<T>(cl, type, name, serializer));
    }

    private static <T> void addType(final Class cl, final QMetaType.Type type, final String name) {
        addType(new QMetaType<T>(cl, type, name));
    }

    private static <T> void addType(final Class cl, final QMetaType.Type type, final PrimitiveSerializer<T> serializer) {
        addType(new QMetaType<T>(cl, type, serializer));
    }

    private static <T> void addType(final Class cl, final QMetaType.Type type) {
        addType(new QMetaType<T>(cl, type));
    }

    private static <T> void addType(final QMetaType<T> metaType) {
        if (!typeSerializerMap.containsKey(metaType.type))
            typeSerializerMap.put(metaType.type, metaType);
        stringSerializerMap.put(metaType.name, metaType);
    }

    public static <T> T deserialize(final ByteBuffer buffer) throws IOException {
        final int id = deserialize(QMetaType.Type.UInt, buffer);
        final QMetaType.Type type = QMetaType.Type.fromId(id);
        return deserialize(type, buffer);
    }

    public static <T> T deserialize(final QMetaType.Type type, final ByteBuffer buffer) throws IOException {
        return deserialize((getMetaTypeByType(type)), buffer);
    }

    public static <T> T deserialize(final String type, final ByteBuffer buffer) throws IOException {
        return deserialize((getMetaTypeByString(type)), buffer);
    }

    public static <T> T deserialize(final QMetaType<T> type, final ByteBuffer buffer) throws IOException {
        return type.serializer.deserialize(buffer);
    }

    public static <T> void serialize(final QMetaType.Type type, final ByteChannel channel, final T data) throws IOException {
        getType(type).serializer.serialize(channel, data);
    }

    public static <T> void serialize(final String type, final ByteChannel channel, final T data) throws IOException {
        getType(type).serializer.serialize(channel, data);
    }

    public static <T> void serializeWithType(final QMetaType.Type type, final ByteChannel channel, final T data) throws IOException {
        serialize(QMetaType.Type.UInt, channel, type.getValue());
        serialize(type, channel, data);
    }

    public static <T> PrimitiveSerializer<T> getSerializer(String typeName) {
        return (PrimitiveSerializer<T>) stringSerializerMap.get(typeName).serializer;
    }

    public static <T> PrimitiveSerializer<T> getSerializer(QMetaType.Type type) {
        return (PrimitiveSerializer<T>) typeSerializerMap.get(type);
    }

    public static <T> QMetaType<T> getType(String typeName) {
        return getMetaTypeByString(typeName);
    }

    private static <T> QMetaType<T> getMetaTypeByString(String typeName) {
        QMetaType<T> result = stringSerializerMap.get(typeName);
        if (result == null || result.serializer == null) {
            throw new RuntimeException("Unknown type: " + typeName);
        }
        return result;
    }

    public static <T> QMetaType<T> getType(QMetaType.Type type) {
        return getMetaTypeByType(type);
    }

    private static <T> QMetaType<T> getMetaTypeByType(QMetaType.Type type) {
        QMetaType<T> result = typeSerializerMap.get(type);
        if (result == null || result.serializer == null) {
            throw new RuntimeException("Unknown type: " + type.toString());
        }
        return result;
    }

    public static <T> QMetaType<T> getTypeByObject(T type) {
        if (type instanceof Void) return getMetaTypeByType(QMetaType.Type.Void);
        else if (type instanceof Boolean)
            return getMetaTypeByType(QMetaType.Type.Bool);
        else if (type instanceof Integer)
            return getMetaTypeByType(QMetaType.Type.Int);
        else if (type instanceof Short)
            return getMetaTypeByType(QMetaType.Type.Short);
        else if (type instanceof DateTime)
            return getMetaTypeByType(QMetaType.Type.QDateTime);
        else if (type instanceof Character)
            return getMetaTypeByType(QMetaType.Type.QChar);
        else if (type instanceof List) {
            if (((List) type).size() > 0 && ((List) type).get(0) instanceof String)
                return getMetaTypeByType(QMetaType.Type.QStringList);
            else if (((List) type).size() > 0 && ((List) type).get(0) instanceof QVariant)
                return new QMetaType<>((Class) type.getClass(), QMetaType.Type.QVariantList, (PrimitiveSerializer<T>) VariantVariantListSerializer.get());
            else
                return getMetaTypeByType(QMetaType.Type.QVariantList);
        } else if (type instanceof Map)
            return getMetaTypeByType(QMetaType.Type.QVariantMap);
        else if (type instanceof String)
            return getMetaTypeByType(QMetaType.Type.QString);
        else if (type instanceof Long)
            return getMetaTypeByType(QMetaType.Type.Long);
        else if (type instanceof Byte)
            return getMetaTypeByType(QMetaType.Type.Char);
        else if (type instanceof QVariant)
            return getMetaTypeByType(QMetaType.Type.QVariant);
        else if (type instanceof Message) return stringSerializerMap.get("Message");
        else if (type instanceof BufferInfo) return stringSerializerMap.get("BufferInfo");
        else
            throw new IllegalArgumentException("Unsupported data type: " + type.getClass().getSimpleName());
    }
}
