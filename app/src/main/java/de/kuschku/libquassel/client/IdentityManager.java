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
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.syncables.types.interfaces.QIdentity;

public class IdentityManager {
    @NonNull
    private final Map<Integer, QIdentity> identities = new HashMap<>();
    private final Client client;

    public IdentityManager(Client client) {
        this.client = client;
    }

    public void createIdentity(@NonNull QIdentity identity) {
        identities.put(identity.id(), identity);
    }

    public void removeIdentity(@IntRange(from = 0) int id) {
        identities.remove(id);
    }

    @Nullable
    public QIdentity identity(@IntRange(from = 0) int id) {
        return identities.get(id);
    }

    public void init(@NonNull List<QIdentity> identities) {
        for (QIdentity identity : identities) {
            createIdentity(identity);
        }
    }
}
