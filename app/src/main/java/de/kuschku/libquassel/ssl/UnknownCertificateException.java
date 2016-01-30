package de.kuschku.libquassel.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import de.kuschku.util.ServerAddress;

public class UnknownCertificateException extends CertificateException {
    public final X509Certificate certificate;
    public final ServerAddress address;

    public UnknownCertificateException(X509Certificate certificate, ServerAddress address) {
        this.certificate = certificate;
        this.address = address;
    }
}
