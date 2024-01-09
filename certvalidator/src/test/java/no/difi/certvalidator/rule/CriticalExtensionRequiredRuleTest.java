package no.difi.certvalidator.rule;


import no.difi.certvalidator.ValidatorBuilder;
import no.difi.certvalidator.api.FailedValidationException;
import no.difi.certvalidator.testutil.X509TestGenerator;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.testng.annotations.Test;

import java.security.cert.X509Certificate;

public class CriticalExtensionRequiredRuleTest extends X509TestGenerator {

    @Test(enabled = false)
    public void shouldValidateCertWithOutAnyCriticalExtentions() throws Exception {
        CriticalExtensionRequiredRule validator = new CriticalExtensionRequiredRule("2");
        X509Certificate cert = createX509Certificate();
        validator.validate(cert);
    }

    @Test
    public void shouldValidateCertWithApprovedCriticalExtentions() throws Exception {
        CriticalExtensionRequiredRule validator = CriticalExtensionRule.requires("2.10.2");
        X509Certificate cert = createX509Certificate(v3CertGen -> v3CertGen.addExtension(new ASN1ObjectIdentifier("2.10.2"), true, new byte[3]));
        validator.validate(cert);
    }

    @Test(expectedExceptions = FailedValidationException.class)
    public void shouldInvalidateCertWithACriticalExtentionsThatIsNotApproved() throws Exception {
        String approvedExtentionList = "2.10.2";
        CriticalExtensionRequiredRule validator = CriticalExtensionRule.requires(approvedExtentionList);
        X509Certificate cert = createX509Certificate(v3CertGen -> {
            String notApprovedExtention = "2.10.6";
            v3CertGen.addExtension(new ASN1ObjectIdentifier(notApprovedExtention), true, new byte[3]);
        });
        validator.validate(cert);
    }

    @Test(expectedExceptions = FailedValidationException.class)
    public void triggerExceptionWhenCertHasNoCriticalOids() throws Exception {
        ValidatorBuilder.newInstance()
                .addRule(CriticalExtensionRule.requires("12.0"))
                .build()
                .validate(getClass().getResourceAsStream("/nooids.cer"));
    }
}
