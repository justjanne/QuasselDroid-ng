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

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.functions.types.HandshakeFunction;
import de.kuschku.libquassel.objects.types.ClientLogin;
import de.kuschku.libquassel.objects.types.Command;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.types.SyncableObject;
import de.kuschku.libquassel.syncables.types.impl.NetworkInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QIdentity;

import static de.kuschku.util.AndroidAssert.fail;
import static junit.framework.Assert.assertNotNull;

public abstract class AClient<T extends AClient<T>> extends SyncableObject<T> implements QClientInterface {
    @Override
    public void sendInput(BufferInfo info, String message) {
        smartRpc("sendInput(BufferInfo, QString)", info, message);
    }

    @Override
    public void sendInput(@NonNull Command command) {
        sendInput(command.buffer, command.command);
    }

    @Override
    public void createIdentity(QIdentity identity) {
        smartRpc("createIdentity(Identity,QVariantMap)", identity);
    }

    @Override
    public void updateIdentity(int id, Map<String, QVariant> serialized) {
        smartRpc("updateIdenity(IdentityId,QVariantMap)", id, serialized);
    }

    @Override
    public void removeIdentity(int id) {
        smartRpc("removeIdentity(IdentityId)", id);
    }

    @Override
    public void createNetwork(NetworkInfo info) {
        createNetwork(info, new ArrayList<>(0));
    }

    @Override
    public void createNetwork(NetworkInfo info, List<String> persistentChannels) {
        smartRpc("createNetwork(NetworkInfo,QStringList)", info, persistentChannels);
    }

    @Override
    public void updateNetwork(NetworkInfo info) {
        smartRpc("updateNetwork(NetworkInfo)", info);
    }

    @Override
    public void removeNetwork(int id) {
        smartRpc("2removeNetwork(NetworkId)", id);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        smartRpc("changePassword(PeerPtr,QString,QString,QString)", 0x0000000000000000L, username, oldPassword, newPassword);
    }

    @Override
    public void update(T from) {
        fail("This is not a real syncable");
    }

    @Override
    public void update(Map<String, QVariant> from) {
        fail("This is not a real syncable");
    }

    @Override
    public void login(@NonNull String username, @NonNull String password) {
        assertNotNull(provider);

        provider.dispatch(new HandshakeFunction(new ClientLogin(username, password)));
    }
}
