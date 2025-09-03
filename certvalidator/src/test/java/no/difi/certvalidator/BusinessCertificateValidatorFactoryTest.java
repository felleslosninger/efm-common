package no.difi.certvalidator;

import no.idporten.validator.certificate.api.CertificateValidationException;
import no.idporten.validator.certificate.api.FailedValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class BusinessCertificateValidatorFactoryTest {

    private final byte[] digdirTestCertificateBytes;
    private final X509Certificate digdirTestCertificate;

    public BusinessCertificateValidatorFactoryTest() throws IOException, CertificateValidationException {
        digdirTestCertificateBytes = Objects.requireNonNull(getClass().getResourceAsStream("/digdirTestCertificate.cer")).readAllBytes();
        digdirTestCertificate = BusinessCertificateValidator.getCertificate(digdirTestCertificateBytes);
    }

    @Test
    public void throwExceptionWhenInvalidModeIsUsed() {
        assertThrows(IllegalStateException.class, () -> new BusinessCertificateValidatorFactory().createValidator("dummy"),
            "Should not create a validator for non existing mode.");
    }

    @ParameterizedTest
    @EnumSource(Mode.class)
    public void noExceptionIsThrownWhenUsingValidMode(Mode mode) {
        assertDoesNotThrow(() -> new BusinessCertificateValidatorFactory().createValidator(mode),
            "Should create a validator for every possible mode.");
    }

    @Test
    public void acceptSelfSignedCertificate() throws Exception {
        BusinessCertificateValidator businessCertificateValidator = new BusinessCertificateValidatorFactory().createValidator(Mode.SELF_SIGNED);
        byte[] certificateBytes = Objects.requireNonNull(getClass().getResourceAsStream("/selfsigned.cer")).readAllBytes();
        X509Certificate certificate = BusinessCertificateValidator.getCertificate(certificateBytes);

        assertDoesNotThrow(() -> businessCertificateValidator.validate(certificateBytes), "Accept self signed certificate when mode is " + Mode.SELF_SIGNED);
        assertDoesNotThrow(() -> businessCertificateValidator.validate(certificate), "Accept self signed certificate when mode is " + Mode.SELF_SIGNED);
    }

    @Test
    public void acceptDigdirTestcertificate() throws Exception {
        BusinessCertificateValidator businessCertificateValidator = new BusinessCertificateValidatorFactory().createValidator(Mode.TEST);

        assertDoesNotThrow(() -> businessCertificateValidator.validate(digdirTestCertificateBytes), "Self signed digdir test certificates should be accepted in " + Mode.TEST + " mode.");
        assertDoesNotThrow(() -> businessCertificateValidator.validate(digdirTestCertificate), "Self signed digdir test certificates should be accepted in " + Mode.TEST + " mode.");
    }

    @Test
    public void noSelfSignedDigdirInProd() throws Exception {
        BusinessCertificateValidator businessCertificateValidator = new BusinessCertificateValidatorFactory().createValidator(Mode.PRODUCTION);

        Exception exception = assertThrows(FailedValidationException.class, () -> businessCertificateValidator.validate(digdirTestCertificateBytes), "Should not accept digdir self signed test certificates in production mode.");
        Exception exception2 = assertThrows(FailedValidationException.class, () -> businessCertificateValidator.validate(digdirTestCertificate), "Should not accept digdir self signed test certificates in production mode.");
        assertEquals("No issuer certificate for certificate in certification path found.", exception.getMessage(), "Should not accept digdir self signed test certificates in production mode.");
        assertEquals("No issuer certificate for certificate in certification path found.", exception2.getMessage(), "Should not accept digdir self signed test certificates in production mode.");
    }
}
