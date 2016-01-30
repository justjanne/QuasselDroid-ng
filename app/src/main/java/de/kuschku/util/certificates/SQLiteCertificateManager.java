package de.kuschku.util.certificates;

import android.content.Context;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.ssl.CertificateManager;
import de.kuschku.libquassel.ssl.UnknownCertificateException;
import de.kuschku.util.ServerAddress;

public class SQLiteCertificateManager implements CertificateManager {
    private CertificateDatabaseHandler handler;

    public SQLiteCertificateManager(Context context) {
        this.handler = new CertificateDatabaseHandler(context);
    }

    @Override
    public boolean isTrusted(X509Certificate certificate, ServerAddress core) {
        try {
            certificate.checkValidity();
            return handler.findCertificates(core.host).contains(CertificateUtils.certificateToFingerprint(certificate));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean addCertificate(X509Certificate certificate, ServerAddress core) {
        try {
            return handler.addCertificate(CertificateUtils.certificateToFingerprint(certificate), core.host);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeCertificate(X509Certificate certificate, ServerAddress core) {
        try {
            return handler.removeCertificate(CertificateUtils.certificateToFingerprint(certificate), core.host);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removeAllCertificates(ServerAddress core) {
        return handler.removeCertificates(core.host);
    }

    @Override
    public void checkTrusted(X509Certificate certificate, ServerAddress address) throws UnknownCertificateException {
        if (!isTrusted(certificate, address))
            throw new UnknownCertificateException(certificate, address);
    }

    public List<String> findCertificates(ServerAddress core) {
        return handler.findCertificates(core.host);
    }

    public Map<String, Collection<String>> findAllCertificates() {
        return handler.findAllCertificates();
    }
}
