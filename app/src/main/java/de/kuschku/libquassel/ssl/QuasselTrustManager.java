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

package de.kuschku.libquassel.ssl;

import android.support.annotation.NonNull;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import de.kuschku.util.accounts.ServerAddress;
import de.kuschku.util.backports.Consumer;
import de.kuschku.util.certificates.CertificateUtils;

public class QuasselTrustManager implements X509TrustManager {
    @NonNull
    private final X509TrustManager wrapped;
    @NonNull
    private final CertificateManager certificateManager;
    @NonNull
    private final ServerAddress address;
    private final Consumer<X509Certificate[]> callback;

    public QuasselTrustManager(@NonNull X509TrustManager wrapped, @NonNull CertificateManager certificateManager, @NonNull ServerAddress address, Consumer<X509Certificate[]> callback) {
        this.wrapped = wrapped;
        this.certificateManager = certificateManager;
        this.address = address;
        this.callback = callback;
    }

    @NonNull
    public static QuasselTrustManager fromFactory(@NonNull TrustManagerFactory factory, @NonNull CertificateManager certificateManager, @NonNull ServerAddress address, Consumer<X509Certificate[]> callback) throws GeneralSecurityException {
        TrustManager[] managers = factory.getTrustManagers();
        for (TrustManager manager : managers) {
            if (manager instanceof X509TrustManager) {
                return new QuasselTrustManager((X509TrustManager) manager, certificateManager, address, callback);
            }
        }
        throw new GeneralSecurityException("Couldn’t find trustmanager provided by factory");
    }

    @NonNull
    public static QuasselTrustManager fromDefault(@NonNull CertificateManager certificateManager, @NonNull ServerAddress address, Consumer<X509Certificate[]> callback) throws GeneralSecurityException {
        TrustManagerFactory factory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        factory.init((KeyStore) null);
        return fromFactory(factory, certificateManager, address, callback);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        wrapped.checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            wrapped.checkServerTrusted(chain, authType);
            chain[0].checkValidity();
            if (!CertificateUtils.getHostnames(chain[0]).contains(address.host))
                throw new CertificateException("Hostname not in certificate");
        } catch (CertificateException e) {
            certificateManager.checkTrusted(chain[0], address);
        }
        callback.apply(chain);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return wrapped.getAcceptedIssuers();
    }
}
