package no.difi.move.common.oauth;

import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpTimeoutException;
import java.time.Duration;

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
    void fetchTokenTimesOutWhenTokenEndpointDoesNotAnswer() {
        tokenEndpoint.respondAfter(Duration.ofSeconds(30));
        var config = tokenEndpoint.config();
        config.setReadTimeout(Duration.ofMillis(500));
        var client = new JwtTokenClient(config);

        long start = System.nanoTime();
        assertThatThrownBy(client::fetchToken)
            .isInstanceOf(ResourceAccessException.class)
            .hasRootCauseInstanceOf(HttpTimeoutException.class);
        Duration elapsed = Duration.ofNanos(System.nanoTime() - start);

        // Den konfigurerte read timeout-en blir brukt: ikkje før 500 ms, og langt før endepunktet ville svart.
        assertThat(elapsed).isBetween(Duration.ofMillis(450), Duration.ofSeconds(5));
        // Klienten prøver ikkje på nytt av seg sjølv.
        assertThat(tokenEndpoint.requests()).hasSize(1);
    }

    @Test
    void fetchTokenTimesOutWhenTokenEndpointStallsInBody() {
        tokenEndpoint.stallBodyFor(Duration.ofSeconds(30));
        var config = tokenEndpoint.config();
        config.setReadTimeout(Duration.ofMillis(500));
        var client = new JwtTokenClient(config);

        long start = System.nanoTime();
        // Headerane er komne, så RestClient ville elles kasta ein generell RestClientException.
        assertThatThrownBy(client::fetchToken).isInstanceOf(ResourceAccessException.class);

        assertThat(Duration.ofNanos(System.nanoTime() - start)).isBetween(Duration.ofMillis(450), Duration.ofSeconds(5));
    }

    @Test
    void invalidJsonFromTokenEndpointIsNotReportedAsResourceAccessException() {
        tokenEndpoint.respondWith(200, "not json");
        var client = new JwtTokenClient(tokenEndpoint.config());

        assertThatThrownBy(client::fetchToken)
            .isInstanceOf(RestClientException.class)
            .isNotInstanceOf(ResourceAccessException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PT0S", "PT-1S", "PT0.0005S"})
    void timeoutsBelowOneMillisecondAreRejected(String value) {
        var zeroRead = tokenEndpoint.config();
        zeroRead.setReadTimeout(Duration.parse(value));
        var zeroConnect = tokenEndpoint.config();
        zeroConnect.setConnectTimeout(Duration.parse(value));

        // 0 ville elles gitt «ingen grense» i JdkClientHttpRequestFactory.
        assertThatThrownBy(() -> new JwtTokenClient(zeroRead))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("readTimeout");
        assertThatThrownBy(() -> new JwtTokenClient(zeroConnect))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("connectTimeout");
    }

    @Test
    void fetchTokenSucceedsWhenTokenEndpointAnswersWithinReadTimeout() {
        tokenEndpoint.respondAfter(Duration.ofMillis(200));
        var config = tokenEndpoint.config();
        config.setReadTimeout(Duration.ofSeconds(5));
        var client = new JwtTokenClient(config);

        assertThat(client.fetchToken().getAccessToken()).isEqualTo("test-access-token");
    }

    @Test
    void timeoutsDefaultWhenNotConfigured() {
        var config = tokenEndpoint.config();

        assertThat(config.getConnectTimeout()).isEqualTo(Duration.ofSeconds(5));
        assertThat(config.getReadTimeout()).isEqualTo(Duration.ofSeconds(10));
        assertThat(new JwtTokenConfig().getConnectTimeout()).isEqualTo(JwtTokenConfig.DEFAULT_CONNECT_TIMEOUT);
        assertThat(new JwtTokenConfig().getReadTimeout()).isEqualTo(JwtTokenConfig.DEFAULT_READ_TIMEOUT);
    }

    @Test
    void timeoutsFallBackToDefaultsWhenSetToNull() {
        var config = tokenEndpoint.config();
        config.setConnectTimeout(null);
        config.setReadTimeout(null);

        assertThat(JwtTokenClient.connectTimeout(config)).isEqualTo(JwtTokenConfig.DEFAULT_CONNECT_TIMEOUT);
        assertThat(JwtTokenClient.readTimeout(config)).isEqualTo(JwtTokenConfig.DEFAULT_READ_TIMEOUT);
        assertThat(new JwtTokenClient(config).fetchToken().getAccessToken()).isEqualTo("test-access-token");
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
