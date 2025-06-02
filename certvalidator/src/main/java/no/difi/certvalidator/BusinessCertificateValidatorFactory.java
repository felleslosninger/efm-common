package no.difi.certvalidator;

import no.digdir.certvalidator.ValidatorBuilder;
import no.digdir.certvalidator.rule.ExpirationRule;
import no.digdir.certvalidator.rule.SigningRule;
import no.idporten.seid2.CertificateAuthoritiesProperties;
import no.idporten.seid2.Environment;
import no.idporten.seid2.SEID2CertificateValidator;
import no.idporten.seid2.SEID2CertificateValidatorBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BusinessCertificateValidatorFactory {

    private final String DIGDIR_SELF_SIGNED_ROOT_CERTIFICATE = "MIIFcDCCA1igAwIBAgIEUDV0qDANBgkqhkiG9w0BAQUFADBdMSkwJwYDVQQDDCBE aWdpdGFsaXNlcmluZ3NkaXJla3RvcmF0ZXQgVEVTVDEOMAwGA1UECwwFTm9yZ2Ux IDAeBgNVBAoMF0RpZ2RpciB0ZXN0IC0gOTkxODI1ODI3MCAXDTI1MDEyMDE0NDQy N1oYDzIwOTkwMjIwMTQ0NDI3WjBdMSkwJwYDVQQDDCBEaWdpdGFsaXNlcmluZ3Nk aXJla3RvcmF0ZXQgVEVTVDEOMAwGA1UECwwFTm9yZ2UxIDAeBgNVBAoMF0RpZ2Rp ciB0ZXN0IC0gOTkxODI1ODI3MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKC AgEAgAeJsk2WEJrkZtWO6SIx5QBeTQxnydVqJZ8kXpp0+iQPLzB1sQpVgtWhSCJe 3fNrth07iW5+LUo7hjncqKFGzTixrZYMnz35P7Ty/ssE/qC1hCmCeZtA9npGxFQ3 o4ztGIZMwlTZVKNFUVG88xmyfPiqhFp6mqb0NKNjvUJrYapYAiqStOGU5urQNF1U DSbJ18Wb2WzMhACaL+OTR96iHsI/dgpq5XlynJ8+P+rzgnD+cXwg8rLTLtcaLWte jLXGziVf+Yx5EUWIXsYE/sK1GsK8/HQGPxjrIkzFfm0n1uZXW5RliR+AKxHElGfP GuOyS2fVqwQnOjNocf5nfi8hn3Eppfq+Cogvxf7RZGlTBVN8L0czZHXCfN+wkpV+ s1Uuq3UZy8pjeUcFnvckfQ5gZbLn6z8VMpE0/R2w98dbk/R6mETAX2VFtj1Nx0F8 5BE1VC0Wj5/BztT7BsSPJEm3RW2I4hBPJJlqQKI+SCWhUCPSe6/SM6k2ZsEBY8le ysQvRt/oa6vgV40Amq0980Co8I2T5F7aOjw7eCDzDt0hufOl8U7l84Mz60V+weVe IaGJVUXQpuYMoZu4yoqf1kqO30BbIBu35XNCdhlGJsMz6bh0A7U72yuOzpdoFLMp TfpvCWmzskVuacSkuHLXcP7KqcvVmZl+n5KeSL3TXrB8/5sCAwEAAaM2MDQwDwYD VR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8EBAMCAQYwEQYDVR0OBAoECEulWng0HHSh MA0GCSqGSIb3DQEBBQUAA4ICAQB9PNEhoEUHHyuIFMY3+Dd72t8M6fsaaxTfqCsF sdvFSuSXIDhwRrRebBL6iKiilN9O/LPmG6udls0eQEBe022uc8wpF1HPX+ACOrqX zrRtfUSD+0X7zy92QPc0JHE/SxWJjREsDuemoWn/zsUy9Cz9mNIdpUbX4s8Ddih6 1kJdKREK+Re9baGs6kYR6U6WEhm272IxaBmTZQBk56K4WPbs67i0N51TOZcugSNN B8jRe0Do7dSIiJ4XlvrhA7AuUhlRk0RZ9P8MiewV4Q89hSEl8xWGpCAp3u2U3uDF vuKjCVOzt2645boB57/A8w7T9DszqA3Zt87tNW8aHiZ5fO/xxOdQtMRQhj60nxmF Gyooaqb2cGaFQZQ/R0SJQ7qP5zHWoTg53gmozLbYcd7735UsRDhn3cxDkseTnoTF O5ijPLnW/m5qaxm64wZFtyZXHjXP9ElmsX301heVDWe12juiLvXTzAaUIeUtM/RX nbkWuXRiWDwwQHEDvY/EPkXObN9oLXUihDUAaQBWiYBOWVppcjs11dvjQ5F11Qdz R7Uk4zSYoBl752NyyenSQRu8vcO5M2atDpV4CWzQpOjzgTLcX6fR98nAeUQqEuKU +qH14RnsGm90PpFuY/id3RogWF6GJ773UwHbyeGprslf2D5XGnEGfET3fkqMv3DO r7JKew==";
    private final String DIGDIR_SELF_SIGNED_INTERMEDIATE_CERTIFICATE = "MIIFFDCCAvygAwIBAgIECMNOQTANBgkqhkiG9w0BAQsFADBdMSkwJwYDVQQDDCBEaWdpdGFsaXNlcmluZ3NkaXJla3RvcmF0ZXQgVEVTVDEOMAwGA1UECwwFTm9yZ2UxIDAeBgNVBAoMF0RpZ2RpciB0ZXN0IC0gOTkxODI1ODI3MCAXDTI1MDEyMDE0NDQyN1oYDzIwOTkwMjIwMTQ0NDI3WjBiMRQwEgYDVQQKEwtEaWdkaXIgdGVzdDESMBAGA1UEBRMJOTkxODI1ODI3MTYwNAYDVQQDEy1EaWdpdGFsaXNlcmluZ3NkaXJla3RvcmF0ZXQgVEVTVCBJTlRFUk1FRElBVEUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDVT/CFTcazvG995Pw157wSURr2OeJMbC1/BlY7PClI6OJ2KZu50W53hC6Fbl55EbGk+XA1hyyHH7+lawLLvpAwQlwmethcijFuOlsNUxlpF+H9m9Y/wHrZkskx8FELI+DV4u2AbGS/gyKoyXCVmip5ojlhjrx8LeE9RpxT4epzvC4DkmlelQMo4M6X0s0/33F2RxEicGGvUkLiX6FgIt+hSJozULvP9AJ3IcEovitEhenyGNeBacubcjgbEy2JLqz8QaFBNhZAkVApGa4WP/OTwR5x23mP92rYrnZqwUPx0+OXjfoxi8CjI7RXPmZMmqqnVMkCyHKlnRKcEbY9CLPFAgMBAAGjgdQwgdEwfAYDVR0jBHUwc4AIS6VaeDQcdKGhYaRfMF0xKTAnBgNVBAMMIERpZ2l0YWxpc2VyaW5nc2RpcmVrdG9yYXRldCBURVNUMQ4wDAYDVQQLDAVOb3JnZTEgMB4GA1UECgwXRGlnZGlyIHRlc3QgLSA5OTE4MjU4MjeCBFA1dKgwHQYDVR0OBBYEFJkHsIGJ2ltyEKvAdNZhWCJQmc6vMA8GA1UdEwEB/wQFMAMBAf8wDgYDVR0PAQH/BAQDAgEGMBEGA1UdIAQKMAgwBgYEVR0gADANBgkqhkiG9w0BAQsFAAOCAgEAa8Xr+1OgRx94Uj/HwLj49y/34FtuNymIEvo0YsmTc1qE8pxu0TJoqJugowvCTVsFXKq/qoQbRtVfDwN8fXAJ+G3BXJLCkOm0dAOhH0c6Ubpam7Zr5IRaa2atZ1LE9E37b9s48nU9B13bNfkwmdinjF/jzHzEB838qTQfGXO06z5h7usAgLiQ+RXnW0orB85opC3mwB1aSOOfZBp9xqBsv7vuwEFuy5yZwkPJff/uQB8ziZyN1sa/12uRmlJLc7vH5mxos3LlR3q7dQS6jPYrXAqWSFQqMaxUbGmmkuYW9KnD3TF6D5cm1G9dhDxObe4D4qsBVw5x9JrCVKSVpJgYBN95TVCVk0Q+7x2RDR8E2bFOZZjDXqDpAYBwb50jwbtQHe2Mxj6gPQ4JBh88h67mYho0q9k0vXbwSy1tREvfz8972ALFsmLy1sny0MIs8jnDAifZ9EuvkMaNle2Tj1mGE9RcLkkAJpBCcWGaGzk7GcCFQL9TOoG1GoB3M/LWWbKpsYFggncPHSkou2Knt9HzXAAP/skzVaDhhjHSMZxhJYKeFtqSdrRNJ57f8CVevC3oVCr6VHAiaeraFd9ekWoTB8VYNR6vGN3I1aoP/raY5bJU5VMmFtuqu3F5evj0IC9nMuoxvNP+JWoAgfq2uzPSPxenuiZ5qt/12Lif5PMrI6Q=";

    public BusinessCertificateValidator createValidator(String mode) throws Exception {
        try {
            return new BusinessCertificateValidatorFactory().createValidator(Mode.valueOf(mode.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid mode set in configuration, needs to be one of " + Arrays.toString(Mode.values()), e);
        }
    }

    public BusinessCertificateValidator createValidator(Mode mode) throws Exception {
        switch (mode) {
            case TEST -> { return Setup(Environment.TEST); }
            case PRODUCTION -> { return Setup(Environment.PROD); }
            case SELF_SIGNED -> { return SetupSelfSigned(); }
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }
    }

    private BusinessCertificateValidator Setup(Environment environment) throws Exception {
        CertificateAuthoritiesProperties properties = CertificateAuthoritiesProperties.defaultProperties(environment);

        if(environment == Environment.TEST) addSelfSignedTestCertificates(properties);

        SEID2CertificateValidator certificateValidator = new SEID2CertificateValidatorBuilder(environment)
            .withProperties(properties)
            .build();

        return new BusinessCertificateValidator(certificateValidator);
    }

    private BusinessCertificateValidator SetupSelfSigned(){
        no.digdir.certvalidator.Validator validator = ValidatorBuilder.newInstance()
            .addRule(new ExpirationRule())
            .addRule(new SigningRule(SigningRule.Kind.SELF_SIGNED_ONLY))
            .build();

        return new BusinessCertificateValidator(new SEID2CertificateValidator(validator));
    }

    private void addSelfSignedTestCertificates(CertificateAuthoritiesProperties properties){
        Set<String> root = new HashSet<>(properties.getRootCertificates());
        Set<String> intermediate = new HashSet<>(properties.getIntermediateCertificates());

        root.add(DIGDIR_SELF_SIGNED_ROOT_CERTIFICATE);
        intermediate.add(DIGDIR_SELF_SIGNED_INTERMEDIATE_CERTIFICATE);

        properties.setRootCertificates(Set.copyOf(root));
        properties.setIntermediateCertificates(Set.copyOf(intermediate));
    }
}
