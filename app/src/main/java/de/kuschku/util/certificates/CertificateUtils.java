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

import de.kuschku.util.Objects;

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
