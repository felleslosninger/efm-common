package no.difi.certvalidator;

import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.certvalidator.lang.ValidatorParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;

@SuppressWarnings("unused")
public class BusinessCertificateValidator {

    private static final String VALIDATION_OF_BUSINESS_CERTIFICATE_FAILED = "Validation of business certificate failed!";

    /**
     * Holds the actual certificate validator.
     */
    private final Validator validator;

    /**
     * Enums used in this method must be annotated with {@link RecipePath}.
     *
     * @param mode Some enum annotated with {@link RecipePath}
     * @return Validator for validation of business certificates.
     * @throws IllegalStateException when loading of validator is unsuccessful.
     */
    public static BusinessCertificateValidator of(Enum<?> mode) {
        return of(pathFromEnum(mode));
    }

    /**
     * Loads a certificate validator by providing mode as {@link String}. When mode is not detected is the value
     * expected to be the path to validator recipe in class path.
     *
     * @param path Path to recipe file in class path.
     * @return Validator for validation of business certificates.
     * @throws IllegalStateException when loading of validator is unsuccessful.
     */
    public static BusinessCertificateValidator of(String path) {
        try (InputStream inputStream = BusinessCertificateValidator.class.getResourceAsStream(path)) {
            return new BusinessCertificateValidator(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load certificate validator", e);
        }
    }

    public static BusinessCertificateValidator of(InputStream inputStream) {
        return new BusinessCertificateValidator(inputStream);
    }

    /**
     * Returns path found in {@link RecipePath} annotation on a given {@link Enum}.
     *
     * @param mode Some enum.
     * @return Path from {@link RecipePath} annotation.
     */
    private static String pathFromEnum(Enum<?> mode) {
        try {
            return mode.getClass().getField(mode.name()).getAnnotation(RecipePath.class).value();
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Something is terribly wrong.", e);
        }
    }

    /**
     * Loads the certificate validator by using the path to the recipe file found in class path.
     *
     * @param inputStream InputStream containing the recipe.
     * @throws IllegalStateException when loading of validator is unsuccessful.
     */
    private BusinessCertificateValidator(InputStream inputStream) {
        validator = getValidator(inputStream);
    }

    private Validator getValidator(InputStream inputStream) {
        try {
            return ValidatorLoader.newInstance().build(inputStream);
        } catch (ValidatorParsingException e) {
            throw new IllegalStateException("Unable to load certificate validator", e);
        }
    }

    /**
     * Validate certificate.
     *
     * @param certificate Certificate as a {@link X509Certificate} object.
     * @throws IllegalStateException validation failed.
     */
    public void validate(X509Certificate certificate) {
        try {
            validator.validate(certificate);
        } catch (CertificateValidationException e) {
            throw new IllegalStateException(VALIDATION_OF_BUSINESS_CERTIFICATE_FAILED, e);
        }
    }

    /**
     * Validate certificate.
     *
     * @param certificate Certificate as a byte array.
     * @throws IllegalStateException validation failed.
     */
    public void validate(byte[] certificate) {
        try {
            validator.validate(certificate);
        } catch (CertificateValidationException e) {
            throw new IllegalStateException(VALIDATION_OF_BUSINESS_CERTIFICATE_FAILED, e);
        }
    }

    /**
     * Validate certificate.
     *
     * @param inputStream Certificate from an {@link InputStream}.
     * @throws IllegalStateException if validation failed.
     */
    public void validate(InputStream inputStream) {
        try {
            validator.validate(inputStream);
        } catch (CertificateValidationException e) {
            throw new IllegalStateException(VALIDATION_OF_BUSINESS_CERTIFICATE_FAILED, e);
        }
    }
}
