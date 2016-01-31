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

package de.kuschku.util.ui.parcelableUtil;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.IOException;

import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.QMetaTypeRegistry;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertNotNull;

@SuppressWarnings("unchecked")
public class QVariantParcelable<T> extends QVariant<T> implements Parcelable {
    @NonNull
    public Creator<QVariantParcelable> CREATOR = new Creator<QVariantParcelable>() {
        @NonNull
        @Override
        public QVariantParcelable createFromParcel(@NonNull Parcel source) {
            try {
                QMetaType type = QMetaTypeRegistry.getType(QMetaType.Type.fromId(source.readInt()));
                Object data;
                switch (type.type) {
                    case Int:
                        data = source.readInt();
                        break;
                    case QByteArray:
                    case QString:
                        data = source.readString();
                        break;
                    case Bool:
                        data = (source.readInt() > 0);
                        break;
                    default:
                        throw new IllegalArgumentException("Can’t deserialize type " + type.name);
                }
                return new QVariantParcelable<>(type.name, data);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @NonNull
        @Override
        public QVariantParcelable[] newArray(int size) {
            return new QVariantParcelable[size];
        }
    };

    public QVariantParcelable(@NonNull String typeName, T data) {
        super(typeName, data);
    }

    public QVariantParcelable(@NonNull QVariant value) {
        super(value.type, (T) value.data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {


        dest.writeInt(type.type.getValue());
        switch (type.type) {
            case Int:
                assertNotNull(data);
                dest.writeInt((Integer) data);
                break;
            case QString:
                dest.writeString((String) data);
                break;
            case Bool:
                assertNotNull(data);
                dest.writeInt(((Boolean) data) ? 1 : 0);
                break;
            default:
                throw new IllegalArgumentException("Can’t serialize type " + type.name);
        }
    }
}
