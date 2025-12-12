package no.difi.move.common.oauth;

import no.difi.move.common.config.JwkProperties;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtHeaderAndSignerFactoryTest {

    @Test
    void getJwsHeaderFromJwk() {

        var jwk = new JwkProperties();
        jwk.setPath(new ClassPathResource(("test.jwk")));
        var config = new JwtTokenConfig();
        config.setJwk(jwk);
        config.setAuthenticationType(AuthenticationType.JWK);

        var result = JwtHeaderAndSignerFactory.getJwsHeader(config);

        assertNotNull(result, "Should return a header");
        assertEquals("0237e536-0241-4751-8213-fdac51596389", result.getKeyID(), "JwsHeader should contain correct key ID");
    }

    @Test
    void getSignerFromJwk() {

        var jwk = new JwkProperties();
        jwk.setPath(new ClassPathResource(("test.jwk")));
        var config = new JwtTokenConfig();
        config.setJwk(jwk);
        config.setAuthenticationType(AuthenticationType.JWK);

        var result = JwtHeaderAndSignerFactory.getSigner(config);

        assertNotNull(result, "Should return a signer");
    }
}
