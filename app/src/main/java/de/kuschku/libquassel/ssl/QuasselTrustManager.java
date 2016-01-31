package de.kuschku.libquassel.ssl;

import android.support.annotation.NonNull;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import de.kuschku.util.ServerAddress;
import de.kuschku.util.certificates.CertificateUtils;

public class QuasselTrustManager implements X509TrustManager {
    @NonNull
    private final X509TrustManager wrapped;
    @NonNull
    private final CertificateManager certificateManager;
    @NonNull
    private ServerAddress address;

    public QuasselTrustManager(@NonNull X509TrustManager wrapped, @NonNull CertificateManager certificateManager, @NonNull ServerAddress address) {
        this.wrapped = wrapped;
        this.certificateManager = certificateManager;
        this.address = address;
    }

    public static QuasselTrustManager fromFactory(@NonNull TrustManagerFactory factory, @NonNull CertificateManager certificateManager, @NonNull ServerAddress address) throws GeneralSecurityException {
        TrustManager[] managers = factory.getTrustManagers();
        for (TrustManager manager : managers) {
            if (manager instanceof X509TrustManager) {
                return new QuasselTrustManager((X509TrustManager) manager, certificateManager, address);
            }
        }
        throw new GeneralSecurityException("Couldn’t find trustmanager provided by factory");
    }

    public static QuasselTrustManager fromDefault(@NonNull CertificateManager certificateManager, @NonNull ServerAddress address) throws GeneralSecurityException {
        TrustManagerFactory factory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        factory.init((KeyStore) null);
        return fromFactory(factory, certificateManager, address);
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        wrapped.checkClientTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            wrapped.checkServerTrusted(chain, authType);
            chain[0].checkValidity();
            if (!CertificateUtils.getHostnames(chain[0]).contains(address.host))
                throw new CertificateException("Hostname not in certificate");
        } catch (CertificateException e) {
            certificateManager.checkTrusted(chain[0], address);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return wrapped.getAcceptedIssuers();
    }
}
