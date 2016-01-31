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

package de.kuschku.libquassel.localtypes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.Network;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class Buffers {
    private Buffers() {

    }

    @Nullable
    public static Buffer fromType(@NonNull BufferInfo info, @NonNull Network network) {
        Buffer result;
        switch (info.type) {
            case QUERY:
                assertNotNull(info.name);
                result = new QueryBuffer(info, network.getUser(info.name));
                break;
            case CHANNEL:
                result = new ChannelBuffer(info, network.getChannels().get(info.name));
                break;
            case STATUS:
                result = new StatusBuffer(info, network);
                break;
            default:
                return null;
        }
        network.getBuffers().add(result);
        return result;
    }
}
