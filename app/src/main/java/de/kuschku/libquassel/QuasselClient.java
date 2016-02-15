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

package de.kuschku.libquassel;

import android.support.annotation.NonNull;

import de.kuschku.libquassel.client.Client;
import de.kuschku.libquassel.client.ClientData;
import de.kuschku.libquassel.localtypes.backlogstorage.BacklogStorage;
import de.kuschku.libquassel.ssl.CertificateManager;
import de.kuschku.util.accounts.ServerAddress;

import static de.kuschku.util.AndroidAssert.assertNotNull;

public class QuasselClient {
    @NonNull
    public final BusProvider provider;
    @NonNull
    public final ProtocolHandler handler;
    @NonNull
    public final Client client;
    @NonNull
    public final CertificateManager certificateManager;
    @NonNull
    private final ClientData data;
    public CoreConnection connection;

    public QuasselClient(@NonNull BusProvider provider, @NonNull ClientData data, @NonNull CertificateManager certificateManager, @NonNull BacklogStorage backlogStorage) {
        assertNotNull(provider);
        assertNotNull(data);
        assertNotNull(certificateManager);
        assertNotNull(backlogStorage);

        this.provider = provider;
        this.data = data;
        this.certificateManager = certificateManager;
        this.client = new Client(provider, backlogStorage);
        this.handler = new ProtocolHandler(provider, this.client);
    }

    public void connect(@NonNull ServerAddress address) {
        assertNotNull(client);
        this.connection = new CoreConnection(address, data, provider, client, certificateManager);
    }

    public void disconnect() {
        this.connection.close();
    }
}
