package de.kuschku.libquassel.primitives;

import org.joda.time.DateTime;

import java.io.IOException;
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
import de.kuschku.libquassel.primitives.types.Message;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.IdentitySerializer;
import de.kuschku.libquassel.syncables.types.Identity;

public class QMetaTypeRegistry {
    private static final Map<QMetaType.Type, QMetaType> typeSerializerMap = new HashMap<>();
    private static final Map<String, QMetaType> stringSerializerMap = new HashMap<>();

    static {
        addType(QMetaType.Type.Void, new VoidSerializer());
        addType(QMetaType.Type.Bool, new BoolSerializer());
        addType(QMetaType.Type.Int, new IntSerializer());
        addType(QMetaType.Type.UserType, "BufferId", new IntSerializer());
        addType(QMetaType.Type.UserType, "NetworkId", new IntSerializer());
        addType(QMetaType.Type.UserType, "IdentityId", new IntSerializer());
        addType(QMetaType.Type.UserType, "MsgId", new IntSerializer());
        addType(QMetaType.Type.UserType, "BufferInfo", new BufferInfoSerializer());
        addType(QMetaType.Type.UserType, "Message", new MessageSerializer());
        addType(QMetaType.Type.UserType, "Identity", new UserTypeSerializer<>(new IdentitySerializer()));
        addType(QMetaType.Type.UserType, "Network::Server", new UserTypeSerializer<>(new NetworkServerSerializer()));
        addType(QMetaType.Type.UInt, new IntSerializer());
        addType(QMetaType.Type.UShort, new ShortSerializer());

        // TODO: Implement more custom quassel types

        addType(QMetaType.Type.QTime, new TimeSerializer());
        addType(QMetaType.Type.LongLong);
        addType(QMetaType.Type.ULongLong);
        addType(QMetaType.Type.Double);
        addType(QMetaType.Type.QChar, new CharSerializer());
        addType(QMetaType.Type.QVariantList, new VariantListSerializer<>());
        addType(QMetaType.Type.QVariantMap, new VariantMapSerializer<>());
        addType(QMetaType.Type.QStringList, new StringListSerializer());
        addType(QMetaType.Type.QString, new StringSerializer());
        addType(QMetaType.Type.QByteArray, new ByteArraySerializer());
        addType(QMetaType.Type.QBitArray);
        addType(QMetaType.Type.QDate);
        addType(QMetaType.Type.QDateTime, new DateTimeSerializer());
        addType(QMetaType.Type.QUrl);
        addType(QMetaType.Type.QLocale);
        addType(QMetaType.Type.QRect);
        addType(QMetaType.Type.QRectF);
        addType(QMetaType.Type.QSize);
        addType(QMetaType.Type.QSizeF);
        addType(QMetaType.Type.QLine);
        addType(QMetaType.Type.QLineF);
        addType(QMetaType.Type.QPoint);
        addType(QMetaType.Type.QPointF);
        // TODO: Handle QRegExp for the IgnoreListManager
        addType(QMetaType.Type.QRegExp);
        addType(QMetaType.Type.QVariantHash);
        addType(QMetaType.Type.QEasingCurve);

        // UI Types
        addType(QMetaType.Type.QFont);
        addType(QMetaType.Type.QPixmap);
        addType(QMetaType.Type.QBrush);
        addType(QMetaType.Type.QColor);
        addType(QMetaType.Type.QPalette);
        addType(QMetaType.Type.QIcon);
        addType(QMetaType.Type.QImage);
        addType(QMetaType.Type.QPolygon);
        addType(QMetaType.Type.QRegion);
        addType(QMetaType.Type.QBitmap);
        addType(QMetaType.Type.QCursor);
        addType(QMetaType.Type.QSizePolicy);
        addType(QMetaType.Type.QKeySequence);
        addType(QMetaType.Type.QPen);
        addType(QMetaType.Type.QTextLength);
        addType(QMetaType.Type.QTextFormat);
        addType(QMetaType.Type.QMatrix);
        addType(QMetaType.Type.QTransform);
        addType(QMetaType.Type.QMatrix4x4);
        addType(QMetaType.Type.QVector2D);
        addType(QMetaType.Type.QVector3D);
        addType(QMetaType.Type.QVector4D);
        addType(QMetaType.Type.QQuaternion);

        addType(QMetaType.Type.VoidStar, "void*");
        addType(QMetaType.Type.Long, new LongSerializer());
        addType(QMetaType.Type.Short, new ShortSerializer());
        addType(QMetaType.Type.Char, new ByteSerializer());
        addType(QMetaType.Type.ULong, new LongSerializer());
        addType(QMetaType.Type.UChar, new ByteSerializer());
        addType(QMetaType.Type.Float);
        addType(QMetaType.Type.QObjectStar, "QObject*");
        addType(QMetaType.Type.QWidgetStar, "QWidget*");
        addType(QMetaType.Type.QVariant, new VariantSerializer());
    }

