/*
 * Copyright 2017 Norwegian Agency for Public Management and eGovernment (Difi)
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package no.difi.move.common.cert.validator;

import no.difi.certvalidator.Validator;
import no.difi.certvalidator.ValidatorLoader;
import no.difi.certvalidator.api.CertificateValidationException;
import no.difi.certvalidator.lang.ValidatorParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;

public class BusinessCertificateValidator {

    private static final String VALIDATION_OF_BUSINESS_CERTIFICATE_FAILED = "Validation of business certificate failed!";

    /**
     * Holds the actual certificate validator.
     */
    private final Validator validator;

    /**
     * Use of {@link Mode} to load the certificate validator using resources part of this package.
     *
     * @param mode One of the modes part of this package.
     * @return Validator for validation of business certificates.
     * @throws BusinessCertificateValidatorLoadingException when loading of validator is unsuccessful.
     */
    public static BusinessCertificateValidator of(Mode mode) throws BusinessCertificateValidatorLoadingException {
        return of((Enum<Mode>) mode);
    }

    /**
     * Use of annotated enum to load the certificate validator. This is overloaded by #of(Mode) to allow for easy
     * detection in IDE of the {@link Mode} enums.
     * <p>
     * Enums used in this method must be annotated with {@link RecipePath}.
     *
     * @param mode Some enum annotated with {@link RecipePath}
     * @return Validator for validation of business certificates.
     * @throws BusinessCertificateValidatorLoadingException when loading of validator is unsuccessful.
     */
    public static BusinessCertificateValidator of(Enum<?> mode) throws BusinessCertificateValidatorLoadingException {
        return of(pathFromEnum(mode));
    }

    /**
     * Loads a certificate validator by providing mode as {@link String}. When mode is not detected is the value
     * expected to be the path to validator recipe in class path.
     *
     * @param modeString Mode as string.
     * @return Validator for validation of business certificates.
     * @throws BusinessCertificateValidatorLoadingException when loading of validator is unsuccessful.
     */
    public static BusinessCertificateValidator of(String modeString) throws BusinessCertificateValidatorLoadingException {
        String path = Mode.of(modeString)
                .map(BusinessCertificateValidator::pathFromEnum)
                .orElse(modeString);

        return new BusinessCertificateValidator(path);
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
     * @param path Path to recipe file in class path.
     * @throws BusinessCertificateValidatorLoadingException when loading of validator is unsuccessful.
     */
    private BusinessCertificateValidator(String path) throws BusinessCertificateValidatorLoadingException {
        try (InputStream inputStream = getClass().getResourceAsStream(path)) {
            this.validator = ValidatorLoader.newInstance().build(inputStream);
        } catch (IOException | ValidatorParsingException e) {
            throw new BusinessCertificateValidatorLoadingException("Unable to load certificate validator.", e);
        }
    }

    /**
     * Validate certificate.
     *
     * @param certificate Certificate as a {@link X509Certificate} object.
     * @throws BusinessCertificateValidationException validation failed.
     */
    public void validate(X509Certificate certificate) throws BusinessCertificateValidationException {
        try {
            validator.validate(certificate);
        } catch (CertificateValidationException e) {
            throw new BusinessCertificateValidationException(VALIDATION_OF_BUSINESS_CERTIFICATE_FAILED, e);
        }
    }

    /**
     * Validate certificate.
     *
     * @param certificate Certificate as a byte array.
     * @throws BusinessCertificateValidationException validation failed.
     */
    public void validate(byte[] certificate) throws BusinessCertificateValidationException {
        try {
            validator.validate(certificate);
        } catch (CertificateValidationException e) {
            throw new BusinessCertificateValidationException(VALIDATION_OF_BUSINESS_CERTIFICATE_FAILED, e);
        }
    }

    /**
     * Validate certificate.
     *
     * @param inputStream Certificate from an {@link InputStream}.
     * @throws BusinessCertificateValidationException validation failed.
     */
    public void validate(InputStream inputStream) throws BusinessCertificateValidationException {
        try {
            validator.validate(inputStream);
        } catch (CertificateValidationException e) {
            throw new BusinessCertificateValidationException(VALIDATION_OF_BUSINESS_CERTIFICATE_FAILED, e);
        }
    }
}
