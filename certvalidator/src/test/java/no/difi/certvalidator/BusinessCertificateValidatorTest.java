package no.difi.certvalidator;

import no.digdir.certvalidator.api.CertificateValidationException;
import no.idporten.seid2.SEID2CertificateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public class BusinessCertificateValidatorTest {

    private SEID2CertificateValidator seid2CertificateValidator;
    private BusinessCertificateValidator businessCertificateValidator;
    private final String certificatePath = "/selfsigned.cer";
    private byte[] certificate;

    @BeforeEach
    public void setup() throws IOException {
        seid2CertificateValidator = Mockito.mock(SEID2CertificateValidator.class);
        businessCertificateValidator = new BusinessCertificateValidator(seid2CertificateValidator);
        certificate = Objects.requireNonNull(getClass().getResourceAsStream(certificatePath)).readAllBytes();
    }

    @Test
    public void getCertificateFromByteArrayReturnsCertificate() throws CertificateValidationException {
        X509Certificate certificate = BusinessCertificateValidator.getCertificate(this.certificate);
        assertNotNull(certificate);
    }

    @Test
    public void getCertificateFromInputStreamReturnsCertificate() throws CertificateValidationException {
        X509Certificate certificate = BusinessCertificateValidator.getCertificate(getClass().getResourceAsStream(certificatePath));
        assertNotNull(certificate);
    }

    @Test
    public void validateCertificateFromX509CertificateCallsSEID2CertificateValidator() throws CertificateValidationException {
        X509Certificate certificate = BusinessCertificateValidator.getCertificate(getClass().getResourceAsStream(certificatePath));
        businessCertificateValidator.validate(certificate);

        verify(seid2CertificateValidator).validate(any(X509Certificate.class));
    }

    @Test
    public void validateCertificateFromByteArrayCallsSEID2CertificateValidator() throws CertificateValidationException {
        businessCertificateValidator.validate(certificate);

        verify(seid2CertificateValidator).validate(any(X509Certificate.class));
    }
}
