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

package de.kuschku.libquassel.primitives.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Set;

import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.QMetaTypeRegistry;
import de.kuschku.libquassel.syncables.SyncableRegistry;
import de.kuschku.libquassel.syncables.types.interfaces.QSyncableObject;

public class QVariant<T> {
    @Nullable
    public final T data;
    @Nullable
    public final QMetaType<T> type;

    @SuppressWarnings("unchecked")
    public QVariant(@NonNull T data) {
        if (data instanceof Set) {
            this.data = (T) new ArrayList((Set) data);
        } else {
            if (QMetaTypeRegistry.getTypeByObject(data) != null) {
                this.data = data;
            } else if (data instanceof QSyncableObject) {
                this.data = (T) SyncableRegistry.toVariantMap((QSyncableObject) data);
            } else {
                throw new IllegalArgumentException();
            }
        }

        this.type = QMetaTypeRegistry.getTypeByObject(this.data);
    }

    public QVariant(@NonNull QMetaType<T> type, @Nullable T data) {
        this.type = type;
        this.data = data;
    }

    public QVariant(@NonNull QMetaType.Type type, @Nullable T data) {
        this.type = QMetaTypeRegistry.getType(type);
        this.data = data;
    }

    public QVariant(@NonNull String typeName, @Nullable T data) {
        this.type = QMetaTypeRegistry.getType(typeName);
        this.data = data;
    }

    @Nullable
    public static <T> T orNull(@Nullable QVariant<T> data) {
        if (data == null) return null;
        else return data.data;
    }

    @NonNull
    public String toString() {
        return "QVariant(data=" + String.valueOf(this.data) + ", type=" + this.type.name + ")";
    }

    @NonNull
    public T or(@NonNull T ifNull) {
        return data == null ? ifNull : data;
    }

    @Nullable
    public T get() {
        return data;
    }
}
