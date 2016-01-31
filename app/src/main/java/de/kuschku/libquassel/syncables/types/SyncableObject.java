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

package de.kuschku.libquassel.syncables.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.functions.types.SyncFunction;
import de.kuschku.libquassel.primitives.types.QVariant;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public abstract class SyncableObject<T extends SyncableObject<T>> {
    @Nullable
    protected BusProvider provider;
    @Nullable
    private String objectName;

    protected void sync(@NonNull String methodName, @NonNull Object[] params) {
        assertNotNull(provider);

        provider.dispatch(new SyncFunction<>(getClassName(), getObjectName(), methodName, Arrays.asList(params)));
    }

    public void setBusProvider(@NonNull BusProvider provider) {
        this.provider = provider;
    }

    @NonNull
    public String getClassName() {
        return getClass().getSimpleName();
    }

    @Nullable
    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(@Nullable String objectName) {
        this.objectName = objectName;
    }

    public void renameObject(@Nullable String objectName) {
        setObjectName(objectName);
    }

    public abstract void init(@NonNull InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client);

    public void doInit() {
    }

    public abstract void update(T from);

    public abstract void update(Map<String, QVariant> from);
}
