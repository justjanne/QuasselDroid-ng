package de.kuschku.util.certificates;

import android.content.Context;
import android.support.annotation.NonNull;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.ssl.CertificateManager;
import de.kuschku.libquassel.ssl.UnknownCertificateException;
import de.kuschku.util.ServerAddress;

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
