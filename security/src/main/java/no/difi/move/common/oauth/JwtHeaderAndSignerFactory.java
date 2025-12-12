package no.difi.move.common.oauth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64;
import lombok.extern.slf4j.Slf4j;
import no.difi.move.common.cert.KeystoreHelper;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

@Slf4j
public class JwtHeaderAndSignerFactory {

    private JwtHeaderAndSignerFactory() {
        // Utility class
    }

    public static RSASSASigner getSigner(JwtTokenConfig config) {
        if (AuthenticationType.JWK.equals(config.getAuthenticationType())) {
            return getSigner(config.getJwk().getPath());
        } else {
            return getSigner(new KeystoreHelper(config.getKeystore()));
        }
    }

    public static JWSHeader getJwsHeader(JwtTokenConfig config) {
        if (AuthenticationType.JWK.equals(config.getAuthenticationType())) {
            return getJwsHeader(config.getJwk().getPath());
        } else {
            return getJwsHeader(new KeystoreHelper(config.getKeystore()));
        }
    }

    private static RSASSASigner getSigner(Resource path) {
        try {
            RSAKey rsaJWK = getJwk(path);
            return new RSASSASigner(rsaJWK);
        } catch (JOSEException e) {
            var message = "Failed to initialize RSASSASigner";
            log.error(message, e);
            throw new IllegalStateException(message, e);
        }
    }

    private static JWSHeader getJwsHeader(Resource path) {
        RSAKey rsaJWK = getJwk(path);
        return new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build();
    }

    private static RSAKey getJwk(Resource path) {
        try {
            var jwkFile = path.getContentAsString(StandardCharsets.UTF_8);
            return RSAKey.parse(jwkFile);
        } catch (ParseException | IOException e) {
            var message = "Failed to load or parse RSA JWK from file";
            log.error(message, e);
            throw new CertificateEncodingRuntimeException(message, e);
        }
    }

    private static JWSHeader getJwsHeader(KeystoreHelper keystoreHelper) {
        return new JWSHeader.Builder(JWSAlgorithm.RS256)
            .x509CertChain(getCertChain(keystoreHelper))
            .build();
    }

    private static List<Base64> getCertChain(KeystoreHelper keystoreHelper) {
        try {
            return Collections.singletonList(Base64.encode(keystoreHelper.getX509Certificate().getEncoded()));
        } catch (CertificateEncodingException e) {
            var message = "Could not get encoded certificate";
            log.error(message, e);
            throw new CertificateEncodingRuntimeException(message, e);
        }
    }

    private static RSASSASigner getSigner(KeystoreHelper keystoreHelper) {
        RSASSASigner s = new RSASSASigner(keystoreHelper.loadPrivateKey());
        if (keystoreHelper.shouldLockProvider()) {
            s.getJCAContext().setProvider(keystoreHelper.getKeyStore().getProvider());
        }
        return s;
    }

    public static class CertificateEncodingRuntimeException extends RuntimeException {
        public CertificateEncodingRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
