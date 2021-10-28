package no.difi.certvalidator.rule;

import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.certvalidator.api.FailedValidationException;

import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Validation making sure certificate doesn't expire in n milliseconds.
 */
public class ExpirationSoonRule extends AbstractRule {

    private long millis;

    public ExpirationSoonRule(long millis) {
        this.millis = millis;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(X509Certificate certificate) throws CertificateValidationException {
        Duration expiry = Duration.ofMillis(millis);
        LocalDateTime notAfter = certificate.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (notAfter.isBefore(LocalDateTime.now().plus(expiry)))
            throw new FailedValidationException(String.format("Certificate expires in less than %s milliseconds.", millis));
    }
}
