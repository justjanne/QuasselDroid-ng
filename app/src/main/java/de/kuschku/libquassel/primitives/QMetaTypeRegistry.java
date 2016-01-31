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

package de.kuschku.libquassel.primitives;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.message.Message;
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
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.serializers.IdentitySerializer;
import de.kuschku.libquassel.syncables.types.Identity;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class QMetaTypeRegistry {
    @NonNull
    private static final Map<QMetaType.Type, QMetaType> typeSerializerMap = new HashMap<>();
    @NonNull
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

    private static <T> void addType(@NonNull final Class cl, @NonNull final QMetaType.Type type, @NonNull final String name, @Nullable final PrimitiveSerializer<T> serializer) {
        addType(new QMetaType<>(cl, type, name, serializer));
    }

    private static <T> void addType(@NonNull final Class cl, @NonNull final QMetaType.Type type, @NonNull final String name) {
        addType(new QMetaType<>(cl, type, name));
    }

    private static <T> void addType(@NonNull final Class cl, @NonNull final QMetaType.Type type, @Nullable final PrimitiveSerializer<T> serializer) {
        addType(new QMetaType<>(cl, type, serializer));
    }

    private static <T> void addType(@NonNull final Class cl, @NonNull final QMetaType.Type type) {
        addType(new QMetaType<>(cl, type));
    }

    private static <T> void addType(@NonNull final QMetaType<T> metaType) {
        if (!typeSerializerMap.containsKey(metaType.type))
            typeSerializerMap.put(metaType.type, metaType);
        stringSerializerMap.put(metaType.name, metaType);
    }

    @Nullable
    public static <T> T deserialize(@NonNull final ByteBuffer buffer) throws IOException {
        final int id = deserialize(QMetaType.Type.UInt, buffer);
        final QMetaType.Type type = QMetaType.Type.fromId(id);
        return deserialize(type, buffer);
    }

    @Nullable
    public static <T> T deserialize(@NonNull final QMetaType.Type type, @NonNull final ByteBuffer buffer) throws IOException {
        return deserialize((getMetaTypeByType(type)), buffer);
    }

    @Nullable
    public static <T> T deserialize(@NonNull final String type, @NonNull final ByteBuffer buffer) throws IOException {
        return deserialize((getMetaTypeByString(type)), buffer);
    }

    @Nullable
    public static <T> T deserialize(@NonNull final QMetaType<T> type, @NonNull final ByteBuffer buffer) throws IOException {
        return type.serializer.deserialize(buffer);
    }

    public static <T> void serialize(@NonNull final QMetaType.Type type, @NonNull final ByteChannel channel, @NonNull final T data) throws IOException {
        getType(type).serializer.serialize(channel, data);
    }

    public static <T> void serialize(@NonNull final String type, @NonNull final ByteChannel channel, @NonNull final T data) throws IOException {
        getType(type).serializer.serialize(channel, data);
    }

    public static <T> void serializeWithType(@NonNull final QMetaType.Type type, @NonNull final ByteChannel channel, @NonNull final T data) throws IOException {
        serialize(QMetaType.Type.UInt, channel, type.getValue());
        serialize(type, channel, data);
    }

    @NonNull
    public static <T> PrimitiveSerializer<T> getSerializer(String typeName) {
        return (PrimitiveSerializer<T>) stringSerializerMap.get(typeName).serializer;
    }

    @NonNull
    public static <T> PrimitiveSerializer<T> getSerializer(QMetaType.Type type) {
        return (PrimitiveSerializer<T>) typeSerializerMap.get(type);
    }

    @NonNull
    public static <T> QMetaType<T> getType(@NonNull String typeName) {
        return getMetaTypeByString(typeName);
    }

    @NonNull
    private static <T> QMetaType<T> getMetaTypeByString(@NonNull String typeName) {
        QMetaType<T> result = stringSerializerMap.get(typeName);
        assertNotNull(String.format("Unknown type %s", typeName), result);
        assertNotNull(String.format("Unknown type %s", typeName), result.serializer);

        return result;
    }

    @NonNull
    public static <T> QMetaType<T> getType(@NonNull QMetaType.Type type) {
        return getMetaTypeByType(type);
    }

    @NonNull
    private static <T> QMetaType<T> getMetaTypeByType(@NonNull QMetaType.Type type) {
        QMetaType<T> result = typeSerializerMap.get(type);
        assertNotNull(String.format("Unknown type %s", type.toString()), result);
        assertNotNull(String.format("Unknown type %s", type.toString()), result.serializer);

        return result;
    }

    @NonNull
    public static <T> QMetaType<T> getTypeByObject(@NonNull T type) {
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
            throw new AssertionError("Unsupported data type: " + type.getClass().getSimpleName());
    }
}
