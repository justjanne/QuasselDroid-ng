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

package de.kuschku.libquassel.localtypes.buffers;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QNetwork;

public class StatusBuffer implements Buffer {
    @NonNull
    private final Client client;
    @NonNull
    private BufferInfo info;
    private ObservableField<BufferInfo.BufferStatus> status = new ObservableField<>();

    public StatusBuffer(@NonNull BufferInfo info, @NonNull Client client) {
        this.info = info;
        this.client = client;
    }

    @NonNull
    @Override
    public BufferInfo getInfo() {
        return info;
    }

    @Override
    public void setInfo(@NonNull BufferInfo info) {
        this.info = info;
    }

    public QNetwork getNetwork() {
        return client.networkManager().network(info.networkId);
    }

    @Nullable
    @Override
    public String getName() {
        return getNetwork().networkName();
    }

    @NonNull
    @Override
    public ObservableField<BufferInfo.BufferStatus> getStatus() {
        // FIXME: Make this dynamic
        status.set(getNetwork().isConnected() ? BufferInfo.BufferStatus.ONLINE : BufferInfo.BufferStatus.OFFLINE);
        return status;
    }

    @NonNull
    @Override
    public String objectName() {
        return objectName(info.name);
    }

    @NonNull
    @Override
    public String objectName(String name) {
        return info.networkId + "/" + name;
    }

    @Override
    public void renameBuffer(@NonNull String newName) {
        this.info.name = newName;
    }

    @NonNull
    @Override
    public String toString() {
        return "StatusBuffer{" +
                "info=" + info +
                '}';
    }
}
