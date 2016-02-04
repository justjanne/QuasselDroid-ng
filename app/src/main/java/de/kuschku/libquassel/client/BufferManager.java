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

package de.kuschku.libquassel.client;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.localtypes.buffers.Buffer;
import de.kuschku.libquassel.localtypes.buffers.Buffers;
import de.kuschku.libquassel.primitives.types.BufferInfo;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class BufferManager {
    @NonNull
    private final Map<Integer, Buffer> buffers = new HashMap<>();
    private final Client client;

    public BufferManager(Client client) {
        this.client = client;
    }

    public void createBuffer(@NonNull Buffer buffer) {
        buffers.put(buffer.getInfo().id(), buffer);
    }

    public void removeBuffer(@IntRange(from = 0) int id) {
        buffers.remove(id);
    }

    public Buffer buffer(@IntRange(from = 0) int id) {
        return buffers.get(id);
    }

    public void updateBufferInfo(@NonNull BufferInfo bufferInfo) {
        Buffer buffer = buffer(bufferInfo.id());
        if (buffer == null) return;
        buffer.setInfo(bufferInfo);
    }

    public void init(List<BufferInfo> bufferInfos) {
        for (BufferInfo info : bufferInfos) {
            createBuffer(info);
        }
    }

    public Map<Integer, Buffer> buffers() {
        return buffers;
    }

    public void createBuffer(BufferInfo info) {
        Buffer buffer = Buffers.fromType(info, client);
        assertNotNull(buffer);
        createBuffer(buffer);
    }

    public boolean exists(BufferInfo info) {
        return buffers.containsKey(info.id());
    }
}
