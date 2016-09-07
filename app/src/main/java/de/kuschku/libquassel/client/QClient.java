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

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.message.Message;
import de.kuschku.libquassel.objects.types.Command;
import de.kuschku.libquassel.primitives.types.BufferInfo;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.Synced;
import de.kuschku.libquassel.syncables.types.impl.Identity;
import de.kuschku.libquassel.syncables.types.impl.NetworkInfo;
import de.kuschku.libquassel.syncables.types.interfaces.QIdentity;

public interface QClient {
    @Synced
    void sendInput(BufferInfo info, String message);

    @Synced
    void sendInput(Command command);

    @Synced
    void createIdentity(QIdentity identity, Map<String, QVariant> certs);

    @Synced
    void updateIdentity(int id, final Map<String, QVariant> serialized);

    @Synced
    void removeIdentity(int id);

    @Synced
    void createNetwork(NetworkInfo info);

    @Synced
    void createNetwork(NetworkInfo info, List<String> persistentChannels);

    @Synced
    void updateNetwork(NetworkInfo info);

    @Synced
    void removeNetwork(int id);

    @Synced
    void changePassword(String username, String oldPassword, String newPassword);

    void _displayMsg(final Message msg);

    void _displayStatusMsg(String network, String message);

    void _bufferInfoUpdated(BufferInfo bufferInfo);

    void _identityCreated(Identity identity);

    void _identityRemoved(int id);

    void _networkCreated(int network);

    void _networkRemoved(int network);

    void _passwordChanged(long peerPtr, boolean success);

    void ___objectRenamed__(String type, String oldName, String newName);

    void login(String username, String password);
}
