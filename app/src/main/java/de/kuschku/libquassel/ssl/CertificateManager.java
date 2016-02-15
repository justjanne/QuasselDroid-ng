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

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.kuschku.util.accounts.ServerAddress;

public interface CertificateManager {
    boolean isTrusted(X509Certificate certificate, ServerAddress core);

    boolean addCertificate(X509Certificate certificate, ServerAddress core);

    boolean removeCertificate(X509Certificate certificate, ServerAddress core);

    boolean removeAllCertificates(ServerAddress core);

    @NonNull
    List<String> findCertificates(ServerAddress core);

    @NonNull
    Map<String, Collection<String>> findAllCertificates();

    void checkTrusted(X509Certificate certificate, ServerAddress address) throws UnknownCertificateException;
}
