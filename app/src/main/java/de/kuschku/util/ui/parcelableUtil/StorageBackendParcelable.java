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

package de.kuschku.util.ui.parcelableUtil;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.objects.types.StorageBackend;
import de.kuschku.libquassel.primitives.QMetaType;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.util.backports.Stream;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class StorageBackendParcelable extends StorageBackend implements Parcelable {
    @NonNull
    public static Creator<StorageBackendParcelable> CREATOR = new Creator<StorageBackendParcelable>() {
        @NonNull
        @Override
        public StorageBackendParcelable createFromParcel(@NonNull Parcel source) {
            String DisplayName = source.readString();
            String Description = source.readString();
            List<String> SetupKeys = new ArrayList<>();
            source.readStringList(SetupKeys);
            Map<String, QVariant> SetupDefaults = new HashMap<>();
            int size = source.readInt();
            for (int i = 0; i < size; i++) {
                String key = source.readString();
                try {
                    QMetaType.Type type = QMetaType.Type.fromId(source.readInt());
                    switch (type) {
                        case Int:
                            SetupDefaults.put(key, new QVariant<>(type, source.readInt()));
                            break;
                        case QString:
                            SetupDefaults.put(key, new QVariant<>(type, source.readString()));
                            break;
                        case Bool:
                            SetupDefaults.put(key, new QVariant<>(type, source.readInt() > 0));
                            break;
                        default:
                            throw new IllegalArgumentException("Can’t serialize type " + type.name());
                    }
                } catch (IOException ignored) {
                }
            }

            assertNotNull(DisplayName);
            assertNotNull(Description);

            return new StorageBackendParcelable(DisplayName, SetupDefaults, Description, SetupKeys);
        }

        @NonNull
        @Override
        public StorageBackendParcelable[] newArray(int size) {
            return new StorageBackendParcelable[size];
        }
    };

    public StorageBackendParcelable(@NonNull StorageBackend backend) {
        this(backend.DisplayName, backend.SetupDefaults, backend.Description, backend.SetupKeys);
    }

    public StorageBackendParcelable(@NonNull String displayName, @NonNull Map<String, QVariant> setupDefaults, @NonNull String description, @NonNull List<String> setupKeys) {
        super(displayName, setupDefaults, description, setupKeys);
    }

    @NonNull
    public static ArrayList<StorageBackendParcelable> wrap(@NonNull List<StorageBackend> backends) {
        return new ArrayList<>(new Stream<>(backends).map(StorageBackendParcelable::new).list());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(DisplayName);
        dest.writeString(Description);
        dest.writeStringList(SetupKeys);
        dest.writeInt(SetupDefaults.size());
        for (String key : SetupDefaults.keySet()) {
            QVariant q = SetupDefaults.get(key);
            QMetaType.Type type = q.type.type;
            dest.writeString(key);
            dest.writeInt(type.getValue());
            switch (type) {
                case Int:
                    assertNotNull(q.data);
                    dest.writeInt((Integer) q.data);
                    break;
                case QString:
                    dest.writeString((String) q.data);
                    break;
                case Bool:
                    assertNotNull(q.data);
                    dest.writeInt(((Boolean) q.data) ? 1 : 0);
                    break;
                default:
                    throw new IllegalArgumentException("Can’t serialize type " + q.type.name);
            }
        }
    }
}
