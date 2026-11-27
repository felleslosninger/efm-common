package no.difi.move.common.oauth;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.difi.move.common.config.JwkProperties;
import no.difi.move.common.config.KeystoreProperties;

import jakarta.validation.Valid;

import java.time.Duration;
import java.util.List;

@Data
@NoArgsConstructor
public class JwtTokenConfig {

    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(5);
    public static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(10);

    public JwtTokenConfig(String clientId, String tokenUri, String audience, List<String> scopes, KeystoreProperties keystore
    ) {
        this.clientId = clientId;
        this.tokenUri = tokenUri;
        this.audience = audience;
        this.scopes = scopes;
        this.keystore = keystore;
        authenticationType = AuthenticationType.CERTIFICATE;
    }

    public JwtTokenConfig(String clientId, String tokenUri, String audience, List<String> scopes, JwkProperties jwk
    ) {
        this.clientId = clientId;
        this.tokenUri = tokenUri;
        this.audience = audience;
        this.scopes = scopes;
        this.jwk = jwk;
        authenticationType = AuthenticationType.JWK;
    }

    private String clientId;
    private String tokenUri;
    private String audience;
    private List<String> scopes;

    private AuthenticationType authenticationType = AuthenticationType.CERTIFICATE;

    @Valid
    private KeystoreProperties keystore;

    @Valid
    private JwkProperties jwk;

    /**
     * Maks tid på å opne tilkopling til token-endepunktet i {@link JwtTokenClient#fetchToken()}.
     * {@code null} gir {@link #DEFAULT_CONNECT_TIMEOUT}. Må vere minst 1 ms.
     * Skriv eining i properties (t.d. {@code 5s}); eit tal utan eining blir tolka som millisekund.
     */
    private Duration connectTimeout = DEFAULT_CONNECT_TIMEOUT;

    /**
     * Maks samla tid for eitt kall i {@link JwtTokenClient#fetchToken()}, frå førespurnaden blir send til heile
     * svaret er lese (tilkopling medrekna). {@code null} gir {@link #DEFAULT_READ_TIMEOUT}. Må vere minst 1 ms.
     * Skriv eining i properties (t.d. {@code 10s}); eit tal utan eining blir tolka som millisekund.
     */
    private Duration readTimeout = DEFAULT_READ_TIMEOUT;
}