    // Disable Constructor
    private QMetaTypeRegistry() {

    }

    private static <T> void addType(final QMetaType.Type type, final String name, final PrimitiveSerializer<T> serializer) {
        addType(new QMetaType<T>(type, name, serializer));
    }

    private static <T> void addType(final QMetaType.Type type, final String name) {
        addType(new QMetaType<T>(type, name));
    }

    private static <T> void addType(final QMetaType.Type type, final PrimitiveSerializer<T> serializer) {
        addType(new QMetaType<T>(type, serializer));
    }

    private static <T> void addType(final QMetaType.Type type) {
        addType(new QMetaType<T>(type));
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
        return deserialize(((QMetaType<T>) typeSerializerMap.get(type)), buffer);
    }

    public static <T> T deserialize(final String type, final ByteBuffer buffer) throws IOException {
        return deserialize(((QMetaType<T>) stringSerializerMap.get(type)), buffer);
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
        return ((QMetaType<T>) stringSerializerMap.get(typeName));
    }

    public static <T> QMetaType<T> getType(QMetaType.Type type) {
        return ((QMetaType<T>) typeSerializerMap.get(type));
    }

    public static <T> QMetaType<T> getTypeByObject(T type) {
        if (type instanceof Void) return (QMetaType<T>) typeSerializerMap.get(QMetaType.Type.Void);
        else if (type instanceof Boolean)
            return (QMetaType<T>) typeSerializerMap.get(QMetaType.Type.Bool);
        else if (type instanceof Integer)
            return (QMetaType<T>) typeSerializerMap.get(QMetaType.Type.Int);
        else if (type instanceof Short)
            return (QMetaType<T>) typeSerializerMap.get(QMetaType.Type.Short);
        else if (type instanceof DateTime)
            return (QMetaType<T>) typeSerializerMap.get(QMetaType.Type.QDateTime);
        else if (type instanceof Character)
            return (QMetaType<T>) typeSerializerMap.get(QMetaType.Type.QChar);
        else if (type instanceof List) {
            if (((List) type).size() > 0 && ((List) type).get(0) instanceof String)
                return (QMetaType<T>) typeSerializerMap.get(QMetaType.Type.QStringList);
            else if (((List) type).size() > 0 && ((List) type).get(0) instanceof QVariant)
                return (QMetaType<T>) new QMetaType<T>(QMetaType.Type.QVariantList, new VariantVariantListSerializer());
            else
                return (QMetaType<T>) typeSerializerMap.get(QMetaType.Type.QVariantList);
        } else if (type instanceof Map)
            return (QMetaType<T>) typeSerializerMap.get(QMetaType.Type.QVariantMap);
        else if (type instanceof String)
            return (QMetaType<T>) typeSerializerMap.get(QMetaType.Type.QString);
        else if (type instanceof Long)
            return (QMetaType<T>) typeSerializerMap.get(QMetaType.Type.Long);
        else if (type instanceof Byte)
            return (QMetaType<T>) typeSerializerMap.get(QMetaType.Type.Char);
        else if (type instanceof QVariant)
            return (QMetaType<T>) typeSerializerMap.get(QMetaType.Type.QVariant);
        else if (type instanceof Message) return stringSerializerMap.get("Message");
        else if (type instanceof BufferInfo) return stringSerializerMap.get("BufferInfo");
        else
            throw new IllegalArgumentException("Unsupported data type: " + type.getClass().getSimpleName());
    }
}
