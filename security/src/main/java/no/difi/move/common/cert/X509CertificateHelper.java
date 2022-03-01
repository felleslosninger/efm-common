package no.difi.move.common.cert;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@SuppressWarnings("unused")
public class X509CertificateHelper {

    private X509CertificateHelper() {
        // Utility class
    }

    public static byte[] getEncoded(X509Certificate certificate) {
        try {
            return certificate.getEncoded();
        } catch (CertificateEncodingException e) {
            throw new IllegalStateException("Couldn't get encoded certificate!", e);
        }
    }

    public static X509Certificate createX509Certificate(byte[] certificate) {
        return createX509Certificate(new ByteArrayInputStream(certificate));
    }

    public static X509Certificate createX509Certificate(InputStream inputStream) {
        try {
            return (X509Certificate) CertificateFactory
                    .getInstance("X509")
                    .generateCertificate(inputStream);
        } catch (CertificateException e) {
            throw new IllegalStateException("Could not create X509Certificate!", e);
        }
    }
}
