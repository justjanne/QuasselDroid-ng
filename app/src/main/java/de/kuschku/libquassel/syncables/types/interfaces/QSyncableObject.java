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

package de.kuschku.libquassel.syncables.types.interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.primitives.types.QVariant;

public interface QSyncableObject<T extends QSyncableObject> {
    void syncVar(@NonNull String methodName, @NonNull Object... params);

    void sync(@NonNull String methodName, @NonNull Object[] params);

    void rpcVar(@NonNull String procedureName, @NonNull Object... params);

    void rpc(@NonNull String procedureName, @NonNull Object[] params);

    void update(Map<String, QVariant> from);

    void update(T from);

    void renameObject(String newName);

    @Nullable
    String getObjectName();

    void setObjectName(@Nullable String objectName);

    void init(@NonNull String objectName, @NonNull BusProvider provider, @NonNull Client client);
}
