package no.difi.move.common.dokumentpakking;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.util.X509CertChainUtils;
import lombok.RequiredArgsConstructor;
import no.difi.certvalidator.BusinessCertificateValidator;
import no.idporten.validator.certificate.api.CertificateValidationException;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class VerifyJWT {

    private final BusinessCertificateValidator businessCertificateValidator;

    public JWSObject verify(String jwt, X509Certificate certificate) {
        JWSObject jwsObject = getJwsObject(jwt);
        RSASSAVerifier verifier = new RSASSAVerifier((RSAPublicKey) certificate.getPublicKey());
        verify(jwsObject, verifier);
        return jwsObject;
    }

    public JWSObject verify(String jwt) {
        JWSObject jwsObject = getJwsObject(jwt);
        RSASSAVerifier verifier = getVerifier(jwsObject);
        verify(jwsObject, verifier);
        return jwsObject;
    }

    public void verify(JWSObject jwsObject, RSASSAVerifier verifier) {
        try {
            if (!jwsObject.verify(verifier)) {
                throw new SignatureException("Signature verification failed");
            }

        } catch (JOSEException e) {
            throw new SignatureException("Signature verification failed!");
        }
    }

    private JWSObject getJwsObject(String jwt) {
        if (jwt == null) {
            throw new SignatureException("JWT string cannot be null");
        }
        try {
            return JWSObject.parse(jwt);
        } catch (ParseException e) {
            throw new SignatureException("Parsing JWT failed!", e);
        }
    }

    private RSASSAVerifier getVerifier(JWSObject jwsObject) {
        return new RSASSAVerifier((RSAPublicKey) getValidatedSigningCertificate(jwsObject).getPublicKey());
    }

    private X509Certificate getValidatedSigningCertificate(JWSObject jwsObject) {
        X509Certificate certificate = getSigningCertificate(jwsObject);
        try {
            businessCertificateValidator.validate(certificate);
        } catch (CertificateValidationException e) {
            throw new SignatureException("Validation of business certificate failed!", e);
        }
        return certificate;
    }

    private X509Certificate getSigningCertificate(JWSObject jwsObject) {
        try {
            return X509CertChainUtils.parse(jwsObject.getHeader().getX509CertChain())
                .stream()
                .findFirst()
                .orElseThrow(() -> new SignatureException("Could not find signingKeystore certificate!"));
        } catch (ParseException e) {
            throw new SignatureException("Could not parse signingKeystore certificate!", e);
        }
    }

}
