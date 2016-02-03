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

package de.kuschku.libquassel.objects.types;

import android.support.annotation.NonNull;

import java.util.List;

import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QIdentity;

public class SessionState {
    @NonNull
    public final List<QIdentity> Identities;
    @NonNull
    public final List<BufferInfo> BufferInfos;
    @NonNull
    public final List<Integer> NetworkIds;

    public SessionState(@NonNull List<QIdentity> identities, @NonNull List<BufferInfo> bufferInfos,
                        @NonNull List<Integer> networkIds) {
        this.Identities = identities;
        this.BufferInfos = bufferInfos;
        this.NetworkIds = networkIds;
    }

    @NonNull
    @Override
    public String toString() {
        return "SessionState{" +
                "Identities=" + Identities +
                ", BufferInfos=" + BufferInfos +
                ", NetworkIds=" + NetworkIds +
                '}';
    }
}
