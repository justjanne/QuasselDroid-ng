package de.kuschku.libquassel.ssl;

import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.kuschku.util.ServerAddress;

public interface CertificateManager {
    boolean isTrusted(X509Certificate certificate, ServerAddress core);

    boolean addCertificate(X509Certificate certificate, ServerAddress core);

    boolean removeCertificate(X509Certificate certificate, ServerAddress core);

    boolean removeAllCertificates(ServerAddress core);

    List<String> findCertificates(ServerAddress core);

    Map<String, Collection<String>> findAllCertificates();

    void checkTrusted(X509Certificate certificate, ServerAddress address) throws UnknownCertificateException;
}
