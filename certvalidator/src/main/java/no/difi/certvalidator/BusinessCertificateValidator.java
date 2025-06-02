package no.difi.certvalidator;

import no.digdir.certvalidator.api.CertificateValidationException;
import no.idporten.seid2.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class BusinessCertificateValidator {
    private final SEID2CertificateValidator seid2CertificateValidator;
    private static CertificateFactory certFactory;

    public static X509Certificate getCertificate(byte[] cert) throws CertificateValidationException {
        return getCertificate(new ByteArrayInputStream(cert));
    }

    public static X509Certificate getCertificate(InputStream inputStream) throws CertificateValidationException {
        try {
            if (certFactory == null)
                certFactory = CertificateFactory.getInstance("X.509");

            return (X509Certificate) certFactory.generateCertificate(inputStream);
        } catch (CertificateException e) {
            throw new CertificateValidationException(e.getMessage(), e);
        }
    }

    public BusinessCertificateValidator(SEID2CertificateValidator seid2CertificateValidator) {
        this.seid2CertificateValidator = seid2CertificateValidator;
    }

    public void validate(X509Certificate certificate) throws CertificateValidationException {
        seid2CertificateValidator.validate(certificate);
    }

    public X509Certificate validate(byte[] bytes) throws CertificateValidationException {
        X509Certificate certificate = getCertificate(bytes);
        validate(certificate);
        return certificate;
    }
}
