package no.difi.move.common.cert.validator;

import no.difi.certvalidator.Validator;
import no.difi.certvalidator.api.CertificateValidationException;
import org.junit.Before;
import org.junit.Test;

import java.security.cert.X509Certificate;

public class BusinessCertificateValidatorTest {

    private X509Certificate certificate;
    private BusinessCertificateValidator businessCertificateValidator;

    @Before
    public void beforeClass() throws CertificateValidationException {
        certificate = Validator.getCertificate(getClass().getResourceAsStream("/bc-test-digdir.cer"));
        businessCertificateValidator = BusinessCertificateValidator.of(Mode.TEST);
    }

    @Test
    public void simpleTest() throws Exception {
        businessCertificateValidator.validate(certificate);
        businessCertificateValidator.validate(certificate.getEncoded());
        businessCertificateValidator.validate(getClass().getResourceAsStream("/bc-test-digdir.cer"));
    }

    @Test(expected = BusinessCertificateValidatorLoadingException.class)
    public void receiptNotFound() {
        BusinessCertificateValidator.of("/invalid-path.xml");
    }
}