package no.difi.move.common.dokumentpakking;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import no.difi.move.common.cert.X509CertificateHelper;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class CreateSignedJWT {

    private CreateSignedJWT() {
        // Utility class
    }

    public static String createSignedJWT(Input input) {
        JWSObject jws = new JWSObject(getHeader(input), new Payload(input.payload));
        try {
            jws.sign(new RSASSASigner(input.privateKey));
        } catch (JOSEException e) {
            throw new SignatureException("Signing failed!");
        }
        return jws.serialize();
    }

    private static JWSHeader getHeader(Input input) {
        JWSHeader.Builder builder = new JWSHeader.Builder(input.algorithm);

        if (input.certificate != null) {
            builder.x509CertChain(getX509CertChain(input.certificate));
        }

        return builder.build();
    }

    private static List<Base64> getX509CertChain(X509Certificate certificate) {
        return Collections.singletonList(Base64.encode(X509CertificateHelper.getEncoded(certificate)));
    }

    @Value
    @Builder
    public static class Input {

        @NonNull
        String payload;
        @NonNull
        PrivateKey privateKey;
        @NonNull
        @Builder.Default
        JWSAlgorithm algorithm = JWSAlgorithm.PS256;

        // Include if the certificate chain should be added to the JWS Header
        X509Certificate certificate;
    }
}
