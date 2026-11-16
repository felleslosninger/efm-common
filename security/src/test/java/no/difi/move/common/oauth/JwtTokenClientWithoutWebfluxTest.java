package no.difi.move.common.oauth;

import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Køyrer i eiga surefire-køyring (without-webflux) der webflux og reactor er fjerna frå classpath,
 * slik ein Spring MVC-app ser modulen. Sjå security/pom.xml.
 */
class JwtTokenClientWithoutWebfluxTest {

    private TokenEndpointStub tokenEndpoint;

    @BeforeEach
    void setUp() throws Exception {
        tokenEndpoint = new TokenEndpointStub();
    }

    @AfterEach
    void tearDown() {
        tokenEndpoint.close();
    }

    @Test
    void webfluxAndReactorAreNotOnClasspath() {
        assertThatThrownBy(() -> Class.forName("reactor.core.publisher.Mono"))
            .isInstanceOf(ClassNotFoundException.class);
        assertThatThrownBy(() -> Class.forName("org.springframework.web.reactive.function.client.WebClient"))
            .isInstanceOf(ClassNotFoundException.class);
    }

    @Test
    void fetchTokenWorksWithoutWebflux() {
        var client = new JwtTokenClient(tokenEndpoint.config());

        JwtTokenResponse response = client.fetchToken();

        assertThat(response.getAccessToken()).isEqualTo("test-access-token");
        assertThat(response.getExpiresIn()).isEqualTo(120);
        assertThat(response.getScope()).isEqualTo("move/dpo.read");

        assertThat(tokenEndpoint.requests()).singleElement().satisfies(request -> {
            assertThat(request.contentType()).startsWith("application/x-www-form-urlencoded");
            assertThat(request.form())
                .containsEntry("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                .containsKey("assertion");
            assertThat(SignedJWT.parse(request.form().get("assertion")).getJWTClaimsSet().getIssuer())
                .isEqualTo("test-client");
        });
    }

    @Test
    void fetchTokenThrowsHttpClientErrorExceptionOnClientError() {
        tokenEndpoint.respondWith(400, "{\"error\":\"invalid_grant\"}");
        var client = new JwtTokenClient(tokenEndpoint.config());

        // HttpClientErrorException er det @Retryable på fetchToken() prøver på nytt for.
        assertThatThrownBy(client::fetchToken)
            .isInstanceOf(HttpClientErrorException.class)
            .hasMessageContaining("400");
    }

    @Test
    void generateJwtWorksWithoutWebflux() throws Exception {
        var client = new JwtTokenClient(tokenEndpoint.config());

        var claims = SignedJWT.parse(client.generateJWT()).getJWTClaimsSet();

        assertThat(claims.getIssuer()).isEqualTo("test-client");
        assertThat(claims.getAudience()).containsExactly("https://test.maskinporten.no/");
        assertThat(claims.getStringClaim("scope")).isEqualTo("move/dpo.read");
    }
}
