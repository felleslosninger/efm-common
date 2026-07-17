package no.difi.certvalidator;

import no.idporten.seid2.SEID2CertificateValidator;
import no.idporten.validator.certificate.api.CertificateValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class BusinessCertificateValidatorTest {

    private SEID2CertificateValidator seid2CertificateValidator;
    private BusinessCertificateValidator businessCertificateValidator;
    private final String certificatePath = "/selfsigned.cer";
    private byte[] certificateBytes;

    @BeforeEach
    void setup() throws IOException {
        seid2CertificateValidator = Mockito.mock(SEID2CertificateValidator.class);
        businessCertificateValidator = new BusinessCertificateValidator(seid2CertificateValidator);
        certificateBytes = Objects.requireNonNull(getClass().getResourceAsStream(certificatePath)).readAllBytes();
    }

    @Test
    void getCertificateFromByteArrayReturnsCertificate() throws CertificateValidationException {
        X509Certificate certificate = BusinessCertificateValidator.getCertificate(this.certificateBytes);
        assertNotNull(certificate);
    }

    @Test
    void getCertificateFromInputStreamReturnsCertificate() throws CertificateValidationException {
        X509Certificate certificate = BusinessCertificateValidator.getCertificate(getClass().getResourceAsStream(certificatePath));
        assertNotNull(certificate);
    }

    @Test
    void validateCertificateFromX509CertificateCallsSEID2CertificateValidator() throws CertificateValidationException {
        X509Certificate certificate = BusinessCertificateValidator.getCertificate(getClass().getResourceAsStream(certificatePath));
        businessCertificateValidator.validate(certificate);

        verify(seid2CertificateValidator).validate(any(X509Certificate.class));
    }

    @Test
    void validateCertificateFromByteArrayCallsSEID2CertificateValidator() throws CertificateValidationException {
        businessCertificateValidator.validate(certificateBytes);

        verify(seid2CertificateValidator).validate(any(X509Certificate.class));
    }
}
