package no.difi.move.common.dokumentpakking;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

public class JavaWebToken {

    private JavaWebToken() {
        // Utility class
    }

    public static String sign(String payload, PrivateKey privateKey) {
        JWSObject jws = new JWSObject(new JWSHeader.Builder(JWSAlgorithm.RS256).build(), new Payload(payload));
        try {
            jws.sign(new RSASSASigner(privateKey));
        } catch (JOSEException e) {
            throw new SignatureException("Signing failed!");
        }
        return jws.serialize();
    }

    public static String verify(String payload, X509Certificate certificate) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(payload);

            RSASSAVerifier verifier = new RSASSAVerifier((RSAPublicKey) certificate.getPublicKey());

            if (!signedJWT.verify(verifier)) {
                throw new SignatureException("Signature verification failed");
            }

            return signedJWT.getPayload().toString();

        } catch (JOSEException e) {
            throw new SignatureException("Signature verification failed!");
        } catch (ParseException e) {
            throw new SignatureException("Parsing signed JWT failed!");
        }
    }
}
