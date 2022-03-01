package no.difi.certvalidator;

import no.difi.certvalidator.api.CertificateValidationException;
import org.junit.Before;
import org.junit.Test;

import java.security.cert.X509Certificate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BusinessCertificateValidatorTest {

    private enum Mode {

        @RecipePath("/recipe-virksert-test.xml")
        TEST
    }

    private X509Certificate certificate;
    private BusinessCertificateValidator businessCertificateValidator;

    @Before
    public void beforeEach() throws CertificateValidationException {
        certificate = Validator.getCertificate(getClass().getResourceAsStream("/virksert-test-riksantikvaren.cer"));
        businessCertificateValidator = BusinessCertificateValidator.of(Mode.TEST);
    }

    @Test
    public void simpleTest() throws Exception {
        businessCertificateValidator.validate(certificate);
        businessCertificateValidator.validate(certificate.getEncoded());
        businessCertificateValidator.validate(getClass().getResourceAsStream("/virksert-test-riksantikvaren.cer"));
    }

    @Test()
    public void receiptNotFound() {
        assertThatThrownBy(() -> BusinessCertificateValidator.of("/invalid-path.xml"))
                .isInstanceOf(IllegalStateException.class);
    }
}