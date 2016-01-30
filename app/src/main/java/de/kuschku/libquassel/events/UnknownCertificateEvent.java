package de.kuschku.libquassel.events;

import java.security.cert.X509Certificate;

import de.kuschku.libquassel.ssl.UnknownCertificateException;
import de.kuschku.util.ServerAddress;

public class UnknownCertificateEvent {
    public final X509Certificate certificate;
    public final ServerAddress address;

    public UnknownCertificateEvent(X509Certificate certificate, ServerAddress address) {
        this.certificate = certificate;
        this.address = address;
    }

    public UnknownCertificateEvent(UnknownCertificateException cause) {
        this(cause.certificate, cause.address);
    }
}
