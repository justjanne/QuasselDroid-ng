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

package de.kuschku.util.certificates;

import android.content.Context;
import android.support.annotation.NonNull;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.ssl.CertificateManager;
import de.kuschku.libquassel.ssl.UnknownCertificateException;
import de.kuschku.util.accounts.ServerAddress;

public class SQLiteCertificateManager implements CertificateManager {
    @NonNull
    private final CertificateDatabaseHandler handler;

    public SQLiteCertificateManager(Context context) {
        this.handler = new CertificateDatabaseHandler(context);
    }

    @Override
    public boolean isTrusted(@NonNull X509Certificate certificate, @NonNull ServerAddress core) {
        try {
            certificate.checkValidity();
            return handler.findCertificates(core.host).contains(CertificateUtils.certificateToFingerprint(certificate));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean addCertificate(@NonNull X509Certificate certificate, @NonNull ServerAddress core) {
        try {
            return handler.addCertificate(CertificateUtils.certificateToFingerprint(certificate), core.host);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeCertificate(@NonNull X509Certificate certificate, @NonNull ServerAddress core) {
        try {
            return handler.removeCertificate(CertificateUtils.certificateToFingerprint(certificate), core.host);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeAllCertificates(@NonNull ServerAddress core) {
        return handler.removeCertificates(core.host);
    }

    @Override
    public void checkTrusted(@NonNull X509Certificate certificate, @NonNull ServerAddress address) throws UnknownCertificateException {
        if (!isTrusted(certificate, address))
            throw new UnknownCertificateException(certificate, address);
    }

    @NonNull
    public List<String> findCertificates(@NonNull ServerAddress core) {
        return handler.findCertificates(core.host);
    }

    @NonNull
    public Map<String, Collection<String>> findAllCertificates() {
        return handler.findAllCertificates();
    }
}
