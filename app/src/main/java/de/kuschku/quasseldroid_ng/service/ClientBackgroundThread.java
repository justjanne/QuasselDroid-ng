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

package de.kuschku.quasseldroid_ng.service;

import android.content.Context;
import android.support.annotation.NonNull;

import org.acra.ACRA;

import java.io.IOException;
import java.net.ConnectException;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.QuasselClient;
import de.kuschku.libquassel.client.ClientData;
import de.kuschku.libquassel.client.FeatureFlags;
import de.kuschku.libquassel.events.ConnectionChangeEvent;
import de.kuschku.libquassel.events.GeneralErrorEvent;
import de.kuschku.libquassel.events.LoginRequireEvent;
import de.kuschku.libquassel.localtypes.backlogstorage.HybridBacklogStorage;
import de.kuschku.libquassel.protocols.RemotePeer;
import de.kuschku.quasseldroid_ng.ui.settings.Settings;
import de.kuschku.util.CompatibilityUtils;
import de.kuschku.util.accounts.Account;
import de.kuschku.util.accounts.AccountManager;
import de.kuschku.util.buffermetadata.SQLiteBufferMetaDataManager;
import de.kuschku.util.certificates.SQLiteCertificateManager;

public class ClientBackgroundThread implements Runnable {
    @NonNull
    private static final ClientData CLIENT_DATA = new ClientData(
            new FeatureFlags(true, true),
            new byte[]{RemotePeer.DATASTREAM},
            "QuasselDroid-ng 0.1 | libquassel 0.3.0",
            RemotePeer.PROTOCOL_VERSION_LEGACY
    );

    @NonNull
    private final QuasselClient client;

    private final Settings settings;
    private final AccountManager manager;

    public ClientBackgroundThread(@NonNull BusProvider provider, @NonNull Account account, @NonNull Context context) {
        this.client = new QuasselClient(
                provider,
                CLIENT_DATA,
                new SQLiteCertificateManager(context),
                new HybridBacklogStorage(),
                new SQLiteBufferMetaDataManager(context),
                account.id.toString()
        );
        this.client.connect(account.toAddress());
        this.client.provider.event.registerSticky(this);

        settings = new Settings(context);
        manager = new AccountManager(context);
    }

    @NonNull
    public QuasselClient client() {
        return client;
    }

    @Override
    public void run() {
        try {
            client.connection.open(CompatibilityUtils.deviceSupportsKeepAlive());
        } catch (IOException e) {
            client.provider.sendEvent(new GeneralErrorEvent(e));
            client.client.setConnectionStatus(ConnectionChangeEvent.Status.DISCONNECTED);
        }
    }

    public void close() {
        client.disconnect();
    }

    public void onEvent(LoginRequireEvent event) {
        if (!event.failedLast) {
            Account account = manager.account(settings.preferenceLastAccount.get());
            client().client.login(account.user, account.pass);
        }
    }

    public void onEvent(GeneralErrorEvent event) {
        if (!(event.exception instanceof ConnectException))
            ACRA.getErrorReporter().handleSilentException(event.exception);
    }
}
