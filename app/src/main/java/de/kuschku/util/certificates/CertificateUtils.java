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

import android.support.annotation.NonNull;

import com.google.common.base.Joiner;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.kuschku.util.backports.Objects;

public class CertificateUtils {
    private CertificateUtils() {
    }

    public static String certificateToFingerprint(@NonNull X509Certificate certificate) throws NoSuchAlgorithmException, CertificateEncodingException {
        return hashToFingerprint(getHash(certificate));
    }

    public static String certificateToFingerprint(@NonNull X509Certificate certificate, String defaultValue) {
        try {
            return certificateToFingerprint(certificate);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static byte[] getHash(@NonNull X509Certificate certificate) throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest digest = java.security.MessageDigest.getInstance("SHA1");
        digest.update(certificate.getEncoded());
        return digest.digest();
    }

    public static String hashToFingerprint(@NonNull byte[] hash) {
        String[] formattedBytes = new String[hash.length];
        for (int i = 0; i < hash.length; i++) {
            // Format each byte as hex string
            formattedBytes[i] = Integer.toHexString(hash[i] & 0xff);
        }
        return Joiner.on(":").join(formattedBytes);
    }

    @NonNull
    public static Collection<String> getHostnames(@NonNull X509Certificate certificate) throws CertificateParsingException {
        Set<String> hostnames = new HashSet<>();
        for (List<?> data : certificate.getSubjectAlternativeNames()) {
            if (Objects.equals(data.get(0), 2) && data.get(1) instanceof String)
                hostnames.add((String) data.get(1));
        }
        return hostnames;
    }
}
