package de.kuschku.libquassel.events;

import java.security.cert.X509Certificate;

public class CertificateAcceptedEvent {
    public final X509Certificate certificate;

    public CertificateAcceptedEvent(X509Certificate certificate) {
        this.certificate = certificate;
    }
}
