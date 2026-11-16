package no.difi.move.common.oauth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testar JwtTokenClient med webflux på classpath, slik appar som brukar fetchTokenMono() ser han.
 */
class JwtTokenClientTest {

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
    void fetchTokenMonoFetchesToken() {
        var client = new JwtTokenClient(tokenEndpoint.config());

        JwtTokenResponse response = client.fetchTokenMono().block(Duration.ofSeconds(10));

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("test-access-token");
        assertThat(response.getExpiresIn()).isEqualTo(120);
        assertThat(tokenEndpoint.requests()).singleElement().satisfies(request -> {
            assertThat(request.contentType()).startsWith("application/x-www-form-urlencoded");
            assertThat(request.form())
                .containsEntry("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                .containsKey("assertion");
        });
    }

    @Test
    void fetchTokenMonoCachesResponseUntilExpiry() {
        var client = new JwtTokenClient(tokenEndpoint.config());
        var mono = client.fetchTokenMono();

        mono.block(Duration.ofSeconds(10));
        mono.block(Duration.ofSeconds(10));

        assertThat(tokenEndpoint.requests()).hasSize(1);
    }

    @Test
    void fetchTokenWorksWithWebfluxOnClasspath() {
        var client = new JwtTokenClient(tokenEndpoint.config());

        assertThat(client.fetchToken().getAccessToken()).isEqualTo("test-access-token");
    }

    @Test
    void jwtWebClientBuilderCanBeCreated() {
        var client = new JwtTokenClient(tokenEndpoint.config());

        assertThat(JwtWebClient.builder("http://localhost", "test", client).build()).isNotNull();
    }
}
