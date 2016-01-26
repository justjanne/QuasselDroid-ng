/*
    QuasselDroid - Quassel client for Android
    Copyright (C) 2015 Ken Børge Viktil
    Copyright (C) 2015 Magnus Fjell
    Copyright (C) 2015 Martin Sandsmark <martin.sandsmark@kde.org>

    This program is free software: you can redistribute it and/or modify it
    under the terms of the GNU General Public License as published by the Free
    Software Foundation, either version 3 of the License, or (at your option)
    any later version, or under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either version 2.1 of
    the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License and the
    GNU Lesser General Public License along with this program.  If not, see
    <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.primitives;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.Locale;

import de.kuschku.libquassel.primitives.serializers.PrimitiveSerializer;

/**
 * This class stores Qt types, the corresponding Java type, and a (De-)Serializer for it.
 *
 * @param <T> the type of the stored value
 * @author Janne Koschinski
 * @author Martin Sandsmark
 */
public class QMetaType<T> {
    @NonNull
    public final Type type;
    @NonNull
    public final String name;
    @NonNull
    public final Class cl;
    public final PrimitiveSerializer<T> serializer;

    public QMetaType(@NonNull Class cl, @NonNull Type type) {
        this(cl, type, type.getSerializableName());
    }

    public QMetaType(@NonNull Class cl, @NonNull Type type, @NonNull String name) {
        this(cl, type, name, null);
    }

    public QMetaType(@NonNull Class cl, @NonNull Type type, @Nullable PrimitiveSerializer<T> serializer) {
        this(cl, type, type.getSerializableName(), serializer);
    }

    public QMetaType(@NonNull Class cl, @NonNull Type type, @NonNull String name, @Nullable PrimitiveSerializer<T> serializer) {
        this.cl = cl;
        this.type = type;
        this.name = name;
        this.serializer = serializer;

    }

    @NonNull
    @Override
    public String toString() {
        return "QMetaType{" +
                "type=" + type +
                ", name='" + name + '\'' +
                ", serializer=" + serializer +
                '}';
    }

    public enum Type {
        Void(0),
        Bool(1),
        Int(2),
        UInt(3),
        LongLong(4),
        ULongLong(5),

        Double(6),
        QChar(7),
        QVariantMap(8),
        QVariantList(9),

        QString(10),
        QStringList(11),
        QByteArray(12),

        QBitArray(13),
        QDate(14),
        QTime(15),
        QDateTime(16),
        QUrl(17),

        QLocale(18),
        QRect(19),
        QRectF(20),
        QSize(21),
        QSizeF(22),

        QLine(23),
        QLineF(24),
        QPoint(25),
        QPointF(26),
        QRegExp(27),

        QVariantHash(28),
        QEasingCurve(29),

        FirstGuiType(63),

        QFont(64),
        QPixmap(65),
        QBrush(66),
        QColor(67),
        QPalette(68),

        QIcon(69),
        QImage(70),
        QPolygon(71),
        QRegion(72),
        QBitmap(73),

        QCursor(74),
        QSizePolicy(75),
        QKeySequence(76),
        QPen(77),

        QTextLength(78),
        QTextFormat(79),
        QMatrix(80),
        QTransform(81),

        QMatrix4x4(82),
        QVector2D(83),
        QVector3D(84),
        QVector4D(85),

        QQuaternion(86),

        VoidStar(128),
        Long(129),
        Short(130),
        Char(131),
        ULong(132),

        UShort(133),
        UChar(134),
        Float(135),
        QObjectStar(136),
        QWidgetStar(137),

        QVariant(138),

        User(256),
        UserType(127),
        LastType(0xffffffff);

        final int value;

        Type(int value) {
            this.value = value;
        }

        @NonNull
        public static Type fromId(int id) throws IOException {
            switch (id) {
                case 0:
                    return Void;
                case 1:
                    return Bool;
                case 2:
                    return Int;
                case 3:
                    return UInt;
                case 4:
                    return LongLong;
                case 5:
                    return ULongLong;

                case 6:
                    return Double;
                case 7:
                    return QChar;
                case 8:
                    return QVariantMap;
                case 9:
                    return QVariantList;

                case 10:
                    return QString;
                case 11:
                    return QStringList;
                case 12:
                    return QByteArray;

                case 13:
                    return QBitArray;
                case 14:
                    return QDate;
                case 15:
                    return QTime;
                case 16:
                    return QDateTime;
                case 17:
                    return QUrl;

                case 18:
                    return QLocale;
                case 19:
                    return QRect;
                case 20:
                    return QRectF;
                case 21:
                    return QSize;
                case 22:
                    return QSizeF;

                case 23:
                    return QLine;
                case 24:
                    return QLineF;
                case 25:
                    return QPoint;
                case 26:
                    return QPointF;
                case 27:
                    return QRegExp;

                case 28:
                    return QVariantHash;
                case 29:
                    return QEasingCurve;

                case 63:
                    return FirstGuiType;

                case 64:
                    return QFont;
                case 65:
                    return QPixmap;
                case 66:
                    return QBrush;
                case 67:
                    return QColor;
                case 68:
                    return QPalette;

                case 69:
                    return QIcon;
                case 70:
                    return QImage;
                case 71:
                    return QPolygon;
                case 72:
                    return QRegion;
                case 73:
                    return QBitmap;

                case 74:
                    return QCursor;
                case 75:
                    return QSizePolicy;
                case 76:
                    return QKeySequence;
                case 77:
                    return QPen;

                case 78:
                    return QTextLength;
                case 79:
                    return QTextFormat;
                case 80:
                    return QMatrix;
                case 81:
                    return QTransform;

                case 82:
                    return QMatrix4x4;
                case 83:
                    return QVector2D;
                case 84:
                    return QVector3D;
                case 85:
                    return QVector4D;

                case 86:
                    return QQuaternion;

                case 128:
                    return VoidStar;
                case 129:
                    return Long;
                case 130:
                    return Short;
                case 131:
                    return Char;
                case 132:
                    return ULong;

                case 133:
                    return UShort;
                case 134:
                    return UChar;
                case 135:
                    return Float;
                case 136:
                    return QObjectStar;
                case 137:
                    return QWidgetStar;

                case 138:
                    return QVariant;

                case 256:
                    return User;
                case 127:
                    return UserType;


                default:
                    throw new IOException("Unknown type: " + id);
            }
        }

        public int getValue() {
            return value;
        }

        @NonNull
        public String getSerializableName() {
            if (name().startsWith("Q")) return name();
            else return name().toLowerCase(Locale.ENGLISH);
        }
    }
}
